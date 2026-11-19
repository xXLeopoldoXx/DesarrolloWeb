import { Component, computed, ElementRef, inject, OnInit, signal, viewChild } from '@angular/core';
import { finalize } from 'rxjs';

import { mensajeDeError } from '../../core/http-errors';
import { Icono } from '../../shared/icono/icono';
import { AsistenteService, RolMensaje } from './asistente.service';

interface Mensaje {
  rol: RolMensaje;
  texto: string;
  emergencia?: boolean;
}

const MAX_CARACTERES = 500;
/** Cuántos mensajes anteriores se reenvían como contexto (el backend admite hasta 6). */
const MAX_HISTORIAL = 6;

/**
 * Chat con el asistente de orientación.
 * La conversación vive solo en memoria de esta pantalla: no se guarda en el navegador ni en el servidor.
 */
@Component({
  selector: 'app-asistente',
  imports: [Icono],
  templateUrl: './asistente.html',
  styleUrl: './asistente.scss'
})
export class Asistente implements OnInit {
  private readonly api = inject(AsistenteService);

  protected readonly maxCaracteres = MAX_CARACTERES;
  protected readonly sugerencias = [
    '¿Qué es un plan de seguridad?',
    '¿Cuáles son señales de alerta en una relación?',
    '¿Qué hace la Línea 100?'
  ];

  protected readonly mensajes = signal<Mensaje[]>([]);
  protected readonly borrador = signal('');
  protected readonly enviando = signal(false);
  protected readonly error = signal('');
  /** null = todavía no se sabe. false = el backend no tiene clave de Gemini. */
  protected readonly disponible = signal<boolean | null>(null);

  protected readonly puedeEnviar = computed(() => {
    const texto = this.borrador().trim();
    return (
      this.disponible() !== false && !this.enviando() && texto.length > 0 && this.borrador().length <= MAX_CARACTERES
    );
  });

  protected readonly sugerenciasActivas = computed(() => this.disponible() !== false && !this.enviando());

  private readonly lista = viewChild<ElementRef<HTMLElement>>('lista');

  ngOnInit(): void {
    this.api.estado().subscribe({
      next: estado => this.disponible.set(estado.disponible),
      error: () => this.disponible.set(null)
    });
  }

  protected alEscribir(evento: Event): void {
    this.borrador.set((evento.target as HTMLTextAreaElement).value);
  }

  /** Enter envía; Shift+Enter agrega un salto de línea. */
  protected alPresionarEnter(evento: Event): void {
    if ((evento as KeyboardEvent).shiftKey) {
      return;
    }
    evento.preventDefault();
    this.enviar();
  }

  protected alEnviarFormulario(evento: Event): void {
    evento.preventDefault();
    this.enviar();
  }

  protected usarSugerencia(texto: string): void {
    this.borrador.set(texto);
    this.enviar();
  }

  protected enviar(): void {
    if (!this.puedeEnviar()) {
      return;
    }

    const texto = this.borrador().trim();
    // Contexto: los últimos mensajes ANTES del nuevo
    const historial = this.mensajes()
      .slice(-MAX_HISTORIAL)
      .map(m => ({ rol: m.rol, texto: m.texto }));

    this.mensajes.update(lista => [...lista, { rol: 'usuaria', texto }]);
    this.borrador.set('');
    this.error.set('');
    this.enviando.set(true);
    this.desplazarAlFinal();

    this.api
      .enviar(texto, historial)
      .pipe(finalize(() => this.enviando.set(false)))
      .subscribe({
        next: respuesta => {
          this.mensajes.update(lista => [
            ...lista,
            { rol: 'asistente', texto: respuesta.respuesta, emergencia: respuesta.emergencia }
          ]);
          this.desplazarAlFinal();
        },
        error: error =>
          this.error.set(mensajeDeError(error, 'No se pudo obtener una respuesta del asistente. Inténtalo de nuevo.'))
      });
  }

  protected limpiarConversacion(): void {
    this.mensajes.set([]);
    this.error.set('');
  }

  private desplazarAlFinal(): void {
    setTimeout(() => {
      const elemento = this.lista()?.nativeElement;
      if (elemento) {
        elemento.scrollTop = elemento.scrollHeight;
      }
    });
  }
}

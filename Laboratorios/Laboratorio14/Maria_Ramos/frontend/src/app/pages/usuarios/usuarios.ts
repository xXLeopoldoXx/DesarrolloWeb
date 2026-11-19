import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { Usuario, UsuarioEntrada } from '../../models/usuario';
import { UsuarioService } from '../../services/usuario';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './usuarios.html',
  styleUrl: './usuarios.scss'
})
export class Usuarios {

  private fb = inject(FormBuilder);
  private usuarioService = inject(UsuarioService);

  usuarios = signal<Usuario[]>([]);

  // Buscador de usuarios
  busqueda = signal('');

  // Filtrar por nombre o correo electrónico
  usuariosFiltrados = computed(() => {
    const termino = this.busqueda().trim().toLowerCase();

    return this.usuarios().filter(usuario =>
      usuario.name.toLowerCase().includes(termino) ||
      usuario.email.toLowerCase().includes(termino)
    );
  });

  cargando = signal(false);
  guardando = signal(false);
  error = signal('');
  mensaje = signal('');
  usuarioEditando = signal<number | null>(null);

  formulario = this.fb.nonNullable.group({
    name: ['', Validators.required],
    username: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phone: [''],
    website: ['']
  });

  constructor() {
    this.obtenerUsuarios();
  }

  // Consultar usuarios
  obtenerUsuarios(): void {
    this.cargando.set(true);
    this.error.set('');
    this.mensaje.set('');

    this.usuarioService.obtenerTodos()
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: (usuarios) => this.usuarios.set(usuarios),
        error: (error) => {
          console.error('Error al obtener usuarios:', error);
          this.error.set('No se pudo obtener la lista de usuarios.');
        }
      });
  }

  // Registrar o actualizar usuario
  guardarUsuario(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    const datos: UsuarioEntrada = this.formulario.getRawValue();
    const id = this.usuarioEditando();

    this.guardando.set(true);
    this.error.set('');
    this.mensaje.set('');

    const peticion = id === null
      ? this.usuarioService.crear(datos)
      : this.usuarioService.actualizar(id, datos);

    peticion
      .pipe(finalize(() => this.guardando.set(false)))
      .subscribe({
        next: (respuesta) => {
          if (id === null) {
            this.usuarios.update(lista => [
              { ...respuesta, ...datos },
              ...lista
            ]);
            this.mensaje.set('Usuario registrado correctamente.');
          } else {
            this.usuarios.update(lista =>
              lista.map(usuario =>
                usuario.id === id ? { ...usuario, ...datos } : usuario
              )
            );
            this.mensaje.set('Usuario actualizado correctamente.');
          }

          this.limpiarFormulario();
        },
        error: (error) => {
          console.error('Error al guardar usuario:', error);
          this.error.set('No se pudo guardar el usuario.');
        }
      });
  }

  // Cargar datos para editar
  editarUsuario(usuario: Usuario): void {
    this.usuarioEditando.set(usuario.id);

    this.formulario.patchValue({
      name: usuario.name,
      username: usuario.username,
      email: usuario.email,
      phone: usuario.phone,
      website: usuario.website
    });

    this.mensaje.set('');
    this.error.set('');
  }

  // Eliminar usuario
  eliminarUsuario(usuario: Usuario): void {
    const confirmar = window.confirm(
      `¿Deseas eliminar al usuario ${usuario.name}?`
    );

    if (!confirmar) return;

    this.error.set('');
    this.mensaje.set('');

    this.usuarioService.eliminar(usuario.id).subscribe({
      next: () => {
        this.usuarios.update(lista =>
          lista.filter(item => item.id !== usuario.id)
        );
        this.mensaje.set('Usuario eliminado correctamente.');
      },
      error: (error) => {
        console.error('Error al eliminar usuario:', error);
        this.error.set('No se pudo eliminar el usuario.');
      }
    });
  }

  // Limpiar formulario
  limpiarFormulario(): void {
    this.formulario.reset({
      name: '',
      username: '',
      email: '',
      phone: '',
      website: ''
    });

    this.usuarioEditando.set(null);
  }

  // Actualizar parcialmente el correo de un usuario
  cambiarCorreo(usuario: Usuario): void {
    this.usuarioService.actualizarParcial(usuario.id, {
      email: 'nuevo@correo.com'
    }).subscribe({
      next: (respuesta) => {
        this.usuarios.update(lista =>
          lista.map(item =>
            item.id === usuario.id
              ? { ...item, email: respuesta.email }
              : item
          )
        );

        this.mensaje.set('Correo actualizado correctamente.');
      },
      error: (error) => {
        console.error('Error al actualizar correo:', error);
        this.error.set('No se pudo actualizar el correo.');
      }
    });
  }

}

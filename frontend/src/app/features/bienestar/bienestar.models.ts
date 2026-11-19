export interface EntradaDiario {
  id: number;
  usuarioId: number;
  titulo: string;
  fecha: string;
  estadoAnimo: number;
  emocion?: string | null;
  contenido: string;
}

export interface EntradaDiarioRequest {
  titulo: string;
  estadoAnimo: number;
  emocion?: string | null;
  contenido: string;
}

export interface RecursoAutocuidado {
  id: number;
  categoria: string;
  titulo: string;
  descripcion: string;
  tipo: string;
  enlaceOConsejo?: string | null;
}

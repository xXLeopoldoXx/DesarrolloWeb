export interface Accion {
  id: number;
  texto: string;
  completada: boolean;
}

export interface LugarSolicitud {
  nombre: string;
  referencia: string;
  nota: string;
}

export interface Lugar {
  id: number;
  nombre: string;
  referencia: string | null;
  nota: string | null;
}

export interface ContactoSolicitud {
  nombre: string;
  telefono: string;
  relacion: string;
}

export interface Contacto {
  id: number;
  nombre: string;
  telefono: string;
  relacion: string | null;
}

export interface EstadoBorradoCuenta {
  disponible: boolean;
}

export interface Contacto {
  nombre: string;
  correo: string;
  tipo: string;
  mensaje: string;
  acepta: boolean;
}

export function crearContactoVacio(): Contacto {
  return {
    nombre: '',
    correo: '',
    tipo: 'consulta',
    mensaje: '',
    acepta: false
  };
}

/** Utilidades de presentación de la Comunidad (puras, para poder probarlas sin Angular). */

const MINUTO = 60;
const HORA = 60 * MINUTO;
const DIA = 24 * HORA;

/** «justo ahora», «hace 5 min», «hace 3 h», «hace 2 días» o la fecha si pasó más de una semana. */
export function haceCuanto(fechaIso: string, ahora: Date = new Date()): string {
  const fecha = new Date(fechaIso);
  if (Number.isNaN(fecha.getTime())) {
    return '';
  }

  const segundos = Math.floor((ahora.getTime() - fecha.getTime()) / 1000);
  if (segundos < MINUTO) {
    return 'justo ahora'; // también cubre relojes levemente desfasados (fechas futuras)
  }
  if (segundos < HORA) {
    return `hace ${Math.floor(segundos / MINUTO)} min`;
  }
  if (segundos < DIA) {
    return `hace ${Math.floor(segundos / HORA)} h`;
  }
  const dias = Math.floor(segundos / DIA);
  if (dias < 7) {
    return `hace ${dias} ${dias === 1 ? 'día' : 'días'}`;
  }

  const dd = String(fecha.getDate()).padStart(2, '0');
  const mm = String(fecha.getMonth() + 1).padStart(2, '0');
  return `${dd}/${mm}/${fecha.getFullYear()}`;
}

/** Primera letra del seudónimo, en mayúscula (o «?» si no hay). */
export function inicialDe(seudonimo: string | null | undefined): string {
  const letra = (seudonimo ?? '').trim().charAt(0);
  return letra ? letra.toLocaleUpperCase('es') : '?';
}

export const TONOS_AVATAR = 5;

/** Un tono estable (0 a 4) por seudónimo: la misma persona siempre tiene el mismo color. */
export function tonoAvatar(seudonimo: string | null | undefined): number {
  const texto = (seudonimo ?? '').trim().toLowerCase();
  let suma = 0;
  for (let i = 0; i < texto.length; i++) {
    suma = (suma * 31 + texto.charCodeAt(i)) % 9973;
  }
  return suma % TONOS_AVATAR;
}

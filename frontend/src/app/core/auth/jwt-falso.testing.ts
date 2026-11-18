/** Ayuda para pruebas: crea un JWT de mentira (la firma no importa, el frontend no la valida). */
export function jwtFalso(roles: string[], expiraEnSegundos: number): string {
  const cuerpo = { sub: 'maria.l', roles, exp: Math.floor(Date.now() / 1000) + expiraEnSegundos };
  return `cabecera.${btoa(JSON.stringify(cuerpo))}.firma`;
}

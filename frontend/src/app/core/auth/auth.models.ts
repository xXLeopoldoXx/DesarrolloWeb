/** Respuesta de POST /api/auth/login y /api/auth/registro */
export interface TokenResponse {
  tokenType: string;
  accessToken: string;
  expiresInSeconds: number;
}

/** Respuesta de GET /api/mi/perfil */
export interface Perfil {
  id: number;
  username: string;
  seudonimo: string | null;
  roles: string[];
}

export interface RegistroDatos {
  username: string;
  password: string;
  seudonimo: string;
}

/** Contenido (claims) del JWT que firma el backend. */
export interface JwtClaims {
  sub: string;
  exp: number;
  roles: string[];
}

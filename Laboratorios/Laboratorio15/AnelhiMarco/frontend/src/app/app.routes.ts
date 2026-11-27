import { Routes } from '@angular/router';
import { adminGuard, authGuard } from './core/auth.guards';
import { Login } from './pages/login/login';
import { Productos } from './pages/productos/productos';
import { SalidaStock } from './pages/salida-stock/salida-stock';
import { NuevoProducto } from './pages/nuevo-producto/nuevo-producto';
import { PanelAdmin } from './pages/panel-admin/panel-admin';
import { NoEncontrado } from './pages/no-encontrado';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'productos' },
  { path: 'login', component: Login, title: 'Acceso | Tienda UTP' },
  { path: 'productos', component: Productos, canActivate: [authGuard], title: 'Catálogo | Tienda UTP' },
  { path: 'productos/nuevo', component: NuevoProducto, canActivate: [authGuard, adminGuard], title: 'Nuevo producto | Tienda UTP' },
  { path: 'inventario/salida/:id', component: SalidaStock, canActivate: [authGuard, adminGuard], title: 'Salida de stock | Tienda UTP' },
  { path: 'admin', component: PanelAdmin, canActivate: [authGuard, adminGuard], title: 'Indicadores | Tienda UTP' },
  { path: '**', component: NoEncontrado, title: 'Página no encontrada | Tienda UTP' },
];

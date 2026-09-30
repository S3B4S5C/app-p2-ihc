import { Routes } from '@angular/router';
import { authGuard, publicOnlyGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  { path: 'login', canActivate: [publicOnlyGuard], loadComponent: () => import('./pages/login/login.page').then((m) => m.LoginPage) },
  { path: 'registro', canActivate: [publicOnlyGuard], loadComponent: () => import('./pages/register/register.page').then((m) => m.RegisterPage) },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () => import('./layouts/app-shell/app-shell.component').then((m) => m.AppShellComponent),
    children: [
      { path: 'mis-mascotas', loadComponent: () => import('./pages/my-pets/my-pets.page').then((m) => m.MyPetsPage) },
      { path: '', pathMatch: 'full', redirectTo: 'mis-mascotas' },
    ],
  },
  { path: '**', redirectTo: '' },
];

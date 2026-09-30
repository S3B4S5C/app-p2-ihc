import { Injectable, signal } from '@angular/core';
import { AuthSession } from '../../models/auth.models';

const SESSION_KEY = 'mascotas-al-dia.session';

@Injectable({ providedIn: 'root' })
export class TokenStorageService {
  readonly session = signal<AuthSession | null>(this.load());

  read(): AuthSession | null {
    const current = this.session();
    if (current && !this.isTokenActive(current.accessToken)) {
      this.clear();
      return null;
    }
    return current;
  }

  save(session: AuthSession): void {
    localStorage.setItem(SESSION_KEY, JSON.stringify(session));
    this.session.set(session);
  }

  clear(): void {
    localStorage.removeItem(SESSION_KEY);
    this.session.set(null);
  }

  token(): string | null {
    return this.read()?.accessToken ?? null;
  }

  private load(): AuthSession | null {
    const raw = localStorage.getItem(SESSION_KEY);
    if (!raw) return null;
    try {
      const session = JSON.parse(raw) as AuthSession;
      if (session.accessToken && this.isTokenActive(session.accessToken)) return session;
      localStorage.removeItem(SESSION_KEY);
      return null;
    } catch {
      localStorage.removeItem(SESSION_KEY);
      return null;
    }
  }

  private isTokenActive(token: string): boolean {
    try {
      const payloadPart = token.split('.')[1];
      if (!payloadPart) return false;
      const normalized = payloadPart.replace(/-/g, '+').replace(/_/g, '/');
      const padded = normalized + '='.repeat((4 - (normalized.length % 4)) % 4);
      const payload = JSON.parse(atob(padded)) as { exp?: number };
      return typeof payload.exp === 'number' && payload.exp * 1000 > Date.now();
    } catch {
      return false;
    }
  }
}

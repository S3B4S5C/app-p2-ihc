import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { environment } from '../../../environments/environment';
import { AuthResponse } from '../../models/auth.models';
import { AuthService } from './auth.service';

function token(): string {
  const encode = (value: object) => btoa(JSON.stringify(value)).replace(/=/g, '').replace(/\+/g, '-').replace(/\//g, '_');
  return `${encode({ alg: 'none' })}.${encode({ exp: Math.floor(Date.now() / 1000) + 3600 })}.signature`;
}

describe('AuthService', () => {
  let service: AuthService;
  let http: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    service = TestBed.inject(AuthService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('persists the session after login', () => {
    const response: AuthResponse = {
      accessToken: token(), tokenType: 'Bearer', expiresInSeconds: 3600,
      user: { id: '1', fullName: 'Ana Pérez', email: 'ana@example.com' },
    };

    service.login({ email: 'ana@example.com', password: 'password123' }).subscribe();
    http.expectOne(`${environment.apiUrl}/auth/login`).flush(response);

    expect(service.isAuthenticated()).toBe(true);
    expect(service.user()?.fullName).toBe('Ana Pérez');
  });

  it('clears session on logout', () => {
    service.logout();
    expect(service.isAuthenticated()).toBe(false);
  });
});

import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { environment } from '../../../environments/environment';
import { authInterceptor } from './auth.interceptor';
import { TokenStorageService } from './token-storage.service';

function token(): string {
  const payload = btoa(JSON.stringify({ exp: Math.floor(Date.now() / 1000) + 3600 })).replace(/=/g, '').replace(/\+/g, '-').replace(/\//g, '_');
  return `header.${payload}.signature`;
}

describe('authInterceptor', () => {
  let http: HttpClient;
  let testing: HttpTestingController;
  let storage: TokenStorageService;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(withInterceptors([authInterceptor])), provideHttpClientTesting()],
    });
    http = TestBed.inject(HttpClient);
    testing = TestBed.inject(HttpTestingController);
    storage = TestBed.inject(TokenStorageService);
  });

  afterEach(() => testing.verify());

  it('adds the bearer token to API requests', () => {
    const accessToken = token();
    storage.save({ accessToken, user: { id: '1', fullName: 'Ana', email: 'ana@example.com' } });

    http.get(`${environment.apiUrl}/auth/me`).subscribe();
    const request = testing.expectOne(`${environment.apiUrl}/auth/me`);

    expect(request.request.headers.get('Authorization')).toBe(`Bearer ${accessToken}`);
    request.flush({});
  });

  it('clears the session after an API 401', () => {
    storage.save({ accessToken: token(), user: { id: '1', fullName: 'Ana', email: 'ana@example.com' } });

    http.get(`${environment.apiUrl}/auth/me`).subscribe({ error: () => undefined });
    testing.expectOne(`${environment.apiUrl}/auth/me`).flush({}, { status: 401, statusText: 'Unauthorized' });

    expect(storage.session()).toBeNull();
  });
});

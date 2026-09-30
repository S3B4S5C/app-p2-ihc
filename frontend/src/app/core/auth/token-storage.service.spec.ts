import { TestBed } from '@angular/core/testing';
import { TokenStorageService } from './token-storage.service';

function tokenWithExpiration(exp: number): string {
  const encode = (value: object) => btoa(JSON.stringify(value)).replace(/=/g, '').replace(/\+/g, '-').replace(/\//g, '_');
  return `${encode({ alg: 'none' })}.${encode({ exp })}.signature`;
}

describe('TokenStorageService', () => {
  let service: TokenStorageService;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({});
    service = TestBed.inject(TokenStorageService);
  });

  it('stores and reads an active session', () => {
    const accessToken = tokenWithExpiration(Math.floor(Date.now() / 1000) + 3600);
    service.save({ accessToken, user: { id: '1', fullName: 'Ana', email: 'ana@example.com' } });

    expect(service.read()?.user.email).toBe('ana@example.com');
    expect(service.token()).toBe(accessToken);
  });

  it('clears an expired token', () => {
    const accessToken = tokenWithExpiration(Math.floor(Date.now() / 1000) - 5);
    service.save({ accessToken, user: { id: '1', fullName: 'Ana', email: 'ana@example.com' } });

    expect(service.read()).toBeNull();
    expect(service.session()).toBeNull();
  });
});

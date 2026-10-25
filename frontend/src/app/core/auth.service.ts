import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';

const TOKEN = 'contos.token';
const NAME = 'contos.name';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly router = inject(Router);
  private readonly token = signal<string | null>(localStorage.getItem(TOKEN));
  readonly name = signal<string | null>(localStorage.getItem(NAME));
  readonly isLoggedIn = computed(() => !!this.token());

  tokenValue(): string | null {
    return this.token();
  }

  setSession(token: string, name: string): void {
    localStorage.setItem(TOKEN, token);
    localStorage.setItem(NAME, name);
    this.token.set(token);
    this.name.set(name);
  }

  clear(): void {
    localStorage.removeItem(TOKEN);
    localStorage.removeItem(NAME);
    this.token.set(null);
    this.name.set(null);
  }

  logout(): void {
    this.clear();
    void this.router.navigateByUrl('/');
  }
}

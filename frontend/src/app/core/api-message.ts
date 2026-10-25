import { HttpErrorResponse } from '@angular/common/http';

export function apiMessage(error: unknown, fallback: string): string {
  if (error instanceof HttpErrorResponse) {
    const message = error.error?.message;
    if (typeof message === 'string' && message.trim()) {
      return message;
    }
    if (error.status === 0) {
      return 'Não foi possível falar com o servidor.';
    }
  }
  return fallback;
}

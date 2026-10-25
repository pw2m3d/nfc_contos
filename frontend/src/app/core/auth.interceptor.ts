import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { tap } from 'rxjs';
import { AuthService } from './auth.service';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const auth = inject(AuthService);
  const token = auth.tokenValue();
  const outgoing = token
    ? request.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : request;
  return next(outgoing).pipe(
    tap({
      error: (error: unknown) => {
        const keepsSession = outgoing.url.includes('/auth/login')
          || outgoing.url.includes('/activate')
          || outgoing.url.includes('/api/admin');
        if (error instanceof HttpErrorResponse && error.status === 401 && token && !keepsSession) {
          auth.clear();
        }
      },
    }),
  );
};

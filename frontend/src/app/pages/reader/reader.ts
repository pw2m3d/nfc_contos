import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { combineLatest, firstValueFrom } from 'rxjs';
import { apiMessage } from '../../core/api-message';
import { ApiService } from '../../core/api.service';

@Component({
  selector: 'app-reader',
  imports: [RouterLink],
  templateUrl: './reader.html',
})
export class ReaderPage {
  private readonly api = inject(ApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly sanitizer = inject(DomSanitizer);

  protected readonly frame = signal<SafeResourceUrl | null>(null);
  protected readonly error = signal('');
  private generation = 0;

  constructor() {
    combineLatest([this.route.paramMap, this.route.queryParamMap]).subscribe(() => {
      void this.open();
    });
  }

  private async open(): Promise<void> {
    const slug = this.route.snapshot.paramMap.get('slug') ?? '';
    const ticket = this.route.snapshot.queryParamMap.get('ticket');
    const generation = ++this.generation;
    this.error.set('');
    try {
      const access = ticket ?? (await firstValueFrom(this.api.ticket(slug))).ticket;
      if (generation !== this.generation) {
        return;
      }
      this.frame.set(this.sanitizer.bypassSecurityTrustResourceUrl(`/api/reading/${encodeURIComponent(access)}`));
    } catch (error) {
      if (generation !== this.generation) {
        return;
      }
      this.frame.set(null);
      if (error instanceof HttpErrorResponse && error.status === 401) {
        await this.router.navigate(['/entrar'], { queryParams: { returnUrl: `/ler/${slug}` } });
        return;
      }
      this.error.set(apiMessage(error, 'Não foi possível abrir esta história.'));
    }
  }
}

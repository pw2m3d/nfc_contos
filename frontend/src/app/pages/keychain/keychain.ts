import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { apiMessage } from '../../core/api-message';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { KeychainView } from '../../core/models';

@Component({
  selector: 'app-keychain',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './keychain.html',
})
export class KeychainPage {
  private readonly api = inject(ApiService);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly error = signal('');
  protected readonly view = signal<KeychainView | null>(null);
  protected readonly form = inject(FormBuilder).nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    pin: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]],
  });

  constructor() {
    this.route.paramMap.subscribe((params) => {
      void this.load(params.get('code') ?? '');
    });
  }

  protected async submit(): Promise<void> {
    const current = this.view();
    if (!current || this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    this.error.set('');
    const value = this.form.getRawValue();
    try {
      const result = await firstValueFrom(this.api.activate(current.publicCode, {
        name: value.name.trim(),
        email: value.email.trim(),
        password: value.password,
        pin: value.pin,
      }));
      this.auth.setSession(result.token, result.name);
      await this.router.navigate(['/ler', result.storySlug], { queryParams: { ticket: result.ticket }, replaceUrl: true });
    } catch (error) {
      this.error.set(apiMessage(error, 'Não foi possível ativar o chaveiro.'));
    } finally {
      this.saving.set(false);
    }
  }

  private async load(code: string): Promise<void> {
    this.loading.set(true);
    this.error.set('');
    this.view.set(null);
    try {
      const view = await firstValueFrom(this.api.keychain(code));
      if (view.status === 'ACTIVATED' && view.ticket) {
        await this.router.navigate(['/ler', view.story.slug], { queryParams: { ticket: view.ticket }, replaceUrl: true });
        return;
      }
      this.view.set(view);
    } catch (error) {
      this.error.set(apiMessage(error, 'Não foi possível ler este chaveiro.'));
    } finally {
      this.loading.set(false);
    }
  }
}

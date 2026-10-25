import { DatePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { apiMessage } from '../../core/api-message';
import { ApiService } from '../../core/api.service';
import { ReaisPipe } from '../../core/reais.pipe';
import { Order } from '../../core/models';

@Component({
  selector: 'app-checkout',
  imports: [RouterLink, ReaisPipe, DatePipe],
  templateUrl: './checkout.html',
})
export class CheckoutPage {
  private readonly api = inject(ApiService);
  private readonly route = inject(ActivatedRoute);

  protected readonly order = signal<Order | null>(null);
  protected readonly error = signal('');
  protected readonly saving = signal(false);

  constructor() {
    this.route.paramMap.subscribe((params) => {
      void this.load(params.get('id') ?? '');
    });
  }

  protected async pay(): Promise<void> {
    const current = this.order();
    if (!current) {
      return;
    }
    this.saving.set(true);
    this.error.set('');
    try {
      this.order.set(await firstValueFrom(this.api.pay(current.id)));
    } catch (error) {
      this.error.set(apiMessage(error, 'Não foi possível confirmar o pagamento.'));
    } finally {
      this.saving.set(false);
    }
  }

  private async load(id: string): Promise<void> {
    this.error.set('');
    try {
      this.order.set(await firstValueFrom(this.api.order(id)));
    } catch (error) {
      this.order.set(null);
      this.error.set(apiMessage(error, 'Pedido não encontrado.'));
    }
  }
}

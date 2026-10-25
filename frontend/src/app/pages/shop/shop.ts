import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { apiMessage } from '../../core/api-message';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { ReaisPipe } from '../../core/reais.pipe';
import { StoryCard } from '../../core/models';

@Component({
  selector: 'app-shop',
  imports: [RouterLink, ReaisPipe],
  templateUrl: './shop.html',
})
export class ShopPage {
  private readonly api = inject(ApiService);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly stories = signal<StoryCard[]>([]);
  protected readonly error = signal('');
  protected readonly buying = signal('');

  constructor() {
    void this.load();
  }

  protected async buy(story: StoryCard): Promise<void> {
    if (!this.auth.isLoggedIn()) {
      await this.router.navigate(['/entrar'], { queryParams: { returnUrl: '/loja' } });
      return;
    }
    this.buying.set(story.slug);
    this.error.set('');
    try {
      const order = await firstValueFrom(this.api.createOrder(story.slug));
      await this.router.navigate(['/pedido', order.id]);
    } catch (error) {
      this.error.set(apiMessage(error, 'Não foi possível abrir o pedido.'));
    } finally {
      this.buying.set('');
    }
  }

  private async load(): Promise<void> {
    try {
      this.stories.set(await firstValueFrom(this.api.stories()));
    } catch (error) {
      this.error.set(apiMessage(error, 'Não foi possível carregar a loja.'));
    }
  }
}

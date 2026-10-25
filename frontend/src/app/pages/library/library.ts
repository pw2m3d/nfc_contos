import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { apiMessage } from '../../core/api-message';
import { ApiService } from '../../core/api.service';
import { LibraryItem } from '../../core/models';

@Component({
  selector: 'app-library',
  imports: [RouterLink],
  templateUrl: './library.html',
})
export class LibraryPage {
  private readonly api = inject(ApiService);
  protected readonly items = signal<LibraryItem[]>([]);
  protected readonly error = signal('');

  constructor() {
    void this.load();
  }

  protected origin(source: LibraryItem['source']): string {
    return source === 'KEYCHAIN' ? 'Veio com o chaveiro' : 'Comprada';
  }

  private async load(): Promise<void> {
    try {
      this.items.set(await firstValueFrom(this.api.library()));
    } catch (error) {
      this.error.set(apiMessage(error, 'Não foi possível abrir a biblioteca.'));
    }
  }
}

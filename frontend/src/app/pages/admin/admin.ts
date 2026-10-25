import { DatePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { apiMessage } from '../../core/api-message';
import { ApiService } from '../../core/api.service';
import { ReaisPipe } from '../../core/reais.pipe';
import { CreatedKeychain, KeychainAdmin, StoryCard } from '../../core/models';

const ADMIN_KEY = 'contos.adminKey';

@Component({
  selector: 'app-admin',
  imports: [ReactiveFormsModule, ReaisPipe, DatePipe],
  templateUrl: './admin.html',
})
export class AdminPage {
  private readonly api = inject(ApiService);
  private readonly forms = inject(FormBuilder);

  protected readonly error = signal('');
  protected readonly notice = signal('');
  protected readonly stories = signal<StoryCard[]>([]);
  protected readonly keychains = signal<KeychainAdmin[]>([]);
  protected readonly created = signal<CreatedKeychain | null>(null);
  protected readonly ready = signal(false);

  protected readonly access = this.forms.nonNullable.group({
    adminKey: [sessionStorage.getItem(ADMIN_KEY) ?? '', Validators.required],
  });
  protected readonly keychainForm = this.forms.nonNullable.group({
    storySlug: ['', Validators.required],
  });
  protected readonly storyForm = this.forms.nonNullable.group({
    code: ['', Validators.required],
    slug: ['', Validators.required],
    title: ['', Validators.required],
    author: ['', Validators.required],
    synopsis: ['', Validators.required],
    price: ['19,90', Validators.required],
    htmlFile: [''],
  });
  protected readonly updateForm = this.forms.nonNullable.group({
    slug: ['', Validators.required],
    price: [''],
    htmlFile: [''],
    active: [true],
  });

  protected async enter(): Promise<void> {
    const adminKey = this.access.controls.adminKey.value.trim();
    sessionStorage.setItem(ADMIN_KEY, adminKey);
    await this.reload(adminKey);
  }

  protected async makeKeychain(): Promise<void> {
    const adminKey = this.key();
    const storySlug = this.keychainForm.controls.storySlug.value;
    if (!adminKey || !storySlug) {
      return;
    }
    this.error.set('');
    try {
      this.created.set(await firstValueFrom(this.api.createKeychain(adminKey, storySlug)));
      this.notice.set('Chaveiro criado. Anote o PIN agora: ele não aparece de novo.');
      await this.reload(adminKey);
    } catch (error) {
      this.error.set(apiMessage(error, 'Não foi possível criar o chaveiro.'));
    }
  }

  protected async makeStory(): Promise<void> {
    const adminKey = this.key();
    const value = this.storyForm.getRawValue();
    const priceCents = reaisToCents(value.price);
    if (!adminKey || priceCents === null) {
      this.error.set('Informe o preço como 19,90.');
      return;
    }
    this.error.set('');
    try {
      await firstValueFrom(this.api.createStory(adminKey, {
        code: value.code.trim(),
        slug: value.slug.trim(),
        title: value.title.trim(),
        author: value.author.trim(),
        synopsis: value.synopsis.trim(),
        priceCents,
        htmlFile: value.htmlFile.trim(),
      }));
      this.notice.set('História cadastrada.');
      this.storyForm.reset({ price: '19,90', htmlFile: '' });
      await this.reload(adminKey);
    } catch (error) {
      this.error.set(apiMessage(error, 'Não foi possível cadastrar a história.'));
    }
  }

  protected async saveStory(): Promise<void> {
    const adminKey = this.key();
    const value = this.updateForm.getRawValue();
    if (!adminKey || !value.slug) {
      return;
    }
    const body: { priceCents?: number; htmlFile?: string; active?: boolean } = { active: value.active };
    if (value.price.trim()) {
      const priceCents = reaisToCents(value.price);
      if (priceCents === null) {
        this.error.set('Informe o preço como 19,90.');
        return;
      }
      body.priceCents = priceCents;
    }
    if (value.htmlFile.trim()) {
      body.htmlFile = value.htmlFile.trim();
    }
    this.error.set('');
    try {
      await firstValueFrom(this.api.updateStory(adminKey, value.slug, body));
      this.notice.set('História atualizada.');
      await this.reload(adminKey);
    } catch (error) {
      this.error.set(apiMessage(error, 'Não foi possível atualizar a história.'));
    }
  }

  protected async copy(value: string): Promise<void> {
    await navigator.clipboard.writeText(value);
    this.notice.set('Copiado.');
  }

  private key(): string {
    return this.access.controls.adminKey.value.trim();
  }

  private async reload(adminKey: string): Promise<void> {
    try {
      const [stories, keychains] = await Promise.all([
        firstValueFrom(this.api.adminStories(adminKey)),
        firstValueFrom(this.api.adminKeychains(adminKey)),
      ]);
      this.stories.set(stories);
      this.keychains.set(keychains);
      this.ready.set(true);
      if (!this.keychainForm.controls.storySlug.value && stories[0]) {
        this.keychainForm.controls.storySlug.setValue(stories[0].slug);
      }
      if (!this.updateForm.controls.slug.value && stories[0]) {
        this.updateForm.controls.slug.setValue(stories[0].slug);
      }
    } catch (error) {
      this.ready.set(false);
      this.error.set(apiMessage(error, 'Chave de administração recusada.'));
    }
  }
}

function reaisToCents(value: string): number | null {
  const normalized = value.trim().replace(/\./g, '').replace(',', '.');
  if (!/^\d+(\.\d{1,2})?$/.test(normalized)) {
    return null;
  }
  return Math.round(Number(normalized) * 100);
}

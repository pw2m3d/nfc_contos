import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  ActivationResponse,
  AuthResponse,
  CreatedKeychain,
  KeychainAdmin,
  KeychainView,
  LibraryItem,
  Order,
  StoryCard,
  TicketResponse,
} from './models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);

  keychain(code: string): Observable<KeychainView> {
    return this.http.get<KeychainView>(`/api/keychains/${encodeURIComponent(code)}`);
  }

  activate(code: string, body: { name: string; email: string; password: string; pin: string }): Observable<ActivationResponse> {
    return this.http.post<ActivationResponse>(`/api/keychains/${encodeURIComponent(code)}/activate`, body);
  }

  login(email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/auth/login', { email, password });
  }

  stories(): Observable<StoryCard[]> {
    return this.http.get<StoryCard[]>('/api/stories');
  }

  library(): Observable<LibraryItem[]> {
    return this.http.get<LibraryItem[]>('/api/me/library');
  }

  ticket(slug: string): Observable<TicketResponse> {
    return this.http.post<TicketResponse>(`/api/stories/${encodeURIComponent(slug)}/ticket`, {});
  }

  createOrder(storySlug: string): Observable<Order> {
    return this.http.post<Order>('/api/orders', { storySlug });
  }

  order(id: string): Observable<Order> {
    return this.http.get<Order>(`/api/orders/${id}`);
  }

  pay(id: string): Observable<Order> {
    return this.http.post<Order>(`/api/orders/${id}/pay`, {});
  }

  adminStories(adminKey: string): Observable<StoryCard[]> {
    return this.http.get<StoryCard[]>('/api/admin/stories', { headers: this.adminHeaders(adminKey) });
  }

  adminKeychains(adminKey: string): Observable<KeychainAdmin[]> {
    return this.http.get<KeychainAdmin[]>('/api/admin/keychains', { headers: this.adminHeaders(adminKey) });
  }

  createKeychain(adminKey: string, storySlug: string): Observable<CreatedKeychain> {
    return this.http.post<CreatedKeychain>('/api/admin/keychains', { storySlug }, { headers: this.adminHeaders(adminKey) });
  }

  createStory(adminKey: string, body: {
    code: string;
    slug: string;
    title: string;
    author: string;
    synopsis: string;
    priceCents: number;
    htmlFile: string;
  }): Observable<StoryCard> {
    return this.http.post<StoryCard>('/api/admin/stories', body, { headers: this.adminHeaders(adminKey) });
  }

  updateStory(adminKey: string, slug: string, body: { priceCents?: number; htmlFile?: string; active?: boolean }): Observable<StoryCard> {
    return this.http.patch<StoryCard>(`/api/admin/stories/${encodeURIComponent(slug)}`, body, { headers: this.adminHeaders(adminKey) });
  }

  private adminHeaders(adminKey: string): HttpHeaders {
    return new HttpHeaders({ 'X-Admin-Key': adminKey });
  }
}

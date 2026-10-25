export interface StoryCard {
  slug: string;
  code: string;
  title: string;
  author: string;
  synopsis: string;
  priceCents: number;
  published: boolean;
  owned: boolean;
}

export interface KeychainView {
  publicCode: string;
  status: 'NEW' | 'ACTIVATED';
  story: StoryCard;
  ticket?: string;
}

export interface ActivationResponse {
  token: string;
  name: string;
  email: string;
  storySlug: string;
  storyTitle: string;
  ticket: string;
}

export interface AuthResponse {
  token: string;
  name: string;
  email: string;
}

export interface LibraryItem {
  slug: string;
  code: string;
  title: string;
  author: string;
  synopsis: string;
  source: 'KEYCHAIN' | 'PURCHASE';
  published: boolean;
}

export interface Order {
  id: string;
  storySlug: string;
  storyTitle: string;
  amountCents: number;
  status: 'PENDING' | 'PAID' | 'CANCELLED';
  createdAt: string;
  paidAt?: string;
}

export interface TicketResponse {
  ticket: string;
}

export interface CreatedKeychain {
  publicCode: string;
  pin: string;
  nfcUrl: string;
  storyTitle: string;
}

export interface KeychainAdmin {
  publicCode: string;
  status: string;
  storyTitle: string;
  storySlug: string;
  ownerEmail?: string;
  createdAt: string;
  activatedAt?: string;
}

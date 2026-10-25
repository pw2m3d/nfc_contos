import { Routes } from '@angular/router';
import { authGuard } from './core/auth.guard';
import { AdminPage } from './pages/admin/admin';
import { CheckoutPage } from './pages/checkout/checkout';
import { HomePage } from './pages/home/home';
import { KeychainPage } from './pages/keychain/keychain';
import { LibraryPage } from './pages/library/library';
import { LoginPage } from './pages/login/login';
import { NotFoundPage } from './pages/not-found/not-found';
import { ReaderPage } from './pages/reader/reader';
import { ShopPage } from './pages/shop/shop';

export const routes: Routes = [
  { path: '', component: HomePage },
  { path: 'entrar', component: LoginPage },
  { path: 'c/:code', component: KeychainPage },
  { path: 'loja', component: ShopPage },
  { path: 'biblioteca', component: LibraryPage, canActivate: [authGuard] },
  { path: 'ler/:slug', component: ReaderPage },
  { path: 'pedido/:id', component: CheckoutPage, canActivate: [authGuard] },
  { path: 'admin', component: AdminPage },
  { path: '**', component: NotFoundPage },
];

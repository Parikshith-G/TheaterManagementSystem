import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';

import { DashboardComponent } from './components/dashboard/dashboard.component';
import { WildCardRedirectsComponent } from './components/wild-card-redirects/wild-card-redirects.component';
import { PlaygroundComponent } from './components/playground/playground.component';

import { PaymentService } from './services/payment/payment.service';
import { PaymentComponent } from './components/payment/payment.component';
import { TheaterDetailComponent } from './components/user/theater-detail/theater-detail.component';
import { TheaterListComponent } from './components/user/theater-list/theater-list.component';

import { authGuard } from './services/guards/auth/auth.guard';
import { adminGuard } from './services/guards/admin/admin.guard';
import { RegisterComponent } from './components/register/register.component';
import { LoggingInComponent } from './components/new/logging-in/logging-in.component';
import { SigningUpComponent } from './components/new/signing-up/signing-up.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [adminGuard],
  },
  {
    path: 'playground',
    component: PlaygroundComponent,
    canActivate: [authGuard],
  },
  {
    path: 'theaters',
    component: TheaterListComponent,
    canActivate: [authGuard],
  },
  {
    path: 'theater-details/:id',
    component: TheaterDetailComponent,
    canActivate: [authGuard],
  },
  {
    path: 'payment',
    component: PaymentComponent,
  },

  { path: '**', component: WildCardRedirectsComponent },
];

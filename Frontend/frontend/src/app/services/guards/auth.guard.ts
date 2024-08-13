import { inject } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivateFn,
  RouterStateSnapshot,
} from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';

export const authGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
) => {
  // if()
  const token = inject(AuthService).getToken();
  if (token == null || token.length == 0) {
    inject(Router).navigateByUrl('/login');
    return false;
  } else {
    return true;
  }
};

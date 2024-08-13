import { inject } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivateFn,
  RouterStateSnapshot,
} from '@angular/router';

import { Router } from '@angular/router';
import { AuthService } from '../../auth/auth.service';

export const authGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
) => {
  const token = inject(AuthService).getToken();

  if (token == null || token.length == 0) {
    inject(Router).navigateByUrl('/login');
    return false;
  } else {
    return true;
  }
};

import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../../auth/auth.service';

export const adminGuard: CanActivateFn = (route, state) => {
  const token = inject(AuthService).getToken();
  const role = inject(AuthService).getUserRole();
  if (
    token == null ||
    token.length == 0 ||
    (role !== 'ADMIN' && role !== 'GOD')
  ) {
    inject(Router).navigateByUrl('/login');
    return false;
  } else {
    return true;
  }
};

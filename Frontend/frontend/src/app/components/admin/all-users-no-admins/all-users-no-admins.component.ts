import { Component, inject, OnInit } from '@angular/core';
import { UserService } from '../../../services/users/user.service';
import { User } from '../../../models/user';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth/auth.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-all-users-no-admins',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './all-users-no-admins.component.html',
  styleUrl: './all-users-no-admins.component.css',
})
export class AllUsersNoAdminsComponent implements OnInit {
  role: string = '';

  users: User[] = [];
  constructor(private userService: UserService, private toastr: ToastrService) {
    this.role = inject(AuthService).getUserRole();
  }
  ngOnInit(): void {
    this.loadUsers();
  }
  loadUsers() {
    this.userService.getAllUsersExceptAdmins().subscribe({
      next: (val) => (this.users = val),
      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
  }

  deleteUser(id: number) {
    if (this.role === 'ADMIN') {
      this.userService.overrideDeleteAdmin(id).subscribe({
        next: (val) => {
          if (val) {
            this.loadUsers();
            this.toastr.success('User deleted successfully', 'Deleted');
          } else {
            this.toastr.error('Please try again', 'Something happened');
          }
        },
        error: (err) => {
          this.toastr.error(err.error.message, 'Error');
        },
      });
    } else if (this.role === 'GOD') {
      this.userService.overrideDeleteGod(id).subscribe({
        next: (val) => {
          if (val) {
            this.loadUsers();
            this.toastr.success('User deleted successfully', 'Deleted');
          } else {
            this.toastr.error('Please try again', 'Something happened');
          }
        },
        error: (err) => {
          this.toastr.error(err.error.message, 'Error');
        },
      });
    }
  }
  promoteUser(id: number) {
    this.userService.updateUserToAdmin(id).subscribe({
      next: (val) => {
        if (val) {
          this.toastr.success('User promoted to admin', 'Success');
          this.loadUsers();
        } else {
          this.toastr.error('Please try again', 'Something happened');
        }
      },
      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
  }
}

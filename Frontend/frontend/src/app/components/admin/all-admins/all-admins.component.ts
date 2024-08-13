import { Component, OnInit } from '@angular/core';
import { User } from '../../../models/user';
import { UserService } from '../../../services/users/user.service';
import { CommonModule } from '@angular/common';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-all-admins',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './all-admins.component.html',
  styleUrl: './all-admins.component.css',
})
export class AllAdminsComponent implements OnInit {
  users: User[] = [];
  constructor(
    private userService: UserService,
    private toastr: ToastrService
  ) {}
  ngOnInit(): void {
    this.loadUsers();
  }
  loadUsers() {
    this.userService.getAllAdmins().subscribe({
      next: (val) => (this.users = val),
      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
  }

  deleteUser(id: number) {
    this.userService.overrideDeleteAdmin(id).subscribe({
      next: (val) => {
        if (val) {
          this.loadUsers();
          this.toastr.success('User deleted successfully', 'Deleted');
        } else {
          this.toastr.error('Something went wrong', 'error');
        }
      },
      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
  }

  demoteUser(id: number) {
    this.userService.updateAdminToUser(id).subscribe({
      next: (val) => {
        if (val) {
          this.loadUsers();
          this.toastr.success('Admin demoted to user', 'Success');
        } else {
          this.toastr.error('Something went wrong', 'Error');
        }
      },
      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
  }
}

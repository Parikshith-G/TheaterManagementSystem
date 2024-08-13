export class UserRegistration {
  username: string;
  emailId: string;
  password: string;
  phone: string;
  constructor(
    username: string,
    emailId: string,
    password: string,
    phone: string
  ) {
    this.emailId = emailId;
    this.password = password;
    this.username = username;
    this.phone = phone;
  }
}

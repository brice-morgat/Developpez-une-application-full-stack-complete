import { Component } from '@angular/core';

@Component({
  selector: 'app-profile',
  standalone: true,
  template: `
    <section class="page">
      <h1>Profile</h1>
      <p>Username: dev_user</p>
      <p>Email: dev&#64;mdd.dev</p>
      <button type="button">Edit profile</button>
    </section>
  `,
})
export class ProfileComponent {}


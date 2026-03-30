import { Component } from '@angular/core';

@Component({
  selector: 'app-register',
  standalone: true,
  template: `
    <section class="page">
      <h1>Register</h1>
      <form class="stack">
        <label>Username</label>
        <input type="text" placeholder="dev_user" />
        <label>Email</label>
        <input type="email" placeholder="you@mdd.dev" />
        <label>Password</label>
        <input type="password" placeholder="********" />
        <button type="button">Create account</button>
      </form>
    </section>
  `,
})
export class RegisterComponent {}


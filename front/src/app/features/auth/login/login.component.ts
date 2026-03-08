import { Component } from '@angular/core';

@Component({
  selector: 'app-login',
  standalone: true,
  template: `
    <section class="page">
      <h1>Login</h1>
      <form class="stack">
        <label>Email</label>
        <input type="email" placeholder="you@mdd.dev" />
        <label>Password</label>
        <input type="password" placeholder="********" />
        <button type="button">Sign in</button>
      </form>
    </section>
  `,
})
export class LoginComponent {}


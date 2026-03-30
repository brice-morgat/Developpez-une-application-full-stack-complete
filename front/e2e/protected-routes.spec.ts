import { expect, test } from '@playwright/test';

const protectedRoutes = ['/feed', '/topics', '/create-post', '/profile'];

test.describe('Protected routes', () => {
  for (const route of protectedRoutes) {
    test(`should redirect unauthenticated user from ${route}`, async ({ page }) => {
      await page.goto(route);
      await expect(page).toHaveURL(/\/(login)?$/);
    });
  }
});

import { expect, test } from '@playwright/test';

test.describe('Public pages', () => {
  test('should open welcome page and navigate to login and register', async ({ page }) => {
    await page.goto('/');

    await expect(page).toHaveURL(/\/$/);

    await page.getByRole('button', { name: /se connecter|connexion|login/i }).first().click();
    await expect(page).toHaveURL(/\/login$/);

    await page.goto('/');
    await page.getByRole('button', { name: /s'inscrire|inscription|register/i }).first().click();
    await expect(page).toHaveURL(/\/register$/);
  });
});

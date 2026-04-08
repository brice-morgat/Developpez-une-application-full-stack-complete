# Rapport de couverture et de tests

Date: 2026-04-09

## Perimetre
- Back-end: Spring Boot 3.2, JUnit 5, Mockito, tests d'integration Spring Boot, JaCoCo
- Front-end: Angular 19, Karma/Jasmine, Cypress, NYC/Istanbul
- Environnement local de verification: poste de developpement Windows avec MySQL de test actif pour les tests d'integration back

## Synthese
- Front unitaires: OK, `74/74`
- Front build production: OK
- Back tests: OK, `66/66`
- Front E2E Cypress: dernier artefact de couverture disponible dans le repo
- Pour un MVP, la couverture actuelle permet de securiser les parcours coeur de produit, avec quelques zones a renforcer avant une phase de scale ou d'industrialisation plus forte.

## Executions verifiees le 2026-04-09

### Front-end
Commandes executees:
```bash
cd front
npm run unit
npm run build
```

Resultats:
- `npm run unit`: succes, `74/74` tests passes
- `npm run build`: succes
- Avertissement de build conserve: bundle initial `529.48 kB`, au-dessus du budget Angular de `500 kB`

Couverture unitaire front:
- Statements: `92.97%` (`291/313`)
- Branches: `73.33%` (`44/60`)
- Functions: `95.41%` (`104/109`)
- Lines: `92.76%` (`282/304`)

Rapport HTML:
- `front/coverage/front/index.html`

### Back-end
Commande executee:
```bash
cd back
./mvnw.cmd test
```

Resultats:
- succes
- `66/66` tests passes
- les tests d'integration Spring Boot ont pu etre executes sur la base MySQL de test locale

Couverture JaCoCo backend:
- Instructions: `84.06%` (`1840/2189`)
- Lignes: `79.83%` (`459/575`)
- Branches: `56.03%` (`65/116`)
- Methodes: `87.41%` (`125/143`)

Rapport HTML:
- `back/target/site/jacoco/index.html`

## Couverture E2E Cypress

Source:
- dernier artefact disponible dans le repo: `front/coverage/cypress/coverage-summary.json`
- date du rapport disponible: `2026-04-01 23:44:56`

Perimetre de la suite E2E:
- `5` fichiers de spec
- `15` scenarios metier couverts au total

Couverture applicative executee via Cypress:
- Statements: `84.19%` (`719/854`)
- Branches: `76.50%` (`140/183`)
- Functions: `84.04%` (`137/163`)
- Lines: `87.50%` (`567/648`)

Rapport HTML:
- `front/coverage/cypress/index.html`

Note de lecture:
- ce rapport mesure le code de l'application execute pendant les parcours Cypress
- il ne mesure pas la couverture des fichiers de tests Cypress eux-memes

## Scenarios couverts

### Front unitaire
- etat d'authentification
- services metier front
- formulaires et validations co-localisees aux composants quand elles ne sont utilisees qu'a un seul endroit
- composants de pages et composants UI critiques

### Front E2E Cypress
- navigation publique et redirections
- authentification login / register
- verification du header JWT Bearer cote front
- chargement du feed et tri
- consultation detail article
- creation d'article
- ajout de commentaire
- mise a jour du profil
- abonnement / desabonnement a un theme
- deconnexion

### Back-end
- services metier
- securite JWT
- controllers
- gestion globale des erreurs
- tests d'integration API sur base MySQL de test

## Conclusion
- Le niveau de couverture est adapte a un MVP d'entreprise: les parcours les plus critiques sont testes et les regressions majeures ont peu de chances de passer inaperçues.
- La couverture E2E Cypress couvre bien les flux coeur de produit, ce qui est pertinent pour une phase de validation metier et de stabilisation.
- Les deux points a surveiller avant un passage a plus forte volumetrie ou a une CI plus exigeante restent la couverture de branches backend et le fait que le bundle front reste legerement au-dessus du budget en production.

# Rapport de couverture et de tests

Date: 2026-04-10

## Périmètre
- Back-end: Spring Boot 3.2, JUnit 5, Mockito, tests d'intégration Spring Boot, JaCoCo
- Front-end: Angular 19, Karma/Jasmine, Cypress, NYC/Istanbul
- Environnement local de vérification: poste de développement Windows avec MySQL de test actif pour les tests d'intégration back

## Synthèse
- Front unitaires: OK, `77/77`
- Front build production: OK
- Back tests: OK, `67` tests
- Front E2E Cypress: dernier artefact de couverture disponible dans le repo
- Pour un MVP, la couverture actuelle permet de sécuriser les parcours cœur de produit, avec quelques zones à renforcer avant une phase de scale ou d'industrialisation plus forte.

## Exécutions vérifiées le 2026-04-10

### Front-end
Commandes exécutées:
```bash
cd front
npm run unit
npm run build
```

Résultats:
- `npm run unit`: succès, `77/77` tests passés
- `npm run build`: succès
- Avertissement de build conservé: bundle initial `523.88 kB`, au-dessus du budget Angular de `500 kB`

Couverture unitaire front:
- Statements: `95.62%` (`306/320`)
- Branches: `76.11%` (`51/67`)
- Functions: `95.57%` (`108/113`)
- Lines: `95.51%` (`298/312`)

Rapport HTML:
- `front/coverage/front/index.html`

### Back-end
Commande exécutée:
```bash
cd back
mvn test
```

Résultats:
- succès
- `67` tests passés
- les tests d'intégration Spring Boot ont pu être exécutés sur la base MySQL de test locale

Couverture JaCoCo backend:
- Instructions: `84.08%` (`1832/2179`)
- Lignes: `79.86%` (`456/571`)
- Branches: `56.03%` (`65/116`)
- Méthodes: `87.94%` (`124/141`)

Rapport HTML:
- `back/target/site/jacoco/index.html`

## Couverture E2E Cypress

Source:
- dernier artefact disponible dans le repo: `front/coverage/cypress/coverage-summary.json`
- date du rapport disponible: `2026-04-01 23:44:56`

Périmètre de la suite E2E:
- `5` fichiers de spec
- `15` scénarios métier couverts au total

Couverture applicative exécutée via Cypress:
- Statements: `84.19%` (`719/854`)
- Branches: `76.50%` (`140/183`)
- Functions: `84.04%` (`137/163`)
- Lines: `87.50%` (`567/648`)

Rapport HTML:
- `front/coverage/cypress/index.html`

Note de lecture:
- ce rapport mesure le code de l'application exécuté pendant les parcours Cypress
- il ne mesure pas la couverture des fichiers de tests Cypress eux-mêmes

## Scénarios couverts

### Front unitaire
- état d'authentification
- services métier front
- formulaires et validations co-localisées aux composants quand elles ne sont utilisées qu'à un seul endroit
- composants de pages et composants UI critiques

### Front E2E Cypress
- navigation publique et redirections
- authentification login / register
- vérification du header JWT Bearer côté front
- chargement du feed et tri
- consultation détail article
- création d'article
- ajout de commentaire
- mise à jour du profil
- abonnement / désabonnement à un thème
- déconnexion

### Back-end
- services métier
- sécurité JWT
- controllers
- gestion globale des erreurs
- tests d'intégration API sur base MySQL de test

## Conclusion
- Le niveau de couverture est adapté à un MVP d'entreprise: les parcours les plus critiques sont testés et les régressions majeures ont peu de chances de passer inaperçues.
- La couverture E2E Cypress couvre bien les flux cœur de produit, ce qui est pertinent pour une phase de validation métier et de stabilisation.
- Les deux points à surveiller avant un passage à plus forte volumétrie ou à une CI plus exigeante restent la couverture de branches backend et le fait que le bundle front reste légèrement au-dessus du budget en production.

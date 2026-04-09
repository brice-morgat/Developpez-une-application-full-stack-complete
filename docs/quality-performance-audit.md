# Audit qualité / performance

Date: 2026-04-09

## Sources utilisées
- vérification locale du 2026-04-09:
  - `front`: `npm run unit`, `npm run build`
  - `back`: `./mvnw.cmd test`
- artefacts qualimétrie existants du 2026-04-03:
  - `docs/qualimetrie/lighthouse-summary.json`
  - `docs/qualimetrie/npm-audit.json`
  - `docs/qualimetrie/rapport-audit-local-2026-04-03.md`

## Synthèse exécutive
- Qualité de code globale: bonne base de projet, architecture lisible, tests présents des deux côtés
- Performance front: acceptable pour un MVP, mais perfectible, principalement à cause du bundle initial
- Risque sécurité principal: backlog de dépendances front vulnérables et stockage du JWT dans le navigateur
- Risque scalabilité principal: absence de pagination sur le feed API et chargement intégral de certaines listes

## État qualité

### Points positifs
- architecture front/back claire et séparée
- validations front de l'inscription co-localisées avec le formulaire Angular qui les consomme, ce qui évite un utilitaire global à usage unique
- couche service backend cohérente, avec transactions en lecture seule par défaut sur les services métier
- `spring.jpa.open-in-view: false`, ce qui limite les accès JPA tardifs non maîtrisés
- repositories backend optimisés sur les lectures critiques avec `@EntityGraph` pour limiter les N+1 sur articles et commentaires
- DTO d'erreur structuré (`ApiErrorDto`) pour une remontée d'erreurs exploitable côté front
- outillage qualité déjà en place:
  - JaCoCo
  - Sonar Maven plugin
  - OWASP dependency-check
  - Lighthouse
  - Cypress avec couverture applicative

### Points de vigilance
- incohérence de politique mot de passe entre inscription et mise à jour du profil:
  - inscription: politique forte validée côté back et directement dans le formulaire Angular d'inscription
  - profil: minimum `6` caractères uniquement
- les rapports qualité ne sont pas encore consolidés automatiquement en CI avec seuils bloquants

## Audit performance

### Front-end
Build production observé:
- bundle initial: `529.48 kB`
- budget Angular: `500.00 kB`
- dépassement: `29.48 kB`

Lighthouse local disponible:
- Performance: `76`
- Accessibility: `100`
- Best Practices: `100`
- SEO: `91`
- FCP: `3.7 s`
- LCP: `4.3 s`
- TBT: `70 ms`
- CLS: `0.019`
- Speed Index: `3.7 s`

Historique avant / après optimisation du 2026-04-09:
- bundle initial: `669.00 kB` -> `529.48 kB` (`-139.52 kB`)
- `main.js`: `539.13 kB` -> `471.20 kB` (`-67.93 kB`)
- `styles.css`: `92.14 kB` -> `20.35 kB` (`-71.79 kB`)
- Performance: `68` -> `76`
- Best Practices: `100` -> `100`
- FCP: `4.7 s` -> `3.7 s`
- LCP: `5.5 s` -> `4.3 s`
- TBT: `90 ms` -> `70 ms`
- CLS: `0.026` -> `0.019`
- Speed Index: `4.7 s` -> `3.7 s`

Lecture:
- le projet reste stable en rendu et sain sur l'accessibilité
- les optimisations appliquées ont apporté un gain visible sur le chargement initial et sur la métrique LCP
- le bundle initial reste légèrement au-dessus du budget, donc la marge d'évolution reste à surveiller

### Back-end
Observations de conception:
- les lectures feed/detail utilisent `@EntityGraph`, ce qui est positif
- le feed récupère l'ensemble des posts des thèmes suivis sans pagination
- le catalogue des thèmes est chargé en entier via `findAll()`

Impact attendu:
- comportement acceptable sur petit volume
- dégradation probable du temps de réponse et du volume transféré quand le nombre de posts ou de thèmes augmente

## Audit sécurité / dépendances

### Front-end
`npm audit` disponible dans `docs/qualimetrie/npm-audit.json`:
- total: `30`
- high: `23`
- moderate: `7`
- critical: `0`

Constat principal:
- la majorité des alertes remontent sur la chaîne Angular CLI / build et dépendances transitives associées
- plusieurs corrections sont disponibles, parfois via une mise à niveau majeure de la CLI

### Back-end
Dernier audit local disponible dans `docs/qualimetrie/rapport-audit-local-2026-04-03.md`:
- `6` vulnérabilités `medium`
- principale exposition citée: pile Swagger UI / DOMPurify

### Stockage du token
- l'état `auth` est persisté via NGXS storage plugin
- cela améliore l'expérience utilisateur
- cela expose davantage le JWT aux conséquences d'une faille XSS qu'une stratégie par cookie `HttpOnly`

## Recommandations priorisées

### P0
- Résorber les dépendances front vulnérables, en commençant par les packages Angular et l'outillage build.
- Revenir sous le budget front de `500 kB`, au minimum sur le bundle initial.
- Aligner la politique mot de passe entre inscription et profil.

### P1
- Ajouter une pagination backend sur le feed et l'exposer côté front.
- Introduire une pagination ou un filtrage sur la liste des thèmes si le volume fonctionnel augmente.
- Arbitrer explicitement la stratégie de stockage du JWT en fonction du niveau de risque XSS accepté.

### P2
- Industrialiser les rapports qualité en CI:
  - tests
  - couvertures
  - `npm audit`
  - OWASP dependency-check
  - Lighthouse

## Conclusion
- La base qualité est bonne et déjà bien outillée.
- Les gains les plus rentables à court terme, dans une logique MVP d'entreprise, sont sur les dépendances front, le poids du bundle initial et la scalabilité du feed.
- Le projet est techniquement sain pour un lancement encadré ou un premier lot utilisateur, mais quelques décisions devront être revisitées avant une montée en charge plus sérieuse.

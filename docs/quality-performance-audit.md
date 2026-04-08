# Audit qualite / performance

Date: 2026-04-09

## Sources utilisees
- verification locale du 2026-04-09:
  - `front`: `npm run unit`, `npm run build`
  - `back`: `./mvnw.cmd test`
- artefacts qualimetrie existants du 2026-04-03:
  - `docs/qualimetrie/lighthouse-summary.json`
  - `docs/qualimetrie/npm-audit.json`
  - `docs/qualimetrie/rapport-audit-local-2026-04-03.md`

## Synthese executive
- Qualite de code globale: bonne base de projet, architecture lisible, tests presents des deux cotes
- Performance front: acceptable pour un MVP, mais perfectible, principalement a cause du bundle initial
- Risque securite principal: backlog de dependances front vulnerables et stockage du JWT dans le navigateur
- Risque scalabilite principal: absence de pagination sur le feed API et chargement integral de certaines listes

## Etat qualite

### Points positifs
- architecture front/back claire et separee
- validations front de l'inscription co-localisees avec le formulaire Angular qui les consomme, ce qui evite un utilitaire global a usage unique
- couche service backend coherente, avec transactions en lecture seule par defaut sur les services metier
- `spring.jpa.open-in-view: false`, ce qui limite les acces JPA tardifs non maitrises
- repositories backend optimises sur les lectures critiques avec `@EntityGraph` pour limiter les N+1 sur articles et commentaires
- DTO d'erreur structure (`ApiErrorDto`) pour une remontee d'erreurs exploitable cote front
- outillage qualite deja en place:
  - JaCoCo
  - Sonar Maven plugin
  - OWASP dependency-check
  - Lighthouse
  - Cypress avec couverture applicative

### Points de vigilance
- incoherence de politique mot de passe entre inscription et mise a jour du profil:
  - inscription: politique forte validee cote back et directement dans le formulaire Angular d'inscription
  - profil: minimum `6` caracteres uniquement
- les rapports qualite ne sont pas encore consolides automatiquement en CI avec seuils bloquants

## Audit performance

### Front-end
Build production observe:
- bundle initial: `529.48 kB`
- budget Angular: `500.00 kB`
- depassement: `29.48 kB`

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

Historique avant / apres optimisation du 2026-04-09:
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
- le projet reste stable en rendu et sain sur l'accessibilite
- les optimisations appliquees ont apporte un gain visible sur le chargement initial et sur la metrique LCP
- le bundle initial reste legerement au-dessus du budget, donc la marge d'evolution reste a surveiller

### Back-end
Observations de conception:
- les lectures feed/detail utilisent `@EntityGraph`, ce qui est positif
- le feed recupere l'ensemble des posts des themes suivis sans pagination
- le catalogue des themes est charge en entier via `findAll()`

Impact attendu:
- comportement acceptable sur petit volume
- degradation probable du temps de reponse et du volume transfere quand le nombre de posts ou de themes augmente

## Audit securite / dependances

### Front-end
`npm audit` disponible dans `docs/qualimetrie/npm-audit.json`:
- total: `30`
- high: `23`
- moderate: `7`
- critical: `0`

Constat principal:
- la majorite des alertes remontent sur la chaine Angular CLI / build et dependances transitives associees
- plusieurs corrections sont disponibles, parfois via une mise a niveau majeure de la CLI

### Back-end
Dernier audit local disponible dans `docs/qualimetrie/rapport-audit-local-2026-04-03.md`:
- `6` vulnerabilites `medium`
- principale exposition citee: pile Swagger UI / DOMPurify

### Stockage du token
- l'etat `auth` est persiste via NGXS storage plugin
- cela ameliore l'experience utilisateur
- cela expose davantage le JWT aux consequences d'une faille XSS qu'une strategie par cookie `HttpOnly`

## Recommandations priorisees

### P0
- Resorber les dependances front vulnerables, en commencant par les packages Angular et outillage build.
- Revenir sous le budget front de `500 kB`, au minimum sur le bundle initial.
- Aligner la politique mot de passe entre inscription et profil.

### P1
- Ajouter une pagination backend sur le feed et l'exposer cote front.
- Introduire une pagination ou un filtrage sur la liste des themes si le volume fonctionnel augmente.
- Arbitrer explicitement la strategie de stockage du JWT en fonction du niveau de risque XSS accepte.

### P2
- Industrialiser les rapports qualite en CI:
  - tests
  - couvertures
  - `npm audit`
  - OWASP dependency-check
  - Lighthouse

## Conclusion
- La base qualite est bonne et deja bien outillee.
- Les gains les plus rentables a court terme, dans une logique MVP d'entreprise, sont sur les dependances front, le poids du bundle initial et la scalabilite du feed.
- Le projet est techniquement sain pour un lancement encadre ou un premier lot utilisateur, mais quelques decisions devront etre revisitees avant une montee en charge plus serieuse.

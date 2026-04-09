# MDD - Application full stack

## Vue d'ensemble
Ce dépôt contient une application full stack composée de :

- un front Angular 19 dans `front/`
- une API Spring Boot 3.2 dans `back/`
- une base de documentation projet dans `docs/`

L'application couvre un socle MVP autour de l'authentification, du feed, des thèmes, des posts, des commentaires et du profil utilisateur.

## Architecture

### Front-end
- `front/src/app/core` : auth, API clients, HTTP interceptors, layout, état global
- `front/src/app/features` : modules fonctionnels (`auth`, `feed`, `posts`, `profile`, `topics`)
- `front/src/app/pages` : pages transverses
- routage lazy-loaded dans `front/src/app/app.routes.ts`
- protection des routes privées par `authGuard`
- injection automatique du JWT par `authInterceptor`

### Back-end
- `back/src/main/java/com/openclassrooms/mddapi/controller` : endpoints REST
- `back/src/main/java/com/openclassrooms/mddapi/service` : logique métier
- `back/src/main/java/com/openclassrooms/mddapi/repository` : accès JPA
- `back/src/main/java/com/openclassrooms/mddapi/dto` : contrats d'entrée / sortie
- `back/src/main/java/com/openclassrooms/mddapi/config` : sécurité JWT, CORS, OpenAPI, bootstrap
- migrations Flyway dans `back/src/main/resources/db/migration`

## Sécurité et données
- authentification JWT côté API
- mots de passe hashés avec `BCryptPasswordEncoder`
- API stateless avec Spring Security
- endpoints `/api/auth/**` publics, le reste protégé
- validation des DTO côté back via Bean Validation
- normalisation des identifiants utilisateur avant persistance
- `open-in-view: false` pour mieux maîtriser la couche persistence

## Prérequis
- Node.js 20+
- npm 10+
- Java 21
- MySQL 8+
- Maven

## Configuration locale

### Variables d'environnement backend
Le backend lit les variables suivantes :

- `MYSQL_NAME` : utilisateur MySQL
- `MYSQL_PASSWORD` : mot de passe MySQL
- `JWT_SECRET` : secret JWT
- `JWT_EXPIRATION_MS` : durée de vie du token en millisecondes, optionnel
- `APP_CORS_ALLOWED_ORIGIN_PATTERNS` : obligatoire en production, optionnel en local

Valeurs par défaut utiles :
- base de développement : `db_mdd`
- base de test : `db_mdd_test`
- URL API front locale : `http://localhost:8080`

### Profils Spring
- `application.yml` : configuration commune
- `application-dev.yml` : SQL lisible + Swagger activé
- `application-prod.yml` : Swagger désactivé + CORS externalisé
- `application-test.yml` : base MySQL de test locale

### Environnements front
- `front/src/environments/environment.ts` : environnement local
- `front/src/environments/environment.prod.ts` : build production

Les deux pointent actuellement vers `http://localhost:8080`.

## Lancer le projet

### 1. Backend
Définir les variables d'environnement requises :
- `MYSQL_NAME`
- `MYSQL_PASSWORD`
- `JWT_SECRET`
- `SPRING_PROFILES_ACTIVE=dev`

Puis lancer l'API depuis le dossier `back/` avec Maven :

```bash
mvn spring-boot:run
```

Swagger est disponible en profil `dev` :
- `http://localhost:8080/swagger-ui/index.html`

### 2. Frontend
```bash
cd front
npm install
npm run start
```

Application disponible sur :
- `http://localhost:4200`

## Données de démonstration

Le dépôt contient un script SQL de seed par défaut :
- `scripts/demo-data.sql`

Ce script insère :
- des thèmes métier autour du développement et de l'infrastructure
- des comptes utilisateurs prêts à l'emploi
- des abonnements à des thèmes
- des articles
- des commentaires

Le script vide d'abord les tables métier avant de réinsérer les données. Il est prévu pour une base locale de démonstration.

### Import du SQL
Après création du schéma par Flyway, importer le script dans la base cible :

```bash
mysql -u <utilisateur> -p <nom_base> < scripts/demo-data.sql
```

Exemple avec la base locale par défaut :

```bash
mysql -u root -p db_mdd < scripts/demo-data.sql
```

### Comptes de test
Les comptes suivants sont fournis dans le script :

- `alice@mdd.dev` / `Password1!`
- `bruno@mdd.dev` / `Password1!`
- `claire@mdd.dev` / `Password1!`
- `diego@mdd.dev` / `Password1!`

Les identifiants utilisateurs correspondants sont :

- `alice`
- `bruno`
- `claire`
- `diego`

## Exécution des tests

### Front unitaires
```bash
cd front
npm run unit
```

Rapport de couverture :
- `front/coverage/front/index.html`

### Front build de production
```bash
cd front
npm run build
```

### Front E2E Cypress
```bash
cd front
npm run e2e
```

Pour générer la couverture applicative via Cypress :

```bash
cd front
npm run coverage:e2e
```

Rapport de couverture E2E :
- `front/coverage/cypress/index.html`

### Back-end
Définir les variables d'environnement backend requises, puis exécuter les tests depuis `back/` :

```bash
mvn test
```

Rapport JaCoCo :
- `back/target/site/jacoco/index.html`

## Qualité et conventions
- architecture front organisée par responsabilités et par fonctionnalités
- architecture back découpée en couches `controller` / `service` / `repository` / `dto`
- tests unitaires et d'intégration présents des deux côtés
- migrations versionnées avec Flyway
- audit et rapports de qualité disponibles dans `docs/` et `docs/qualimetrie/`

## Documentation projet
- rapport de tests : [docs/testing-report.md](docs/testing-report.md)
- revue technique : [docs/technical-review.md](docs/technical-review.md)
- audit qualité / performance : [docs/quality-performance-audit.md](docs/quality-performance-audit.md)
- FAQ : [docs/faq.md](docs/faq.md)

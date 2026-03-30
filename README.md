# MDD - Application full stack

## Prérequis
- Node.js 20+
- Java 21 (obligatoire pour le backend)
- Maven Wrapper (`./mvnw` inclus)
- MySQL local (pour le profil `dev`)

## Lancer le projet

### Backend
```bash
cd back
./mvnw spring-boot:run
```

### Frontend
```bash
cd front
npm install
npm run start
```

## Endpoints API
Tous les endpoints (sauf login/register) nécessitent un token `Bearer`.

### Authentification
- `POST /api/auth/register`
  - body: `{ "email": "...", "username": "...", "password": "..." }`
- `POST /api/auth/login`
  - body: `{ "identifier": "email_ou_username", "password": "..." }`

### Feed & posts
- `GET /api/feed?sort=desc|asc`
- `POST /api/posts`
  - body: `{ "topicId": 1, "title": "...", "content": "..." }`
- `GET /api/posts/{postId}`
- `POST /api/posts/{postId}/comments`
  - body: `{ "content": "..." }`

### Topics
- `GET /api/topics`
- `POST /api/topics/{topicId}/subscribe`
- `DELETE /api/topics/{topicId}/subscribe`

### Utilisateur
- `GET /api/users/me`
- `PUT /api/users/me`
  - body: `{ "email": "...", "username": "...", "password": "..." }`

## Tests

### Backend
```bash
cd back
./mvnw clean test
```
JaCoCo est configuré dans `back/pom.xml`.
Rapport généré: `back/target/site/jacoco/index.html`.
Les tests d'integration utilisent le profil `test` connecté à `db_mdd_test`.

### Frontend
```bash
cd front
npm run test
```

## Documentation projet
- Rapport de tests: [docs/testing-report.md](docs/testing-report.md)
- Revue technique: [docs/technical-review.md](docs/technical-review.md)
- FAQ utilisateur: [docs/faq.md](docs/faq.md)

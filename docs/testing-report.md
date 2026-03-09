# Rapport de tests

Date: 2026-03-09

## Outils
- Back-end: JUnit 5 + Mockito + JaCoCo (Maven)
- Front-end: Angular/Karma (existant)
- E2E: Cypress (a implementer sur branche front tests)

## Strategie appliquee (back)
- Tests unitaires des services metier principaux:
  - `AuthService`
  - `TopicService`
  - `PostService`
  - `UserService`
  - `CurrentUserService`
- Tests unitaires de la securite JWT:
  - `JwtService`
- Tests des controllers (delegation + codes de retour):
  - `AuthController`
  - `PostController`
  - `TopicController`
  - `UserController`
- Tests d'integration controllers (Given/When/Then) sans mock de service:
  - `AuthControllerIT`, `TopicControllerIT`, `PostControllerIT`, `UserControllerIT`
  - execution avec base MySQL de test `db_mdd_test`
  - parcours verifies: register/login, subscribe/unsubscribe, feed, detail+comment, update profile
- Tests mappers et gestion globale des erreurs:
  - `UserMapper`, `TopicMapper`, `PostMapper`
  - `GlobalExceptionHandler`

## Execution
Commande executee (JDK IntelliJ 21):
```bash
cd back
$env:JAVA_HOME='C:\Users\brice.morgat\.jdks\temurin-21.0.9'
$env:Path="$env:JAVA_HOME\\bin;$env:Path"
./mvnw clean test
```

Resultat:
- SUCCESS
- 52 tests executes
- 0 echec
- 0 erreur

## Couverture JaCoCo
Rapport genere:
- `back/target/site/jacoco/index.html`

Mesures globales:
- LINE: 80.19% (340/424)
- INSTRUCTION: 85.05% (1496/1759)

Objectif projet:
- couverture >= 70%: atteint.

## Suite
- Maintenir ce seuil en CI sur les prochaines PR.
- Completer la branche front tests (unit + e2e) pour couvrir les parcours critiques UI.

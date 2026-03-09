# Rapport de tests

Date: 2026-03-09

## Outils
- Back-end: JUnit 5 + Mockito + JaCoCo (Maven)
- Front-end: Angular/Karma (existant)
- E2E: Cypress (à implémenter sur branche front tests)

## Stratégie appliquée (back)
- Tests unitaires des services métier principaux:
  - `AuthService`
  - `TopicService`
  - `PostService`
  - `UserService`
  - `CurrentUserService`
- Tests unitaires de la sécurité JWT:
  - `JwtService`

## Exécution
Commande cible:
```bash
cd back
./mvnw clean test
```

Résultat dans cet environnement local:
- échec d'exécution avant lancement des tests applicatifs, car JDK 21 requis par le projet et machine locale en JDK 17 (`release version 21 not supported`).

## Couverture
JaCoCo est configuré dans `back/pom.xml`.
Rapport attendu après exécution en JDK 21:
- `back/target/site/jacoco/index.html`

Objectif projet:
- couverture >= 70 % (à valider sur CI ou machine locale JDK 21).

## Suite
- Exécuter la commande ci-dessus sur JDK 21.
- Capturer le pourcentage global lignes/instructions et l'ajouter à ce rapport.
- Compléter avec les tests front (Jest/Karma/Cypress) sur la branche dédiée.

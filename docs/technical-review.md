# Revue technique

Date: 2026-03-09

## Forces
- Architecture front/back claire et découplée.
- Authentification JWT centralisée côté API.
- Intégration front des flux métier principaux:
  - feed
  - détail post + commentaires
  - création de post
  - thèmes + abonnement/désabonnement
  - profil + mise à jour + désabonnement
- Gestion d'état simple et lisible côté front (NGXS + services API).

## Axes d'amélioration
- Couverture de tests encore insuffisamment mesurée (blocage environnement JDK local).
- Budget bundle front dépassé (warning Angular build).
- `TopicResponseDto` ne renvoie pas de description, ce qui force un texte générique côté front.
- Mélange ponctuel de responsabilités UI/données dans certains composants front (logique HTTP + rendu dans le même composant).

## Recommandations
- Exécuter et imposer JaCoCo avec seuil minimal (70 %) en CI.
- Ajouter des tests d'intégration API (MockMvc/Testcontainers) sur endpoints critiques.
- Ajouter tests front unitaires + E2E Cypress sur parcours clés.
- Introduire DTO `TopicResponseDto` enrichi (`description`) pour alignement fonctionnel.
- Traiter le budget bundle front:
  - lazy loading des pages
  - revue imports Angular Material
  - optimisation build

## Risques résiduels
- Régressions silencieuses possibles tant que les pipelines de tests ne sont pas obligatoires sur PR.
- Différences de comportement entre environnements si version Java non homogène (17 vs 21).

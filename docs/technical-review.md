# Revue technique

Date: 2026-03-09

## Forces
- Architecture front/back claire et decouplee.
- Authentification JWT centralisee cote API.
- Integration front des flux metier principaux:
  - feed
  - detail post + commentaires
  - creation de post
  - themes + abonnement/desabonnement
  - profil + mise a jour + desabonnement
- Couverture de tests backend portee au-dessus du seuil cible.

## Axes d'amelioration
- Budget bundle front depasse (warning Angular build).
- `TopicResponseDto` ne renvoie pas de description, ce qui force un texte generique cote front.
- Melange ponctuel de responsabilites UI/donnees dans certains composants front (logique HTTP + rendu dans le meme composant).

## Recommandations
- Imposer le seuil de couverture (>= 70%) en CI.
- Ajouter des tests d'integration API (MockMvc/Testcontainers) sur endpoints critiques.
- Ajouter tests front unitaires + E2E Cypress sur parcours cles.
- Introduire DTO `TopicResponseDto` enrichi (`description`) pour alignement fonctionnel.
- Traiter le budget bundle front:
  - lazy loading des pages
  - revue imports Angular Material
  - optimisation build

## Risques residuels
- Regessions silencieuses possibles tant que les pipelines de tests ne sont pas obligatoires sur PR.
- Dependance a un environnement local MySQL pour certains tests de contexte (`@SpringBootTest`).

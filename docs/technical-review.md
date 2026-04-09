# Rapport de revue technique

Date: 2026-04-09

## Résumé
- Le projet présente une architecture full-stack lisible, avec une séparation claire entre UI Angular et API Spring Boot.
- Les parcours métier principaux sont couverts par des tests front et back.
- Dans une logique MVP en entreprise, la base technique est suffisamment propre pour soutenir les premiers usages, mais plusieurs sujets devront être arbitrés ou traités avant une généralisation plus large: scalabilité du feed, sécurité du stockage du token, cohérence fonctionnelle de certaines validations et poids du bundle front.

## Points forts
- Architecture découplée front/back, simple à relire et à faire évoluer.
- Authentification JWT centralisée et bien intégrée entre front et back.
- Gestion d'erreurs backend structurée avec un DTO dédié.
- Services backend globalement bien découpés, avec `@Transactional(readOnly = true)` par défaut.
- Requêtes de lecture critiques optimisées avec `@EntityGraph` sur les posts et commentaires.
- `open-in-view` désactivé, ce qui est un bon choix de maîtrise de la couche persistence.
- Base de tests des deux côtés déjà sérieuse:
  - tests unitaires front
  - E2E Cypress
  - tests unitaires et d'intégration backend

## Constats techniques

### 1. Scalabilité du feed à surveiller
Le feed backend charge tous les articles des thèmes suivis, avec tri ascendant ou descendant, mais sans pagination.

Effets:
- volume de données non borné côté API
- temps de réponse amené à croître avec les abonnements et le nombre de posts
- charge front également croissante sur le rendu initial du feed

### 2. Chargement intégral des thèmes
La liste des thèmes est construite à partir d'un `findAll()` et d'un mapping en mémoire avec les abonnements utilisateurs.

Effets:
- très acceptable à petite volumétrie
- moins robuste si le catalogue de thèmes devient important

### 3. Stratégie de stockage du JWT
L'état `auth` est persisté côté front via le storage plugin NGXS.

Effets:
- bon confort utilisateur, cohérent avec une phase MVP où l'on privilégie la fluidité d'usage
- exposition accrue du token si une faille XSS apparaissait dans l'application

### 4. Incohérence de validation métier sur le mot de passe
Le parcours d'inscription applique une politique de mot de passe forte, alors que la mise à jour du profil n'impose actuellement qu'une longueur minimale de `6` caractères.

Effets:
- incohérence fonctionnelle
- diminution du niveau de sécurité attendu sur la mise à jour des credentials

### 5. Bundle front au-dessus du budget
Le build production reste fonctionnel et a été nettement allégé, mais il dépasse encore légèrement le budget Angular fixé pour le bundle initial.

Effets:
- chargement initial sensiblement amélioré
- marge de progression restante avant de revenir sous le seuil cible et d'absorber sereinement de nouvelles features

## Recommandations

### Priorité haute
- Introduire une pagination sur le feed backend et côté front.
- Uniformiser la politique mot de passe entre inscription et mise à jour de profil.
- Réduire le bundle initial front pour revenir sous le budget de `500 kB`.
- Planifier la remédiation des vulnérabilités `npm audit` les plus critiques.

### Priorité moyenne
- Revoir la stratégie de persistance du token si le projet vise un contexte plus exposé.
- Ajouter des seuils qualité bloquants en CI sur tests, couverture et audits.
- Évaluer si la liste des thèmes doit rester chargée en entier ou devenir paginée / filtrable.

### Priorité basse
- Consolider la documentation qualité pour qu'elle soit régénérable automatiquement.

## Risques résiduels
- Les volumes de données ne sont pas encore bornés sur certains flux.
- La sécurité du JWT reste dépendante de l'absence de faille XSS côté front.
- La qualimétrie existe, mais elle reste encore partiellement manuelle.

## Conclusion
- La revue technique est globalement positive pour un MVP en contexte entreprise.
- Les fondamentaux d'architecture, de couverture et de séparation des responsabilités sont suffisants pour soutenir une première mise en service encadrée.
- Le prochain palier de maturité passe surtout par l'industrialisation qualité, la réduction du poids front et une meilleure préparation à la volumétrie.

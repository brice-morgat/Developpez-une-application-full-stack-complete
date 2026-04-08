# Rapport de revue technique

Date: 2026-04-09

## Resume
- Le projet presente une architecture full-stack lisible, avec une separation claire entre UI Angular et API Spring Boot.
- Les parcours metier principaux sont couverts par des tests front et back.
- Dans une logique MVP en entreprise, la base technique est suffisamment propre pour soutenir les premiers usages, mais plusieurs sujets devront etre arbitres ou traites avant une generalisation plus large: scalabilite du feed, securite du stockage du token, coherence fonctionnelle de certaines validations et poids du bundle front.

## Points forts
- Architecture decouplee front/back, simple a relire et a faire evoluer.
- Authentification JWT centralisee et bien integree entre front et back.
- Gestion d'erreurs backend structuree avec un DTO dedie.
- Services backend globalement bien decoupes, avec `@Transactional(readOnly = true)` par defaut.
- Requetes de lecture critiques optimisees avec `@EntityGraph` sur les posts et commentaires.
- `open-in-view` desactive, ce qui est un bon choix de maitrise de la couche persistence.
- Base de tests des deux cotes deja serieuse:
  - tests unitaires front
  - E2E Cypress
  - tests unitaires et d'integration backend

## Constats techniques

### 1. Scalabilite du feed a surveiller
Le feed backend charge tous les articles des themes suivis, avec tri ascendant ou descendant, mais sans pagination.

Effets:
- volume de donnees non borne cote API
- temps de reponse amene a croitre avec les abonnements et le nombre de posts
- charge front egalement croissante sur le rendu initial du feed

### 2. Chargement integral des themes
La liste des themes est construite a partir d'un `findAll()` et d'un mapping en memoire avec les abonnements utilisateurs.

Effets:
- tres acceptable a petite volumetrie
- moins robuste si le catalogue de themes devient important

### 3. Strategie de stockage du JWT
L'etat `auth` est persiste cote front via le storage plugin NGXS.

Effets:
- bon confort utilisateur, coherent avec une phase MVP ou l'on privilegie la fluidite d'usage
- exposition accrue du token si une faille XSS apparaissait dans l'application

### 4. Incoherence de validation metier sur le mot de passe
Le parcours d'inscription applique une politique de mot de passe forte, alors que la mise a jour du profil n'impose actuellement qu'une longueur minimale de `6` caracteres.

Effets:
- incoherence fonctionnelle
- diminution du niveau de securite attendu sur la mise a jour des credentials

### 5. Bundle front au-dessus du budget
Le build production reste fonctionnel et a ete nettement allege, mais il depasse encore legerement le budget Angular fixe pour le bundle initial.

Effets:
- chargement initial sensiblement ameliore
- marge de progression restante avant de revenir sous le seuil cible et d'absorber sereinement de nouvelles features

## Recommandations

### Priorite haute
- Introduire une pagination sur le feed backend et cote front.
- Uniformiser la politique mot de passe entre inscription et mise a jour de profil.
- Reduire le bundle initial front pour revenir sous le budget de `500 kB`.
- Planifier la remediations des vulnerabilites `npm audit` les plus critiques.

### Priorite moyenne
- Revoir la strategie de persistance du token si le projet vise un contexte plus expose.
- Ajouter des seuils qualite bloquants en CI sur tests, couverture et audits.
- Evaluer si la liste des themes doit rester chargee en entier ou devenir paginee / filtrable.

### Priorite basse
- Consolider la documentation qualite pour qu'elle soit regenerable automatiquement.

## Risques residuels
- Les volumes de donnees ne sont pas encore bornes sur certains flux.
- La securite du JWT reste dependante de l'absence de faille XSS cote front.
- La qualimetrie existe, mais elle reste encore partiellement manuelle.

## Conclusion
- La revue technique est globalement positive pour un MVP en contexte entreprise.
- Les fondamentaux d'architecture, de couverture et de separation des responsabilites sont suffisants pour soutenir une premiere mise en service encadree.
- Le prochain palier de maturite passe surtout par l'industrialisation qualite, la reduction du poids front et une meilleure preparation a la volumetrie.

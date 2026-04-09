-- Jeu de données de démonstration MDD
-- Compatible MySQL 8+
-- Prérequis : les migrations Flyway doivent déjà avoir créé les tables.
-- Attention : ce script vide d'abord les tables métiers avant de réinsérer les données.

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE comments;
TRUNCATE TABLE posts;
TRUNCATE TABLE subscriptions;
TRUNCATE TABLE topics;
TRUNCATE TABLE users;

SET FOREIGN_KEY_CHECKS = 1;

-- Mot de passe pour tous les comptes ci-dessous : Password1!
-- Hash BCrypt généré pour Spring Security.

INSERT INTO users (id, username, email, password, created_at) VALUES
  (1, 'alice', 'alice@mdd.dev', '$2a$10$vDSuqv5zlLLIpr2STlcF5eJY/NoBvz7Pe.cSXsU.osakWdQI8FWqu', '2026-04-01 09:00:00'),
  (2, 'bruno', 'bruno@mdd.dev', '$2a$10$vDSuqv5zlLLIpr2STlcF5eJY/NoBvz7Pe.cSXsU.osakWdQI8FWqu', '2026-04-01 09:10:00'),
  (3, 'claire', 'claire@mdd.dev', '$2a$10$vDSuqv5zlLLIpr2STlcF5eJY/NoBvz7Pe.cSXsU.osakWdQI8FWqu', '2026-04-01 09:20:00'),
  (4, 'diego', 'diego@mdd.dev', '$2a$10$vDSuqv5zlLLIpr2STlcF5eJY/NoBvz7Pe.cSXsU.osakWdQI8FWqu', '2026-04-01 09:30:00');

INSERT INTO topics (id, name, description, created_at) VALUES
  (1, 'Java', 'Bonnes pratiques Java, Spring, performance JVM et architecture backend.', '2026-04-01 10:00:00'),
  (2, 'Datacenters', 'Infrastructure physique, résilience, énergie, refroidissement et exploitation des datacenters.', '2026-04-01 10:05:00'),
  (3, 'Cloud & Kubernetes', 'Conteneurs, orchestration, plateformes cloud et stratégies de déploiement.', '2026-04-01 10:10:00'),
  (4, 'DevOps', 'CI/CD, qualité logicielle, automatisation et observabilité des plateformes.', '2026-04-01 10:15:00'),
  (5, 'Architecture Logicielle', 'DDD, événements, scalabilité et structuration des applications métiers.', '2026-04-01 10:20:00');

INSERT INTO subscriptions (id, user_id, topic_id, created_at) VALUES
  (1, 1, 1, '2026-04-01 11:00:00'),
  (2, 1, 4, '2026-04-01 11:01:00'),
  (3, 1, 5, '2026-04-01 11:02:00'),
  (4, 2, 2, '2026-04-01 11:05:00'),
  (5, 2, 3, '2026-04-01 11:06:00'),
  (6, 2, 4, '2026-04-01 11:07:00'),
  (7, 3, 1, '2026-04-01 11:10:00'),
  (8, 3, 3, '2026-04-01 11:11:00'),
  (9, 3, 5, '2026-04-01 11:12:00'),
  (10, 4, 2, '2026-04-01 11:15:00'),
  (11, 4, 4, '2026-04-01 11:16:00');

INSERT INTO posts (id, title, content, author_id, topic_id, created_at) VALUES
  (
    1,
    'Spring Boot 3 : sécuriser proprement une API métier',
    'Sur un projet MVP, la tentation est forte de brancher l''authentification au plus vite. En pratique, il faut aussi penser à la normalisation des identifiants, au hash du mot de passe, aux DTO d''erreur et au découpage entre contrôleurs, services et persistance. Une API propre au départ coûte moins cher à maintenir quand les premiers retours métier arrivent.',
    1,
    1,
    '2026-04-02 09:00:00'
  ),
  (
    2,
    'Datacenter de proximité : pourquoi la latence reste un sujet produit',
    'Quand une application sert des équipes réparties sur plusieurs régions, la question du datacenter n''est pas seulement technique. La localisation des workloads impacte directement la perception de fluidité, la tolérance aux incidents et la capacité à respecter certaines contraintes réglementaires. Le design d''une plateforme doit intégrer ce sujet dès la conception.',
    2,
    2,
    '2026-04-02 11:30:00'
  ),
  (
    3,
    'Kubernetes sans suringénierie : commencer petit, observer tôt',
    'Toutes les équipes n''ont pas besoin d''une plateforme très complexe dès le premier sprint. En revanche, poser rapidement les bases sur les déploiements, les probes, les ressources et l''observabilité évite beaucoup de dette opérationnelle. La priorité reste de rendre les déploiements fiables et compréhensibles.',
    3,
    3,
    '2026-04-03 08:45:00'
  ),
  (
    4,
    'CI/CD : les garde-fous qualité qui évitent les régressions silencieuses',
    'Un pipeline utile ne se limite pas à lancer un build. Il doit aussi porter la stratégie de test, la couverture minimale, les audits de dépendances et les métriques de performance. Plus ces garde-fous arrivent tôt, moins les équipes ont à arbitrer entre vitesse de livraison et stabilité.',
    4,
    4,
    '2026-04-03 16:10:00'
  ),
  (
    5,
    'Architecture logicielle : quand introduire une vraie séparation des responsabilités',
    'Sur une application métier qui grossit, la confusion entre logique HTTP, règles métier et accès aux données devient vite coûteuse. Clarifier les responsabilités par couches et par fonctionnalités permet de tester plus facilement, de faire évoluer les cas d''usage et de rendre le code beaucoup plus lisible pour l''équipe.',
    1,
    5,
    '2026-04-04 10:20:00'
  ),
  (
    6,
    'Java et performance : trois optimisations utiles avant de parler microservices',
    'Avant de découper une application en plusieurs services, il est souvent plus rentable d''inspecter les requêtes SQL, de maîtriser les chargements d''entités et de mesurer le coût réel du rendu des pages. Beaucoup de goulets d''étranglement d''un MVP se résolvent d''abord par de la simplicité et de la visibilité.',
    3,
    1,
    '2026-04-05 14:00:00'
  );

INSERT INTO comments (id, content, post_id, author_id, created_at) VALUES
  (1, 'Très bon rappel sur le fait que la sécurité ne se résume pas au JWT.', 1, 3, '2026-04-02 10:05:00'),
  (2, 'Le point sur les DTO d''erreur est important pour garder un front propre.', 1, 4, '2026-04-02 10:40:00'),
  (3, 'On sous-estime souvent l''impact métier d''une latence irrégulière.', 2, 1, '2026-04-02 13:20:00'),
  (4, 'Le sujet énergie et refroidissement devient aussi un vrai critère d''arbitrage.', 2, 4, '2026-04-02 15:45:00'),
  (5, 'Commencer petit avec Kubernetes est probablement le meilleur conseil pour une équipe produit.', 3, 2, '2026-04-03 09:30:00'),
  (6, 'Les probes et l''observabilité sont souvent ajoutées trop tard.', 3, 1, '2026-04-03 10:10:00'),
  (7, 'Oui, un pipeline sans seuils qualité finit vite par devenir décoratif.', 4, 1, '2026-04-03 17:00:00'),
  (8, 'On a gagné beaucoup de temps en ajoutant la couverture et l''audit de dépendances au plus tôt.', 4, 3, '2026-04-03 18:25:00'),
  (9, 'La séparation controller / service / repository change vraiment la capacité à tester.', 5, 2, '2026-04-04 11:05:00'),
  (10, 'Le bon niveau d''architecture dépend aussi beaucoup du rythme attendu sur le produit.', 5, 4, '2026-04-04 12:15:00'),
  (11, 'Complètement d''accord : profiler avant de distribuer évite beaucoup de complexité inutile.', 6, 1, '2026-04-05 15:20:00');

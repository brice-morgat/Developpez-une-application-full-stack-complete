# Comparatif Lighthouse - 2026-04-09

## Périmètre
- URL mesurée: page d'accueil locale servie depuis `dist/front`
- Outil: Lighthouse headless en local
- Baseline: avant optimisation du shell Angular / Material
- Mesure finale: après optimisation du shell, chargement asynchrone des animations et allègement du thème Material global

## Résultats
- Bundle initial: `669.00 kB` -> `529.48 kB` (`-139.52 kB`)
- `main.js`: `539.13 kB` -> `471.20 kB` (`-67.93 kB`)
- `styles.css`: `92.14 kB` -> `20.35 kB` (`-71.79 kB`)
- Performance: `68` -> `76`
- Accessibility: `100` -> `100`
- Best Practices: `100` -> `100`
- SEO: `91` -> `91`
- FCP: `4.7 s` -> `3.7 s`
- LCP: `5.5 s` -> `4.3 s`
- TBT: `90 ms` -> `70 ms`
- CLS: `0.026` -> `0.019`
- Speed Index: `4.7 s` -> `3.7 s`

## Lecture
- Le gain principal vient de l'allègement du shell initial et du remplacement du thème Material prebuilt par un thème ciblé sur les composants réellement utilisés.
- Le chargement asynchrone des animations déplace aussi une partie du coût Angular hors du bundle initial.
- Le point `Best Practices` initialement dégradé a finalement été corrigé en remettant un ratio d'image cohérent sur le logo de la page d'accueil.

## Artefacts
- Avant: `docs/qualimetrie/lighthouse-summary-before-optimizations.json`
- Après: `docs/qualimetrie/lighthouse-summary-after-optimizations.json`
- Rapport courant: `docs/qualimetrie/lighthouse-summary.json`

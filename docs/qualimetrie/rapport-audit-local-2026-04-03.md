# Rapport d'audit qualimetrie local - 2026-04-03 (code actuel)

## Contexte
- Projet Sonar cible: `AuditSonar`
- Perimetre: `front` (Angular 19) + `back` (Spring Boot 3.2)
- Date de mesure: 2026-04-03

## KPI globaux
- Front build: OK
- Front tests: OK (`74/74`)
- Front couverture:
  - Lignes: 96.56%
  - Branches: 74.58%
  - Fonctions: 99.05%
- Back tests unitaires: OK (`51/51`)
- Back couverture (JaCoCo):
  - Lignes: 58.78%
  - Branches: 43.97%
  - Instructions: 63.86%
  - Methodes: 68.53%
- Vulnerabilites dependances front (`npm audit`): `30` (23 high, 7 moderate)
- Vulnerabilites dependances back (OWASP dependency-check): `6` (medium)
- Lighthouse:
  - Performance: 70
  - Accessibility: 100
  - Best Practices: 100
  - SEO: 91
  - FCP: 4.2 s
  - LCP: 5.2 s
  - TBT: 70 ms
  - CLS: 0.026

## Resultats Sonar
- Etat: NON EXECUTE
- Motif:
  - `SONAR_TOKEN` non defini
  - SonarQube local non demarre sur cette machine
- Config prete: `sonar-project.properties` (project key/name = `AuditSonar`)

## Resultats securite dependances
### Front
- Source: `docs/qualimetrie/npm-audit.json`
- Resultat: `30` vulnerabilites (23 high, 7 moderate)

### Back
- Sources:
  - `back/target/dependency-check-report.html`
  - `back/target/dependency-check-report.json`
- Resultat: `6` vulnerabilites medium
- Principale exposition remontee:
  - `swagger-ui` embarquant `DOMPurify` avec CVE signalees

## Qualite execution tests
- Les tests d'integration backend (`*IT`) exigent un MySQL actif et echouent sinon.
- Pour l'audit qualimetrie local, la couverture backend est calculee sur tests unitaires (`*Test/*Tests`) en excluant `MddApiApplicationTests` qui charge le contexte DB complet.

## Modifications outillage appliquees
- `sonar-project.properties` ajoute (projet `AuditSonar`)
- `scripts/audit-local.sh` ajoute/mis a jour
- `scripts/lighthouse-audit.sh` ajoute/mis a jour
- `front/karma.conf.js`: ajout `lcovonly`
- `front/package.json`: scripts `quality:*`
- `back/pom.xml`: plugins Sonar + OWASP dependency-check

## Backlog de remediation priorise
### P0
1. Demarrer SonarQube local + definir `SONAR_TOKEN` + lancer le scan `AuditSonar`.
2. Traiter les 23 vulnerabilites npm `high` (priorite dependances runtime/build critiques).
3. Corriger la perf front (LCP 5.2 s, score perf 70) en reduisant le bundle initial (669 kB > budget 500 kB).

### P1
1. Monter la couverture backend (58.78% lignes, 43.97% branches).
2. Isoler clairement les tests d'integration DB (profil/docker compose test) pour pouvoir les rejouer en CI.
3. Mettre a jour swagger-ui / dependances liees a `DOMPurify` remontees par OWASP.

### P2
1. Industrialiser en CI: quality gate Sonar + npm audit + OWASP + Lighthouse.
2. Ajouter suivi de tendance des KPIs dans `docs/qualimetrie`.

## Artefacts generes
- `docs/qualimetrie/npm-audit.json`
- `docs/qualimetrie/lighthouse-report.json`
- `docs/qualimetrie/lighthouse-summary.json`
- `back/target/site/jacoco/jacoco.xml`
- `back/target/dependency-check-report.html`
- `back/target/dependency-check-report.json`

## Commandes
```bash
# Audit local
bash scripts/audit-local.sh

# Sonar (si SonarQube local + token disponibles)
export SONAR_TOKEN=<token>
sonar-scanner -Dsonar.host.url=http://localhost:9000 -Dsonar.token=$SONAR_TOKEN
```

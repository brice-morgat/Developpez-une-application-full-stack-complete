#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
FRONT_DIR="$ROOT_DIR/front"
BACK_DIR="$ROOT_DIR/back"
REPORT_DIR="$ROOT_DIR/docs/qualimetrie"
NPM_AUDIT_JSON="$REPORT_DIR/npm-audit.json"
DC_AUTO_UPDATE="${DEPENDENCY_CHECK_AUTO_UPDATE:-false}"

mkdir -p "$REPORT_DIR"

ensure_jdk21() {
  if command -v java >/dev/null 2>&1; then
    return
  fi

  local jdk_dir="$ROOT_DIR/.tools/jdk-21"
  local archive="$ROOT_DIR/.tools/jdk21.tar.gz"

  if [[ ! -x "$jdk_dir/bin/java" ]]; then
    mkdir -p "$ROOT_DIR/.tools"
    curl -fsSL -o "$archive" "https://api.adoptium.net/v3/binary/latest/21/ga/linux/x64/jdk/hotspot/normal/eclipse"
    rm -rf "$jdk_dir"
    mkdir -p "$jdk_dir"
    tar -xzf "$archive" -C "$jdk_dir" --strip-components=1
  fi

  export JAVA_HOME="$jdk_dir"
  export PATH="$JAVA_HOME/bin:$PATH"
}

run_back_maven() {
  local cmd="$1"
  ensure_jdk21
  (cd "$BACK_DIR" && ./mvnw -B $cmd)
}

echo "[1/6] Front install"
(cd "$FRONT_DIR" && npm ci)

echo "[2/6] Front build"
(cd "$FRONT_DIR" && npm run -s quality:build)

echo "[3/6] Front tests + coverage"
(cd "$FRONT_DIR" && npm run -s quality:test)

echo "[4/6] Front dependency audit"
(cd "$FRONT_DIR" && npm run -s quality:audit > "$NPM_AUDIT_JSON" || true)

echo "[5/6] Backend tests + JaCoCo"
run_back_maven "clean test jacoco:report -Dtest='*Test,*Tests,!MddApiApplicationTests'"

echo "[6/6] Backend dependency-check"
run_back_maven "org.owasp:dependency-check-maven:check -DautoUpdate=${DC_AUTO_UPDATE} -DfailOnError=false"

echo "[bonus] Lighthouse"
"$ROOT_DIR/scripts/lighthouse-audit.sh"

if [[ -n "${SONAR_TOKEN:-}" ]]; then
  echo "[bonus] Sonar scan"
  if command -v sonar-scanner >/dev/null 2>&1; then
    (cd "$ROOT_DIR" && sonar-scanner -Dsonar.host.url=http://localhost:9000 -Dsonar.token="$SONAR_TOKEN")
  else
    echo "WARN: sonar-scanner indisponible localement"
  fi
else
  echo "WARN: SONAR_TOKEN non défini, scan Sonar ignoré"
fi

echo "Audit local terminé. Artefacts dans: $REPORT_DIR"

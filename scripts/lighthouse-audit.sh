#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
FRONT_DIR="$ROOT_DIR/front"
REPORT_DIR="$ROOT_DIR/docs/qualimetrie"
JSON_REPORT="$REPORT_DIR/lighthouse-report.json"
SUMMARY_REPORT="$REPORT_DIR/lighthouse-summary.json"
PORT="${LIGHTHOUSE_PORT:-4173}"

mkdir -p "$REPORT_DIR"

pushd "$FRONT_DIR" >/dev/null
npm run -s build

npx -y http-server dist/front -p "$PORT" >/tmp/http-server-front.log 2>&1 &
SERVER_PID=$!
cleanup() {
  kill "$SERVER_PID" >/dev/null 2>&1 || true
  wait "$SERVER_PID" 2>/dev/null || true
}
trap cleanup EXIT

sleep 4

npx -y lighthouse "http://localhost:${PORT}" \
  --quiet \
  --chrome-flags='--headless --no-sandbox' \
  --output=json \
  --output-path="$JSON_REPORT"

node -e '
const fs = require("fs");
const data = JSON.parse(fs.readFileSync(process.argv[1], "utf8"));
const c = data.categories;
const summary = {
  performance: Math.round(c.performance.score * 100),
  accessibility: Math.round(c.accessibility.score * 100),
  bestPractices: Math.round(c["best-practices"].score * 100),
  seo: Math.round(c.seo.score * 100),
  fcp: data.audits["first-contentful-paint"].displayValue,
  lcp: data.audits["largest-contentful-paint"].displayValue,
  tbt: data.audits["total-blocking-time"].displayValue,
  cls: data.audits["cumulative-layout-shift"].displayValue,
  speedIndex: data.audits["speed-index"].displayValue
};
fs.writeFileSync(process.argv[2], JSON.stringify(summary, null, 2));
console.log(JSON.stringify(summary, null, 2));
' "$JSON_REPORT" "$SUMMARY_REPORT"

popd >/dev/null

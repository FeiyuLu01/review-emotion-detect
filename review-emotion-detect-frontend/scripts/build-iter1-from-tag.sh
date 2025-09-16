#!/usr/bin/env bash
set -euo pipefail

TAG_NAME="${ITER1_TAG:-iteration1-locked}"

TMP_DIR="$(mktemp -d)"

echo "==> Building archived snapshot from tag: $TAG_NAME"

git fetch --tags --force
git worktree add --detach "$TMP_DIR" "$TAG_NAME"

pushd "$TMP_DIR/review-emotion-detect-frontend" >/dev/null

if [ -f package-lock.json ] || [ -f npm-shrinkwrap.json ]; then
  npm ci
else
  echo "No lockfile found in archived tag. Falling back to 'npm install'."
  npm install --no-audit --no-fund
fi

export VITE_ROUTER_BASE="/iteration1/"
npx vite build --base=/iteration1/ --outDir="dist/iteration1" --emptyOutDir=false

popd >/dev/null

git worktree remove "$TMP_DIR" --force
rm -rf "$TMP_DIR"

echo "==> Done. Archived build is at: dist/iteration1"
set -euo pipefail

TAG_NAME="${ITER1_TAG:-iteration1-locked}"   
REPO_ROOT="$(git rev-parse --show-toplevel)"
DIST_DIR="$REPO_ROOT/review-emotion-detect-frontend/dist/iteration1"
TMP_DIR="$(mktemp -d)"

echo "==> Building archived snapshot from tag: $TAG_NAME"

rm -rf "$DIST_DIR"
mkdir -p "$DIST_DIR"

git fetch --tags --force
git worktree add --detach "$TMP_DIR" "$TAG_NAME"

pushd "$TMP_DIR/review-emotion-detect-frontend" >/dev/null

OUT_DIR_REL="dist/iteration1"

if [ -f package-lock.json ] || [ -f npm-shrinkwrap.json ]; then
  npm ci
else
  echo "No lockfile found in archived tag. Falling back to 'npm install'."
  npm install --no-audit --no-fund
fi

npx vite build --base=/iteration1/ --outDir="$OUT_DIR_REL" --emptyOutDir=false

popd >/dev/null

git worktree remove "$TMP_DIR" --force
rm -rf "$TMP_DIR"

echo "==> Done. Archived build is at: $DIST_DIR"
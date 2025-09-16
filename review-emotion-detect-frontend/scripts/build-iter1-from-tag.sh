set -euo pipefail
TAG_NAME="${ITER1_TAG:-iteration1}"
DIST_DIR="$(pwd)/dist/iteration1"
TMP_DIR="$(mktemp -d)"
echo "==> Building archived snapshot from tag: $TAG_NAME"
rm -rf "$DIST_DIR"
mkdir -p "$DIST_DIR"
git fetch --tags --force
git worktree add --detach "$TMP_DIR" "$TAG_NAME"

pushd "$TMP_DIR" >/dev/null
npm ci
npx vite build --base=/iteration1/ --outDir="$DIST_DIR"
popd >/dev/null
git worktree remove "$TMP_DIR" --force
rm -rf "$TMP_DIR"

echo "==> Done. Archived build is at: $DIST_DIR"

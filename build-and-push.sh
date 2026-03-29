#!/usr/bin/env bash
# Builds and pushes ARM64 images to Docker Hub.
# Usage: ./build-and-push.sh [tag]   (default tag: latest)

set -euo pipefail

REGISTRY="unamanic"
API_IMAGE="$REGISTRY/hexle-api"
UI_IMAGE="$REGISTRY/hexle-ui"
TAG="${1:-latest}"

echo "🔨 Setting up buildx builder for linux/arm64..."
docker buildx create --use --name hexle-builder 2>/dev/null || docker buildx use hexle-builder
docker buildx inspect --bootstrap

echo ""
echo "🚀 Building & pushing API image: $API_IMAGE:$TAG"
docker buildx build \
  --platform linux/arm64 \
  --file api/Dockerfile \
  --tag "$API_IMAGE:$TAG" \
  --push \
  .

echo ""
echo "🚀 Building & pushing UI image: $UI_IMAGE:$TAG"
docker buildx build \
  --platform linux/arm64 \
  --file ui/Dockerfile \
  --tag "$UI_IMAGE:$TAG" \
  --push \
  ui/

echo ""
echo "✅ Done!"
echo "   $API_IMAGE:$TAG"
echo "   $UI_IMAGE:$TAG"

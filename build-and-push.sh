#!/usr/bin/env bash
# Builds and pushes multi-arch images (linux/amd64 + linux/arm64) to Docker Hub,
# then updates K8s manifests with the new tag.
# Usage: ./build-and-push.sh [tag]   (default tag: git short SHA)

set -euo pipefail

REGISTRY="unamanic"
API_IMAGE="$REGISTRY/hexle-api"
UI_IMAGE="$REGISTRY/hexle-ui"
TAG="${1:-$(git rev-parse --short HEAD)}"

echo "🔨 Setting up buildx builder for linux/amd64 + linux/arm64..."
docker buildx create --use --name hexle-builder 2>/dev/null || docker buildx use hexle-builder
docker buildx inspect --bootstrap

echo ""
echo "🚀 Building & pushing API image: $API_IMAGE:$TAG"
docker buildx build \
  --platform linux/amd64,linux/arm64 \
  --file api/Dockerfile \
  --tag "$API_IMAGE:$TAG" \
  --tag "$API_IMAGE:latest" \
  --push \
  .

echo ""
echo "🚀 Building & pushing UI image: $UI_IMAGE:$TAG"
docker buildx build \
  --platform linux/amd64,linux/arm64 \
  --file ui/Dockerfile \
  --tag "$UI_IMAGE:$TAG" \
  --tag "$UI_IMAGE:latest" \
  --push \
  ui/

echo ""
echo "📝 Updating K8s manifests with tag: $TAG"
sed -i "s|$API_IMAGE:.*|$API_IMAGE:$TAG|" k8s/api-deployment.yaml
sed -i "s|$UI_IMAGE:.*|$UI_IMAGE:$TAG|" k8s/ui-deployment.yaml

echo ""
echo "✅ Done!"
echo "   $API_IMAGE:$TAG (linux/amd64 + linux/arm64)"
echo "   $UI_IMAGE:$TAG (linux/amd64 + linux/arm64)"
echo ""
echo "Deploy with:"
echo "   kubectl apply -f k8s/"

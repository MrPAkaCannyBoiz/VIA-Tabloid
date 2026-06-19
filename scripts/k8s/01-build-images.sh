#!/usr/bin/env bash
# Build all VIA-Tabloid Docker images against the host Docker daemon.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
BACKEND_IMAGE="via-tabloid-spring-boot:latest"
FRONTEND_IMAGE="via-tabloid-frontend:latest"

command -v docker >/dev/null 2>&1 || { echo "ERROR: docker not found in PATH"; exit 1; }

cd "$REPO_ROOT"

echo ">> Building backend image: $BACKEND_IMAGE"
docker build -t "$BACKEND_IMAGE" -f via-tabloid-spring-boot/Dockerfile .

echo ">> Building frontend image: $FRONTEND_IMAGE"
docker build -t "$FRONTEND_IMAGE" ./frontend

echo ">> Done. Built images:"
docker images --filter "reference=via-tabloid-*:latest"

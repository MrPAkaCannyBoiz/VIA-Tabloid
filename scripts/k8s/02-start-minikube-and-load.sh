#!/usr/bin/env bash
# Start minikube if it isn't running, then load the locally-built images into the cluster.
# Loading is required because the app deployments use imagePullPolicy: Never.
set -euo pipefail

BACKEND_IMAGE="via-tabloid-spring-boot:latest"
FRONTEND_IMAGE="via-tabloid-frontend:latest"

command -v minikube >/dev/null 2>&1 || { echo "ERROR: minikube not found in PATH"; exit 1; }

if minikube status >/dev/null 2>&1; then
  echo ">> minikube already running"
else
  echo ">> minikube not ready, starting..."
  # If the apiserver fails to come up, retry with more resources:
  #   minikube start --memory=4096 --cpus=2
  minikube start
fi

echo ">> Loading $BACKEND_IMAGE into minikube"
minikube image load "$BACKEND_IMAGE"

echo ">> Loading $FRONTEND_IMAGE into minikube"
minikube image load "$FRONTEND_IMAGE"

echo ">> Done. Images inside minikube:"
minikube image ls | grep via-tabloid || true

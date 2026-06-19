#!/usr/bin/env bash
# Apply the Kustomize manifests in ./k8s and wait for the rollouts.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

command -v kubectl >/dev/null 2>&1 || { echo "ERROR: kubectl not found in PATH"; exit 1; }

# secrets.env is git-ignored but required by the kustomize secretGenerator.
[ -f "$REPO_ROOT/k8s/secrets.env" ] || {
  echo "ERROR: $REPO_ROOT/k8s/secrets.env is missing (required by k8s/kustomization.yaml)"; exit 1;
}

# Record which app deployments already exist. These are UPDATES that need a restart to pick
# up a reloaded :latest image (same tag => kubectl apply alone won't recreate the pods).
RESTART_TARGETS=()
for d in via-tabloid-spring-boot frontend; do
  if kubectl get deployment "$d" >/dev/null 2>&1; then
    RESTART_TARGETS+=("deployment/$d")
  fi
done

echo ">> Applying manifests from $REPO_ROOT/k8s"
kubectl apply -k "$REPO_ROOT/k8s/"

# Restart only pre-existing app deployments. NEVER restart postgres: it has no PVC
# (k8s/postgres-deployment.yaml mounts only the init-SQL ConfigMap), so a restart wipes the DB.
if [ "${#RESTART_TARGETS[@]}" -gt 0 ]; then
  echo ">> Restarting updated deployments: ${RESTART_TARGETS[*]}"
  kubectl rollout restart "${RESTART_TARGETS[@]}"
fi

# Set ROLLOUT_TIMEOUT (e.g. 120s) to fail fast instead of waiting indefinitely (used by CI).
TIMEOUT_ARG=()
[ -n "${ROLLOUT_TIMEOUT:-}" ] && TIMEOUT_ARG=(--timeout="$ROLLOUT_TIMEOUT")

echo ">> Waiting for rollouts..."
kubectl rollout status deployment/postgres "${TIMEOUT_ARG[@]}"
kubectl rollout status deployment/via-tabloid-spring-boot "${TIMEOUT_ARG[@]}"
kubectl rollout status deployment/frontend "${TIMEOUT_ARG[@]}"

echo ">> Done."
kubectl get pods

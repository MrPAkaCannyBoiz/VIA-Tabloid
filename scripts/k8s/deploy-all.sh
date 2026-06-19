#!/usr/bin/env bash
# One-shot local deploy: build images -> start minikube + load images -> apply manifests.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Invoke sub-scripts via `bash` so this works regardless of the exec bit
# (the repo may live on an NTFS / /mnt/c mount where chmod +x doesn't stick).
bash "$SCRIPT_DIR/01-build-images.sh"
bash "$SCRIPT_DIR/02-start-minikube-and-load.sh"
bash "$SCRIPT_DIR/03-apply-k8s.sh"

MK_IP="$(minikube ip)"
echo ""
echo "================ VIA-Tabloid is deployed ================"
echo "Frontend:    http://$MK_IP:30080"
echo "Backend API: http://$MK_IP:30808/api"
echo "========================================================"

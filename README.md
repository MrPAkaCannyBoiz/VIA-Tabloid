# VIA Tabloid (VIATAB)

A full-stack tabloid application for managing sensational stories from departments at VIA University College. Built as a DevOps course assignment.

**Stack:** React + Vite (frontend) · Spring Boot (backend) · PostgreSQL (database)

---

## Project Structure

```
VIA-Tabloid/
├── frontend/                      # React + Vite + TypeScript
├── via-tabloid-spring-boot/       # Spring Boot REST API
├── k8s/                           # Kubernetes manifests (Kustomize)
├── scripts/
│   ├── init.sql                   # Database schema
│   └── k8s/                       # Minikube build / load / deploy bash scripts
├── docker-compose.yml
└── .env                           # Local environment variables
```

---

## Prerequisites

| Tool | Purpose |
|------|---------|
| Docker + Docker Compose | Running the app locally |
| Java 25 + Maven | Building/running the backend locally |
| Node.js 20+ | Building/running the frontend locally |
| kubectl | Managing Kubernetes resources |
| Minikube | Local Kubernetes cluster |

---

## Option 1 — Docker Compose (recommended for local dev)

### 1. Configure environment

The root `.env` file already contains default values:

```dotenv
DB_URL="jdbc:postgresql://localhost:5432/postgres?currentSchema=via_tabloid"
DB_USERNAME="postgres"
DB_PASSWORD="via1234"
DB_URL_DOCKER="jdbc:postgresql://postgres:5432/via_tabloid"
```

Edit it if you need different credentials.

### 2. Start all services

```bash
docker compose up --build
```

This starts three containers:
- `postgres_via_tabloid` — PostgreSQL on port `5432`
- `via-tabloid-spring-boot` — REST API on port `8080`
- `via-tabloid-frontend` — React app on port `3000`

### 3. Open the app

- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api

### 4. Stop

```bash
docker compose down
```

To also remove the database volume:

```bash
docker compose down -v
```

---

## Option 2 — Kubernetes with Minikube

The app deployments use `imagePullPolicy: Never`, so the images must already exist **inside**
the Minikube cluster before you apply the manifests. The flow is always:
**build → load into Minikube → apply**. Skipping the load step leaves the pods stuck in
`ErrImageNeverPull`.

### Quick start (scripts)

Helper scripts live in `scripts/k8s/`. The one-command deploy builds the images, starts
Minikube (if it isn't already running), loads the images into the cluster, and applies the
manifests — then prints the frontend and backend URLs:

```bash
bash scripts/k8s/deploy-all.sh
```

Prefer to run the steps individually? Each script is self-contained:

| Script | What it does |
|--------|--------------|
| `scripts/k8s/01-build-images.sh` | Builds the backend and frontend images against the host Docker daemon |
| `scripts/k8s/02-start-minikube-and-load.sh` | Starts Minikube if it isn't running, then loads both images into the cluster |
| `scripts/k8s/03-apply-k8s.sh` | Applies `k8s/`, restarts the app deployments, and waits for the rollouts |
| `scripts/k8s/deploy-all.sh` | Runs all three of the above in order |

> Re-running `deploy-all.sh` is safe: it rebuilds the images, reloads them, and restarts
> **only** the app deployments — never PostgreSQL, which has no persistent volume and would
> lose its data on restart.

### Manual steps

If you'd rather run the commands yourself:

#### 1. Start Minikube

```bash
minikube start
```

If the API server fails to come up, retry with more resources:

```bash
minikube start --memory=4096 --cpus=2
```

#### 2. Build the Docker images

From the repo root, against your host Docker daemon:

```bash
docker build -t via-tabloid-spring-boot:latest -f via-tabloid-spring-boot/Dockerfile .
docker build -t via-tabloid-frontend:latest ./frontend
```

#### 3. Load the images into Minikube

Because the deployments use `imagePullPolicy: Never`, load the images into the cluster's
image store **before** applying — otherwise the pods fail with `ErrImageNeverPull`:

```bash
minikube image load via-tabloid-spring-boot:latest
minikube image load via-tabloid-frontend:latest

# Verify they're inside the cluster:
minikube image ls | grep via-tabloid
```

> **Alternative:** instead of build-then-load, point your shell's Docker CLI at Minikube's
> daemon and build straight into it — `eval $(minikube docker-env)` (Linux/macOS) or
> `& minikube -p minikube docker-env | Invoke-Expression` (Windows PowerShell) — then run the
> `docker build` commands from step 2. Run `eval $(minikube docker-env -u)` to revert.

#### 4. Apply Kubernetes manifests

```bash
kubectl apply -k k8s/
```

This creates:
- `postgres` Deployment + ClusterIP Service
- `via-tabloid-spring-boot` Deployment + NodePort Service (port `30808`)
- `frontend` Deployment + NodePort Service (port `30080`)
- `via-tabloid-secrets` Secret (from `k8s/secrets.env`)
- `postgres-cm0` ConfigMap (database init SQL)

#### 5. Wait for all pods to be ready

```bash
kubectl rollout status deployment/postgres
kubectl rollout status deployment/via-tabloid-spring-boot
kubectl rollout status deployment/frontend
```

Or watch all pods at once:

```bash
kubectl get pods --watch
```

#### 6. Open the app

Get the Minikube IP:

```bash
minikube ip
```

Then open in your browser:

| Service | URL |
|---------|-----|
| Frontend | `http://<minikube-ip>:30080` |
| Backend API | `http://<minikube-ip>:30808/api` |

Alternatively, let Minikube open them for you:

```bash
minikube service frontend
minikube service via-tabloid-spring-boot
```

#### 7. Useful commands

```bash
# See all resources
kubectl get all

# View logs for a specific pod
kubectl logs deployment/via-tabloid-spring-boot
kubectl logs deployment/frontend

# Describe a pod (useful for debugging CrashLoopBackOff)
kubectl describe pod <pod-name>

# Open the Minikube dashboard
minikube dashboard

# Delete all deployed resources
kubectl delete -k k8s/

# Stop Minikube
minikube stop
```

#### 8. Updating a deployment after code changes

After changing source code, rebuild the image, **reload it into Minikube**, and restart the
deployment so the new image is picked up (the `:latest` tag is unchanged, so `apply` alone
won't recreate the pods):

```bash
# Rebuild
docker build -t via-tabloid-spring-boot:latest -f via-tabloid-spring-boot/Dockerfile .

# Reload into the cluster
minikube image load via-tabloid-spring-boot:latest

# Restart the deployment to pick up the new image
kubectl rollout restart deployment/via-tabloid-spring-boot
```

Or just re-run `bash scripts/k8s/deploy-all.sh`, which does all of this for you.

---

## API Reference

Base URL: `http://localhost:8080` (Docker Compose) or `http://<minikube-ip>:30808` (K8s)

### Departments

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/departments` | List all departments |
| GET | `/api/departments/{id}` | Get department by ID |
| POST | `/api/departments` | Create department — body: `{"name": "Engineering"}` |
| DELETE | `/api/departments/{id}` | Delete department |

### Stories

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/stories` | List all stories |
| GET | `/api/stories/{id}` | Get story by ID |
| POST | `/api/stories` | Create story — body: `{"title": "...", "description": "...", "departmentId": 1}` |
| PUT | `/api/stories/{id}` | Update story — same body as POST |
| DELETE | `/api/stories/{id}` | Delete story |

---

## CI/CD

GitHub Actions workflow at `.github/workflows/ci-cd-workflow.yaml`:

- **CI** (all branches): runs Maven tests, then builds the backend and frontend Docker images and uploads them as job artifacts
- **CD** (`main` only, on push): starts Minikube, downloads the image artifacts and loads them into the cluster, generates `k8s/secrets.env` from repository secrets, then runs `scripts/k8s/03-apply-k8s.sh` to apply the manifests and wait for the rollouts

> The CD job reuses the same `scripts/k8s/03-apply-k8s.sh` as local development (with `ROLLOUT_TIMEOUT=120s`, so a stuck rollout fails the build), keeping the apply/rollout behavior identical in both environments.

### Required GitHub Secrets

`k8s/secrets.env` is git-ignored and generated at deploy time from repository secrets. Add these in **Settings → Secrets and variables → Actions**:

| Secret name | Value |
|-------------|-------|
| `DB_USERNAME` | `postgres` |
| `DB_PASSWORD` | `[your password as your wish]` |
| `DB_URL_DOCKER` | `jdbc:postgresql://postgres:5432/via_tabloid` |

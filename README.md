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
│   └── init.sql                   # Database schema
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

### 1. Start Minikube

```bash
minikube start
```

### 2. Point your terminal's Docker to Minikube's daemon

This means images you build will be available inside the cluster (no registry needed).

**Linux / macOS:**
```bash
eval $(minikube docker-env)
```

**Windows PowerShell:**
```powershell
& minikube -p minikube docker-env | Invoke-Expression
```

> Run this in every new terminal session before building images.

### 3. Build the Docker images

```bash
# From the repo root
docker build -t via-tabloid-spring-boot:latest -f via-tabloid-spring-boot/Dockerfile .
docker build -t via-tabloid-frontend:latest ./frontend
```

### 4. Apply Kubernetes manifests

```bash
kubectl apply -k k8s/
```

This creates:
- `postgres` Deployment + ClusterIP Service
- `via-tabloid-spring-boot` Deployment + NodePort Service (port `30808`)
- `frontend` Deployment + NodePort Service (port `30080`)
- `via-tabloid-secrets` Secret (from `k8s/secrets.env`)
- `postgres-cm0` ConfigMap (database init SQL)

### 5. Wait for all pods to be ready

```bash
kubectl rollout status deployment/postgres
kubectl rollout status deployment/via-tabloid-spring-boot
kubectl rollout status deployment/frontend
```

Or watch all pods at once:

```bash
kubectl get pods --watch
```

### 6. Open the app

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

### 7. Useful commands

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

### 8. Updating a deployment after code changes

After changing source code, rebuild the image and restart the deployment:

```bash
# Re-point Docker to Minikube (if in a new terminal)
eval $(minikube docker-env)          # Linux/macOS
& minikube -p minikube docker-env | Invoke-Expression  # Windows PowerShell

# Rebuild
docker build -t via-tabloid-spring-boot:latest -f via-tabloid-spring-boot/Dockerfile .

# Restart the deployment to pick up the new image
kubectl rollout restart deployment/via-tabloid-spring-boot
```

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

- **CI** (all branches): runs Maven tests, builds backend and frontend Docker images
- **CD** (main branch only): starts Minikube, loads images, applies `k8s/` manifests, verifies rollout

### Required GitHub Secrets

`k8s/secrets.env` is git-ignored and generated at deploy time from repository secrets. Add these in **Settings → Secrets and variables → Actions**:

| Secret name | Value |
|-------------|-------|
| `DB_USERNAME` | `postgres` |
| `DB_PASSWORD` | `[your password as your wish]` |
| `DB_URL_DOCKER` | `jdbc:postgresql://postgres:5432/via_tabloid` |

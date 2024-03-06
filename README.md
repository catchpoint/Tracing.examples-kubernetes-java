# Catchpoint Tracing Examples - Kubernetes/Java

## Pre-Requirements
1. **JDK** (`1.8+`)
2. **Maven** (`3.x`)
3. Please make sure **Docker Desktop** is installed and **Kubernetes** is activated. 
   Read [this](https://birthday.play-with-docker.com/kubernetes-docker-desktop) for details.
4. Please make sure **Kubernetes CLI** (`kubernetes-cli`/`kubectl`) is installed. 
   See [here](https://kubernetes.io/docs/tasks/tools/) for details.
   
## Setup with OpenTelemetry From the Beginning
This setup shows how you can install OpenTelemetry resources into Kubernetes first and deploy application later then.
So deployed applications will be auto instrumented.

### Setup OpenTelemetry Resources in Kubernetes
1. Replace `<CATCHPOINT-TRACING-API-KEY>` with your Catchpoint Tracing API key in `otel/collector.yaml` file.
2. Then run the setup script which installs **OpenTelemetry Operator** and **OpenTelemetry Instrumentation CR** (Custom Resource) into Kubernetes:
   ```bash
   ./otel/setup-otel.sh
   ```

### Deploy the Applications into Kubernetes with OTEL
1. Run the deployment script which deploys database and applications **with** OpenTelemetry configuration into Kubernetes:
	```bash
	./apps/deploy-apps-with-otel-conf.sh
	```
2. Wait until all services are activated.
3. Go to http://localhost:30000.

## Install OpenTelemetry After Setup

### Deploy the Applications into Kubernetes without OTEL
1. Run the deployment script which deploys database and applications **without** OpenTelemetry configuration into Kubernetes:
   ```bash
   ./apps/deploy-apps.sh
   ```
2. Wait until all services are activated.
3. Go to http://localhost:30000.

### Setup OpenTelemetry Resources in Kubernetes
1. Replace `<CATCHPOINT-TRACING-API-KEY>` with your Catchpoint Tracing API key in `otel/collector.yaml` file.
2. Then run the setup script which installs **OpenTelemetry Operator** and **OpenTelemetry Instrumentation CR** (Custom Resource) into Kubernetes:
   ```bash
   ./otel/setup-otel.sh
   ```
   
### Patch Services to be Traced by OpenTelemetry
1. Run the patch script which updates application deployments by adding **OpenTelemetry Auto Instrumentation Annotation** (`instrumentation.opentelemetry.io/inject-java`) to the services to be traced:
   ```bash
   ./apps/patch-apps-with-otel-conf.sh
   ```
2. Wait until all services are restarted.
3. Go to http://localhost:30000.

## Destroy

### Destroy the Deployment of Applications
1. Run the application destroy script which deletes applications and database deployed to Kubernetes:
   ```bash
   ./apps/destroy-apps.sh
   ```
2. Wait until all resources are deleted.

### Destroy the Resources of OpenTelemetry
1. Run the OpenTelemetry destroy script for deleting the OpenTelemetry resources (operator and instrumentation CR), 
   ```bash
   ./otel/destroy-otel.sh
   ```
2. Wait until all resources are deleted.

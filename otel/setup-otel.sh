#!/bin/bash

function check_command() {
    if [ $? -ne 0 ]; then
        echo: "Last command failed"
        exit 1
    fi
}

function apply_manifest() {
    echo "Applying manifest $1 ..."
    kubectl apply -f $1
}

function check_service_status() {
    SERVICE_PREFIX=$1
    EXPECTED_COUNT=$2
    SERVICES=$(kubectl get pods --all-namespaces -o json | jq -r --arg prefix "$SERVICE_PREFIX" '.items[] | select(.metadata.name | startswith($prefix)) | select(.status.phase == "Running") | "\(.metadata.namespace) \(.metadata.name) \(.status.phase)"')
    SERVICE_COUNT=$(echo "$SERVICES" | wc -l)
    if [ $SERVICE_COUNT -ne $EXPECTED_COUNT ]; then
        SERVICES=""
    fi
    echo "$SERVICES"
}


echo "Installing certificate manager ..."
echo "================================================================================"
while ! kubectl get namespace cert-manager &> /dev/null;
do
    apply_manifest https://github.com/cert-manager/cert-manager/releases/download/v1.11.0/cert-manager.yaml
    sleep 10
done
check_command
SERVICES=$(check_service_status "cert-manager" 3)
while [ -z "$SERVICES" ];
do
    echo "Waiting for cert-manager services to be ready ..."
    SERVICES=$(check_service_status "cert-manager" 3)
    sleep 15
done
echo "================================================================================"
echo "Installed certificate manager"

echo "Installing OpenTelemetry operator ..."
echo "================================================================================"
while ! kubectl get namespace opentelemetry-operator-system &> /dev/null;
do
    apply_manifest https://github.com/open-telemetry/opentelemetry-operator/releases/latest/download/opentelemetry-operator.yaml
    sleep 10
done
check_command
SERVICES=$(check_command "opentelemetry-operator-controller" 1)
while [ -z "$SERVICES" ];
do
    echo "Waiting for OpenTelemetry operator services to be ready ..."
    SERVICES=$(check_service_status "opentelemetry-operator-controller" 1)
    sleep 15
done
echo "================================================================================"
echo "Installed OpenTelemetry operator"
kubectl config set-context --current --namespace=opentelemetry-operator-system
check_command

echo "Creating OpenTelemetry collector ..."
echo "================================================================================"
while ! kubectl get opentelemetrycollector otel -o jsonpath='{.metadata.name}' &> /dev/null;
do
    apply_manifest otel/collector.yaml
    sleep 15
done
check_command
SERVICES=$(check_service_status "otel-collector" 1)
while [ -z "$SERVICES" ];
do
    echo "Waiting for OpenTelemetry collector services to be ready ..."
    SERVICES=$(check_service_status "otel-collector" 1)
    sleep 15
done
echo "================================================================================"
echo "Created OpenTelemetry collector"

echo "Creating OpenTelemetry instrumentation ..."
echo "================================================================================"
while ! kubectl get instrumentation otel-instrumentation -o jsonpath='{.metadata.name}' &> /dev/null;
do
    apply_manifest otel/instrumentation.yaml
    sleep 15
done
check_command
echo "================================================================================"
echo "Created OpenTelemetry instrumentation"

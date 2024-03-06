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

echo "Installing certificate manager ..."
echo "================================================================================"
while ! kubectl get namespace cert-manager &> /dev/null;
do
    apply_manifest https://github.com/cert-manager/cert-manager/releases/download/v1.11.0/cert-manager.yaml
    sleep 5
done
check_command
echo "================================================================================"
echo "Installed certificate manager"

echo "Installing OpenTelemetry operator ..."
echo "================================================================================"
while ! kubectl get namespace opentelemetry-operator-system &> /dev/null;
do
  apply_manifest https://github.com/open-telemetry/opentelemetry-operator/releases/latest/download/opentelemetry-operator.yaml
  sleep 5
done
check_command
echo "================================================================================"
echo "Installed OpenTelemetry operator"
kubectl config set-context --current --namespace=opentelemetry-operator-system
check_command

echo "Creating OpenTelemetry collector ..."
echo "================================================================================"
while ! kubectl get opentelemetrycollector otel -o jsonpath='{.metadata.name}' &> /dev/null;
do
    apply_manifest otel/collector.yaml
    sleep 5
done
check_command
echo "================================================================================"
echo "Created OpenTelemetry collector"

echo "Creating OpenTelemetry instrumentation ..."
echo "================================================================================"
while ! kubectl get instrumentation otel-instrumentation -o jsonpath='{.metadata.name}' &> /dev/null;
do
    apply_manifest otel/instrumentation.yaml
    sleep 5
done
check_command
echo "================================================================================"
echo "Created OpenTelemetry instrumentation"

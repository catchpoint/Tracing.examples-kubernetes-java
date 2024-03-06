#!/bin/bash

echo "Patching services with OTEL configurations ..."
echo "================================================================================"
kubectl config set-context --current --namespace=todo-apps
OTEL_CONF='{"spec": {"template": {"metadata": {"annotations": {"instrumentation.opentelemetry.io/inject-java": "opentelemetry-operator-system/otel-instrumentation"}}}}}'
kubectl patch deployment.apps/todo-service -p "$OTEL_CONF"
kubectl patch deployment.apps/user-service -p "$OTEL_CONF"
echo "================================================================================"
echo "Patched services with OTEL configurations"

echo "Waiting for the services to be ready ..."
sleep 30
echo "Services are ready now. Visit http://localhost:30000"

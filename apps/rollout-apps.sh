#!/bin/bash

echo "Rolling out services ..."
echo "================================================================================"
kubectl rollout restart deployment user-service --namespace=todo-apps
kubectl rollout restart deployment todo-service --namespace=todo-apps
echo "================================================================================"
echo "Rolled out services"

echo "Waiting for the services to be ready ..."
sleep 30
echo "Services are ready now. Visit http://localhost:30000"

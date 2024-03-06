#!/bin/bash

echo "Building project ..."
echo "================================================================================"
mvn clean install -DskipTests
echo "================================================================================"
echo "Built project"

echo "Creating Docker image ..."
echo "================================================================================"
docker build -f apps/Dockerfile -t todo-app .
echo "================================================================================"
echo "Created Docker image"

echo "Creating and activating application namespace ..."
echo "================================================================================"
kubectl create namespace todo-apps
kubectl config set-context --current --namespace=todo-apps
echo "================================================================================"
echo "Created and activating application namespace"

echo "Creating database ..."
echo "================================================================================"
kubectl apply -f apps/database.yaml
echo "================================================================================"
echo "Created database"

echo "Creating deployment ..."
echo "================================================================================"
kubectl apply -f apps/deployment.yaml
echo "================================================================================"
echo "Created deployment"

echo "Waiting for the services to be ready ..."
sleep 30
echo "Services are ready now. Visit http://localhost:30000"

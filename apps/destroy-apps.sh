#!/bin/bash

echo "Deleting services ..."
echo "================================================================================"
kubectl delete service user-svc
kubectl delete service todo-svc
echo "================================================================================"
echo "Deleted services"

echo "Deleting databases ..."
echo "================================================================================"
kubectl delete deployment,svc mysql
kubectl delete pvc mysql-pv-claim
kubectl delete pv mysql-pv-volume
echo "================================================================================"
echo "Deleted databases"

echo "Deleting deployments ..."
echo "================================================================================"
kubectl delete deployment todo-service
kubectl delete deployment user-service
echo "================================================================================"
echo "Deleted deployments"

echo "Deleting namespace ..."
echo "================================================================================"
kubectl delete namespace todo-apps
echo "================================================================================"
echo "Deleted namespace"

echo "All resources have been deleted"

#!/bin/bash

echo "Deleting namespace ..."
echo "================================================================================"
kubectl delete namespace opentelemetry-operator-system
echo "================================================================================"
echo "Deleted namespace"

echo "Deleting certificate manager ..."
echo "================================================================================"
kubectl delete namespace cert-manager
echo "================================================================================"
echo "Deleted certificate manager"

echo "All resources have been deleted"

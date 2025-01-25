#!/bin/bash

kubectl delete deployments,services,pods --all
kubectl delete pvc --all
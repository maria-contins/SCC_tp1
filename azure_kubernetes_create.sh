#!/bin/bash
source kubernetes_configs.azure 
az group create --name $RESOURCE_GROUP --location $LOCATION

SUBSCRIPTION_ID=$(echo $(az account subscription list) | jq '.[0].id')
SERVICE_PRINCIPAL=$(az ad sp create-for-rbac --name $SP_NAME --role="Contributor" --scope "/subscriptions/2f1aa27f-55aa-46e7-b28b-9a14ec7c8934")
APP_ID=$(echo $SERVICE_PRINCIPAL | jq -r .appId)
echo "-----------" 
echo $APP_ID
echo "___________"
echo $SERVICE_PRINCIPAL
echo "___________"
PASSWORD=$(echo $SERVICE_PRINCIPAL | jq -r .password)

az aks create --resource-group $RESOURCE_GROUP \
--name $CLUSTER_NAME --node-vm-size $VM_SIZE \
--node-count $NODE_COUNT \
--generate-ssh-keys --service-principal $APP_ID --client-secret $PASSWORD

az aks get-credentials --resource-group $RESOURCE_GROUP --name $CLUSTER_NAME

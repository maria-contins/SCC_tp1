#!/bin/bash

source test_config.azure

#Create resource group
az group create --name $RESOURCE_GROUP --location $LOCATION

#Deploy the container
az container create --resource-group $RESOURCE_GROUP --name $CONTAINER_NAME --image $TEST_IMAGE --dns-name-label $CONTAINER_NAME --environment-variables APPLICATION_URL=$APPLICATION_URL

#Attach container output to console to the the url for transfer.sh
az container attach --resource-group $RESOURCE_GROUP --name $CONTAINER_NAME



#!/bin/bash

bash ./build_and_push_backend.sh

bash ./kubernetes_delete_deployment.sh

bash ./kubernetes_deploy.sh
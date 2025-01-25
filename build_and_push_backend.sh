#!/bin/bash

mvn clean compile package
docker build -t mariacontins/scc57503-backend .
docker push mariacontins/scc57503-backend

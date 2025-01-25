#!/bin/bash

mvn clean compile assembly:single
docker build -t mariacontins/scc57503-cronfun .
docker push mariacontins/scc57503-cronfun

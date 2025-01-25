FROM node:current-alpine

WORKDIR /usr/app


RUN apk add bash zip curl git

#Install artillery
RUN npm install -g artillery 

RUN npm install @faker-js/faker --save-dev

RUN npm install -g node-fetch --save

RUN npm install -g https://github.com/preguica/artillery-plugin-metrics-by-endpoint.git

#ENV APPLICATION_URL "http://4.245.106.32/scc-tp1-1.0/rest"

COPY ./tests /usr/app/tests

COPY ./test_run.sh /usr/app/

CMD ["/bin/bash", "-c", "bash ./test_run.sh"]
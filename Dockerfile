FROM tomcat:10.0-jdk17-openjdk

WORKDIR /usr/local/tomcat

COPY ../target/scc-tp1-1.0.war /usr/local/tomcat/webapps/

EXPOSE 8080

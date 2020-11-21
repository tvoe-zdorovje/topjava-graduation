FROM tomcat:9.0.40-jdk15-openjdk-slim-buster

COPY target/*.war webapps/

EXPOSE 8080



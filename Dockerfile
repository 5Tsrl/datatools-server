FROM java:openjdk-8-jre-alpine

EXPOSE 4000
ENV TZ Europe/Rome
ENV JAVA_OPTS -Xms2048m -Xmx5120m -Djava.security.egd=file:/dev/./urandom -Dlogback.configurationFile=./logback.xml
ENV ENV_FILE configurations/default/env.yml
ENV SERVER_FILE configurations/default/server.yml
WORKDIR /cafe
# add the jar directly
COPY target/cafe-server.jar .
COPY configurations ./configurations
COPY src/main/resources/logback.xml .

CMD java ${JAVA_OPTS} -jar cafe-server.jar ${ENV_FILE} ${SERVER_FILE}

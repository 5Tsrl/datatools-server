FROM java:openjdk-8-jre-alpine

EXPOSE 4000

WORKDIR /cafe
# add the jar directly 
COPY target/cafe-server.jar .
COPY configurations ./configurations

CMD java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar cafe-server.jar


FROM java:openjdk-8-jre-alpine

WORKDIR /cafe
# add the jar directly 
#COPY target/cafe-server.jar /cafe/cafe-server.jar
#COPY configurations /cafe/configurations
COPY target/cafe-server.jar .
COPY configurations ./configurations

#VOLUME /var/cafe

EXPOSE 4000

CMD java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar cafe-server.jar


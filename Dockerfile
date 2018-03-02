FROM java:openjdk-8-jre-alpine

# add the jar directly 
COPY target/cafe-server.jar /cafe/cafe-server.jar
COPY configurations /cafe/configurations

VOLUME /var/cafe

EXPOSE 4000

CMD java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /cafe/cafe-server.jar


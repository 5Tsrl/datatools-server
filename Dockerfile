FROM java:openjdk-8-jre-alpine

LABEL "5Tsrl.project_name"="cafe"

ENV SLEEP_TIME 2

# add directly the jar
COPY target/cafe-server.jar /cafe-server.jar
COPY configurations /configurations

RUN sh -c 'touch /datatools-server.jar'
RUN sh -c 'mkdir /conf'

#VOLUME /conf
VOLUME /var/cafe

EXPOSE 4000

CMD echo "The application will start in ${SLEEP_TIME}s..." && \
    sleep ${SLEEP_TIME} && \
    java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /cafe-server.jar
    
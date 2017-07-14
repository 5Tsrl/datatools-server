FROM java:openjdk-8-jre-alpine

LABEL "5Tsrl.project_name"="cafe"

ENV SLEEP_TIME 3

# add directly the jar
#ADD target/dt-*.jar /datatools-server.jar
COPY target/dt-3b1a6e5.jar /datatools-server.jar
COPY configurations /configurations

RUN sh -c 'touch /datatools-server.jar'
RUN sh -c 'mkdir /conf'

#VOLUME /conf
VOLUME /var/cafe

EXPOSE 4000

CMD echo "The application will start in ${SLEEP_TIME}s..." && \
    sleep ${SLEEP_TIME} && \
    #java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /datatools-server.jar /conf/env.yml /conf/server.yml
    java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /datatools-server.jar 
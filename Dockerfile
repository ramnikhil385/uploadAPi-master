FROM docker.repo1.uhc.com/doc360/java8

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

RUN apt-get update && apt-get install lsof zip -y
ADD target/Doc360-upload-Microservice-0.0.1-SNAPSHOT.jar /opt/app/
ADD entrypoint.sh /opt/app/entrypoint.sh
ADD cacerts /opt/app/cacerts

EXPOSE 9090 9091 9095 9096

LABEL app=doc360_upload_doc_Microservice

ENTRYPOINT ["/bin/bash", "/opt/app/entrypoint.sh"]

FROM ubuntu:18.04

RUN apt-get update && apt-get install -y \
  autoconf \
  automake \
  build-essential \
  curl \
  git \
  libtool \
  libpcre3-dev \
  libssl-dev \
  make \
  software-properties-common \
  zip \
  zlib1g \
  zlib1g-dev

RUN add-apt-repository ppa:openjdk-r/ppa && \
  apt-get update && apt-get install -y \
  openjdk-8-jdk \
  openjdk-8-jre

VOLUME /tmp

ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
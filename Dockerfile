FROM java:8
RUN mkdir /data
COPY target/lastfm-spotify-migrator-*.jar /jar/migrator.jar

EXPOSE 8081
VOLUME /data

WORKDIR /data
CMD ["java","-jar","/jar/migrator.jar"]

FROM adoptopenjdk:14.0.2_12-jre-hotspot-bionic
RUN adduser --system --group app
USER app:app
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

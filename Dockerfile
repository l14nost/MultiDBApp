FROM openjdk:17
COPY target/MultiDBApp.jar MultiDBApp.jar
ENTRYPOINT ["java","-jar", "MultiDBApp.jar"]
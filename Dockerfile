FROM openjdk
ADD app.jar /app.jar
ENTRYPOINT ["java","--spring.profiles.active=dev -jar","/app.jar"]

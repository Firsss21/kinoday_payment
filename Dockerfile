FROM openjdk
ADD app.jar /app.jar
ENTRYPOINT ["java","-jar -Dspring.profiles.active=dev ","/app.jar"]

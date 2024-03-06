FROM openjdk:8

RUN mkdir -p /app
COPY ../target/todo-app.jar /app/todo-app.jar
WORKDIR /app

EXPOSE 5000 5000

ENV AWS_REGION=us-west-2

CMD ["java", "-jar", "todo-app.jar"]

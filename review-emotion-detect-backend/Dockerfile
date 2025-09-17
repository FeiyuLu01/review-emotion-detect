FROM openjdk:11-jre-slim

WORKDIR /app

# 复制Maven构建的jar文件
COPY target/review-emotion-detect-backend-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080

# 设置JVM参数
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
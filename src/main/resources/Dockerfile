# 第一阶段，构建自定义JRE
FROM eclipse-temurin:17-jdk-alpine AS jre-builder

# 安装binutils，jlink所需
RUN apk update && apk add binutils

# 构建小型JRE镜像
RUN $JAVA_HOME/bin/jlink \
         --verbose \
         --add-modules ALL-MODULE-PATH \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /optimized-jdk-17

# 第二阶段，使用自定义JRE并构建应用镜像
FROM alpine:latest
ENV JAVA_HOME=/opt/jdk/jdk-17
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# 从基础镜像中复制JRE
COPY --from=jre-builder /optimized-jdk-17 $JAVA_HOME

# 添加应用用户
ARG APPLICATION_USER=spring

# 创建一个用户来运行应用，不以root用户运行
RUN addgroup --system $APPLICATION_USER && adduser --system $APPLICATION_USER --ingroup $APPLICATION_USER

# 创建应用目录
RUN mkdir /app && chown -R $APPLICATION_USER /app
RUN mkdir /app/config && chown -R $APPLICATION_USER /app/config

USER $APPLICATION_USER
COPY --chown=$APPLICATION_USER jd_server-1.0-SNAPSHOT.jar /app/app.jar
COPY --chown=$APPLICATION_USER application.yml /app/config/application.yml

WORKDIR /app

USER $APPLICATION_USER

EXPOSE 5607
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=file:/app/config/application.yml"]
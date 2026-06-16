# ============================================
# 家政上门服务系统 - 多阶段 Dockerfile
# 支持通过 ARG MODULE 构建任意微服务模块
#
# 构建示例:
#   docker build --build-arg MODULE=homeservice-auth -t homeservice/auth:latest .
#   docker build --build-arg MODULE=homeservice-order -t homeservice/order:latest .
# ============================================

# ============ 构建阶段 ============
FROM maven:3.9-eclipse-temurin-17 AS build
ARG MODULE

WORKDIR /build

# 1. 先复制父 POM 和所有模块 POM（利用 Docker 层缓存加速依赖下载）
COPY pom.xml .
COPY homeservice-common/pom.xml homeservice-common/
COPY homeservice-gateway/pom.xml homeservice-gateway/
COPY homeservice-auth/pom.xml homeservice-auth/
COPY homeservice-user/pom.xml homeservice-user/
COPY homeservice-order/pom.xml homeservice-order/
COPY homeservice-service/pom.xml homeservice-service/
COPY homeservice-payment/pom.xml homeservice-payment/
COPY homeservice-review/pom.xml homeservice-review/
COPY homeservice-marketing/pom.xml homeservice-marketing/
COPY homeservice-statistics/pom.xml homeservice-statistics/
COPY homeservice-dispatch/pom.xml homeservice-dispatch/
COPY homeservice-location/pom.xml homeservice-location/
COPY homeservice-notify/pom.xml homeservice-notify/
COPY homeservice-finance/pom.xml homeservice-finance/

# 2. 复制公共模块源码（所有模块都依赖它）
COPY homeservice-common/src homeservice-common/src/

# 3. 预下载依赖（缓存层 — 源码未变时跳过）
RUN mvn dependency:go-offline -pl ${MODULE} -am -DskipTests -q || true

# 4. 复制全部源码
COPY . .

# 5. 编译指定模块（跳过测试）
RUN mvn clean package -pl ${MODULE} -am -DskipTests -q

# ============ 运行阶段 ============
FROM eclipse-temurin:17-jre-alpine
ARG MODULE

# 安装运行时依赖
RUN apk add --no-cache curl tzdata fontconfig

# 设置时区
ENV TZ=Asia/Shanghai
ENV LANG=zh_CN.UTF-8

# 创建非 root 用户运行服务
RUN addgroup -g 1000 appgroup && \
    adduser -u 1000 -G appgroup -D appuser

# 复制构建产物
COPY --from=build /build/${MODULE}/target/*.jar /app/app.jar

# 设置文件权限
RUN chown -R appuser:appgroup /app

# 切换到非 root 用户
USER appuser

# 工作目录
WORKDIR /app

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --retries=5 --start-period=90s \
    CMD curl -s -f http://localhost:${SERVER_PORT:-8080}/actuator/health || exit 1

# 启动命令 — 支持 JAVA_OPTS 环境变量自定义 JVM 参数
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS:--Xms256m -Xmx512m} -jar /app/app.jar"]

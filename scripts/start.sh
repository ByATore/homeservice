#!/bin/bash
# ============================================
# 家政上门服务系统 - 一键启动脚本（容器化版）
# 启动所有基础设施和微服务容器
# 用法: bash scripts/start.sh [选项]
#   -b, --build  启动前先构建镜像
#   -d, --detach 后台运行（默认）
#   --no-deps    不启动依赖（仅微服务）
#   --infra-only 仅启动基础设施
# ============================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# 颜色
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log_info()  { echo -e "${GREEN}[INFO]${NC} $1"; }
log_warn()  { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }
log_step()  { echo -e "${BLUE}[STEP]${NC} $1"; }

BUILD_FLAG=""
COMPOSE_ARGS="--detach"
INFRA_ONLY=false

while [[ $# -gt 0 ]]; do
    case $1 in
        -b|--build)
            BUILD_FLAG="--build"
            shift
            ;;
        --no-detach)
            COMPOSE_ARGS=""
            shift
            ;;
        --infra-only)
            INFRA_ONLY=true
            shift
            ;;
        *)
            shift
            ;;
    esac
done

cd "${PROJECT_DIR}"

# ============================================
# 1. 构建镜像（可选）
# ============================================
if [ -n "${BUILD_FLAG}" ]; then
    log_step "构建微服务镜像..."
    bash scripts/build-images.sh
fi

# ============================================
# 2. 启动基础设施
# ============================================
log_step "启动基础设施容器 (MySQL, Redis, Nacos, RabbitMQ, Seata)..."
docker-compose up -d mysql redis nacos rabbitmq seata-server

log_info "等待基础设施就绪..."
log_info "  MySQL     -> localhost:3306"
log_info "  Redis     -> localhost:6379"
log_info "  Nacos     -> http://localhost:8848/nacos (nacos/nacos)"
log_info "  RabbitMQ  -> http://localhost:15672 (guest/guest)"
log_info "  Seata     -> localhost:8091"

# 等待关键服务健康
log_info "等待 MySQL 就绪..."
docker-compose wait mysql 2>/dev/null || true

log_info "等待 Nacos 就绪..."
for i in $(seq 1 30); do
    if curl -s -o /dev/null -w "%{http_code}" -u nacos:nacos "http://localhost:8848/nacos/v1/console/health/readiness" 2>/dev/null | grep -q "200"; then
        log_info "Nacos 就绪！"
        break
    fi
    sleep 3
done

log_info "运行 Nacos 配置初始化..."
docker-compose up -d nacos-init
docker-compose wait nacos-init 2>/dev/null || true

log_info "等待 RabbitMQ 就绪..."
docker-compose wait rabbitmq 2>/dev/null || true

log_info "等待 Seata 就绪..."
docker-compose wait seata-server 2>/dev/null || true

if [ "${INFRA_ONLY}" = true ]; then
    log_info "基础设施启动完成！（跳过微服务）"
    exit 0
fi

# ============================================
# 3. 启动所有微服务
# ============================================
log_step "启动所有微服务容器..."
docker-compose up ${COMPOSE_ARGS} ${BUILD_FLAG}

# ============================================
# 4. 启动完成
# ============================================
echo ""
log_info "=============================================="
log_info "  家政上门服务系统启动完成！"
log_info "=============================================="
log_info ""
log_info "  核心入口:"
log_info "    Gateway:  http://localhost:8083"
log_info ""
log_info "  基础设施:"
log_info "    Nacos:    http://localhost:8848/nacos  (nacos/nacos)"
log_info "    RabbitMQ: http://localhost:15672       (guest/guest)"
log_info "    Seata:    http://localhost:7091        (seata/seata)"
log_info ""
log_info "  查看日志: docker-compose logs -f [服务名]"
log_info "  查看状态: docker-compose ps"
log_info "  停止服务: bash scripts/stop.sh"
log_info ""

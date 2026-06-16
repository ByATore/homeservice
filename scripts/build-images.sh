#!/bin/bash
# ============================================
# 家政上门服务系统 - Docker 镜像构建脚本
# 构建所有微服务 Docker 镜像
# 用法: bash scripts/build-images.sh [模块名]
#   - 不带参数: 构建所有 13 个微服务镜像
#   - 带模块名: 只构建指定模块 (如 homeservice-auth)
# ============================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
SERVICE_DIR="${PROJECT_DIR}/homeservice"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log_info()  { echo -e "${GREEN}[INFO]${NC} $1"; }
log_warn()  { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }
log_step()  { echo -e "${BLUE}[STEP]${NC} $1"; }

# 所有微服务模块列表
MODULES=(
    "homeservice-auth"
    "homeservice-user"
    "homeservice-gateway"
    "homeservice-service"
    "homeservice-payment"
    "homeservice-review"
    "homeservice-marketing"
    "homeservice-statistics"
    "homeservice-location"
    "homeservice-notify"
    "homeservice-order"
    "homeservice-dispatch"
    "homeservice-finance"
)

IMAGE_TAG="${IMAGE_TAG:-latest}"

# 如果指定了模块名则只构建该模块
if [ -n "$1" ]; then
    MODULES=("$1")
    log_info "仅构建模块: $1"
fi

log_step "开始构建 ${#MODULES[@]} 个微服务 Docker 镜像..."
log_info "镜像标签: ${IMAGE_TAG}"
echo ""

SUCCESS_COUNT=0
FAIL_COUNT=0
FAILED_MODULES=()

cd "${SERVICE_DIR}"

for module in "${MODULES[@]}"; do
    image_name="homeservice/${module}:${IMAGE_TAG}"

    log_step "构建 ${module} -> ${image_name}"

    if docker build \
        --build-arg MODULE="${module}" \
        -t "${image_name}" \
        -f Dockerfile \
        . ; then
        log_info "  ${module} 构建成功 ✓"
        ((SUCCESS_COUNT++))
    else
        log_error "  ${module} 构建失败 ✗"
        ((FAIL_COUNT++))
        FAILED_MODULES+=("${module}")
    fi
    echo ""
done

# 汇总
echo ""
log_info "============================================"
log_info "  构建完成: 成功 ${SUCCESS_COUNT}/${#MODULES[@]}, 失败 ${FAIL_COUNT}"
log_info "============================================"

if [ ${FAIL_COUNT} -gt 0 ]; then
    log_error "失败的模块: ${FAILED_MODULES[*]}"
    exit 1
fi

# 显示镜像列表
log_info ""
log_info "已构建的镜像:"
docker images | grep "homeservice/" | head -20

log_info ""
log_info "运行以下命令启动所有服务:"
log_info "  cd ${PROJECT_DIR} && docker-compose up -d"

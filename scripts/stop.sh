#!/bin/bash
# ============================================
# 家政上门服务系统 - 停止脚本（容器化版）
# 停止并移除所有服务容器
# 用法: bash scripts/stop.sh [选项]
#   --clean   同时删除数据卷（慎用！）
# ============================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

log_info()  { echo -e "${GREEN}[INFO]${NC} $1"; }
log_warn()  { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

cd "${PROJECT_DIR}"

CLEAN_VOLUMES=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --clean)
            CLEAN_VOLUMES=true
            shift
            ;;
        *)
            shift
            ;;
    esac
done

log_info "停止所有服务容器..."
docker-compose down --remove-orphans

if [ "${CLEAN_VOLUMES}" = true ]; then
    log_warn "即将删除所有数据卷..."
    read -p "确认删除所有数据卷？此操作不可恢复！(y/N): " confirm
    if [ "$confirm" = "y" ] || [ "$confirm" = "Y" ]; then
        docker-compose down -v --remove-orphans
        log_info "数据卷已删除"
    else
        log_info "取消删除数据卷"
    fi
fi

log_info "所有服务已停止！"
log_info ""
log_info "重新启动: bash scripts/start.sh"

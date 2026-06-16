#!/bin/sh
# ============================================
# Nacos 初始化脚本
# 在 Nacos 启动后导入配置
# ============================================

NACOS_URL="http://homeservice-nacos:8848"
NACOS_USER="nacos"
NACOS_PASS="nacos"
NAMESPACE="homeservice"
NAMESPACE_ID=""

echo "=== 等待 Nacos 就绪 ==="
until curl -s -o /dev/null -w "%{http_code}" -u ${NACOS_USER}:${NACOS_PASS} "${NACOS_URL}/nacos/v1/console/health/readiness" | grep -q "200"; do
    echo "等待 Nacos 启动..."
    sleep 3
done
echo "Nacos 已就绪！"

# 登录获取 accessToken
echo ""
echo "=== 登录 Nacos 获取 Token ==="
LOGIN_RESP=$(curl -s -X POST \
    "${NACOS_URL}/nacos/v1/auth/login" \
    -d "username=${NACOS_USER}" \
    -d "password=${NACOS_PASS}")
ACCESS_TOKEN=$(echo "$LOGIN_RESP" | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)
echo "Token: ${ACCESS_TOKEN}"

if [ -z "$ACCESS_TOKEN" ]; then
    echo "错误: 无法获取 accessToken，登录响应: ${LOGIN_RESP}"
    exit 1
fi

AUTH_PARAM="accessToken=${ACCESS_TOKEN}"

# ============================================
# 1. 创建命名空间 homeservice（如果不存在）
# ============================================
echo ""
echo "=== 创建命名空间 ==="
NAMESPACE_CHECK=$(curl -s "${NACOS_URL}/nacos/v1/console/namespaces?${AUTH_PARAM}")
if echo "$NAMESPACE_CHECK" | grep -q "\"namespace\":\"${NAMESPACE}\""; then
    NAMESPACE_ID=$(echo "$NAMESPACE_CHECK" | grep -o "\"namespace\":\"${NAMESPACE}\"[^}]*\"namespaceId\":\"[^\"]*\"" | grep -o '"namespaceId":"[^"]*"' | head -1 | cut -d'"' -f4)
    echo "命名空间已存在: ${NAMESPACE} (ID: ${NAMESPACE_ID})"
else
    RESULT=$(curl -s -X POST \
        "${NACOS_URL}/nacos/v1/console/namespaces?${AUTH_PARAM}" \
        -d "customNamespaceId=${NAMESPACE}" \
        -d "namespaceName=${NAMESPACE}" \
        -d "namespaceDesc=家政服务系统")
    echo "创建命名空间结果: ${RESULT}"
    NAMESPACE_ID="${NAMESPACE}"
fi

# ============================================
# 2. 导入 Seata 配置到 Nacos（逐条导入，每条属性一个 config）
# ============================================
echo ""
echo "=== 导入 Seata 配置 ==="

if [ -f /nacos-configs/seata.properties ]; then
    SEATA_COUNT=0
    while IFS='=' read -r key value; do
        # 跳过注释行和空行
        case "$key" in
            ""|\#*) continue ;;
        esac
        # 去除首尾空格
        key=$(echo "$key" | xargs)
        value=$(echo "$value" | xargs)

        RESULT=$(curl -s -X POST \
            "${NACOS_URL}/nacos/v1/cs/configs?${AUTH_PARAM}" \
            -d "tenant=${NAMESPACE}" \
            -d "dataId=${key}" \
            -d "group=SEATA_GROUP" \
            -d "content=${value}")

        if echo "$RESULT" | grep -q "true"; then
            SEATA_COUNT=$((SEATA_COUNT + 1))
        elif echo "$RESULT" | grep -q "config already exist"; then
            SEATA_COUNT=$((SEATA_COUNT + 1))
        fi
    done < /nacos-configs/seata.properties
    echo "Seata 配置导入完成，共 ${SEATA_COUNT} 条"
else
    echo "警告: seata.properties 文件不存在，跳过"
fi

# ============================================
# 3. 导入所有 Nacos 共享配置文件
# ============================================
echo ""
echo "=== 导入服务共享配置 ==="

for config_file in /nacos-configs/*.yaml; do
    if [ -f "$config_file" ]; then
        data_id=$(basename "$config_file")
        CONTENT=$(cat "$config_file")
        RESULT=$(curl -s -X POST \
            "${NACOS_URL}/nacos/v1/cs/configs?${AUTH_PARAM}" \
            -d "tenant=${NAMESPACE}" \
            -d "dataId=${data_id}" \
            -d "group=DEFAULT_GROUP" \
            -d "content=${CONTENT}")

        if echo "$RESULT" | grep -q "true"; then
            echo "  ${data_id} 已导入"
        elif echo "$RESULT" | grep -q "config already exist"; then
            echo "  ${data_id} 已存在，跳过"
        else
            echo "  ${data_id} 导入结果: ${RESULT}"
        fi
    fi
done

echo ""
echo "=== Nacos 初始化完成！ ==="

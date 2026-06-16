#!/bin/sh
# ============================================
# Nacos 配置初始化脚本
# 将 config/nacos-configs 下的配置文件导入 Nacos
# ============================================

NACOS_URL="http://nacos:8848"
NACOS_USERNAME="nacos"
NACOS_PASSWORD="nacos"
NAMESPACE="homeservice"

echo "=== Waiting for Nacos to be ready ==="
until curl -s -u ${NACOS_USERNAME}:${NACOS_PASSWORD} "${NACOS_URL}/nacos/v1/console/health/readiness" > /dev/null 2>&1; do
  echo "Waiting for Nacos..."
  sleep 5
done

echo "=== Nacos is ready, importing configurations ==="

# Import all YAML configs as common configs
for file in /nacos-configs/*.yaml; do
  filename=$(basename "$file")
  dataId="${filename}"
  echo "Importing ${dataId}..."
  
  content=$(cat "$file")
  
  curl -s -X POST \
    -u ${NACOS_USERNAME}:${NACOS_PASSWORD} \
    "${NACOS_URL}/nacos/v1/cs/configs" \
    -d "dataId=${dataId}" \
    -d "group=DEFAULT_GROUP" \
    -d "tenant=${NAMESPACE}" \
    -d "type=yaml" \
    --data-urlencode "content=${content}"
  
  if [ $? -eq 0 ]; then
    echo "  [OK] ${dataId} imported"
  else
    echo "  [FAIL] ${dataId} import failed"
  fi
done

# Import seata.properties
if [ -f /nacos-configs/seata.properties ]; then
  echo "Importing seata.properties..."
  content=$(cat /nacos-configs/seata.properties)
  
  curl -s -X POST \
    -u ${NACOS_USERNAME}:${NACOS_PASSWORD} \
    "${NACOS_URL}/nacos/v1/cs/configs" \
    -d "dataId=seata.properties" \
    -d "group=SEATA_GROUP" \
    -d "tenant=${NAMESPACE}" \
    -d "type=properties" \
    --data-urlencode "content=${content}"
  
  if [ $? -eq 0 ]; then
    echo "  [OK] seata.properties imported"
  else
    echo "  [FAIL] seata.properties import failed"
  fi
fi

echo "=== Nacos configuration import completed ==="
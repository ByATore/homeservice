@echo off
REM 启动缺失的 4 个服务

echo Starting location (8089)...
start "location-service" /MIN cmd /c "cd /d E:\CodeGit\homeservice\homeservice\homeservice-location && mvn spring-boot:run -q"

echo Starting notify (8090)...
start "notify-service" /MIN cmd /c "cd /d E:\CodeGit\homeservice\homeservice\homeservice-notify && mvn spring-boot:run -q"

echo Starting order (8092)...
start "order-service" /MIN cmd /c "cd /d E:\CodeGit\homeservice\homeservice\homeservice-order && mvn spring-boot:run -q"

echo Starting dispatch (8093)...
start "dispatch-service" /MIN cmd /c "cd /d E:\CodeGit\homeservice\homeservice\homeservice-dispatch && mvn spring-boot:run -q"

echo All 4 services starting in background windows...
pause

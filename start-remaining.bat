@echo off
start "location" /MIN cmd /c "cd /d E:\CodeGit\homeservice\homeservice\homeservice-location && mvn spring-boot:run -q"
start "notify" /MIN cmd /c "cd /d E:\CodeGit\homeservice\homeservice\homeservice-notify && mvn spring-boot:run -q"
start "order" /MIN cmd /c "cd /d E:\CodeGit\homeservice\homeservice\homeservice-order && mvn spring-boot:run -q"
start "dispatch" /MIN cmd /c "cd /d E:\CodeGit\homeservice\homeservice\homeservice-dispatch && mvn spring-boot:run -q"
echo Started 4 services
exit

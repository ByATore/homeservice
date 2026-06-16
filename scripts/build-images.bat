@echo off
REM ============================================
REM 家政上门服务系统 - Docker 镜像构建脚本 (Windows)
REM 用法: build-images.bat [模块名]
REM   - 不带参数: 构建所有 13 个微服务镜像
REM   - 带模块名: 只构建指定模块 (如 homeservice-auth)
REM ============================================

setlocal enabledelayedexpansion

set SCRIPT_DIR=%~dp0
set PROJECT_DIR=%SCRIPT_DIR%..
set SERVICE_DIR=%PROJECT_DIR%\homeservice

if "%IMAGE_TAG%"=="" set IMAGE_TAG=latest

if not "%1"=="" (
    call :build_module %1
    goto :summary
)

echo ============================================
echo  构建所有 13 个微服务 Docker 镜像
echo  标签: %IMAGE_TAG%
echo ============================================
echo.

set SUCCESS=0
set FAIL=0

call :build_module homeservice-auth
call :build_module homeservice-user
call :build_module homeservice-gateway
call :build_module homeservice-service
call :build_module homeservice-payment
call :build_module homeservice-review
call :build_module homeservice-marketing
call :build_module homeservice-statistics
call :build_module homeservice-location
call :build_module homeservice-notify
call :build_module homeservice-order
call :build_module homeservice-dispatch
call :build_module homeservice-finance

:summary
echo.
echo ============================================
echo  构建完成
echo ============================================
echo.
echo 运行以下命令启动所有服务:
echo   cd %PROJECT_DIR% ^&^& docker-compose up -d
goto :eof

:build_module
echo [BUILD] %~1 -^> homeservice/%~1:%IMAGE_TAG%
cd /d "%SERVICE_DIR%"
docker build --build-arg MODULE=%~1 -t homeservice/%~1:%IMAGE_TAG% -f Dockerfile .
if %ERRORLEVEL% equ 0 (
    echo   [OK] %~1 构建成功
    set /a SUCCESS+=1
) else (
    echo   [FAIL] %~1 构建失败
    set /a FAIL+=1
)
echo.
goto :eof

@echo off
setlocal
cd /d "%~dp0"

if exist "config.json" (
  for /f "usebackq delims=" %%A in (`powershell -NoProfile -Command "$c=Get-Content -Raw 'config.json'|ConvertFrom-Json; 'ANCHORBOT_CONVEX_DEPLOY_KEY='+$c.ANCHORBOT_CONVEX_DEPLOY_KEY; 'ANCHORBOT_CONVEX_HTTP_SECRET='+$c.ANCHORBOT_CONVEX_HTTP_SECRET"`) do set "%%A"
)

if "%ANCHORBOT_CONVEX_DEPLOY_KEY%"=="" set /p ANCHORBOT_CONVEX_DEPLOY_KEY=Convex deploy key: 
if "%ANCHORBOT_CONVEX_HTTP_SECRET%"=="" set /p ANCHORBOT_CONVEX_HTTP_SECRET=Convex HTTP secret: 

if "%ANCHORBOT_CONVEX_DEPLOY_KEY%"=="" echo Missing Convex deploy key.& pause& exit /b 1
if "%ANCHORBOT_CONVEX_HTTP_SECRET%"=="" echo Missing Convex HTTP secret.& pause& exit /b 1

set "CONVEX_DEPLOY_KEY=%ANCHORBOT_CONVEX_DEPLOY_KEY%"

if not exist "node_modules\convex" call npm install
if errorlevel 1 pause& exit /b 1

call npx convex env set ANCHORBOT_CONVEX_KEY "%ANCHORBOT_CONVEX_HTTP_SECRET%"
if errorlevel 1 pause& exit /b 1

call npx convex deploy
if errorlevel 1 pause& exit /b 1

echo Convex HTTPS routes deployed.
pause

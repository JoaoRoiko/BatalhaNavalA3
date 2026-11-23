@echo off
rem Executa o jogo com o console em UTF-8 (necessario para os emojis aparecerem)
chcp 65001 >nul <nul
cd /d "%~dp0"

if not exist out mkdir out

echo Compilando...
javac -encoding UTF-8 -d out -sourcepath src src\controller\JogoController.java >nul 2>&1
if errorlevel 1 (
    echo.
    echo Falha ao compilar. Confira se o JDK esta instalado ^(javac -version^).
    pause
    exit /b 1
)

java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -cp out controller.JogoController
pause

@echo off
setlocal enabledelayedexpansion

:: Function to check if Java 21 is installed on the system
java -version >nul 2>&1
if %errorlevel% equ 0 (
    for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
        set "jver=%%~g"
        for /f "delims=. tokens=1" %%v in ("!jver!") do (
            if "%%v"=="21" (
                echo System Java 21 detected. Using it...
                goto run_app
            )
        )
    )
)

:: Check if local JDK is already downloaded
set "LOCAL_JDK_DIR=%~dp0.jdk"
if exist "%LOCAL_JDK_DIR%" (
    echo Using local JDK 21...
    for /r "%LOCAL_JDK_DIR%" %%f in (javac.exe) do (
        if exist "%%f" (
            set "BIN_DIR=%%~dpf"
            :: Remove trailing backslash
            set "BIN_DIR=!BIN_DIR:~0,-1!"
            for %%p in ("!BIN_DIR!") do set "JAVA_HOME=%%~dpp"
            set "JAVA_HOME=!JAVA_HOME:~0,-1!"
            set "PATH=!BIN_DIR!;%PATH%"
            goto run_app
        )
    )
)

echo Java 21 not found. Downloading a portable JDK 21...
mkdir "%LOCAL_JDK_DIR%" >nul 2>&1

:: Adoptium JDK 21 URL for Windows x64
set "URL=https://api.adoptium.net/v3/binary/latest/21/ga/windows/x64/jdk/hotspot/normal/eclipse"
set "ZIP_FILE=%LOCAL_JDK_DIR%\jdk.zip"

echo Downloading JDK from: %URL%
powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object System.Net.WebClient).DownloadFile('%URL%', '%ZIP_FILE%')"

if not exist "%ZIP_FILE%" (
    echo Download failed. Please download and install JDK 21 manually.
    exit /b 1
)

echo Extracting JDK...
powershell -Command "Expand-Archive -Path '%ZIP_FILE%' -DestinationPath '%LOCAL_JDK_DIR%'"
del "%ZIP_FILE%"

for /r "%LOCAL_JDK_DIR%" %%f in (javac.exe) do (
    if exist "%%f" (
        set "BIN_DIR=%%~dpf"
        set "BIN_DIR=!BIN_DIR:~0,-1!"
        for %%p in ("!BIN_DIR!") do set "JAVA_HOME=%%~dpp"
        set "JAVA_HOME=!JAVA_HOME:~0,-1!"
        set "PATH=!BIN_DIR!;%PATH%"
        echo JDK 21 installed successfully in .jdk folder.
        goto run_app
    )
)

echo Failed to find bin folder after extraction.
exit /b 1

:run_app
echo Launching MP3 Player...
call mvnw.cmd javafx:run

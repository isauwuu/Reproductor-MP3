#!/bin/bash

# Function to check if a Java version is 21 or higher
is_java21_installed() {
    if type -p java > /dev/null; then
        version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
        # Get the major version (e.g. 21 from 21.0.1 or 21)
        major=$(echo "$version" | cut -d'.' -f1)
        if [ "$major" -eq 21 ]; then
            return 0
        fi
    fi
    return 1
}

# Create local JDK directory if it doesn't exist
LOCAL_JDK_DIR="$(pwd)/.jdk"
export JAVA_HOME=""

if [ -d "$LOCAL_JDK_DIR" ] && [ "$(ls -A "$LOCAL_JDK_DIR")" ]; then
    echo "Using local JDK 21..."
    # Find the bin directory inside the extracted JDK (could be inside a subfolder like jdk-21.x.x+x)
    JDK_BIN_DIR=$(find "$LOCAL_JDK_DIR" -maxdepth 3 -name "javac" -print -quit)
    if [ -n "$JDK_BIN_DIR" ]; then
        export JAVA_HOME="$(dirname "$(dirname "$JDK_BIN_DIR")")"
        export PATH="$JAVA_HOME/bin:$PATH"
    fi
elif is_java21_installed; then
    echo "System Java 21 detected. Using it..."
else
    echo "Java 21 not found. Downloading a portable JDK 21..."
    mkdir -p "$LOCAL_JDK_DIR"
    
    # Detect OS and Architecture
    OS="$(uname -s)"
    ARCH="$(uname -m)"
    
    URL=""
    if [ "$OS" = "Linux" ]; then
        if [ "$ARCH" = "x86_64" ]; then
            URL="https://api.adoptium.net/v3/binary/latest/21/ga/linux/x64/jdk/hotspot/normal/eclipse"
        elif [ "$ARCH" = "aarch64" ] || [ "$ARCH" = "arm64" ]; then
            URL="https://api.adoptium.net/v3/binary/latest/21/ga/linux/aarch64/jdk/hotspot/normal/eclipse"
        fi
    elif [ "$OS" = "Darwin" ]; then
        if [ "$ARCH" = "x86_64" ]; then
            URL="https://api.adoptium.net/v3/binary/latest/21/ga/mac/x64/jdk/hotspot/normal/eclipse"
        elif [ "$ARCH" = "aarch64" ] || [ "$ARCH" = "arm64" ]; then
            URL="https://api.adoptium.net/v3/binary/latest/21/ga/mac/aarch64/jdk/hotspot/normal/eclipse"
        fi
    fi
    
    if [ -z "$URL" ]; then
        echo "Unsupported OS or Architecture: $OS $ARCH. Please install JDK 21 manually."
        exit 1
    fi
    
    echo "Downloading JDK 21 from: $URL"
    TAR_FILE="$LOCAL_JDK_DIR/jdk.tar.gz"
    
    if type curl > /dev/null; then
        curl -L -o "$TAR_FILE" "$URL"
    elif type wget > /dev/null; then
        wget -O "$TAR_FILE" "$URL"
    else
        echo "Neither curl nor wget found. Please install curl or wget."
        exit 1
    fi
    
    echo "Extracting JDK..."
    tar -xzf "$TAR_FILE" -C "$LOCAL_JDK_DIR"
    rm "$TAR_FILE"
    
    JDK_BIN_DIR=$(find "$LOCAL_JDK_DIR" -maxdepth 3 -name "javac" -print -quit)
    if [ -n "$JDK_BIN_DIR" ]; then
        export JAVA_HOME="$(dirname "$(dirname "$JDK_BIN_DIR")")"
        export PATH="$JAVA_HOME/bin:$PATH"
        echo "JDK 21 installed successfully in .jdk folder."
    else
        echo "Failed to set up local JDK. Please verify."
        exit 1
    fi
fi

# Ensure mvnw has execute permissions
chmod +x mvnw

# Run the app
echo "Launching MP3 Player..."
./mvnw javafx:run

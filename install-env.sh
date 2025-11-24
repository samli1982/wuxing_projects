#!/bin/bash

echo "======================================"
echo "五行项目开发环境一键安装脚本"
echo "======================================"
echo ""

# 检查是否已安装 Homebrew
if ! command -v brew &> /dev/null; then
    echo "❌ Homebrew 未安装，请先安装 Homebrew"
    echo "访问: https://brew.sh"
    exit 1
fi

echo "✅ Homebrew 已安装"
echo ""

# 1. 安装 JDK 17
echo ">>> [1/3] 安装 JDK 17..."
if command -v java &> /dev/null && java -version 2>&1 | grep -q "17"; then
    echo "✅ JDK 17 已安装，跳过"
else
    echo "正在安装 OpenJDK 17..."
    brew install openjdk@17
    
    echo "正在创建软链接..."
    sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk 2>/dev/null || \
    sudo ln -sfn /usr/local/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk
    
    echo "✅ JDK 17 安装完成"
fi
echo ""

# 2. 安装 Maven
echo ">>> [2/3] 安装 Maven..."
if command -v mvn &> /dev/null; then
    echo "✅ Maven 已安装，跳过"
else
    echo "正在安装 Maven..."
    brew install maven
    echo "✅ Maven 安装完成"
fi
echo ""

# 3. 安装 Redis
echo ">>> [3/3] 安装 Redis..."
if command -v redis-cli &> /dev/null; then
    echo "✅ Redis 已安装，跳过"
else
    echo "正在安装 Redis..."
    brew install redis
    echo "✅ Redis 安装完成"
fi
echo ""

# 4. 配置环境变量
echo ">>> 配置环境变量..."
SHELL_RC="$HOME/.zshrc"

# 检查是否已配置 JAVA_HOME
if ! grep -q "JAVA_HOME" "$SHELL_RC" 2>/dev/null; then
    echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> "$SHELL_RC"
    echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> "$SHELL_RC"
    echo "✅ JAVA_HOME 已添加到 $SHELL_RC"
else
    echo "✅ JAVA_HOME 已配置，跳过"
fi
echo ""

# 5. 配置 Maven 国内镜像
echo ">>> 配置 Maven 阿里云镜像..."
MAVEN_SETTINGS="$HOME/.m2/settings.xml"
mkdir -p "$HOME/.m2"

if [ ! -f "$MAVEN_SETTINGS" ]; then
    cat > "$MAVEN_SETTINGS" << 'MAVEN_EOF'
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 
          http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <mirrors>
    <mirror>
      <id>aliyun</id>
      <mirrorOf>central</mirrorOf>
      <name>Aliyun Maven</name>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>
</settings>
MAVEN_EOF
    echo "✅ Maven 镜像配置完成"
else
    echo "✅ Maven 配置文件已存在，跳过"
fi
echo ""

# 6. 启动 Redis
echo ">>> 启动 Redis 服务..."
brew services start redis 2>/dev/null
echo "✅ Redis 服务已启动"
echo ""

# 7. 使环境变量生效
echo ">>> 使环境变量生效..."
source "$SHELL_RC" 2>/dev/null || true
export JAVA_HOME=$(/usr/libexec/java_home -v 17 2>/dev/null)
export PATH="$JAVA_HOME/bin:$PATH"
echo ""

echo "======================================"
echo "安装完成！正在验证..."
echo "======================================"
echo ""

# 验证安装
echo "📋 安装验证结果："
echo ""

echo -n "Java: "
if command -v java &> /dev/null; then
    java -version 2>&1 | head -n 1
else
    echo "❌ 未找到（请重新打开终端或执行: source ~/.zshrc）"
fi

echo -n "Maven: "
if command -v mvn &> /dev/null; then
    mvn -version 2>&1 | head -n 1
else
    echo "❌ 未找到（请重新打开终端或执行: source ~/.zshrc）"
fi

echo -n "Redis: "
if command -v redis-cli &> /dev/null; then
    redis-cli --version
else
    echo "❌ 未找到"
fi

echo -n "Redis 服务: "
if redis-cli ping &> /dev/null; then
    echo "✅ 运行中"
else
    echo "⚠️  未运行（请执行: brew services start redis）"
fi

echo ""
echo "======================================"
echo "✅ 所有工具安装完成！"
echo "======================================"
echo ""
echo "📝 后续步骤："
echo "1. 重新打开终端或执行: source ~/.zshrc"
echo "2. 验证环境: java -version && mvn -version"
echo "3. 配置数据库（见环境检查报告.md）"
echo "4. 启动项目: cd backend && mvn spring-boot:run"
echo ""

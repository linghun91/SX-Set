# Maven到Gradle迁移指南

## 📋 迁移概述

本文档说明了如何将SX-Set项目从Maven构建系统迁移到Gradle构建系统。

## 🔄 文件对应关系

### Maven → Gradle 文件映射

| Maven文件 | Gradle文件 | 说明 |
|-----------|------------|------|
| `pom.xml` | `build.gradle` | 主构建配置文件 |
| 无 | `settings.gradle` | 项目设置文件 |
| 无 | `gradle.properties` | 全局属性配置 |
| 无 | `gradle/wrapper/gradle-wrapper.properties` | Gradle Wrapper配置 |

## ⚙️ 配置对比

### 基本项目信息

**Maven (pom.xml):**
```xml
<groupId>cn.i7mc</groupId>
<artifactId>sx-set</artifactId>
<version>1.0.0</version>
<packaging>jar</packaging>
```

**Gradle (build.gradle):**
```gradle
group = 'cn.i7mc'
archivesBaseName = 'sx-set'
version = '1.0.0'
```

### Java版本配置

**Maven:**
```xml
<properties>
    <java.version>21</java.version>
</properties>
```

**Gradle:**
```gradle
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
```

### 依赖管理

**Maven:**
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.30</version>
    <scope>provided</scope>
</dependency>
```

**Gradle:**
```gradle
compileOnly 'org.projectlombok:lombok:1.18.30'
annotationProcessor 'org.projectlombok:lombok:1.18.30'
```

### 本地JAR依赖

**Maven:**
```xml
<dependency>
    <groupId>org.spigotmc</groupId>
    <artifactId>spigot-api</artifactId>
    <version>1.21.4-R0.1-SNAPSHOT</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/libs/spigot-api-1.21.4-R0.1-SNAPSHOT.jar</systemPath>
</dependency>
```

**Gradle:**
```gradle
compileOnly files('libs/spigot-api-1.21.4-R0.1-SNAPSHOT.jar')
```

### 插件配置

**Maven (Shade Plugin):**
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.5.1</version>
</plugin>
```

**Gradle (Shadow Plugin):**
```gradle
plugins {
    id 'com.github.johnrengelman.shadow' version '8.1.1'
}
```

## 🚀 使用方法

### 构建命令对比

| 操作 | Maven命令 | Gradle命令 |
|------|-----------|------------|
| 清理 | `mvn clean` | `./gradlew clean` |
| 编译 | `mvn compile` | `./gradlew compileJava` |
| 打包 | `mvn package` | `./gradlew build` |
| 清理并构建 | `mvn clean package` | `./gradlew clean build` |
| 跳过测试构建 | `mvn package -DskipTests` | `./gradlew build -x test` |

### 常用Gradle任务

```bash
# 查看所有可用任务
./gradlew tasks

# 清理项目
./gradlew clean

# 编译源码
./gradlew compileJava

# 运行测试
./gradlew test

# 构建项目（包含shadowJar）
./gradlew build

# 只生成shadowJar
./gradlew shadowJar

# 自定义清理构建任务
./gradlew cleanBuild
```

## ✨ Gradle优势

### 1. 性能提升
- **增量构建**: 只重新构建变更的部分
- **构建缓存**: 复用之前的构建结果
- **并行执行**: 支持任务并行执行

### 2. 灵活性
- **Groovy/Kotlin DSL**: 更强大的构建脚本语言
- **自定义任务**: 轻松创建自定义构建任务
- **插件生态**: 丰富的插件生态系统

### 3. 现代化特性
- **Gradle Wrapper**: 确保团队使用相同的Gradle版本
- **依赖锁定**: 确保构建的可重现性
- **构建扫描**: 详细的构建分析

## 🔧 迁移步骤

### 1. 安装Gradle（可选）
如果使用Gradle Wrapper，无需单独安装Gradle：
```bash
# 使用Wrapper（推荐）
./gradlew build

# 或者安装Gradle
# Windows: choco install gradle
# macOS: brew install gradle
# Linux: sdk install gradle
```

### 2. 验证构建
```bash
# 清理并构建项目
./gradlew clean build

# 检查生成的JAR文件
ls -la build/libs/
```

### 3. IDE配置
- **IntelliJ IDEA**: 直接导入Gradle项目
- **Eclipse**: 安装Gradle插件后导入
- **VS Code**: 安装Gradle扩展

## ⚠️ 注意事项

### 1. 文件路径
- Gradle使用相对路径引用本地JAR文件
- 确保`libs/`目录中的JAR文件存在

### 2. 编码设置
- 已在`gradle.properties`中设置UTF-8编码
- 确保所有文本文件使用UTF-8编码

### 3. Java版本
- 项目配置为Java 21
- 确保开发环境安装了正确的JDK版本

### 4. 依赖范围
- `compileOnly`: 编译时需要，运行时由环境提供（类似Maven的provided）
- `implementation`: 编译和运行时都需要
- `testImplementation`: 仅测试时需要

## 🎯 最佳实践

### 1. 使用Gradle Wrapper
始终使用`./gradlew`而不是全局的`gradle`命令，确保团队使用相同版本。

### 2. 版本管理
在`gradle.properties`中集中管理版本号和配置。

### 3. 任务组织
将相关任务分组，便于管理和查找。

### 4. 构建优化
- 启用并行构建
- 使用构建缓存
- 合理配置JVM参数

## 📚 参考资源

- [Gradle官方文档](https://docs.gradle.org/)
- [Gradle用户指南](https://docs.gradle.org/current/userguide/userguide.html)
- [Shadow插件文档](https://imperceptiblethoughts.com/shadow/)
- [Maven到Gradle迁移指南](https://docs.gradle.org/current/userguide/migrating_from_maven.html)

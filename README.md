# BlockSound Agent - 1.7.10 暴击和格挡音效

为 Minecraft 1.7.10 提供暴击和格挡音效的独立 Java Agent 模组。

## 功能

- ✅ **暴击音效** - 当你暴击攻击时播放音效 (`random.orb`)
- ✅ **格挡音效** - 当你格挡伤害时播放音效 (`random.anvil_use`)
- ✅ **本地检测** - 不依赖服务器数据包，在单人和多人都能工作
- ✅ **Lunar Client 兼容** - 使用 Java Agent 加载，不需要修改游戏文件

## 编译

本项目使用 GitHub Actions 自动编译。

### 自动编译（推荐）

1. Fork 这个仓库到你的 GitHub 账号
2. 进入 Actions 标签页
3. 点击 "Build BlockSound Agent" 工作流
4. 点击 "Run workflow"
5. 等待编译完成（约 2-3 分钟）
6. 下载编译好的 `BlockSound-Agent.jar`

### 本地编译

需要：
- Java 8 JDK
- 所有依赖库（见 `.github/workflows/build.yml`）

```bash
# 下载依赖
mkdir libs
cd libs
wget <依赖库 URL>

# 编译
javac -cp "libs/*" src/wtf/tatp/blocksound/*.java
jar cvf BlockSound-Agent.jar -C build/classes .
```

## 使用方法

### 1. 准备文件

将以下文件放到同一个文件夹（例如 `D:\bs\agent\`）：
- `BlockSound-Agent.jar` （编译生成的）
- `lunar-agent.jar` （从 Meowtils-Lunar 复制）
- `agent-mods.json` （配置文件，见下方）

### 2. 创建配置文件

创建 `agent-mods.json`：

```json
{
  "mods": [
    {
      "jar": "D:\\bs\\agent\\BlockSound-Agent.jar",
      "mixin": "mixins.blocksound.json",
      "property": "blocksound.agent.injected"
    }
  ]
}
```

**注意**：路径使用双反斜杠 `\\`

### 3. 配置 Lunar Client

在 Lunar Client 的 JVM 参数中添加：

```
-javaagent:D:\bs\agent\lunar-agent.jar=D:\bs\agent\agent-mods.json
```

### 4. 启动游戏

选择 Minecraft 1.7.10，启动游戏。

如果成功加载，控制台会显示：
```
[BlockSound] Core initialized!
[BlockSound] Version: 2.0.0-Agent
```

## 技术细节

### 实现原理

使用 SpongePowered Mixin 框架注入字节码：

- **暴击检测**：注入 `PlayerControllerMP.attackEntity()` 方法
  - 检测条件：玩家不在地面、下落距离为0、不在水中、不在骑乘
  - 音效：`random.orb` (1.5x pitch)

- **格挡检测**：注入 `EntityPlayerSP.onUpdate()` 方法  
  - 检测条件：玩家正在格挡 + 受到伤害（hurtTime > 0）
  - 500ms 网络延迟容错窗口
  - 音效：`random.anvil_use` (1.0x pitch)

### 依赖库

- Forge 1.7.10 (Minecraft 类)
- SpongePowered Mixin 0.7.11
- Google Gson 2.8.0
- ASM 5.2
- Google Guava 21.0

## 故障排除

### 游戏崩溃或无法启动

1. 检查 JVM 参数路径是否正确
2. 确认所有文件都存在
3. 查看 `.minecraft/logs/latest.log` 中的错误信息

### 音效不播放

1. 确认控制台显示 "[BlockSound] Core initialized!"
2. 检查游戏音量设置
3. 确认你满足触发条件（暴击/格挡）

### Mixin 加载失败

检查 `lunar-agent.jar` 和 `agent-mods.json` 配置是否正确。

## 致谢

- 原始 ActionSounds 功能来自 Meowtils 2.0.1
- 移植和适配：Claude (AI)
- 测试和反馈：用户

## 许可证

本项目仅供学习和个人使用。

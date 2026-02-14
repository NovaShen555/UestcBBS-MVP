# GitHub Actions CI/CD 工作流程图示

## 概览

```
开发者推送代码
    │
    ├─→ [推送到 master]
    │       │
    │       ├─→ 触发 android-build.yml
    │       │       │
    │       │       ├─→ 编译 Debug APK
    │       │       ├─→ 运行测试
    │       │       └─→ 上传 APK 为 Artifact
    │       │
    │       └─→ 结果：✅ 代码质量检查完成
    │
    └─→ [推送标签 (如 v1.1.0)]
            │
            ├─→ 触发 android-release.yml
            │       │
            │       ├─→ 编译 Release APK
            │       ├─→ 提取版本号
            │       ├─→ 生成发布说明
            │       └─→ 创建 Preview Release
            │               │
            │               └─→ 上传 APK 到 Release
            │
            └─→ 结果：🎉 预览版本已发布

测试人员下载并测试 Preview Release
    │
    └─→ [测试通过]
            │
            └─→ 开发者手动运行 promote-release.yml
                    │
                    ├─→ 输入：标签名 (如 v1.1.0)
                    ├─→ 可选：更新发布说明
                    │
                    └─→ 结果：🚀 正式版本发布完成
```

## 详细流程

### 1. 日常开发流程

```
┌─────────────────────────────────────────────────────────┐
│  开发者在本地开发新功能                                   │
├─────────────────────────────────────────────────────────┤
│  git add .                                              │
│  git commit -m "Add new feature"                        │
│  git push origin master                                 │
└─────────────────────────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────┐
│  GitHub Actions: android-build.yml                      │
├─────────────────────────────────────────────────────────┤
│  ✓ Checkout 代码                                         │
│  ✓ 设置 JDK 11                                          │
│  ✓ Setup Gradle (使用缓存)                              │
│  ✓ ./gradlew assembleDebug                             │
│  ✓ ./gradlew test                                      │
│  ✓ 上传 Debug APK (保留 7 天)                           │
└─────────────────────────────────────────────────────────┘
                        │
                        ▼
            ✅ CI 检查通过/失败
```

### 2. 发布预览版本流程

```
┌─────────────────────────────────────────────────────────┐
│  开发者准备发布新版本                                     │
├─────────────────────────────────────────────────────────┤
│  1. 更新 buildSrc/src/main/kotlin/Build.kt            │
│     - versionCode = 10200                              │
│     - versionName = "1.2.0"                            │
│                                                         │
│  2. 提交版本更新                                         │
│     git add buildSrc/src/main/kotlin/Build.kt          │
│     git commit -m "Bump version to 1.2.0"              │
│     git push origin master                             │
│                                                         │
│  3. 创建并推送标签                                       │
│     git tag v1.2.0                                     │
│     git push origin v1.2.0                             │
└─────────────────────────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────┐
│  GitHub Actions: android-release.yml                    │
├─────────────────────────────────────────────────────────┤
│  ✓ Checkout 代码 (fetch-depth: 0，获取完整历史)         │
│  ✓ 设置 JDK 11                                          │
│  ✓ Setup Gradle (使用缓存)                              │
│  ✓ ./gradlew assembleRelease                          │
│  ✓ 从 Build.kt 提取版本号: "1.2.0"                      │
│  ✓ 生成发布说明 (从上次标签以来的提交)                    │
│  ✓ 创建 Preview Release                                │
│     - 名称: "Preview v1.2.0"                           │
│     - 标记为预发布 (prerelease: true)                   │
│     - 上传: app-release.apk                            │
│     - 发布说明: 自动生成                                │
└─────────────────────────────────────────────────────────┘
                        │
                        ▼
     🎉 预览版本发布在 GitHub Releases 页面
```

### 3. 升级为正式版本流程

```
┌─────────────────────────────────────────────────────────┐
│  测试人员测试预览版本                                     │
├─────────────────────────────────────────────────────────┤
│  1. 访问 GitHub Releases 页面                           │
│  2. 下载 Preview v1.2.0 的 APK                         │
│  3. 在设备上安装测试                                     │
│  4. 验证所有功能正常                                     │
│  5. ✅ 确认可以发布                                      │
└─────────────────────────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────┐
│  开发者手动升级为正式版本                                 │
├─────────────────────────────────────────────────────────┤
│  1. 访问仓库的 Actions 页面                             │
│  2. 选择 "Promote to Release" 工作流                   │
│  3. 点击 "Run workflow"                                │
│  4. 填写参数:                                           │
│     - tag: v1.2.0                                      │
│     - release_notes: (可选) 更新的发布说明              │
│  5. 点击 "Run workflow" 执行                           │
└─────────────────────────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────┐
│  GitHub Actions: promote-release.yml                    │
├─────────────────────────────────────────────────────────┤
│  ✓ Checkout 代码                                         │
│  ✓ 获取 Release 信息 (通过 GitHub API)                  │
│     - 查找标签 v1.2.0 对应的 Release                    │
│     - 提取 Release ID                                   │
│     - 提取现有发布说明                                   │
│  ✓ 升级为正式版本                                        │
│     - 使用 jq 正确转义 JSON                             │
│     - prerelease: false                                │
│     - name: "Release v1.2.0"                           │
│     - body: 更新的发布说明或保持原有                     │
└─────────────────────────────────────────────────────────┘
                        │
                        ▼
     🚀 正式版本发布！用户可以下载
```

## 权限配置

每个工作流都使用最小权限原则：

### android-build.yml
```yaml
permissions:
  contents: read  # 只需要读取代码
```

### android-release.yml
```yaml
permissions:
  contents: write  # 需要创建 Release
```

### promote-release.yml
```yaml
permissions:
  contents: write  # 需要修改 Release
```

## 快速参考

### 查看 CI 构建状态
- 访问: `https://github.com/NovaShen555/UestcBBS-MVP/actions`
- 或者查看 README 中的徽章: [![Android CI](https://github.com/NovaShen555/UestcBBS-MVP/actions/workflows/android-build.yml/badge.svg)](https://github.com/NovaShen555/UestcBBS-MVP/actions/workflows/android-build.yml)

### 下载构建产物
- **CI 构建**: Actions → 选择运行 → Artifacts 部分
- **Release**: Releases 页面 → 选择版本 → Assets 部分

### 常用命令
```bash
# 查看当前标签
git tag

# 创建新标签
git tag v1.2.0

# 推送标签
git push origin v1.2.0

# 删除本地标签
git tag -d v1.2.0

# 删除远程标签
git push origin :refs/tags/v1.2.0
```

## 故障排除速查表

| 问题 | 可能原因 | 解决方法 |
|------|---------|---------|
| CI 构建失败 | 代码编译错误 | 检查 Actions 日志，修复编译错误 |
| Release 未创建 | 标签格式错误 | 确保标签以 `v` 开头 (如 v1.2.0) |
| APK 未上传 | 编译失败 | 检查 assembleRelease 步骤的日志 |
| 无法升级到正式版 | 标签不存在或不是预览版 | 确认标签存在且对应 Release 是预览版 |
| 发布说明为空 | 浅克隆问题 | 已修复 (fetch-depth: 0) |
| JSON 格式错误 | 特殊字符未转义 | 已修复 (使用 jq) |

## 优点总结

✅ **自动化**: 无需手动编译和上传 APK  
✅ **版本控制**: 每个版本都有对应的标签和提交记录  
✅ **可追溯性**: 自动生成的发布说明包含所有变更  
✅ **安全**: 使用最小权限原则，通过 CodeQL 检查  
✅ **灵活**: 支持预览→正式的两阶段发布  
✅ **易用**: 一键升级预览为正式版本  

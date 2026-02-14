# 快速开始 - GitHub Actions CI/CD

这个文档提供最简洁的步骤来使用新的 CI/CD 系统。

## 📋 文件说明

本次更新添加了以下文件：

```
.github/workflows/
├── android-build.yml        # 自动构建和测试
├── android-release.yml      # 自动创建预览版本
└── promote-release.yml      # 手动升级为正式版本

README.MD                     # 添加了构建状态徽章
CI_CD_GUIDE.md               # 详细的 CI/CD 使用指南
CI_CD_WORKFLOW_DIAGRAM.md    # 可视化工作流程图
QUICK_START.md               # 本文件 - 快速开始指南
```

## 🚀 快速开始

### 第一次使用

**无需任何配置！** 只需推送代码，GitHub Actions 会自动运行。

### 日常开发

```bash
# 1. 正常开发和提交
git add .
git commit -m "Add new feature"
git push origin master

# ✅ 自动触发 CI 构建
# 查看结果: https://github.com/NovaShen555/UestcBBS-MVP/actions
```

### 发布新版本（3 步）

#### 步骤 1: 更新版本号

编辑 `buildSrc/src/main/kotlin/Build.kt`:

```kotlin
object BuildVersion {
    const val versionCode = 10200  // 从 10100 改为 10200
    const val versionName = "1.2.0" // 从 "1.1.0" 改为 "1.2.0"
    // ... 其他不变
}
```

#### 步骤 2: 创建标签并推送

```bash
# 提交版本更新
git add buildSrc/src/main/kotlin/Build.kt
git commit -m "Bump version to 1.2.0"
git push origin master

# 创建标签（注意：必须以 v 开头）
git tag v1.2.0

# 推送标签
git push origin v1.2.0
```

**🎉 完成！** GitHub Actions 会自动：
- 编译 Release APK
- 创建预览版本
- 上传 APK 到 Releases 页面

#### 步骤 3: 测试并升级为正式版本

1. 访问 [Releases 页面](https://github.com/NovaShen555/UestcBBS-MVP/releases)
2. 下载 "Preview v1.2.0" 的 APK 并测试
3. 确认无误后，访问 [Actions 页面](https://github.com/NovaShen555/UestcBBS-MVP/actions)
4. 点击左侧 "Promote to Release"
5. 点击 "Run workflow"
6. 输入标签名: `v1.2.0`
7. 点击 "Run workflow" 按钮

**🚀 完成！** 预览版本已升级为正式版本。

## 💡 提示

### 查看 CI 状态

- **方式 1**: 在 README 中查看徽章 [![Android CI](https://github.com/NovaShen555/UestcBBS-MVP/actions/workflows/android-build.yml/badge.svg)](https://github.com/NovaShen555/UestcBBS-MVP/actions/workflows/android-build.yml)
- **方式 2**: 访问 [Actions 页面](https://github.com/NovaShen555/UestcBBS-MVP/actions)

### 下载构建产物

- **开发版本 (Debug APK)**: Actions → 选择运行 → 下拉找到 "Artifacts"
- **发布版本 (Release APK)**: [Releases 页面](https://github.com/NovaShen555/UestcBBS-MVP/releases)

### 版本号规则

```
versionCode:  a.b.c → a*10000 + b*100 + c
versionName:  "a.b.c"

例子:
- 1.0.0 → versionCode: 10000, versionName: "1.0.0"
- 1.1.0 → versionCode: 10100, versionName: "1.1.0"
- 1.2.3 → versionCode: 10203, versionName: "1.2.3"
```

## ❓ 常见问题

### Q: 如何删除错误的标签？

```bash
# 删除本地标签
git tag -d v1.2.0

# 删除远程标签
git push origin :refs/tags/v1.2.0
```

### Q: 构建失败怎么办？

1. 查看 [Actions 页面](https://github.com/NovaShen555/UestcBBS-MVP/actions)
2. 点击失败的运行
3. 查看日志找到错误信息
4. 修复代码后重新推送

### Q: 如何查看自动生成的发布说明？

发布说明包含从上一个标签到当前标签之间的所有提交信息。

例子：
```
## 版本 1.2.0

### 更新内容
- Add new login feature
- Fix crash on startup
- Update dependencies

---
构建时间: 2024-02-14 16:30:00
```

## 📚 更多信息

- **详细使用指南**: 查看 `CI_CD_GUIDE.md`
- **可视化流程图**: 查看 `CI_CD_WORKFLOW_DIAGRAM.md`
- **GitHub Actions 文档**: https://docs.github.com/en/actions

## 🎯 总结

现在你只需要：
1. ✅ 正常开发和推送代码 → CI 自动构建
2. ✅ 创建标签并推送 → 自动创建预览版本
3. ✅ 手动点击按钮 → 升级为正式版本

**不再需要手动编译、打包和上传！** 🎉

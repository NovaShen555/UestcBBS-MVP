# CI/CD 工作流程说明

本项目使用 GitHub Actions 实现自动化构建和发布流程。

## 工作流程概览

### 1. Android CI (android-build.yml)

**触发条件：**
- 推送到 `master` 或 `main` 分支
- Pull Request 到 `master` 或 `main` 分支

**功能：**
- 自动编译 Debug 版本 APK
- 运行测试（如果存在）
- 将 APK 作为 Artifact 上传，保存 7 天

**使用方法：**
只需要正常推送代码或创建 PR，工作流会自动运行。

---

### 2. Android Release (android-release.yml)

**触发条件：**
- 推送标签（tag），格式为 `v*`（例如：`v1.1.0`, `v1.2.0`）

**功能：**
- 自动编译 Release 版本 APK
- 从 `Build.kt` 中提取版本号
- 自动生成发布说明（包含自上次标签以来的提交记录）
- 创建 **Preview Release**（预发布）
- 上传 APK 到 Release

**使用方法：**

```bash
# 1. 更新版本号（在 buildSrc/src/main/kotlin/Build.kt 中）
# 2. 提交更改
git add .
git commit -m "Bump version to 1.1.0"

# 3. 创建标签
git tag v1.1.0

# 4. 推送标签
git push origin v1.1.0
```

推送标签后，GitHub Actions 会自动：
- 编译 APK
- 创建预览版本发布
- 上传 APK

---

### 3. Promote to Release (promote-release.yml)

**触发条件：**
- 手动触发（Workflow Dispatch）

**功能：**
- 将预览版本（Preview Release）升级为正式版本（Release）
- 可选：更新发布说明

**使用方法：**

1. 进入 GitHub 仓库页面
2. 点击 "Actions" 标签
3. 在左侧选择 "Promote to Release" 工作流
4. 点击 "Run workflow" 按钮
5. 填写参数：
   - **tag**: 要升级的标签名称（例如：`v1.1.0`）
   - **release_notes**: （可选）新的发布说明，留空则使用原有的
6. 点击 "Run workflow" 开始执行

工作流会将指定的预览版本升级为正式版本。

---

## 完整发布流程

### 开发和测试

1. 在 `master` 分支上开发新功能
2. 每次推送会自动触发 CI 构建，检查代码是否能正常编译
3. 创建 PR 时也会自动构建，确保代码质量

### 创建预览版本

1. 更新版本号：
   - 编辑 `buildSrc/src/main/kotlin/Build.kt`
   - 更新 `versionCode` 和 `versionName`

2. 提交并创建标签：
   ```bash
   git add buildSrc/src/main/kotlin/Build.kt
   git commit -m "Bump version to X.X.X"
   git tag vX.X.X
   git push origin master
   git push origin vX.X.X
   ```

3. 等待自动构建：
   - GitHub Actions 会自动编译 Release APK
   - 创建预览版本
   - 上传 APK 到 Releases 页面

4. 测试预览版本：
   - 从 Releases 页面下载 APK
   - 在设备上安装测试
   - 确认功能正常

### 升级为正式版本

1. 确认预览版本测试通过
2. 使用 "Promote to Release" 工作流将预览版本升级为正式版本：
   - 进入 Actions 页面
   - 运行 "Promote to Release" 工作流
   - 输入标签名称（例如：`v1.1.0`）
   - （可选）更新发布说明
3. 正式版本发布完成，用户可以下载

---

## 版本号管理

版本号在 `buildSrc/src/main/kotlin/Build.kt` 中管理：

```kotlin
object BuildVersion {
    const val versionCode = 10100  // a.b.c -> a*10000 + b*100 + c
    const val versionName = "1.1.0"
    // ...
}
```

**规则：**
- `versionCode`: 必须递增，格式为 `a*10000 + b*100 + c`
  - 例如：1.1.0 -> 10100, 1.2.3 -> 10203
- `versionName`: 语义化版本，格式为 `major.minor.patch`
  - major: 重大更新
  - minor: 功能更新
  - patch: 修复更新

---

## 构建状态徽章

可以在 README 中添加构建状态徽章：

```markdown
[![Android CI](https://github.com/NovaShen555/UestcBBS-MVP/actions/workflows/android-build.yml/badge.svg)](https://github.com/NovaShen555/UestcBBS-MVP/actions/workflows/android-build.yml)
```

---

## 故障排除

### 构建失败

1. 检查 Actions 页面的构建日志
2. 确认 Gradle 配置正确
3. 确认依赖项可以正常下载

### 无法创建 Release

1. 确认标签格式正确（必须以 `v` 开头）
2. 确认仓库有 `GITHUB_TOKEN` 权限
3. 检查 APK 是否成功编译

### 无法升级为正式版本

1. 确认标签存在且对应的 Release 是预览版本
2. 确认工作流有正确的权限
3. 检查 Actions 日志中的错误信息

---

## 注意事项

1. **标签管理**：删除标签需要同时删除本地和远程：
   ```bash
   git tag -d vX.X.X
   git push origin :refs/tags/vX.X.X
   ```

2. **APK 签名**：当前配置使用 Debug 签名。如需使用正式签名，需要：
   - 在仓库 Settings -> Secrets 中添加签名密钥
   - 更新 `app/build.gradle.kts` 配置签名
   - 更新工作流以使用签名配置

3. **发布说明**：自动生成的发布说明基于提交信息，建议使用清晰的提交信息格式。

4. **缓存优化**：工作流已配置 Gradle 缓存，加快构建速度。

---

## 未来改进

- [ ] 添加自动化测试
- [ ] 添加代码质量检查（Lint）
- [ ] 集成 ProGuard 混淆
- [ ] 添加正式签名配置
- [ ] 自动更新版本号
- [ ] 添加更详细的发布说明模板

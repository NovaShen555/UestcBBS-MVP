package com.novashen.riverside.module.main.model

import com.novashen.riverside.api.GitHubRetrofitUtil
import com.novashen.riverside.entity.GitHubReleaseBean
import com.novashen.riverside.entity.SettingsBean
import com.novashen.riverside.entity.UpdateBean
import com.novashen.riverside.util.RetrofitUtil
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

/**
 * Created by sca_tl at 2023/4/11 17:20
 */
class MainModel {
    fun getUpdate(oldVersionCode: Int, isTest: Boolean): Observable<UpdateBean> {
        // 从 GitHub Releases 获取更新信息
        return GitHubRetrofitUtil.getInstance()
            .apiService
            .getLatestRelease("NovaShen555", "UestcBBS-MVP")
            .subscribeOn(Schedulers.io())
            .map { release ->
                // 将 GitHub Release 转换为 UpdateBean
                convertGitHubReleaseToUpdateBean(release, oldVersionCode)
            }
    }

    private fun convertGitHubReleaseToUpdateBean(release: GitHubReleaseBean, oldVersionCode: Int): UpdateBean {
        val updateBean = UpdateBean()
        updateBean.returnCode = 200
        updateBean.returnMsg = "success"

        val updateInfo = UpdateBean.UpdateInfoBean()

        // 从 tag_name 提取版本号 (例如: v3.1.9 -> 3.1.9)
        val versionName = release.tagName.removePrefix("v")

        // 将版本号转换为 versionCode (例如: 3.1.9 -> 30109)
        val versionCode = parseVersionCode(versionName)

        updateInfo.apkVersionName = versionName
        updateInfo.apkVersionCode = versionCode
        updateInfo.title = release.name ?: "新版本更新"
        updateInfo.updateContent = release.body ?: "请查看 GitHub Release 页面了解更新内容"
        updateInfo.releaseDate = 0
        updateInfo.isValid = true
        updateInfo.isForceUpdate = false

        // 查找 APK 文件
        val apkAsset = release.assets?.find { it.name.endsWith(".apk") }
        if (apkAsset != null) {
            updateInfo.apkUrl = apkAsset.browserDownloadUrl
            updateInfo.apkName = apkAsset.name
            updateInfo.apkSize = formatFileSize(apkAsset.size)
        }

        updateInfo.webDownloadUrl = release.htmlUrl
        updateInfo.apkMD5 = ""
        updateInfo.id = 0
        updateInfo.apkImages = emptyList()

        updateBean.updateInfo = updateInfo
        return updateBean
    }

    private fun parseVersionCode(versionName: String): Int {
        try {
            // 将版本号转换为整数 (例如: 3.1.9 -> 30109)
            val parts = versionName.split(".")
            if (parts.size >= 3) {
                val major = parts[0].toIntOrNull() ?: 0
                val minor = parts[1].toIntOrNull() ?: 0
                val patch = parts[2].toIntOrNull() ?: 0
                return major * 10000 + minor * 100 + patch
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    private fun formatFileSize(size: Long): String {
        val mb = size / (1024.0 * 1024.0)
        return String.format("%.2f MB", mb)
    }

    fun getSettings(): Observable<SettingsBean> =
         RetrofitUtil
            .getInstance()
            .apiService
            .settings

}

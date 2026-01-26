package com.novashen.riverside.api;

import com.novashen.riverside.entity.GitHubReleaseBean;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * GitHub API 服务
 */
public interface GitHubApiService {

    /**
     * 获取最新的 Release
     * @param owner 仓库所有者
     * @param repo 仓库名称
     */
    @GET("repos/{owner}/{repo}/releases/latest")
    Observable<GitHubReleaseBean> getLatestRelease(
            @Path("owner") String owner,
            @Path("repo") String repo
    );
}

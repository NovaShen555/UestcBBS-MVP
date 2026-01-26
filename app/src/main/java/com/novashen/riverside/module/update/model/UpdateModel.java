package com.novashen.riverside.module.update.model;

import com.novashen.riverside.api.ApiConstant;
import com.novashen.riverside.api.ApiService;
import com.novashen.riverside.helper.rxhelper.Observer;

import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * author: sca_tl
 * description:
 * date: 2019/12/18 19:58
 */
public class UpdateModel {

    public void downloadApk(String url, Observer<ResponseBody> observer) {
        // 从完整 URL 中提取 baseUrl（协议 + 域名）
        String baseUrl;
        try {
            if (url.startsWith("http://") || url.startsWith("https://")) {
                // 解析 URL，提取 baseUrl
                int protocolEnd = url.indexOf("://") + 3;
                int pathStart = url.indexOf("/", protocolEnd);
                if (pathStart > 0) {
                    baseUrl = url.substring(0, pathStart + 1); // 包含协议和域名，以 / 结尾
                } else {
                    baseUrl = url + "/";
                }
            } else {
                // 如果不是完整 URL，使用默认的 baseUrl
                baseUrl = ApiConstant.BASE_ADDITIONAL_URL;
            }
        } catch (Exception e) {
            baseUrl = ApiConstant.BASE_ADDITIONAL_URL;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        retrofit.create(ApiService.class)
                .downloadFile(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())  //注意是io线程，主线程会出错
                .subscribe(observer);
    }
}

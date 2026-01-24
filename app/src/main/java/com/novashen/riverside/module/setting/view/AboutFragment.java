package com.novashen.riverside.module.setting.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.preference.Preference;

import com.novashen.riverside.R;
import com.novashen.riverside.api.ApiConstant;
import com.novashen.riverside.base.BasePreferenceFragment;
import com.novashen.riverside.base.BasePresenter;
import com.novashen.riverside.module.message.view.PrivateChatActivity;
import com.novashen.riverside.module.user.view.UserDetailActivity;
import com.novashen.riverside.module.webview.view.WebViewActivity;
import com.novashen.riverside.util.CommonUtil;
import com.novashen.riverside.util.Constant;

/**
 * author: sca_tl
 * description:
 * date: 2020/1/27 13:37
 */
public class AboutFragment extends BasePreferenceFragment {
    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setSharedPreferencesName("settings");
        addPreferencesFromResource(R.xml.perf_about);

        //init();
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getKey().equals(getString(R.string.about_developer_mail))) {
            Intent data = new Intent(Intent.ACTION_SENDTO);
            data.setData(Uri.parse("mailto:sca_tl@foxmail.com"));
            startActivity(data);
        }

        if (preference.getKey().equals(getString(R.string.about_developer))) {
            Intent intent = new Intent(mActivity, UserDetailActivity.class);
            intent.putExtra(Constant.IntentKey.USER_ID, 217992);
            startActivity(intent);
        }

        if (preference.getKey().equals(getString(R.string.about_source_code))) {
            CommonUtil.openBrowser(mActivity, ApiConstant.OPEN_SOURCE_URL);
        }

        if (preference.getKey().equals(getString(R.string.about_open_source))) {
            Intent intent = new Intent(mActivity, OpenSourceActivity.class);
            startActivity(intent);
        }

        if (preference.getKey().equals(getString(R.string.app_suggestion_contact_developer))) {
            Intent intent = new Intent(mActivity, PrivateChatActivity.class);
            intent.putExtra(Constant.IntentKey.USER_ID, 217992);
            intent.putExtra(Constant.IntentKey.USER_NAME, "私信开发者：sca_tl");
            startActivity(intent);
        }

        if (preference.getKey().equals(getString(R.string.app_suggestion_contact_web))) {
            Intent intent = new Intent(mActivity, WebViewActivity.class);
            intent.putExtra(Constant.IntentKey.URL, "https://support.qq.com/product/141698");
            startActivity(intent);
        }


        return super.onPreferenceTreeClick(preference);
    }
}

package com.novashen.riverside.module.houqin.view;

import com.novashen.riverside.entity.HouQinReportReplyBean;
import com.novashen.riverside.entity.HouQinReportTopicBean;

/**
 * author: sca_tl
 * date: 2020/10/25 17:09
 * description:
 */
public interface HouQinReportDetailView {
    void onGetHouQinReportTopicSuccess(HouQinReportTopicBean houQinReportTopicBean);
    void onGetHouQinReportReplySuccess(HouQinReportReplyBean houQinReportReplyBean);
    void onGetReportDetailError(String msg);

}

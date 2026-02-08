package com.novashen.riverside.module.post.view;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.jaeger.library.StatusBarUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.novashen.riverside.R;
import com.novashen.riverside.api.discourse.entity.CreatePostResponse;
import com.novashen.riverside.annotation.ToastType;
import com.novashen.riverside.base.BaseActivity;
import com.novashen.riverside.base.BaseEvent;
import com.novashen.riverside.base.BaseVBFragmentForBottom;
import com.novashen.riverside.entity.CommonPostBean;
import com.novashen.riverside.entity.SelectBoardResultEvent;
import com.novashen.riverside.entity.UserDetailBean;
import com.novashen.riverside.module.board.view.SelectBoardFragment;
import com.novashen.riverside.module.board.view.DiscourseSelectBoardFragment;
import com.novashen.riverside.util.DebugUtil;
import com.novashen.riverside.widget.MyLinearLayoutManger;
import com.novashen.riverside.widget.ContentEditor;
import com.novashen.riverside.entity.AttachmentBean;
import com.novashen.riverside.entity.PostDraftBean;
import com.novashen.riverside.entity.SendPostBean;
import com.novashen.riverside.entity.UploadResultBean;
import com.novashen.riverside.helper.glidehelper.GlideEngineForPictureSelector;
import com.novashen.riverside.module.post.adapter.AttachmentAdapter;
import com.novashen.riverside.module.post.adapter.CreatePostPollAdapter;
import com.novashen.riverside.module.post.presenter.CreatePostPresenter;
import com.novashen.riverside.module.user.view.AtUserListActivity;
import com.novashen.riverside.module.user.view.UserDetailActivity;
import com.novashen.util.ColorUtil;
import com.novashen.riverside.util.CommonUtil;
import com.novashen.riverside.util.Constant;
import com.novashen.riverside.util.SharePrefUtil;
import com.novashen.riverside.util.TimeUtil;
import com.novashen.riverside.util.ToastUtil;
import com.novashen.widget.emotion.EmotionPanelLayout;
import com.novashen.widget.emotion.IEmotionEventListener;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import am.widget.smoothinputlayout.SmoothInputLayout;

public class CreatePostActivity extends BaseActivity<CreatePostPresenter> implements CreatePostView,
        TextWatcher, AdapterView.OnItemClickListener, IEmotionEventListener {

    private static final String TAG = "CreatePostActivity";

    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    private ImageView addEmotionBtn, atBtn, addPhotoBtn, sendBtn, addPollBtn, addAttachmentBtn, moreOptionsBtn;
    private EmotionPanelLayout emoticonPanelLayout;
    private AppCompatEditText postTitle;
    private TextView boardName, autoSaveText;
    private ContentEditor contentEditor;
    private ProgressDialog progressDialog;
    private TextView sendBtn1;

    private RecyclerView pollRv, attachmentRv;
    private CreatePostPollAdapter createPostPollAdapter;
    private AttachmentAdapter attachmentAdapter;
    private LinearLayout pollLayout;
    private TextView pollDesp;
    private ImageView boardIcon;
    private View selectBoardLayout;
    private View sanShuiLayout;
    private TextView sanShuiDsp;
    private TextInputEditText sanShuiCountEachReply, sanShuiTotalTimes;
    private MaterialAutoCompleteTextView sanShuiEachTime, sanShuiRandom;

    private SmoothInputLayout lytContent;

    private static final int ACTION_ADD_PHOTO = 14;
    private static final int AT_USER_REQUEST = 110;
    private static final int ACTION_ADD_ATTACHMENT = 119;
    private static final int ADD_ATTACHMENT_REQUEST = 120;

    private int currentBoardId, currentFilterId;
    private int lastCreatedTopicId = 0;
    private String currentBoardName, currentFilterName;
    private long createTime;
    private String currentTitle, currentContent;

    private List<String> currentPollOptions;
    private int currentPollExp, currentPollChoice;
    private boolean currentPollVisible, currentPollShowVoters, currentAnonymous, currentOnlyAuthor, currentOriginalPic;

    private int currentSanShuiCountEachReply, currentSanShuiTotalTimes, currentSanShuiEachTime = 1, currentSanShuiRandom = 100;

    private int currentShuiDiCount, currentTaxShuiDiCount;

    private boolean sendPostSuccess;

    private boolean isSanShui;

    // 全部使用Discourse API发帖

    private Map<Uri, Integer> attachments = new LinkedHashMap<>(); //附件aid

    public static final String CREATE_TYPE_SANSHUI = "sanshui";

    @Override
    protected void getIntent(Intent intent) {
        super.getIntent(intent);
        createTime = TimeUtil.getLongMs();
        PostDraftBean postDraftBean = (PostDraftBean) intent.getSerializableExtra(Constant.IntentKey.DATA_2);
        isSanShui = TextUtils.equals(CREATE_TYPE_SANSHUI, intent.getStringExtra(Constant.IntentKey.TYPE));

        if (postDraftBean != null) {
            currentBoardId = postDraftBean.board_id;
            currentFilterId = postDraftBean.filter_id;
            currentBoardName = postDraftBean.board_name;
            currentFilterName = postDraftBean.filter_name;
            currentTitle = postDraftBean.title;
            currentContent = postDraftBean.content;
            createTime = postDraftBean.time;
            currentPollOptions = CommonUtil.toList(postDraftBean.poll_options);
            currentPollExp = postDraftBean.poll_exp;
            currentPollChoice = postDraftBean.poll_choices;
            currentPollVisible = postDraftBean.poll_visible;
            currentPollShowVoters = postDraftBean.poll_show_voters;
            currentAnonymous = postDraftBean.anonymous;
            currentOnlyAuthor = postDraftBean.only_user;
            isSanShui = postDraftBean.isSanShui;
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_create_post;
    }

    @Override
    protected void findView() {
        coordinatorLayout = findViewById(R.id.create_post_coor_layout);
        toolbar = findViewById(R.id.toolbar);
        addEmotionBtn = findViewById(R.id.create_post_add_emotion_btn);
        atBtn = findViewById(R.id.create_post_at_btn);
        addPhotoBtn = findViewById(R.id.create_post_add_image_btn);
        sendBtn = findViewById(R.id.create_post_send_btn);
        emoticonPanelLayout = findViewById(R.id.create_post_emoticon_layout);
        postTitle = findViewById(R.id.create_post_title);
        contentEditor = findViewById(R.id.create_post_content_editor);
        boardName = findViewById(R.id.create_post_board_name);
        autoSaveText = findViewById(R.id.create_post_auto_save_text);
        addPollBtn = findViewById(R.id.create_post_add_poll_btn);
        pollRv = findViewById(R.id.create_post_poll_rv);
        pollLayout = findViewById(R.id.create_post_poll_info);
        pollDesp = findViewById(R.id.create_post_poll_desp);
        addAttachmentBtn = findViewById(R.id.create_post_add_attachment_btn);
        moreOptionsBtn = findViewById(R.id.create_post_more_options_btn);
        attachmentRv = findViewById(R.id.create_post_attachment_rv);
        lytContent = findViewById(R.id.sil_lyt_content);
        sendBtn1 = findViewById(R.id.create_post_send_btn_1);
        boardIcon = findViewById(R.id.select_board_icon);
        selectBoardLayout = findViewById(R.id.select_board_layout);
        sanShuiLayout = findViewById(R.id.san_shui_layout);
        sanShuiCountEachReply = findViewById(R.id.sanshui_count_each_reply);
        sanShuiEachTime = findViewById(R.id.sanshui_each_time);
        sanShuiRandom = findViewById(R.id.sanshui_random);
        sanShuiTotalTimes = findViewById(R.id.sanshui_total_times);
        sanShuiDsp = findViewById(R.id.sanshui_dsp);
    }

    @Override
    protected void initView() {
        super.initView();

        CommonUtil.showSoftKeyboard(this, postTitle, 100);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("发表帖子");
        if (isSanShui) {
            toolbar.setTitle("散水");
            addPhotoBtn.setVisibility(View.GONE);
            selectBoardLayout.setVisibility(View.GONE);
            addPollBtn.setVisibility(View.GONE);
            addAttachmentBtn.setVisibility(View.GONE);
            moreOptionsBtn.setVisibility(View.GONE);
            sanShuiLayout.setVisibility(View.VISIBLE);

            sanShuiCountEachReply.setText("1");
            sanShuiTotalTimes.setText("1");
            sanShuiCountEachReply.addTextChangedListener(this);
            sanShuiTotalTimes.addTextChangedListener(this);
            sanShuiRandom.setText("100%", false);
            sanShuiEachTime.setText("1", false);
            sanShuiEachTime.setOnItemClickListener(this);
            sanShuiRandom.setOnItemClickListener(this);

            setSanShuiDsp();
        }

        addEmotionBtn.setOnClickListener(this);
        atBtn.setOnClickListener(this::onClickListener);
        addPhotoBtn.setOnClickListener(this::onClickListener);
        sendBtn.setOnClickListener(this::onClickListener);
        boardName.setOnClickListener(this::onClickListener);
        addPollBtn.setOnClickListener(this::onClickListener);
        addAttachmentBtn.setOnClickListener(this::onClickListener);
        moreOptionsBtn.setOnClickListener(this);
        sendBtn1.setOnClickListener(this);
        emoticonPanelLayout.setEventListener(this);

        //投票
        createPostPollAdapter = new CreatePostPollAdapter(R.layout.item_create_post_poll);
        pollRv.setLayoutManager(new MyLinearLayoutManger(this));
        pollRv.setAdapter(createPostPollAdapter);

        //附件
        attachmentAdapter = new AttachmentAdapter(R.layout.item_attachment);
        LinearLayoutManager linearLayoutManager1 = new MyLinearLayoutManger(this);
        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        attachmentRv.setLayoutManager(linearLayoutManager1);
        attachmentRv.setAdapter(attachmentAdapter);

        postTitle.setText(TextUtils.isEmpty(currentTitle) ? "" : currentTitle);
        boardName.setText(TextUtils.isEmpty(currentBoardName) && TextUtils.isEmpty(currentFilterName) ? "请选择合适的板块" :
                currentBoardName + "-" + currentFilterName);

        //若内容不为空，则说明是草稿，直接显示内容
        if (! TextUtils.isEmpty(currentContent)) {
            contentEditor.setEditorData(currentContent);
        }
        if (currentPollOptions != null && currentPollOptions.size() > 0) {
            pollLayout.setVisibility(View.VISIBLE);
            createPostPollAdapter.setNewData(currentPollOptions);
            String a = "可选" + currentPollChoice + "项，";
            String c = "投票" + (currentPollVisible ? "后结果可见，" : "前结果可见");
            String d = (currentPollShowVoters ? "公开" : "不公开") + "投票参与人";

            pollDesp.setText(new StringBuilder().append(a).append(c).append(d));
        } else {
            currentPollOptions = new ArrayList<>();
        }

        presenter.getUserDetail(SharePrefUtil.getUid(this));
        presenter.getFormHash(this);
        countDownTimer.start();
    }

    @Override
    protected CreatePostPresenter initPresenter() {
        return new CreatePostPresenter();
    }

    @Override
    protected void onClickListener(View view) {
        if (view.getId() == R.id.create_post_add_emotion_btn) {
            if (emoticonPanelLayout.getVisibility() == View.GONE) {
                lytContent.closeKeyboard(true);// 关闭键盘
                lytContent.showInputPane(true);//显示面板
            } else {
                lytContent.closeInputPane();// 关闭面板
                lytContent.showKeyboard();// 显示键盘
            }
        }
        if (view.getId() == R.id.create_post_at_btn) {
            Intent intent = new Intent(this, AtUserListActivity.class);
            startActivityForResult(intent, AT_USER_REQUEST);
        }
        if (view.getId() == R.id.create_post_add_image_btn) {
//            Gallery.Companion
//                    .getINSTANCE()
//                    .with(this)
//
//                    .show(999);
            presenter.requestPermission(this, ACTION_ADD_PHOTO, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (view.getId() == R.id.create_post_add_attachment_btn) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            this.startActivityForResult(intent, ADD_ATTACHMENT_REQUEST);
        }
        if (view.getId() == R.id.create_post_add_poll_btn) {
            Bundle bundle = new Bundle();
            bundle.putString(Constant.IntentKey.TYPE, BaseVBFragmentForBottom.BIZ_ADD_VOTE);
            bundle.putBoolean(Constant.IntentKey.DRAGGABLE, false);
            bundle.putDouble(Constant.IntentKey.MAX_HEIGHT_MULTIPLIER, 0.8);
            bundle.putBoolean(Constant.IntentKey.CANCELABLE, false);
            if (createPostPollAdapter.getData().size() != 0) {
                bundle.putBoolean(Constant.IntentKey.POLL_VISIBLE, currentPollVisible);
                bundle.putBoolean(Constant.IntentKey.POLL_SHOW_VOTERS, currentPollShowVoters);
                bundle.putInt(Constant.IntentKey.POLL_CHOICES, currentPollChoice);
                bundle.putStringArrayList(Constant.IntentKey.POLL_OPTIONS, (ArrayList<String>) createPostPollAdapter.getData());
            }
            BaseVBFragmentForBottom.Companion.getInstance(bundle).show(getSupportFragmentManager(), TimeUtil.getStringMs());
        }

        if (view.getId() == R.id.create_post_board_name) {
            DiscourseSelectBoardFragment.Companion.getInstance(null)
                    .show(getSupportFragmentManager(), TimeUtil.getStringMs());
        }
        if (view.getId() == R.id.create_post_send_btn || view.getId() == R.id.create_post_send_btn_1) {
            if (isSanShui) {
                createSanShuiPost();
            } else {
                createCommonPost();
            }
        }

        if (view.getId() == R.id.create_post_more_options_btn) {
            presenter.showCreatePostMoreOptionsDialog(this, currentAnonymous, currentOnlyAuthor, currentOriginalPic);
        }
    }

    private void createSanShuiPost() {
        if (TextUtils.isEmpty(sanShuiCountEachReply.getText().toString())) {
            showToast("请输入每次回帖奖励水滴数量", ToastType.TYPE_ERROR);
        } else if (TextUtils.isEmpty(sanShuiTotalTimes.getText().toString())) {
            showToast("请输入总共奖励次数", ToastType.TYPE_ERROR);
        } else if (TextUtils.isEmpty(postTitle.getText().toString())) {
            showToast("请输入帖子标题", ToastType.TYPE_ERROR);
        } else if (contentEditor.isEditorEmpty()) {
            showToast("请输入帖子内容", ToastType.TYPE_ERROR);
        } else if (currentShuiDiCount < currentTaxShuiDiCount) {
            showToast("水滴数量不够，给自己留点吧😂", ToastType.TYPE_ERROR);
        } else if (TextUtils.isEmpty(SharePrefUtil.getForumHash(this))) {
            showToast("未能够获取formhash，请重新登录", ToastType.TYPE_ERROR);
        } else if (currentSanShuiCountEachReply > 1000) {
            showToast("每次回帖奖励水滴数不能大于1000", ToastType.TYPE_ERROR);
        } else {
            progressDialog.setMessage("正在发表帖子，请稍候...");
            progressDialog.show();

            String content = "散水";
            List<ContentEditor.EditData> data = contentEditor.buildEditorData();
            for (int i = 0; i < data.size(); i ++) {
                if (data.get(i).content_type == ContentEditor.CONTENT_TYPE_TEXT) {
                    content = data.get(i).inputStr;
                }
            }

            DebugUtil.d(TAG, currentSanShuiCountEachReply, ",,", currentSanShuiTotalTimes,
            ",,", currentSanShuiEachTime, ",, ", currentSanShuiRandom);

            presenter.sanShui(this, postTitle.getText().toString(), content,
                    currentSanShuiCountEachReply, currentSanShuiTotalTimes,
                    currentSanShuiEachTime, currentSanShuiRandom);
        }
    }

    private void createCommonPost() {
        if (currentBoardId == 0) {
            showToast("请选择板块", ToastType.TYPE_WARNING);
            return;
        }

        // 全部使用Discourse API发帖
        if (TextUtils.isEmpty(postTitle.getText().toString())) {
            showToast("请输入帖子标题", ToastType.TYPE_ERROR);
        } else {
            progressDialog.setMessage("正在发表帖子，请稍候...");
            progressDialog.show();

                    presenter.sendDiscoursePost(contentEditor,
                    currentBoardId,
                    postTitle.getText().toString(),
                    currentPollOptions,
                    currentPollChoice,
                    currentPollVisible,
                    currentPollShowVoters);
        }
    }

    @Override
    protected void setOnItemClickListener() {
        attachmentAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.item_attachment_delete_file) {
                attachments.remove(attachmentAdapter.getData().get(position).localPath);
                attachmentAdapter.delete(position);
            }
        });
    }

    @Override
    public void onSendPostSuccessBack() {
        CommonUtil.hideSoftKeyboard(this, contentEditor);
    }

    @Override
    public void onSendPostSuccessViewPost() {
        progressDialog.show();
        progressDialog.setMessage("请稍候...");
        if (lastCreatedTopicId > 0) {
            Intent intent = new Intent(this, NewPostDetailActivity.class);
            intent.putExtra(Constant.IntentKey.TOPIC_ID, lastCreatedTopicId);
            startActivity(intent);
            progressDialog.dismiss();
            finish();
        } else {
            presenter.userPost(SharePrefUtil.getUid(this));
        }
    }

    @Override
    public void onSendPostSuccess(SendPostBean sendPostBean) {
        sendPostSuccess = true;
        progressDialog.dismiss();
        if (currentAnonymous) {
            ToastUtil.showToast(this, "发帖成功", ToastType.TYPE_SUCCESS);
            finish();
        } else {
            presenter.showCreatePostSuccessDialog(this);
        }
    }

    @Override
    public void onSendDiscoursePostSuccess(CreatePostResponse response) {
        if (response == null) {
            return;
        }
        if (response.topicId > 0) {
            lastCreatedTopicId = response.topicId;
            return;
        }
        if (response.post != null && response.post.topicId > 0) {
            lastCreatedTopicId = response.post.topicId;
        }
    }

    @Override
    public void onSendPostError(String msg) {
        progressDialog.dismiss();
        showToast("发表帖子失败：" + msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onUploadSuccess(UploadResultBean uploadResultBean) {
        progressDialog.setMessage("正在发表帖子，请稍候...");

        List<Integer> imgIds = new ArrayList<>();
        List<String> imgUrls = new ArrayList<>();

        for (int i = 0; i < uploadResultBean.body.attachment.size(); i ++) {
            imgIds.add(uploadResultBean.body.attachment.get(i).id);
            imgUrls.add(uploadResultBean.body.attachment.get(i).urlName);
        }

        if (imgUrls.size() != contentEditor.getImgPathList().size()) {
            onUploadError("部分图片上传失败，请重试！可能原因：图片太大；暂不支持该格式（例如HEIC、HEIF）的图片");
        } else {
            presenter.sendPost(contentEditor,
                    currentBoardId, currentFilterId,
                    postTitle.getText().toString(),
                    imgUrls, imgIds,
                    currentPollOptions, attachments, currentPollChoice, currentPollExp,
                    currentPollVisible, currentPollShowVoters, currentAnonymous, currentOnlyAuthor,
                    this);
        }
    }

    @Override
    public void onUploadError(String msg) {
        progressDialog.dismiss();
        showToast("上传图片失败：" + msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onCompressImageSuccess(List<File> compressedFiles) {
        progressDialog.setMessage("图片压缩成功，正在上传图片，请稍候...");
        presenter.uploadImages(compressedFiles, "forum", "image", this);
    }

    @Override
    public void onCompressImageFail(String msg) {
        progressDialog.dismiss();
        showToast("压缩图片失败：" + msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onPermissionGranted(int action) {
        if (action == ACTION_ADD_PHOTO) {
            PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofImage())
                    .isCamera(true)
                    .isGif(true)
                    .showCropFrame(false)
                    .hideBottomControls(false)
                    .maxSelectNum(20)
                    .isEnableCrop(false)
                    .imageEngine(GlideEngineForPictureSelector.createGlideEngine())
                    .forResult(action);
        }
    }

    @Override
    public void onPermissionRefused() {
        showToast(getString(R.string.permission_request), ToastType.TYPE_WARNING);
    }

    @Override
    public void onPermissionRefusedWithNoMoreRequest() {
        showToast(getString(R.string.permission_refuse), ToastType.TYPE_ERROR);
    }

    @Override
    public void onStartUploadAttachment() {
        progressDialog.show();
        progressDialog.setMessage("正在上传附件，请稍候...");
    }

    @Override
    public void onUploadAttachmentSuccess(AttachmentBean attachmentBean, String msg) {
        progressDialog.dismiss();
        attachments.put(attachmentBean.uri, attachmentBean.aid);
        attachmentAdapter.addData(attachmentBean);
    }

    @Override
    public void onUploadAttachmentError(String msg) {
        progressDialog.dismiss();
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onGetUserPostSuccess(CommonPostBean userPostBean) {
        if (userPostBean != null && userPostBean.list != null && userPostBean.list.size() > 0) {
            int tid = userPostBean.list.get(0).topic_id;
            Intent intent = new Intent(this, NewPostDetailActivity.class);
            intent.putExtra(Constant.IntentKey.TOPIC_ID, tid);
            startActivity(intent);
        }
        progressDialog.dismiss();
        finish();
    }

    @Override
    public void onGetUserPostError(String msg) {
        Intent intent = new Intent(this, UserDetailActivity.class);
        intent.putExtra(Constant.IntentKey.USER_ID, SharePrefUtil.getUid(this));
        startActivity(intent);
        progressDialog.dismiss();
        finish();
    }

    @Override
    public void onGetUserDetailSuccess(UserDetailBean userDetailBean) {
        if (userDetailBean != null && userDetailBean.body != null
                && userDetailBean.body.creditList != null && userDetailBean.body.creditList.size() >= 3) {
            currentShuiDiCount = userDetailBean.body.creditList.get(2).data;
            setSanShuiDsp();
        }
    }

    @Override
    public void onSanShuiSuccess(int tid) {
        progressDialog.dismiss();
        showToast("散水成功", ToastType.TYPE_SUCCESS);
        Intent intent = new Intent(this, NewPostDetailActivity.class);
        intent.putExtra(Constant.IntentKey.TOPIC_ID, tid);
        startActivity(intent);
    }

    @Override
    public void onSanShuiError(String msg) {
        progressDialog.dismiss();
        showToast(msg, ToastType.TYPE_ERROR);
    }

    @Override
    public void onMoreOptionsChanged(boolean isAnonymous, boolean isOnlyAuthor, boolean originalPic) {
        this.currentAnonymous = isAnonymous;
        this.currentOnlyAuthor = isOnlyAuthor;
        this.currentOriginalPic = originalPic;
        sendBtn1.setText(currentAnonymous ? "匿名发表" : "发表");
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Override
    public void onEmotionClick(@Nullable String path) {
        if (path != null) {
            contentEditor.insertEmotion(path);
        }
    }

    @Override
    public void onEventBusReceived(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.BOARD_SELECTED) {
            SelectBoardResultEvent boardSelected = (SelectBoardResultEvent)baseEvent.eventData;
            currentBoardId = boardSelected.getChildBoardId();
            currentBoardName = boardSelected.getChildBoardName();
            currentFilterId = boardSelected.getClassificationId();
            currentFilterName = boardSelected.getClassificationName();

            // 全部使用Discourse板块，直接显示板块名称
            boardName.setText(currentBoardName);
        }
        if (baseEvent.eventCode == BaseEvent.EventCode.DELETE_POLL) {
            pollLayout.setVisibility(View.GONE);
            currentPollOptions = new ArrayList<>();
            createPostPollAdapter.setNewData(currentPollOptions);
        }
        if (baseEvent.eventCode == BaseEvent.EventCode.ADD_POLL) {
            pollLayout.setVisibility(View.VISIBLE);
            BaseEvent.AddPoll addPoll = (BaseEvent.AddPoll)baseEvent.eventData;
            currentPollOptions = addPoll.pollOptions;
            currentPollChoice = addPoll.pollChoice;
            currentPollVisible = addPoll.pollVisible;
            currentPollShowVoters = addPoll.showVoters;

            createPostPollAdapter.setNewData(addPoll.pollOptions);

            String a = "可选" + addPoll.pollChoice + "项，";
            String c = "投票" + (addPoll.pollVisible ? "后结果可见，" : "前结果可见，");
            String d = (addPoll.showVoters ? "公开" : "不公开") + "投票参与人";
            pollDesp.setText(new StringBuilder().append(a).append(c).append(d));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_ADD_PHOTO && resultCode == Activity.RESULT_OK && data != null) {
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            for (int i = 0; i < selectList.size(); i ++) {
                contentEditor.insertImage(selectList.get(i).getRealPath(), 1000);
            }
        }
//        if (requestCode == 999 && resultCode == Activity.RESULT_OK && data != null) {
//            List<MediaEntity> selectList = data.getParcelableArrayListExtra("data");
//            for (int i = 0; selectList != null && i < selectList.size(); i ++) {
//                contentEditor.insertImage(selectList.get(i).getAbsolutePath(), 1000);
//            }
//        }
        if (requestCode == AT_USER_REQUEST && resultCode == AtUserListActivity.AT_USER_RESULT && data != null) {
            contentEditor.insertText(data.getStringExtra(Constant.IntentKey.AT_USER));
        }
        if (requestCode == ADD_ATTACHMENT_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (!attachments.containsKey(uri)) {
                presenter.readyUploadAttachment(this, uri, currentBoardId);
            } else {
                showToast("已添加该文件，无需重复添加", ToastType.TYPE_NORMAL);
            }
        }
    }

    private void setSanShuiDsp() {
        try {
            currentSanShuiEachTime = Integer.parseInt(sanShuiEachTime.getText().toString());
            currentSanShuiRandom = Integer.parseInt(sanShuiRandom.getText().toString().replace("%", ""));
            currentSanShuiCountEachReply = Integer.parseInt(sanShuiCountEachReply.getText().toString());
            currentSanShuiTotalTimes = Integer.parseInt(sanShuiTotalTimes.getText().toString());
            int totalNoTax = currentSanShuiCountEachReply * currentSanShuiTotalTimes;
            currentTaxShuiDiCount = (int) Math.ceil(totalNoTax * 1.45f);
            sanShuiDsp.setText(getString(R.string.san_shui_dsp, totalNoTax, currentTaxShuiDiCount, currentShuiDiCount));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * author: sca_tl
     * description: 保存草稿
     */
    private void onSaveDraftData() {
        List<ContentEditor.EditData> dataList = contentEditor.buildEditorData();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < dataList.size(); i ++) {
            JSONObject content_json = new JSONObject();
            if (dataList.get(i).content_type == ContentEditor.CONTENT_TYPE_TEXT) {
                content_json.put("content_type", ContentEditor.CONTENT_TYPE_TEXT);
                content_json.put("content", dataList.get(i).inputStr);
            }
            if (dataList.get(i).content_type == ContentEditor.CONTENT_TYPE_IMAGE) {
                content_json.put("content_type", ContentEditor.CONTENT_TYPE_IMAGE);
                content_json.put("content", dataList.get(i).imagePath);
            }
            jsonArray.add(content_json);
        }

        PostDraftBean postDraftBean = new PostDraftBean();
        postDraftBean.board_id = currentBoardId;
        postDraftBean.filter_id = currentFilterId;
        postDraftBean.title = postTitle.getText().toString();
        postDraftBean.content = jsonArray.toJSONString();
        postDraftBean.board_name = currentBoardName;
        postDraftBean.filter_name = currentFilterName;
        postDraftBean.time = createTime;
        postDraftBean.poll_options = currentPollOptions.toString();
        postDraftBean.poll_choices = currentPollChoice;
        postDraftBean.poll_exp = currentPollExp;
        postDraftBean.poll_visible = currentPollVisible;
        postDraftBean.poll_show_voters = currentPollShowVoters;
        postDraftBean.anonymous = currentAnonymous;
        postDraftBean.only_user = currentOnlyAuthor;
        postDraftBean.isSanShui = isSanShui;

        postDraftBean.saveOrUpdate("time = ?", String.valueOf(createTime));
    }

    private final CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {
        @Override
        public void onTick(long l) { }

        @Override
        public void onFinish() {
            onSaveDraftData();
            autoSaveText.setText(String.valueOf(TimeUtil.getFormatDate(TimeUtil.getLongMs(), "HH:mm:ss") + "  已自动保存"));
            countDownTimer.start();
        }
    };

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        if (sendPostSuccess) {
            //删除草稿数据
            LitePal.deleteAll(PostDraftBean.class, "time = " + createTime);
        } else {
            onSaveDraftData();
            showToast("已保存至草稿", ToastType.TYPE_SUCCESS);
        }
        EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.EXIT_CREATE_POST));
        super.onDestroy();
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setColor(
                this,
                ColorUtil.getAttrColor(this, R.attr.colorOnSurfaceInverse), 0);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        setSanShuiDsp();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        setSanShuiDsp();
    }
}

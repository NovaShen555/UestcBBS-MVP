package com.novashen.riverside.api.discourse.entity;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PollVoteResponse {
    public TopicDetailResponse.Poll poll;

    @SerializedName("vote")
    public List<String> vote;
}

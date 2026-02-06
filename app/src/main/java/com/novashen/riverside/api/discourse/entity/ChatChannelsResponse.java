package com.novashen.riverside.api.discourse.entity;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChatChannelsResponse {

    @SerializedName("channels")
    private List<ChatChannel> channels;

    public List<ChatChannel> getChannels() {
        return channels;
    }

    public void setChannels(List<ChatChannel> channels) {
        this.channels = channels;
    }
}

package com.novashen.riverside.api.discourse.entity;

import com.google.gson.annotations.SerializedName;

public class ChatChannel {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("chatable")
    private Chatable chatable;

    @SerializedName("last_message")
    private ChatMessage lastMessage;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Chatable getChatable() {
        return chatable;
    }

    public void setChatable(Chatable chatable) {
        this.chatable = chatable;
    }

    public ChatMessage getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(ChatMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    public static class Chatable {
        @SerializedName("name")
        private String name;
        @SerializedName("color")
        private String color;
        @SerializedName("description")
        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}

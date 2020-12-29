package me.spec.eris.event.chat;

import me.spec.eris.event.Event;

public class ChatMessageEvent extends Event {

    private String chatMessage;

    public ChatMessageEvent(String chatMessage) {
        this.chatMessage = chatMessage;
    }

    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }
}

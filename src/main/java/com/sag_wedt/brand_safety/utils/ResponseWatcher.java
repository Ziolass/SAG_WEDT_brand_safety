package com.sag_wedt.brand_safety.utils;

import akka.actor.ActorRef;
import com.sag_wedt.brand_safety.messages.CommonMessages;

import java.time.LocalDateTime;
import java.util.UUID;

public class ResponseWatcher {
    ActorRef sender;
    LocalDateTime date;
    CommonMessages.MyMessage message;
    int replace;

    public ResponseWatcher(ActorRef sender, CommonMessages.MyMessage message) {
        this.sender = sender;
        this.date = LocalDateTime.now();
        this.message = message;
        this.replace = 0;
    }

    public int getReplace() {
        return replace;
    }

    public void incrementReplace() {
        this.replace = this.replace++;
    }

    public ActorRef getSender() {
        return sender;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public UUID getMessageId() {
        return message.id;
    }

    public CommonMessages.MyMessage getMessage() {
        return message;
    }
}

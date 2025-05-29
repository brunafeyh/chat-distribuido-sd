package common;

import java.io.Serializable;

public class Message implements Serializable {
    private final String sender;
    private String recipient;
    private final String content;
    private final boolean isBroadcast;

    public Message(String sender, String content, boolean isBroadcast) {
        this.sender = sender;
        this.content = content;
        this.isBroadcast = isBroadcast;
    }

    public Message(String sender, String recipient, String content) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.isBroadcast = false;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getContent() {
        return content;
    }

    public boolean isBroadcast() {
        return isBroadcast;
    }

    @Override
    public String toString() {
        if (isBroadcast) {
            return "[" + sender + "]: " + content;
        } else {
            return "[Privado de " + sender + " para " + recipient + "]: " + content;
        }
    }
}

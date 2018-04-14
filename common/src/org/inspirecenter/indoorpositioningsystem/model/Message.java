package org.inspirecenter.indoorpositioningsystem.model;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

public final class Message implements Serializable {

    public enum Type { OK, ERROR };

    private Type type;
    private List<String> messages = new Vector<>();

    public static Message createOkMessage() {
        return new Message(Type.OK);
    }

    public static Message createOkMessage(final String message) {
        final Vector<String> messages = new Vector<>();
        messages.add(message);
        return new Message(Type.OK, messages);
    }

    public static Message createErrorMessage(final String message) {
        final Vector<String> messages = new Vector<>();
        messages.add(message);
        return new Message(Type.ERROR, messages);
    }

    public static Message createErrorMessage(final Vector<String> messages) {
        return new Message(Type.ERROR, new Vector<>(messages));
    }

    private Message(final Type type) {
        this.type = type;
    }

    private Message(final Type type, final List<String> messages) {
        this(type);
        this.messages = messages;
    }

    public Type getType() {
        return type;
    }

    public int getNumberOfMessages() {
        return messages.size();
    }

    public String getMessage(final int index) {
        return messages.get(index);
    }
}
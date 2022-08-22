package com.pickpockethelper;

import net.runelite.api.ChatMessageType;
import net.runelite.client.Notifier;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Simplifies interacting with Runelite's chat- and notification services.
 * Handles any feedback to the player through chat messages and notifications.
 */
@Singleton
public class FeedbackManager {
    private final PickpocketHelperConfig config;

    private final Notifier notifier;

    private final ChatMessageManager chatMessageManager;

    @Inject
    public FeedbackManager(PickpocketHelperConfig config, Notifier notifier, ChatMessageManager chatMessageManager) {
        this.config = config;
        this.notifier = notifier;
        this.chatMessageManager = chatMessageManager;
    }

    /**
     * Send a simple chat messages with a unified type.
     * @param content the text of the chat message.
     * @param type the message's color type.
     */
    public void sendChatMessage(String content, ChatColorType type) {
        String message = new ChatMessageBuilder()
                .append(type)
                .append(content)
                .build();

        sendChatMessage(message);
    }

    /**
     * Send chat message without additional style applied.
     * @param content the content of the message, optionally made with the message builder.
     */
    public void sendChatMessage(String content) {
        chatMessageManager.queue(QueuedMessage.builder()
                .type(ChatMessageType.CONSOLE)
                .runeLiteFormattedMessage(content)
                .build());
    }

    /**
     * Send a notification, with the option to also send the message in chat.
     * @param content content of the notification.
     * @param sendChatMessage if a chat message is included.
     */
    public void sendNotification(String content, boolean sendChatMessage) {
        // Send notification.
        notifier.notify(content);

        // Don't send chat message if muted.
        if (sendChatMessage && !config.muteChatMessages()) {
            sendChatMessage(content, ChatColorType.HIGHLIGHT);
        }
    }
}

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.restaction.MessageActionImpl;

public final class SilentMessages {
    private static final int SUPPRESS_NOTIFICATIONS = 1 << 12;

    private SilentMessages() {
    }

    public static MessageAction send(MessageChannel channel, String content) {
        return action(channel).content(content);
    }

    public static MessageAction send(MessageChannel channel, MessageEmbed embed) {
        return action(channel).embed(embed);
    }

    public static WebhookMessage webhook(WebhookEmbed embed) {
        SilentWebhookMessageBuilder message = new SilentWebhookMessageBuilder();
        message.suppressNotifications();
        message.addEmbeds(embed);
        return message.build();
    }

    private static SilentMessageAction action(MessageChannel channel) {
        return new SilentMessageAction(channel.getJDA(), channel);
    }

    private static final class SilentMessageAction extends MessageActionImpl {
        private SilentMessageAction(JDA api, MessageChannel channel) {
            super(api, null, channel);
        }

        @Override
        protected DataObject getJSON() {
            return super.getJSON().put("flags", SUPPRESS_NOTIFICATIONS);
        }
    }

    private static final class SilentWebhookMessageBuilder extends WebhookMessageBuilder {
        private void suppressNotifications() {
            this.flags |= SUPPRESS_NOTIFICATIONS;
        }
    }
}

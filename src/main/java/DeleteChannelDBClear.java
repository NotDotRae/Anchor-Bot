import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DeleteChannelDBClear extends ListenerAdapter {
    public void onTextChannelDelete(TextChannelDeleteEvent event) {
        String channelId = event.getChannel().getId();

        Main.mapMessage.remove(channelId);
        Main.mapMessageSlow.remove(channelId);
        Main.mapMessageEmbed.remove(channelId);
        Main.mapImageLinkEmbed.remove(channelId);
        Main.mapBigImageLinkEmbed.remove(channelId);
        Main.webhookURL.remove(channelId);
        Main.webhookMessage.remove(channelId);
        Main.mapDisable.remove(channelId);
        ConvexDb.deleteChannel(channelId);
    }
}

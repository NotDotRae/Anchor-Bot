import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.text.NumberFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class JoinNewGuild extends ListenerAdapter {
    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        Member stickyBot = event.getGuild().getMemberById(Main.botId);

        EmbedBuilder em = new EmbedBuilder();

        em.setTitle("AnchorBot Joined a New Server!");
        em.addField("Server Name: ", event.getGuild().getName(), false);
        em.addField("Server ID", event.getGuild().getId(), false);
        em.addField("Guild Members: ", NumberFormat.getInstance().format(event.getGuild().retrieveMetaData().complete().getApproximateMembers()), false);
        em.addField("Guild Region: ",  event.getGuild().getRegion().getEmoji() + " " + event.getGuild().getRegion().getName(), false);
        em.addField("Guild Owner Tag", event.getGuild().retrieveOwner().complete().getAsMention(), false);
        em.addField("Guild Owner Raw", event.getGuild().retrieveOwner().complete().getEffectiveName() + "#" + event.getGuild().retrieveOwner().complete().getUser().getDiscriminator(), false);
        em.addField("Guild Owner ID", event.getGuild().retrieveOwner().complete().getId(), false);
        em.addField("Time", OffsetDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)) + " PST", false);
        em.setFooter("AnchorBot is now in " + NumberFormat.getInstance().format(Main.jda.getGuildCache().size()) + " guilds", stickyBot.getUser().getEffectiveAvatarUrl());
        em.setThumbnail(event.getGuild().getIconUrl());
        em.setColor(Color.GREEN);

        Main.jda.getTextChannelById("643974985446326272").sendMessage(em.build()).queue();

        //DM server owner info
        event.getGuild().retrieveOwner().queue((u) -> {
            u.getUser().openPrivateChannel().queue((channel) ->
            {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(Color.yellow);
                eb.setTitle("**Thank You For Adding AnchorBot To Your Server!**");
                eb.setDescription("Here are the basics to get you started:");
                eb.addField("Note:", "The stickied message is sent every 5 messages or 15 seconds to comply with discord TOS.", false);
                eb.addField("**Commands:** ", "Do ``?commands`` or ``?help``", false);
                eb.addField("Issues?", "Make sure the bot has permission to send messages, delete messages, and bypass slow mode in sticky channels.", false);
                eb.addField("Included Features: ", "-Unlimited Stickied Messages."  +
                        "\n-Use Custom Embeds as Stickies." +
                        "\n-Create a sticky embed with a custom name and profile pic." +
                        "\n-Custom Prefix." +
                        "\n-Removes \"Stickied Message:\" header." +
                        "\n-Slower Posting Stickies." +
                        "\n-All features are free to use." +
                        "\n-More to come!", false);
                eb.setFooter("AnchorBot", Main.jda.getShards().get(0).getSelfUser().getAvatarUrl());
                channel.sendMessageEmbeds(eb.build()).queue();
            });
        });

        if (Main.topggAPI != null) {
            int serverCount = (int) Main.jda.getGuildCache().size();
            Main.topggAPI.setStats(serverCount);
        }

    }
}

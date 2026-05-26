import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.OffsetDateTime;

public class WebHookAnchor extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        String prefix = "?";


        if (Main.mapPrefix.containsKey(event.getGuild().getId())) {
            prefix = Main.mapPrefix.get(event.getGuild().getId());
        }


        if (args[0].equalsIgnoreCase(prefix + "setwebhook")) {

            //If server is not features
            if (false) {
                EmbedBuilder emb = new EmbedBuilder();;
                emb.setColor(Color.ORANGE)
                        .setDescription("**Whoops!**\nThis feature command is available to all servers.");
                event.getMessage().replyEmbeds(emb.build()).queue();
                event.getMessage().addReaction("\u274C").queue();
                return;
            }

            //If there is already a URL, remove it
            if (Main.webhookURL.containsKey(event.getChannel().getId())) {
                Main.webhookURL.remove(event.getChannel().getId());
                removeDBurl(event.getChannel().getId());
            }

            if (args.length != 2) {
                event.getMessage().reply("**Whoops!**\nPlease use this format:\n`" + prefix + "setwebhook URL HERE`").queue();
                return;
            }

            String o = event.getMessage().getContentRaw();
            String [] arr = o.split(" ", 2);

            String url = arr[1];

            Main.webhookURL.put(event.getChannel().getId(), url);
            addDBurl(event.getChannel().getId(), url);
            event.getMessage().delete().queue();
            try {
                event.getMessage().delete().queue();
            } catch (Exception e) {
                event.getMessage().reply("*(I am missing `Manage Messages` permission, so just make sure to keep your WebHook URL a secret from server members)*").queue();
            }
            event.getChannel().sendMessage(event.getMember().getAsMention() + " DONE! WebHook URL has been set for this channel.\n*(If the WebHook gets deleted you will need to make a new one and set the WebHook URL again).*").queue();

        }

        if (args[0].equalsIgnoreCase(prefix + "stickwebhook")) {

            if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                event.getMessage().reply(event.getMember().getAsMention() + "**Whoops!**\n You need the `MANAGE_MESSAGES` permission to use this command!").queue();
                event.getMessage().addReaction("\u274C").queue();
                return;
            }

            if (false) {
                EmbedBuilder emb = new EmbedBuilder();;
                emb.setColor(Color.ORANGE)
                        .setDescription("**Whoops!**\nThis feature command is available to all servers.");
                event.getMessage().replyEmbeds(emb.build()).queue();
                event.getMessage().addReaction("\u274C").queue();
                return;
            }


            //if webhook URL is not yet set.
            if (!Main.webhookURL.containsKey(event.getChannel().getId())) {
                EmbedBuilder emb = new EmbedBuilder();
                emb.setDescription("**Whoops!**\nYou need to set the WebHook URL for this channel first!" +
                        "\nYou can do this by going to:\n`Channel Settings -> Integrations -> WebHooks -> New WebHook -> Copy WebHook URL`"
                    + "\nThen add the link by doing:\n`" + prefix + "setwebhook URL HERE` in the channel.")
                        .setColor(Color.ORANGE);
                event.getMessage().addReaction("\u274C").queue();
                event.getMessage().replyEmbeds(emb.build()).queue();
                return;
            }


            String o = event.getMessage().getContentRaw();
            String[] arr = o.split(" ", 2);

            String message = arr[1].trim();

            Main.webhookMessage.put(event.getChannel().getId(), message);
            addDBmessage(event.getChannel().getId(), message);

            // Using the builder
            WebhookClientBuilder builder = new WebhookClientBuilder(Main.webhookURL.get(event.getChannel().getId()));
            builder.setThreadFactory((job) -> {
                Thread thread = new Thread(job);
                thread.setName("webhookThread");
                thread.setDaemon(true);
                return thread;
            });
            builder.setWait(true);
            WebhookClient client = builder.build();

            WebhookEmbedBuilder embed = new WebhookEmbedBuilder()
                    .setColor(event.getGuild().getMemberById(Main.botId).getColorRaw())
                    .setDescription(Main.webhookMessage.get(event.getChannel().getId()));


            if (Main.mapImageLinkEmbed.containsKey(event.getChannel().getId())) {
                embed.setThumbnailUrl(Main.mapImageLinkEmbed.get(event.getChannel().getId()));
            }
            if (Main.mapBigImageLinkEmbed.containsKey(event.getChannel().getId())) {
                embed.setImageUrl(Main.mapBigImageLinkEmbed.get(event.getChannel().getId()));
            }

            client.send(embed.build());
            client.close();

            event.getMessage().addReaction("\u2705").queue();
            return;
        }

        if (args[0].equalsIgnoreCase(prefix + "webhookstop")) {
            if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                event.getMessage().reply(event.getMember().getAsMention() + "**Whoops!**\n You need the `MANAGE_MESSAGES` permission to use this command!").queue();
                event.getMessage().addReaction("\u274C").queue();
                return;
            }

            if (Main.webhookMessage.containsKey(event.getChannel().getId())) {
                event.getChannel().getHistory().retrievePast(8).queue(history -> {
                    for (Message m : history.subList(0, 5)) {
                        if (m.isWebhookMessage() && !m.getEmbeds().isEmpty() && m.getEmbeds().get(0).getDescription().equals(Main.webhookMessage.get(event.getChannel().getId()))) {
                            m.delete().queue();
                        }
                    }
                });

                Main.webhookMessage.remove(event.getChannel().getId());
                removeDBmessage(event.getChannel().getId());
                event.getMessage().addReaction("\u2705").queue();
            }
        }



        //do sticky stuff
        if (Main.webhookMessage.containsKey(event.getChannel().getId())) {
            String channelId = event.getChannel().getId();


            event.getChannel().getHistory().retrievePast(8).queue(history -> {

                Boolean check = false;

                try {
                    for (Message m : history.subList(0, 5)) {
                        //if message is sticky message
                        if (m.isWebhookMessage() && !m.getEmbeds().isEmpty() && m.getEmbeds().get(0).getDescription().equals(Main.webhookMessage.get(channelId))) {
                            check = true;
                            //if message is older then 30 sec
                            if (m.getTimeCreated().compareTo(OffsetDateTime.now().minusSeconds(15)) < 0) {
                                m.delete().queue(null, (error) -> {});
                                //send new sticky
                                // Using the builder
                                WebhookClientBuilder builder = new WebhookClientBuilder(Main.webhookURL.get(channelId)); // or id, token
                                builder.setThreadFactory((job) -> {
                                    Thread thread = new Thread(job);
                                    thread.setName("webhookThread");
                                    thread.setDaemon(true);
                                    return thread;
                                });

                                builder.setWait(true);
                                WebhookClient client = builder.build();

                                WebhookEmbedBuilder embed = new WebhookEmbedBuilder()
                                        .setColor(event.getGuild().getMemberById(Main.botId).getColorRaw())
                                        .setDescription(Main.webhookMessage.get(channelId));


                                if (Main.mapImageLinkEmbed.containsKey(channelId)) {
                                     embed.setThumbnailUrl(Main.mapImageLinkEmbed.get(channelId));
                                }
                                if (Main.mapBigImageLinkEmbed.containsKey(channelId)) {
                                    embed.setImageUrl(Main.mapBigImageLinkEmbed.get(channelId));
                                }

                                client.send(embed.build());
                                client.close();
                            }
                            break;
                        }
                    }

                    //if check = true (is set to true if 1 of last 5 are the sticky)
                    if (!check) {
                            for (Message m : history.subList(0, 7)) {
                                if (m.isWebhookMessage() && !m.getEmbeds().isEmpty() && m.getEmbeds().get(0).getDescription().equals(Main.webhookMessage.get(channelId))) {
                                    m.delete().queue(null, (error) -> {});
                                }
                            }

                        //send new sticky
                        // Using the builder
                        WebhookClientBuilder builder = new WebhookClientBuilder(Main.webhookURL.get(channelId)); // or id, token
                        builder.setThreadFactory((job) -> {
                            Thread thread = new Thread(job);
                            thread.setName("webhookThread");
                            thread.setDaemon(true);
                            return thread;
                        });

                        builder.setWait(true);
                        WebhookClient client = builder.build();

                        WebhookEmbedBuilder embed = new WebhookEmbedBuilder()
                                .setColor(event.getGuild().getMemberById(Main.botId).getColorRaw())
                                .setDescription(Main.webhookMessage.get(channelId));


                        if (Main.mapImageLinkEmbed.containsKey(channelId)) {
                            embed.setThumbnailUrl(Main.mapImageLinkEmbed.get(channelId));
                        }
                        if (Main.mapBigImageLinkEmbed.containsKey(channelId)) {
                            embed.setImageUrl(Main.mapBigImageLinkEmbed.get(channelId));
                        }

                        client.send(embed.build());
                        client.close();
                    }

                } catch (Exception e) {
                    //do nothing
                }
                });
        }

    }

    public void addDBurl(String channelId, String url) {
        ConvexDb.upsert(DbKinds.WEBHOOK_URL, channelId, url);
    }


    public void removeDBurl(String channelId) {
        ConvexDb.delete(DbKinds.WEBHOOK_URL, channelId);
    }

    public void addDBmessage(String channelId, String message) {
        ConvexDb.upsert(DbKinds.WEBHOOK_MESSAGE, channelId, message);
    }


    public void removeDBmessage(String channelId) {
        ConvexDb.delete(DbKinds.WEBHOOK_MESSAGE, channelId);
    }

}

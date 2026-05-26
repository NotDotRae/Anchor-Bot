import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.text.NumberFormat;
import java.time.OffsetDateTime;
import java.util.List;

public class AnchorEmbed extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        String[] args = event.getMessage().getContentRaw().split("\\s+");
        //Member stickyBot = event.getGuild().getMemberById(Main.botId);
        Guild stickyServer = Main.jda.getGuildById("0");
        String channelId = event.getChannel().getId();

        String prefix = "?";
        if(Main.mapPrefix.containsKey(event.getGuild().getId())) {
            prefix = Main.mapPrefix.get(event.getGuild().getId());
        }

        if (event.getMessage().getContentRaw().startsWith(prefix + "stickembed") && event.getMessage().getContentRaw().matches("[\\S]+\\s{2,}.*") && (permCheck(event.getMember())) && !event.getAuthor().isBot() && true) {
            event.getMessage().reply(event.getMember().getAsMention() + " please only use one space after the `?stickembed` command!").queue();
            return;
        }

        //Set image for embed command
        if (args[0].equalsIgnoreCase(prefix + "setimage") && (permCheck(event.getMember())) && !event.getAuthor().isBot()) {
            if (false) {
                EmbedBuilder em = new EmbedBuilder();
                em.setTitle("**Whoops! This is an AnchorBot Command!** ")
                        .addField("__AnchorBot__ allows for pin embeds plus other features.", "This command is available to all servers.", false);
                event.getMessage().reply(em.setColor(Color.ORANGE).build()).queue();
            } else {

                if (args.length == 2) {
                    if (Main.mapImageLinkEmbed.containsKey(channelId)) {
                        removeDBimage(channelId);
                        Main.mapImageLinkEmbed.remove(channelId);
                    }
                    Main.mapImageLinkEmbed.put(channelId, args[1]);
                    addDBimage(channelId, args[1]);
                    event.getMessage().reply(event.getMember().getAsMention() + " success! Image set for `?stickembed` pins.").queue();
                } else {
                    event.getMessage().reply(event.getMember().getAsMention() + " please provide an image URL.\nExample: `?setimage <image-url>`.").queue();
                }
            }

        } else if (args[0].equalsIgnoreCase(prefix + "setimage") && (!permCheck(event.getMember()))) {
        //Adds X emote
        event.getMessage().addReaction("\u274C").queue();
            event.getMessage().reply(event.getMember().getAsMention() + " you need the `Manage Messages` permission to use this command!").queue();
    }


        if (args[0].equalsIgnoreCase(prefix + "removeimage") && (permCheck(event.getMember())) && !event.getAuthor().isBot()) {

            if (false) {
                EmbedBuilder em = new EmbedBuilder();
                em.setTitle("**Whoops! This is an AnchorBot Command!** ")
                        .addField("__AnchorBot__ allows for pin embeds plus other features.", "This command is available to all servers.", false);
                event.getMessage().reply(em.setColor(Color.ORANGE).build()).queue();
            } else {
                removeDBimage(channelId);
                Main.mapImageLinkEmbed.remove(channelId);
                event.getMessage().reply(event.getMember().getAsMention() + " success! Image removed for pin embeds in this channel.").queue();
            }

        } else if (args[0].equalsIgnoreCase(prefix + "removeimage") && (!permCheck(event.getMember()))) {
        //Adds X emote
        event.getMessage().addReaction("\u274C").queue();
            event.getMessage().reply(event.getMember().getAsMention() + " you need the `Manage Messages` permission to use this command!").queue();
        }


        if (args[0].equalsIgnoreCase(prefix + "getimage") && (permCheck(event.getMember())) && !event.getAuthor().isBot()) {

            if (Main.mapImageLinkEmbed.containsKey(channelId) || Main.mapBigImageLinkEmbed.containsKey(channelId)) {
                EmbedBuilder em = new EmbedBuilder();
                em.setTitle("Current image for pin embeds in this channel:");
                if (Main.mapImageLinkEmbed.containsKey(channelId)) {
                    em.setThumbnail(Main.mapImageLinkEmbed.get(channelId));
                    em.addField("Small Image Link: ", "[here](" + Main.mapImageLinkEmbed.get(channelId) + ")", true);
                }

                if (Main.mapBigImageLinkEmbed.containsKey(channelId)) {
                    em.setImage(Main.mapBigImageLinkEmbed.get(channelId));
                    em.addField("Big Image Link: ", "[here](" + Main.mapBigImageLinkEmbed.get(channelId) + ")" , true);
                }

                event.getMessage().replyEmbeds(em.setColor(Color.ORANGE).build()).queue();

            } else {
                event.getMessage().reply(event.getMember().getAsMention() + " there is no image currently set for pin embeds in this channel.\nSet one with the `" + prefix + "setimage` command.").queue();
            }

        }





        //Set BIG image for embed command
        if (args[0].equalsIgnoreCase(prefix + "setbigimage") && (permCheck(event.getMember())) && !event.getAuthor().isBot()) {
            if (false) {
                EmbedBuilder em = new EmbedBuilder();
                em.setTitle("**Whoops! This is an AnchorBot Command!** ")
                        .addField("__AnchorBot__ allows for pin embeds plus other features.", "This command is available to all servers.", false);
                event.getMessage().replyEmbeds(em.setColor(Color.ORANGE).build()).queue();
            } else {

                if (args.length == 2) {
                    if (Main.mapBigImageLinkEmbed.containsKey(channelId)) {
                        removeDBBigImage(channelId);
                        Main.mapBigImageLinkEmbed.remove(channelId);
                    }
                    Main.mapBigImageLinkEmbed.put(channelId, args[1]);
                    addDBBigImage(channelId, args[1]);
                    event.getMessage().reply(event.getMember().getAsMention() + " success! Big image set for `?stickembed` pins.").queue();
                } else {
                    event.getMessage().reply(event.getMember().getAsMention() + " please provide an image URL.\nExample: `?setbigimage <image-url>`.").queue();
                }
            }

        } else if (args[0].equalsIgnoreCase(prefix + "setbigimage") && (!permCheck(event.getMember()))) {
            //Adds X emote
            event.getMessage().addReaction("\u274C").queue();
            event.getMessage().reply(event.getMember().getAsMention() + " you need the `Manage Messages` permission to use this command!").queue();
        }


        if (args[0].equalsIgnoreCase(prefix + "removebigimage") && (permCheck(event.getMember())) && !event.getAuthor().isBot()) {

            if (false) {
                EmbedBuilder em = new EmbedBuilder();
                em.setTitle("**Whoops! This is an AnchorBot Command!** ")
                        .addField("__AnchorBot__ allows for pin embeds plus other features.", "This command is available to all servers.", false);
                event.getMessage().replyEmbeds(em.setColor(Color.ORANGE).build()).queue();
            } else {
                removeDBBigImage(channelId);
                Main.mapBigImageLinkEmbed.remove(channelId);
                event.getMessage().reply(event.getMember().getAsMention() + " success! Big image removed for pin embeds in this channel.").queue();
            }

        } else if (args[0].equalsIgnoreCase(prefix + "removebigimage") && (!permCheck(event.getMember()))) {
            //Adds X emote
            event.getMessage().addReaction("\u274C").queue();
            event.getMessage().reply(event.getMember().getAsMention() + " you need the `Manage Messages` permission to use this command!").queue();
        }


        if (args[0].equalsIgnoreCase(prefix + "getbigimage") && (permCheck(event.getMember())) && !event.getAuthor().isBot()) {

            if (Main.mapImageLinkEmbed.containsKey(channelId)) {
                EmbedBuilder em = new EmbedBuilder();
                em.setTitle("Current big image for pin embeds in this channel:");
                em.setThumbnail(Main.mapBigImageLinkEmbed.get(channelId));
                em.setDescription("Link: " + Main.mapBigImageLinkEmbed.get(channelId));
                event.getMessage().replyEmbeds(em.setColor(Color.ORANGE).build()).queue();

            } else {
                event.getMessage().reply(event.getMember().getAsMention() + " there is no image currently set for pin embeds in this channel.\nSet one with the `" + prefix + "setimage` command.").queue();
            }

        }



        if (args[0].equalsIgnoreCase(prefix + "stickembed") && (permCheck(event.getMember())) && !event.getAuthor().isBot()) {


            if (false) {
                EmbedBuilder em = new EmbedBuilder();
                em.setTitle("**Whoops! This is an AnchorBot Command!** ")
                        .addField("__AnchorBot__ allows for pin embeds plus other features.", "This command is available to all servers.", false);
                event.getMessage().replyEmbeds(em.setColor(Color.ORANGE).build()).queue();
            }
            else  {
                try {

                    for (Emote emote : event.getMessage().getEmotes()) {
                        event.getGuild().retrieveEmoteById(emote.getId()).queue(success -> {}, failure -> {
                            event.getMessage().reply(event.getMember().getAsMention() + " Error: Please only use emotes that are from this server.").queue();
                            Main.mapMessageEmbed.remove(event.getChannel().getId());
                            removeDB(channelId);
                        });
                    }

                    if (event.getMessage().getContentRaw().contains(prefix + "stickembed \n")) {
                        event.getMessage().reply(event.getMember().getAsMention() + " Error: Please provide text after `" + prefix + "stickembed` before using a new line.").queue();
                        return;
                    }

                    String o = event.getMessage().getContentRaw();
                    String [] arr = o.split(" ", 2);

                    String message = arr[1];

                    if(Character.isWhitespace(message.charAt(0))) {
                        message.replaceFirst("\\s+", "");
                    }


                    Main.mapMessageEmbed.put(event.getChannel().getId(), message);
                    removeDB(channelId);
                    addDB(channelId,(message));

                    EmbedBuilder emb = new EmbedBuilder();
                    emb.setDescription(message);

                    if (Main.mapImageLinkEmbed.containsKey(channelId)) {
                        emb.setThumbnail(Main.mapImageLinkEmbed.get(channelId));
                    }
                    if (Main.mapBigImageLinkEmbed.containsKey(channelId)) {
                        emb.setImage(Main.mapBigImageLinkEmbed.get(channelId));
                    }

                    emb.setColor(event.getGuild().getMemberById(Main.botId).getColor());
                    SilentMessages.send(event.getChannel(), emb.build()).queue(m -> Main.mapDeleteIdEmbed.put(event.getChannel().getId(), m.getId()));
                    event.getMessage().addReaction("\u2705").queue();
                } catch (Exception e) {
                    event.getMessage().reply(event.getMember().getAsMention() + " please use this format: `" + prefix + "stickembed <message>`\n*Only include emotes that are from this server.*").queue();
                }
            }

        } else if (args[0].equalsIgnoreCase(prefix + "stickembed") && (!permCheck(event.getMember()))) {
            //Adds X emote
            event.getMessage().addReaction("\u274C").queue();
            //event.getChannel().sendMessage("You need the `Manage Messages` permission to use this command!").queue();
        }

        else if ( (args[0].equalsIgnoreCase(prefix + "stickstop") || args[0].equalsIgnoreCase(prefix + "unstick")) && (permCheck(event.getMember()))) {
            Main.mapMessageEmbed.remove(channelId);

            if(Main.mapDeleteIdEmbed.get(channelId) != null) {
                event.getChannel().deleteMessageById(Main.mapDeleteIdEmbed.get(channelId)).queue(null, (error) -> {});
            }

            removeDB(channelId);
            event.getMessage().addReaction("\u2705").queue();
        } else if ( (args[0].equalsIgnoreCase(Main.prefix + "stickstop") || args[0].equalsIgnoreCase(Main.prefix + "unstick")) && (!permCheck(event.getMember() ))) {
            //Adds X mark
            event.getMessage().addReaction("\u274C").queue();
            //event.getChannel().sendMessage("You need the global `Manage Messages` permission to use this command!").queue();
        }

        if(Main.mapMessageEmbed.get(channelId) != null) {
            event.getChannel().getHistory().retrievePast(5).queue(history -> {

                for(Message m : history) {
                    //if message is sticky message
                    if(!m.getEmbeds().isEmpty() && embedCheck(m, channelId)) {
                        //if message is older then 30 sec
                        if(m.getTimeCreated().compareTo(OffsetDateTime.now().minusSeconds(15)) < 0) {
                            m.delete().queue(null, (error) -> {});

                            EmbedBuilder emb = new EmbedBuilder();
                            emb.setDescription(Main.mapMessageEmbed.get(channelId));
                            emb.setColor(event.getGuild().getMemberById(Main.botId).getColor());
                            if (Main.mapImageLinkEmbed.containsKey(channelId)) {
                                emb.setThumbnail(Main.mapImageLinkEmbed.get(channelId));
                            }
                            if (Main.mapBigImageLinkEmbed.containsKey(channelId)) {
                                emb.setImage(Main.mapBigImageLinkEmbed.get(channelId));
                            }
                            SilentMessages.send(event.getChannel(), emb.build()).queue(mes -> Main.mapDeleteIdEmbed.put(channelId, mes.getId()));

                            //Added to make sure it does not bug and send two stickies (next 5 lines)
                            event.getChannel().getHistory().retrievePast(10).queue(messageListDelete -> {

                            for (Message mess : messageListDelete.subList(1, 10)) {
                                if (!mess.getEmbeds().isEmpty() && embedCheck(mess, channelId)) {
                                    mess.delete().queue(null, (error) -> {});
                                }
                            }
                            });
                        }
                        break;
                    }
                }

                //gets set to true if one of last five messages contains sticky message.
                Boolean check = false;

                for(Message m : history) {
                    if(!m.getEmbeds().isEmpty() && embedCheck(m, channelId)) {
                        check = true;
                    }
                }
                if(!check) {
                    if(Main.mapDeleteIdEmbed.get(channelId) != null) {
                        event.getChannel().deleteMessageById(Main.mapDeleteIdEmbed.get(channelId)).queue(null, (error) -> {});
                    }

                    event.getChannel().getHistory().retrievePast(6).queue(history2 -> {
                        for(Message m : history2) {
                            //if message is sticky message
                            if(!m.getEmbeds().isEmpty() && m.getEmbeds().get(0).getDescription().equals(Main.mapMessageEmbed.get(channelId))) {
                                m.delete().queue(null, (error) -> {});
                            }
                        }
                    });

                    EmbedBuilder emb = new EmbedBuilder();
                    emb.setDescription(Main.mapMessageEmbed.get(channelId));
                    if (Main.mapImageLinkEmbed.containsKey(channelId)) {
                        emb.setThumbnail(Main.mapImageLinkEmbed.get(channelId));
                    }
                    if (Main.mapBigImageLinkEmbed.containsKey(channelId)) {
                        emb.setImage(Main.mapBigImageLinkEmbed.get(channelId));
                    }
                    emb.setColor(event.getGuild().getMemberById(Main.botId).getColor());

                    try {
                        SilentMessages.send(event.getChannel(), emb.build()).queue(null, (error) -> {

                            Main.mapMessageEmbed.remove(channelId);
                            Main.mapDeleteId.remove(channelId);
                            removeDB(channelId);
                            System.out.println("Tried to send sticky message in channel with no MESSAGE_WRITE perms. Sticky message has been stopped:");
                            System.out.println("Channel ID: " + channelId + "\nServer ID: " + event.getGuild().getId());

                        });
                    } catch (Exception e) {
                        Main.mapMessageEmbed.remove(channelId);
                        Main.mapDeleteId.remove(channelId);
                        removeDB(channelId);
                        System.out.println("Tried to send sticky message in channel with no MESSAGE_WRITE perms. Sticky message has been stopped:");
                        System.out.println("Channel ID: " + channelId + "\nServer ID: " + event.getGuild().getId());
                    }
                }
            });
        }
    }

    public boolean permCheck(Member member) {
        if (member.hasPermission(Permission.MESSAGE_MANAGE)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean embedCheck(Message mess, String channelId) {
        //returns true if embed description is sticky message

        try {
            if (mess.getEmbeds().get(0).getDescription().contains(Main.mapMessageEmbed.get(channelId)) ) {
                return true;
            }
            else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }


    public void addDB(String channelId, String message) {
        ConvexDb.upsert(DbKinds.EMBED_STICKY, channelId, message);
    }

    public void removeDB(String channelId) {
        ConvexDb.delete(DbKinds.EMBED_STICKY, channelId);
    }


    public void addDBimage(String channelId, String message) {
        ConvexDb.upsert(DbKinds.EMBED_IMAGE, channelId, message);
    }

    public void removeDBimage(String channelId) {
        ConvexDb.delete(DbKinds.EMBED_IMAGE, channelId);
    }

    public void addDBBigImage(String channelId, String message) {
        ConvexDb.upsert(DbKinds.BIG_EMBED_IMAGE, channelId, message);
    }

    public void removeDBBigImage(String channelId) {
        ConvexDb.delete(DbKinds.BIG_EMBED_IMAGE, channelId);
    }

}

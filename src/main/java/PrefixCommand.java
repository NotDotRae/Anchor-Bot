import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PrefixCommand extends ListenerAdapter
{
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");

        String prefix = "?";

        if(event.getAuthor().isBot()) {
            return;
        }

        if(Main.mapPrefix.containsKey(event.getGuild().getId())) {
            prefix = Main.mapPrefix.get(event.getGuild().getId());
        }

        if (args[0].equalsIgnoreCase(Main.prefix + "prefix") || args[0].equalsIgnoreCase(prefix + "prefix")) {

             if (!event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                 event.getMessage().reply(event.getMember().getAsMention() + " you need the `Manage Server` permission to use this command.").queue();
             } else {
                 try {
                     if (args[1].isEmpty()) {
                         event.getMessage().reply(event.getMember().getAsMention() + " please provide the new prefix in the command. Example: `?prefix !`").queue();
                      } else {
                         Main.mapPrefix.put(event.getGuild().getId(), args[1]);
                         event.getChannel().sendMessage("Prefix set to `" + args[1] + "`").queue();
                         removeDB(event.getGuild().getId());
                         addDB(event.getGuild().getId(), args[1]);
                      }
                 } catch (Exception e) {
                     event.getMessage().reply(event.getMember().getAsMention() + " please provide the new prefix in the command.").queue();
                    }
                }
            }

        if ( args[0].equalsIgnoreCase(Main.prefix + "resetprefix") || args[0].equalsIgnoreCase("?resetprefix") && (event.getMember().hasPermission(Permission.MANAGE_SERVER)) ) {
            Main.mapPrefix.remove(event.getGuild().getId());
            event.getMessage().reply("Prefix reset to `?`").queue();
            removeDB(event.getGuild().getId());
        }
    }

    public void addDB(String serverId, String prefix) {
        ConvexDb.upsert(DbKinds.PREFIX, serverId, prefix);
    }

    public void removeDB(String serverId) {
        ConvexDb.delete(DbKinds.PREFIX, serverId);
    }


}

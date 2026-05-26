import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.discordbots.api.client.DiscordBotListAPI;

import javax.security.auth.login.LoginException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static net.dv8tion.jda.api.entities.Activity.playing;

public class Main {
    public static final String APP_NAME = "AnchorBot";
    public static final String WEBSITE_URL = "";
    public static final String DOCS_URL = "";
    public static final String SUPPORT_URL = "";
    public static final String INVITE_URL = "";

    public static ShardManager jda;
    public static String prefix = "?";

    public static String botId = env("ANCHORBOT_ID", "YOUR_BOT_ID");
    public static String token = env("ANCHORBOT_TOKEN", "YOUR_BOT_TOKEN");

    public static String convexUrl = env("ANCHORBOT_CONVEX_URL", "");
    public static String convexSiteUrl = env("ANCHORBOT_CONVEX_SITE_URL", "");
    public static String convexKey = env("ANCHORBOT_CONVEX_KEY", "");

    public static String topggAPIToken = env("ANCHORBOT_TOPGG_TOKEN", "");
    public static DiscordBotListAPI topggAPI;

    public static Set<Long> ownerIds = parseOwnerIds(env("ANCHORBOT_OWNER_IDS", ""));

    public static Map<String, String> mapMessage = new HashMap<>();
    public static Map<String, String> mapDeleteId = new HashMap<>();

    public static Map<String, String> mapMessageEmbed = new HashMap<>();
    public static Map<String, String> mapDeleteIdEmbed = new HashMap<>();
    public static Map<String, String> mapImageLinkEmbed = new HashMap<>();
    public static Map<String, String> mapBigImageLinkEmbed = new HashMap<>();

    public static Map<String, String> mapMessageEmbed2 = new HashMap<>();
    public static Map<String, String> mapDeleteIdEmbed2 = new HashMap<>();

    public static Map<String, String> mapDeleteId2 = new HashMap<>();
    public static Map<String, String> mapMessageSlow = new HashMap<>();
    public static Map<String, String> webhookURL = new HashMap<>();
    public static Map<String, String> webhookMessage = new HashMap<>();

    public static Map<String, String> mapPrefix = new HashMap<>();

    //serverId, 1=true
    public static Map<String, String> mapDisable = new HashMap<>();

    public static void main (String[] args) throws LoginException {
        ConvexDb.loadState();


        jda = DefaultShardManagerBuilder.create(token,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS
                ).setChunkingFilter(ChunkingFilter.NONE).setMemberCachePolicy(MemberCachePolicy.NONE)
                 .addEventListeners(
                         new Commands(),
                         new JoinNewGuild(),
                         new DeleteChannelDBClear(),
                         new AnchorTime(),
                         new ShardCommands(),
                         new PrefixCommand(),
                         new AnchorEmbed(),
                         new AdminCommands(),
                         new AdvancedPoll(),
                         new SlowAnchor(),
                         new ButtonTest(),
                         new DisconnectEventMessages(),
                         new HelpNew(),
                         new GetStickCommand()
                 )
                 .disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS)
                .build();

        jda.setActivity(playing("?help"));

        if (!topggAPIToken.isBlank() && !botId.equals("YOUR_BOT_ID")) {
            topggAPI = new DiscordBotListAPI.Builder()
                   .token(topggAPIToken)
                   .botId(botId)
                   .build();
            int serverCount = (int) jda.getGuildCache().size();
            topggAPI.setStats(serverCount);
        }

    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }

    public static boolean isOwner(long userId) {
        return ownerIds.contains(userId);
    }

    private static Set<Long> parseOwnerIds(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return new HashSet<>();
        }

        return Arrays.stream(rawValue.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(Long::parseLong)
                .collect(Collectors.toSet());
    }
}


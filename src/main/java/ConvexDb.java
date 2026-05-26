import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConvexDb {
    private static final Gson gson = new Gson();
    private static final Path OUTBOX_PATH = Path.of("convex-outbox.jsonl");

    public static void loadState() {
        if (!isConfigured()) {
            System.out.println("Convex settings are missing.");
            return;
        }

        flushOutbox();

        try {
            HttpResponse<String> response = request("state", null);
            if (!isOk(response)) {
                System.out.println("Convex state load failed: " + response.getStatus() + ". Check the site URL and deploy the Convex HTTP routes.");
                return;
            }

            JsonObject root = JsonParser.parseString(response.getBody()).getAsJsonObject();
            loadMap(root, "stickies", Main.mapMessage);
            loadMap(root, "slowStickies", Main.mapMessageSlow);
            loadMap(root, "embedStickies", Main.mapMessageEmbed);
            loadMap(root, "embedImages", Main.mapImageLinkEmbed);
            loadMap(root, "bigEmbedImages", Main.mapBigImageLinkEmbed);
            loadMap(root, "disabled", Main.mapDisable);
            loadMap(root, "webhookUrls", Main.webhookURL);
            loadMap(root, "webhookMessages", Main.webhookMessage);
            loadMap(root, "prefixes", Main.mapPrefix);
        } catch (Exception e) {
            System.out.println("Convex state load failed.");
            e.printStackTrace();
        }
    }

    public static void upsert(String kind, String id, String value) {
        JsonObject body = new JsonObject();
        body.addProperty("kind", kind);
        body.addProperty("id", id);
        body.addProperty("value", value);
        write("upsert", body);
    }

    public static void delete(String kind, String id) {
        JsonObject body = new JsonObject();
        body.addProperty("kind", kind);
        body.addProperty("id", id);
        write("delete", body);
    }

    public static void deleteChannel(String channelId) {
        JsonObject body = new JsonObject();
        body.addProperty("channelId", channelId);
        write("deleteChannel", body);
    }

    public static void flushOutbox() {
        if (!isConfigured() || !Files.exists(OUTBOX_PATH)) {
            return;
        }

        try {
            List<String> remaining = new ArrayList<>();
            for (String line : Files.readAllLines(OUTBOX_PATH, StandardCharsets.UTF_8)) {
                if (line.isBlank()) {
                    continue;
                }

                JsonObject payload = JsonParser.parseString(line).getAsJsonObject();
                HttpResponse<String> response = request(payload.get("path").getAsString(), payload.getAsJsonObject("body"));
                if (!isOk(response)) {
                    remaining.add(line);
                }
            }

            if (remaining.isEmpty()) {
                Files.deleteIfExists(OUTBOX_PATH);
            } else {
                Files.write(OUTBOX_PATH, remaining, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (Exception e) {
            System.out.println("Convex retry queue flush failed.");
            e.printStackTrace();
        }
    }

    private static void write(String path, JsonObject body) {
        if (!isConfigured()) {
            enqueue(path, body);
            return;
        }

        try {
            HttpResponse<String> response = request(path, body);
            if (!isOk(response)) {
                enqueue(path, body);
                System.out.println("Convex write queued: " + response.getStatus());
            }
        } catch (Exception e) {
            enqueue(path, body);
            System.out.println("Convex write queued after request failure.");
            e.printStackTrace();
        }
    }

    private static HttpResponse<String> request(String path, JsonObject body) {
        String baseUrl = Main.convexSiteUrl;
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = Main.convexUrl;
        }
        baseUrl = baseUrl.replace(".convex.cloud", ".convex.site");
        String url = baseUrl.replaceAll("/+$", "") + "/" + path;
        if (body == null) {
            return Unirest.get(url)
                    .header("Authorization", "Bearer " + Main.convexKey)
                    .asString();
        }

        return Unirest.post(url)
                .header("Authorization", "Bearer " + Main.convexKey)
                .header("Content-Type", "application/json")
                .body(gson.toJson(body))
                .asString();
    }

    private static boolean isOk(HttpResponse<String> response) {
        return response.getStatus() >= 200 && response.getStatus() < 300;
    }

    private static boolean isConfigured() {
        String baseUrl = Main.convexSiteUrl;
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = Main.convexUrl;
        }
        return baseUrl != null && !baseUrl.isBlank()
                && Main.convexKey != null && !Main.convexKey.isBlank();
    }

    private static void enqueue(String path, JsonObject body) {
        try {
            JsonObject payload = new JsonObject();
            payload.addProperty("path", path);
            payload.add("body", body);
            Files.writeString(OUTBOX_PATH, gson.toJson(payload) + System.lineSeparator(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Failed to write Convex retry queue.");
            e.printStackTrace();
        }
    }

    private static void loadMap(JsonObject root, String key, Map<String, String> target) {
        target.clear();
        if (!root.has(key) || !root.get(key).isJsonObject()) {
            return;
        }

        JsonObject object = root.getAsJsonObject(key);
        for (String id : object.keySet()) {
            target.put(id, object.get(id).getAsString());
        }
    }
}

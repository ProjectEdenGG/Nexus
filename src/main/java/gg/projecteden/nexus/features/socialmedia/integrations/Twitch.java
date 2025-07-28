package gg.projecteden.nexus.features.socialmedia.integrations;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.SocialMediaSite;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUserService;
import gg.projecteden.nexus.models.twitch.TwitchOAuthConfigService;
import gg.projecteden.nexus.utils.HttpUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import lombok.SneakyThrows;
import okhttp3.FormBody.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Twitch {

	private static final LoadingCache<UUID, StreamingResponse> STREAMING_CACHE = CacheBuilder.newBuilder()
		.expireAfterWrite(3, TimeUnit.MINUTES)
		.build(new CacheLoader<>() {
			public StreamingResponse load(UUID uuid) {
				return checkStreaming(uuid);
			}
		});

	static {
		Tasks.repeat(TickTime.MINUTE, TickTime.MINUTE.x(5), () -> {
			for (Player player : OnlinePlayers.getAll())
				checkStreaming(player.getUniqueId());
		});
	}

	@SneakyThrows
	public static boolean isStreaming(UUID uuid) {
		return STREAMING_CACHE.get(uuid).isLive();
	}

	@SneakyThrows
	public static StreamingResponse checkStreaming(UUID uuid) {
		var configService = new TwitchOAuthConfigService();
		var config = configService.get0();

		var userService = new SocialMediaUserService();
		var user = userService.get(uuid);
		var connection = user.getConnection(SocialMediaSite.TWITCH);

		if (connection == null || connection.getUsername() == null)
			return new StreamingResponse();

		var request = new Request.Builder()
			.url("https://api.twitch.tv/helix/streams?user_login=" + connection.getUsername())
			.addHeader("Client-ID", config.getClientId())
			.addHeader("Authorization", "Bearer " + config.getAccessToken())
			.build();

		try (var response = HttpUtils.getClient().newCall(request).execute()) {
			if (response.code() == 401) {
				Nexus.log("[Twitch] Access token expired. Refreshing token...");
				retrieveAccessToken();
				return checkStreaming(uuid); // Retry with new token
			}

			if (!response.isSuccessful())
				throw new IOException("Failed to get " + user.getNickname() + " streaming status: Status: " + response.code() + ", Body: " + (response.body() != null ? response.body().string() : null));

			if (response.body() == null)
				throw new IOException("Failed to get " + user.getNickname() + " streaming status: Response body is null");

			var responseBody = response.body().string();
			var array = new JSONObject(responseBody).getJSONArray("data");
			if (array.isEmpty())
				return new StreamingResponse();

			return Utils.getGson().fromJson(array.getJSONObject(0).toString(), StreamingResponse.class);
		}
	}

	@SneakyThrows
	private static void retrieveAccessToken() throws IOException {
		var service = new TwitchOAuthConfigService();
		var config = service.get0();

		RequestBody body = new Builder()
			.add("client_id", config.getClientId())
			.add("client_secret", config.getClientSecret())
			.add("grant_type", "client_credentials")
			.build();

		Request request = new Request.Builder()
			.url("https://id.twitch.tv/oauth2/token")
			.post(body)
			.build();

		try (var response = HttpUtils.getClient().newCall(request).execute()) {
			if (!response.isSuccessful())
				throw new IOException("Failed to get OAuth token: Status: " + response.code() + ", Body: " + (response.body() != null ? response.body().string() : null));

			if (response.body() == null)
				throw new IOException("Failed to get OAuth token: Response body is null");

			var responseBody = response.body().string();
			config.setAccessToken(new JSONObject(responseBody).getString("access_token"));
			service.save(config);
		}
	}

	public static class StreamingResponse {
		private LocalDateTime localDateTime;
		private String id;
		private String user_id;
		private String user_login;
		private String user_name;
		private String game_id;
		private String game_name;
		private String type;
		private String title;
		private int viewer_count;
		private LocalDateTime started_at;
		private String language;
		private String thumbnail_url;
		private List<String> tags;
		private boolean is_mature;

		public boolean isLive() {
			return "live".equals(type);
		}
	}

}

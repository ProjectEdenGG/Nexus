package gg.projecteden.nexus.features.socialmedia.integrations;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.TwitchHelixBuilder;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.SocialMediaSite;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUser.Connection;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUserService;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Twitch {
	@Getter
	public static TwitchHelix client;

	private static final Map<UUID, Boolean> STREAMING_CACHE = new HashMap<>();

	public static void connect() {
		Tasks.async(() -> {
			client = TwitchHelixBuilder.builder()
				.withClientId(Nexus.getInstance().getConfig().getString("tokens.twitch.id"))
				.withClientSecret(Nexus.getInstance().getConfig().getString("tokens.twitch.secret"))
				.build();

			Tasks.repeat(TickTime.MINUTE, TickTime.MINUTE.x(3), () -> {
				for (Player player : OnlinePlayers.getAll())
					checkStreaming(player.getUniqueId());
			});
		});
	}

	public static TwitchHelix get() {
		return client;
	}

	public static CompletableFuture<Boolean> checkStreaming(UUID uuid) {
		CompletableFuture<Boolean> future = new CompletableFuture<>();
		Tasks.async(() -> {
			boolean streaming;

			final Connection connection = new SocialMediaUserService().get(uuid).getConnection(SocialMediaSite.TWITCH);
			if (client == null || connection == null)
				streaming = false;
			else
				streaming = !client.getStreams(null, null, null, 1, null, null, null, List.of(connection.getUsername()))
					.execute()
					.getStreams()
					.isEmpty();

			STREAMING_CACHE.put(uuid, streaming);
			future.complete(streaming);
		});

		return future;
	}

	public static boolean isStreaming(UUID uuid) {
		return STREAMING_CACHE.getOrDefault(uuid, false);
	}

}

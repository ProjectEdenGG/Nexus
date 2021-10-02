package gg.projecteden.nexus.features.socialmedia.integrations;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.TwitchHelixBuilder;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.socialmedia.SocialMedia;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
					SocialMedia.checkStreaming(player.getUniqueId());
			});
		});
	}

	public static TwitchHelix get() {
		return client;
	}

}

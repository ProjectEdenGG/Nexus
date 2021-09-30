package gg.projecteden.nexus.features.socialmedia.integrations;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.TwitchHelixBuilder;
import com.github.twitch4j.helix.domain.StreamList;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.SocialMediaSite;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUser;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUser.Connection;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.Utils;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.Member;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class Twitch {
	@Getter
	public static TwitchHelix client;

	public static void connect() {
		Tasks.async(() -> client = TwitchHelixBuilder.builder()
			.withClientId(Nexus.getInstance().getConfig().getString("tokens.twitch.id"))
			.withClientSecret(Nexus.getInstance().getConfig().getString("tokens.twitch.secret"))
			.build());
	}

	public static TwitchHelix get() {
		return client;
	}

	public static CompletableFuture<Boolean> isStreaming(SocialMediaUser user) {
		final Connection connection = user.getConnection(SocialMediaSite.TWITCH);
		if (connection == null)
			return CompletableFuture.completedFuture(false);

		Future<StreamList> streams = client.getStreams(null, null, null, 1, null, null, null, List.of(connection.getUsername())).queue();
		return CompletableFuture.supplyAsync(() -> {
			try {
				return !streams.get().getStreams().isEmpty();
			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}
		});
	}

	public static boolean isStreaming(DiscordUser user) {
		final Member member = user.getMember();
		if (member == null)
			return false;

		final List<Activity> activities = member.getActivities();
		if (Utils.isNullOrEmpty(activities))
			return false;

		return activities.stream().anyMatch(activity -> activity.getType() == ActivityType.STREAMING);
	}

}

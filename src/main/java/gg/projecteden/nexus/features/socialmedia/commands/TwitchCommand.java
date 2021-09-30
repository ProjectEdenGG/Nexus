package gg.projecteden.nexus.features.socialmedia.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.utils.Utils;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.entity.Player;

import java.util.List;

public class TwitchCommand extends CustomCommand {

	public TwitchCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("status <user>")
	void status(DiscordUser user) {
		final boolean streaming = isStreaming(user);
		send(PREFIX + "User " + (streaming ? "&ais" : "is &cnot") + " &3streaming");
	}

	public static boolean isStreaming(Player player) {
		return isStreaming(new DiscordUserService().get(player));
	}

	public static boolean isStreaming(DiscordUser user) {
		final Member member = user.getMember();
		if (member == null) {
			Dev.GRIFFIN.send("1");
			return false;
		}

		final List<Activity> activities = member.getActivities();
		if (Utils.isNullOrEmpty(activities)) {
			Dev.GRIFFIN.send("2");
			return false;
		}

		Dev.GRIFFIN.send("3");
		return activities.stream().anyMatch(activity -> activity.getType() == ActivityType.STREAMING);
	}

}

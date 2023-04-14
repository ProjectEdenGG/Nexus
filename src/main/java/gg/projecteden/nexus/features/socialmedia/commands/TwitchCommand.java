package gg.projecteden.nexus.features.socialmedia.commands;

import gg.projecteden.nexus.features.socialmedia.SocialMedia;
import gg.projecteden.nexus.features.socialmedia.integrations.Twitch;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUserService;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@NoArgsConstructor
public class TwitchCommand extends CustomCommand implements Listener {

	public TwitchCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		Twitch.connect();
	}

	@NoLiterals
	@Description("Receive a link to apply for Creator")
	void run() {
		send(SocialMedia.PREFIX + "Want to become a verified content creator? Apply at &ehttps://projecteden.gg/apply/creator");
	}

	@Path("api status <user>")
	@Permission(Group.ADMIN)
	@Description("View whether a player is streaming")
	void status(DiscordUser user) {
		final boolean streaming = new SocialMediaUserService().get(user.getUuid()).isStreaming();
		send(PREFIX + "User " + (streaming ? "&ais" : "is &cnot") + " &3streaming");
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Tasks.async(() -> SocialMedia.checkStreaming(event.getPlayer().getUniqueId()));
	}

}

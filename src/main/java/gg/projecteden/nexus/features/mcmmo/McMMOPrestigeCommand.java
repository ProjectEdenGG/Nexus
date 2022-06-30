package gg.projecteden.nexus.features.mcmmo;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUserService;
import gg.projecteden.nexus.models.nickname.Nickname;
import org.bukkit.OfflinePlayer;

public class McMMOPrestigeCommand extends CustomCommand {
	private static final McMMOPrestigeUserService service = new McMMOPrestigeUserService();

	public McMMOPrestigeCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void main(@Arg("self") OfflinePlayer player) {
		final var user = service.get(player);

		line();
		send("&ePrestige for " + Nickname.of(player));
		user.getPrestiges().forEach((type, count) -> send("&3" + camelCase(type) + ": &e" + count));
	}

}

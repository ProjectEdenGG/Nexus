package me.pugabyte.nexus.features.mcmmo;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.mcmmo.McMMOPrestige;
import me.pugabyte.nexus.models.mcmmo.McMMOService;
import me.pugabyte.nexus.models.nickname.Nickname;
import org.bukkit.OfflinePlayer;

public class McMMOPrestigeCommand extends CustomCommand {
	private static final McMMOService service = new McMMOService();

	public McMMOPrestigeCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void main(@Arg("self") OfflinePlayer player) {
		McMMOPrestige mcMMOPrestige = service.getPrestige(player.getUniqueId().toString());

		line();
		send("&ePrestige for " + Nickname.of(player));
		mcMMOPrestige.getPrestiges().forEach((type, count) -> send("&3" + camelCase(type) + ": &e" + count));

	}

}

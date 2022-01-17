package gg.projecteden.nexus.features.mcmmo;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.mcmmo.McMMOPrestige;
import gg.projecteden.nexus.models.mcmmo.McMMOService;
import gg.projecteden.nexus.models.nickname.Nickname;
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

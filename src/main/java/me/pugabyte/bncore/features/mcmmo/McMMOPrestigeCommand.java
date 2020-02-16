package me.pugabyte.bncore.features.mcmmo;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.mcmmo.McMMOPrestige;
import me.pugabyte.bncore.models.mcmmo.McMMOService;
import org.bukkit.OfflinePlayer;

import static me.pugabyte.bncore.utils.Utils.camelCase;

public class McMMOPrestigeCommand extends CustomCommand {
	private McMMOService service = new McMMOService();

	public McMMOPrestigeCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void main(@Arg("self") OfflinePlayer player) {
		McMMOPrestige mcMMOPrestige = service.getPrestige(player.getUniqueId().toString());

		line();
		send("Prestige for " + player.getName());
		mcMMOPrestige.getPrestiges().forEach((type, count) -> send(camelCase(type) + ": " + count));

	}

}

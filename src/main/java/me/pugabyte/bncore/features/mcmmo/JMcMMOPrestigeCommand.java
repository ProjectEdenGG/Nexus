package me.pugabyte.bncore.features.mcmmo;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.mcmmo.McMMOPrestige;
import me.pugabyte.bncore.models.mcmmo.McMMOService;
import org.bukkit.OfflinePlayer;

import java.util.Map;

import static me.pugabyte.bncore.Utils.camelCase;

public class JMcMMOPrestigeCommand extends CustomCommand {
	private McMMOService service = new McMMOService();

	public JMcMMOPrestigeCommand(CommandEvent event) {
		super(event);
	}

	@Path("{offlineplayer}")
	void main(@Arg("self") OfflinePlayer player) {
		McMMOPrestige mcMMOPrestige = service.getPrestige(player.getUniqueId().toString());

		newline();
		reply("Prestige for " + player.getName());
		for (Map.Entry<String, Integer> entry : mcMMOPrestige.getPrestiges().entrySet()) {
			String type = entry.getKey();
			Object count = entry.getValue();
			reply(camelCase(type) + ": " + count);
		}
	}

}

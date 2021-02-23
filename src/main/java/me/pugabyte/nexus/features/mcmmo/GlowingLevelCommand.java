package me.pugabyte.nexus.features.mcmmo;

import me.pugabyte.nexus.features.mcmmo.menus.McMMOResetProvider;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.mcmmo.McMMOPrestige;
import me.pugabyte.nexus.models.mcmmo.McMMOService;
import org.bukkit.OfflinePlayer;

@Permission("group.admin")
public class GlowingLevelCommand extends CustomCommand {

	public GlowingLevelCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void setLevel(@Arg("self") OfflinePlayer player) {
		McMMOService service = new McMMOService();
		McMMOPrestige prestige = service.getPrestige(player.getUniqueId().toString());
		int level = prestige.getPrestige(McMMOResetProvider.ResetSkillType.MINING.name());

		if (!inventory().getItemInMainHand().getType().name().toLowerCase().contains("helmet"))
			error("You must be holding a helemt to execute this command");

		for (String lore : inventory().getItemInMainHand().getLore())
			if (lore.contains("Glowing"))
				runCommand("ce remove glowing");
		runCommand("ce enchant glowing " + level);
	}

}

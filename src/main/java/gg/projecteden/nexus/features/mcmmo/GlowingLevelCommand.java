package gg.projecteden.nexus.features.mcmmo;

import gg.projecteden.nexus.features.mcmmo.menus.McMMOResetProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.mcmmo.McMMOPrestige;
import gg.projecteden.nexus.models.mcmmo.McMMOService;
import org.bukkit.OfflinePlayer;

@Permission("group.admin")
public class GlowingLevelCommand extends CustomCommand {

	public GlowingLevelCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void setLevel(@Arg("self") OfflinePlayer player) {
		McMMOPrestige prestige = new McMMOService().getPrestige(player.getUniqueId().toString());
		int level = prestige.getPrestige(McMMOResetProvider.ResetSkillType.MINING.name());

		if (!inventory().getItemInMainHand().getType().name().toLowerCase().contains("helmet"))
			error("You must be holding a helmet to execute this command");

		runCommand("ce remove glowing");
		runCommand("ce enchant glowing " + level);
	}

}

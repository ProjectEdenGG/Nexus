package me.pugabyte.bncore.features.mcmmo.menus;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.mcmmo.McMMOPrestige;
import me.pugabyte.bncore.models.mcmmo.McMMOService;
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

		if (!player().getInventory().getItemInMainHand().getType().name().toLowerCase().contains("helmet"))
			error("You must be holding a helemt to execute this command");

		for (String lore : player().getInventory().getItemInMainHand().getLore())
			if (lore.contains("Glowing"))
				runCommand("ce remove glowing");
		runCommand("ce enchant glowing " + level);
	}

}

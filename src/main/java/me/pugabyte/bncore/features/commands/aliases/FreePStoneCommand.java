package me.pugabyte.bncore.features.commands.aliases;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class FreePStoneCommand extends CustomCommand {

	public FreePStoneCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Cooldown(60 * 60 * 24 * 7)
	void run() {
		Utils.giveItem(player(), new ItemStack(Material.COAL_ORE));
	}
}

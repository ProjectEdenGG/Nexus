package me.pugabyte.bncore.features.commands.aliases;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Cooldown(@Part(Time.MONTH))
public class FreePStoneCommand extends CustomCommand {

	public FreePStoneCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		Utils.giveItem(player(), new ItemStack(Material.COAL_ORE));
	}
}

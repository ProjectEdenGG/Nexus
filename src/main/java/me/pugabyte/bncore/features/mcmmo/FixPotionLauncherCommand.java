package me.pugabyte.bncore.features.mcmmo;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class FixPotionLauncherCommand extends CustomCommand {

	public FixPotionLauncherCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
		if (item.getType() == Material.HOPPER) {
			if (item.getItemMeta().getDisplayName().equalsIgnoreCase("&8Potion Launcher")) {
				player().getInventory().remove(item);
				runCommand("ce give " + player().getName() + " hopper potionlauncher");
				return;
			}
		}
		send("&cYou are not holding a potion launcher!");
	}

}

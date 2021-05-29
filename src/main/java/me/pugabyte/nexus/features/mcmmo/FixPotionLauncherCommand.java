package me.pugabyte.nexus.features.mcmmo;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

@NoArgsConstructor
public class FixPotionLauncherCommand extends CustomCommand implements Listener {

	public FixPotionLauncherCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		ItemStack item = getToolRequired();
		if (!isPotionLauncher(item))
			error("You are not holding a potion launcher!");

		inventory().removeItem(item);
		runCommandAsConsole("ce give " + name() + " hopper potionlauncher");
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (isPotionLauncher(event.getItemInHand()))
			event.setCancelled(true);
	}

	private boolean isPotionLauncher(ItemStack item) {
		return item.getType() == Material.HOPPER && item.getItemMeta().getDisplayName().equalsIgnoreCase(colorize("&8Potion Launcher"));
	}

}

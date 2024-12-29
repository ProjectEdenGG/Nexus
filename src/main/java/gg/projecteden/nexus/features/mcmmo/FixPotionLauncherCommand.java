package gg.projecteden.nexus.features.mcmmo;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

@HideFromWiki // TODO Convert to custom item that cant break
@NoArgsConstructor
public class FixPotionLauncherCommand extends CustomCommand implements Listener {

	public FixPotionLauncherCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Fix a broken potion launcher")
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
		return item.getType() == Material.HOPPER && item.getItemMeta().getDisplayName().equalsIgnoreCase(StringUtils.colorize("&8Potion Launcher"));
	}

}

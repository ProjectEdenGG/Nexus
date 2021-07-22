package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.tip.Tip;
import gg.projecteden.nexus.models.tip.Tip.TipType;
import gg.projecteden.nexus.models.tip.TipService;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.WorldGroup;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor
@Description("Turn your concrete powder into hardened concrete with ease")
public class ConcreteCommand extends CustomCommand implements Listener {
	private static final String TITLE = StringUtils.colorize("&6Concrete Exchange");

	public ConcreteCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void concrete() {
		Inventory inv = Bukkit.createInventory(null, 54, TITLE);
		player().openInventory(inv);
	}

	@EventHandler
	public void onChestClose(InventoryCloseEvent event) {
		if (event.getInventory().getHolder() != null) return;
		if (!Utils.equalsInvViewTitle(event.getView(), TITLE)) return;

		for (ItemStack item : event.getInventory().getContents()) {
			if (ItemUtils.isNullOrAir(item)) continue;
			if (!MaterialTag.CONCRETE_POWDERS.isTagged(item.getType())) {
				PlayerUtils.giveItem((Player) event.getPlayer(), item);
				continue;
			}
			item.setType(Material.valueOf(item.getType().name().replace("_POWDER", "")));
			PlayerUtils.giveItem((Player) event.getPlayer(), item);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (WorldGroup.of(event.getPlayer()) != WorldGroup.SURVIVAL)
			return;

		if (!MaterialTag.CONCRETE_POWDERS.isTagged(event.getBlock().getType()))
			return;

		TipService tipService = new TipService();
		Tip tip = tipService.get(event.getPlayer());
		if (tip.show(TipType.CONCRETE))
			send(event.getPlayer(), "&3Did you know? &e- &3You can use &c/concrete &3to easily convert concrete powder into hardened concrete.");
	}

}

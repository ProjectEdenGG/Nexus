package me.pugabyte.nexus.features.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Description;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.tip.Tip;
import me.pugabyte.nexus.models.tip.Tip.TipType;
import me.pugabyte.nexus.models.tip.TipService;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Utils;
import me.pugabyte.nexus.utils.WorldGroup;
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
		if (WorldGroup.get(event.getPlayer()) != WorldGroup.SURVIVAL)
			return;

		if (!MaterialTag.CONCRETE_POWDERS.isTagged(event.getBlock().getType()))
			return;

		TipService tipService = new TipService();
		Tip tip = tipService.get(event.getPlayer());
		if (tip.show(TipType.CONCRETE))
			send(event.getPlayer(), "&3Did you know? &e- &3You can use &c/concrete &3to easily convert concrete powder into hardened concrete.");
	}

}

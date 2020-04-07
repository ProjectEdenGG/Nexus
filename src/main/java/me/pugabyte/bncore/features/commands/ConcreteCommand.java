package me.pugabyte.bncore.features.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor
public class ConcreteCommand extends CustomCommand implements Listener {

	public ConcreteCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void concrete() {
		Inventory inv = Bukkit.createInventory(null, 27, StringUtils.colorize("&6Concrete Exchange"));
		player().openInventory(inv);
	}

	@EventHandler
	public void onChestClose(InventoryCloseEvent event) {
		if (!event.getView().getTitle().equals(StringUtils.colorize("&6Concrete Exchange"))) return;
		for (ItemStack item : event.getInventory().getContents()) {
			// Liar
			if (item == null) continue;
			if (MaterialTag.CONCRETE_POWDERS.isTagged(item.getType())) {
				event.getPlayer().getInventory().addItem(item);
				continue;
			}
			item.setType(Material.valueOf(item.getType().name().replace("_POWDER", "")));
			event.getPlayer().getInventory().addItem(item);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!MaterialTag.ALL_CONCRETES.isTagged(event.getBlock().getType())) return;
		SettingService service = new SettingService();
		Setting setting = service.get(event.getPlayer(), "concreteExchange");
		if (setting.getBoolean()) return;
		event.getPlayer().sendMessage(StringUtils.colorize("&3Did you know? &e- &3You can use &c/concrete &3to convert concrete powder into hardened concrete."));
		setting.setBoolean(true);
		service.save(setting);
	}


}

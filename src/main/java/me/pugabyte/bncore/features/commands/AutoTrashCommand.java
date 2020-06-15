package me.pugabyte.bncore.features.commands;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.autotrash.AutoTrash;
import me.pugabyte.bncore.models.autotrash.AutoTrashService;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@NoArgsConstructor
@Permission("automaticinventory.autotrash")
public class AutoTrashCommand extends CustomCommand implements Listener {
	private static final String PERMISSION = "automaticinventory.autotrash";

	private final AutoTrashService service = new AutoTrashService();
	private AutoTrash autoTrash;

	public AutoTrashCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayer())
			autoTrash = service.get(player());
	}

	@Path
	void run() {
		Inventory inventory = Bukkit.createInventory(null, 6 * 9, StringUtils.colorize("&eAuto Trash"));
		inventory.setContents(autoTrash.getMaterials().stream().map(ItemStack::new).toArray(ItemStack[]::new));
		player().openInventory(inventory);
	}

	@Path("<on|off>")
	void toggle(Boolean enable) {
		autoTrash.setEnabled(enable);
		service.save(autoTrash);
		send(PREFIX + (enable ? "&aEnabled" : "&cDisabled"));
	}

	@EventHandler
	public void onChestClose(InventoryCloseEvent event) {
		if (event.getInventory().getHolder() != null) return;
		if (!event.getView().getTitle().equals(StringUtils.colorize("&eAuto Trash"))) return;

		Player player = (Player) event.getPlayer();
		if (!player.hasPermission(PERMISSION)) return;

		AutoTrashService service = new AutoTrashService();
		AutoTrash autoTrash = service.get(player);

		autoTrash.getMaterials().clear();

		Arrays.stream(event.getInventory().getContents())
				.filter(item -> !Utils.isNullOrAir(item))
				.forEach(item -> autoTrash.getMaterials().add(item.getType()));

		service.save(autoTrash);

		send(player, StringUtils.getPrefix("AutoTrash") + "Automatically trashing " + autoTrash.getMaterials().size() + " materials");
	}

	@EventHandler
	public void onPickup(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		if (!player.hasPermission(PERMISSION)) return;

		AutoTrashService service = new AutoTrashService();
		AutoTrash autoTrash = service.get(player);

		if (!autoTrash.isEnabled()) return;

		if (autoTrash.getMaterials().contains(event.getItem().getItemStack().getType()))
			event.setCancelled(true);
	}

}

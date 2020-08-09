package me.pugabyte.bncore.features.commands;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.autotrash.AutoTrash;
import me.pugabyte.bncore.models.autotrash.AutoTrash.Behavior;
import me.pugabyte.bncore.models.autotrash.AutoTrashService;
import me.pugabyte.bncore.models.dumpster.Dumpster;
import me.pugabyte.bncore.models.dumpster.DumpsterService;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Comparator;

@NoArgsConstructor
@Permission("automaticinventory.autotrash")
public class AutoTrashCommand extends CustomCommand implements Listener {
	private static final String PERMISSION = "automaticinventory.autotrash";
	private static final String TITLE = StringUtils.colorize("&eAuto Trash");

	private final AutoTrashService service = new AutoTrashService();
	private AutoTrash autoTrash;

	public AutoTrashCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayer())
			autoTrash = service.get(player());
	}

	@Path
	void run() {
		Inventory inventory = Bukkit.createInventory(null, 6 * 9, TITLE);
		inventory.setContents(autoTrash.getMaterials().stream()
				.map(ItemStack::new)
				.sorted(Comparator.comparing(ItemStack::getType))
				.toArray(ItemStack[]::new));
		player().openInventory(inventory);
	}

	@Path("<on|off>")
	void toggle(Boolean enable) {
		autoTrash.setEnabled(enable);
		service.save(autoTrash);
		send(PREFIX + (enable ? "&aEnabled" : "&cDisabled"));
	}

	@Path("behavior [behavior]")
	void toggle(Behavior behavior) {
		if (behavior == null) {
			send("Current behavior is " + camelCase(autoTrash.getBehavior()));
			return;
		}

		autoTrash.setBehavior(behavior);
		service.save(autoTrash);
		send(PREFIX + "Behavior set to " + camelCase(behavior));
	}

	@EventHandler
	public void onChestClose(InventoryCloseEvent event) {
		if (event.getInventory().getHolder() != null) return;
		if (!Utils.equalsInvViewTitle(event.getView(), TITLE)) return;

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
		if (!Arrays.asList(WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK).contains(WorldGroup.get(player))) return;

		AutoTrashService service = new AutoTrashService();
		AutoTrash autoTrash = service.get(player);

		if (!autoTrash.isEnabled()) return;

		if (autoTrash.getMaterials().contains(event.getItem().getItemStack().getType())) {
			event.setCancelled(true);
			if (autoTrash.getBehavior() == Behavior.TRASH) {
				DumpsterService dumpsterService = new DumpsterService();
				Dumpster dumpster = dumpsterService.get();

				dumpster.add(event.getItem().getItemStack());
				dumpsterService.save(dumpster);

				event.getItem().remove();
			}
		}
	}

}

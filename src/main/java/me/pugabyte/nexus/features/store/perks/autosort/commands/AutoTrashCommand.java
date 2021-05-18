package me.pugabyte.nexus.features.store.perks.autosort.commands;

import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.autosort.AutoSortUser;
import me.pugabyte.nexus.models.autosort.AutoSortUser.AutoTrashBehavior;
import me.pugabyte.nexus.models.autosort.AutoSortUserService;
import me.pugabyte.nexus.utils.ItemUtils.ItemStackComparator;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;

@Permission("store.autosort")
public class AutoTrashCommand extends CustomCommand {
	private final AutoSortUserService service = new AutoSortUserService();
	private AutoSortUser user;

	public AutoTrashCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@Path("materials")
	void materials() {
		new AutoTrashMaterialEditor(user);
	}

	@Path("behavior [behavior]")
	void behavior(AutoTrashBehavior behavior) {
		if (behavior == null) {
			send("Current behavior is " + camelCase(user.getAutoTrashBehavior()));
			return;
		}

		user.setAutoTrashBehavior(behavior);
		service.save(user);
		send(PREFIX + "Auto Trash behavior set to " + camelCase(behavior));
	}

	public static class AutoTrashMaterialEditor implements Listener {
		private static final String TITLE = StringUtils.colorize("&eAuto Trash");
		private final AutoSortUser user;

		public AutoTrashMaterialEditor(AutoSortUser user) {
			this.user = user;

			Inventory inv = Bukkit.createInventory(null, 6 * 9, TITLE);
			inv.setContents(user.getAutoTrashInclude().stream()
					.map(ItemStack::new)
					.sorted(new ItemStackComparator())
					.toArray(ItemStack[]::new));

			Nexus.registerTempListener(this);
			user.getOnlinePlayer().openInventory(inv);
		}

		@EventHandler
		public void onChestClose(InventoryCloseEvent event) {
			if (event.getInventory().getHolder() != null) return;
			if (!Utils.equalsInvViewTitle(event.getView(), TITLE)) return;
			if (!event.getPlayer().equals(user.getOnlinePlayer())) return;

			Set<Material> materials = Arrays.stream(event.getInventory().getContents())
					.filter(item -> !isNullOrAir(item))
					.map(ItemStack::getType)
					.collect(Collectors.toSet());
			user.setAutoTrashInclude(materials);

			new AutoSortUserService().save(user);

			user.sendMessage(StringUtils.getPrefix("AutoTrash") + "Automatically trashing " + materials.size() + " materials");

			Nexus.unregisterTempListener(this);
			event.getPlayer().closeInventory();
		}
	}
}

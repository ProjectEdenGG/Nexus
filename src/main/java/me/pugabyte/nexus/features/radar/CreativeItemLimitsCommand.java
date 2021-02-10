package me.pugabyte.nexus.features.radar;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.commands.staff.admin.MaterialTagCommand.MaterialTagMaterialsMenu;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@Permission("")
@NoArgsConstructor
public class CreativeItemLimitsCommand extends CustomCommand implements Listener {
	private static final MaterialTag NO_META_ALLOWED = new MaterialTag(new NamespacedKey(Nexus.getInstance(), "NO_META_ALLOWED"))
			.append(
					MaterialTag.BOOKS,
					MaterialTag.POTIONS,
					MaterialTag.SPAWN_EGGS,
					MaterialTag.TOOLS,
					MaterialTag.WEAPONS,
					MaterialTag.ARMOR,
					MaterialTag.INVENTORY_BLOCKS,
					MaterialTag.COMMAND_BLOCKS,
					MaterialTag.MINECARTS
			)
			.append(
					Material.SPAWNER,
					Material.FIREWORK_ROCKET
			)
			.exclude(
					Material.FLINT_AND_STEEL,
					Material.LEAD
			);

	public CreativeItemLimitsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("list")
	void list() {
		new MaterialTagMaterialsMenu(NO_META_ALLOWED).open(player());
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (WorldGroup.get(event.getWhoClicked().getWorld()) != WorldGroup.CREATIVE) return;
		if (!event.getWhoClicked().hasPermission("rank.guest")) return;

		if (event.getCurrentItem() != null)
			if (NO_META_ALLOWED.isTagged(event.getCurrentItem().getType()))
				event.setCurrentItem(clearMeta(event.getCurrentItem()));
		if (NO_META_ALLOWED.isTagged(event.getCursor().getType()))
			event.setCursor(clearMeta(event.getCursor()));
	}

	@EventHandler
	public void onInventoryCreative(InventoryCreativeEvent event) {
		if (WorldGroup.get(event.getWhoClicked().getWorld()) != WorldGroup.CREATIVE) return;
		if (!event.getWhoClicked().hasPermission("rank.guest")) return;

		if (event.getCurrentItem() != null)
			if (NO_META_ALLOWED.isTagged(event.getCurrentItem().getType()))
				event.setCurrentItem(clearMeta(event.getCurrentItem()));
		if (NO_META_ALLOWED.isTagged(event.getCursor().getType()))
			event.setCursor(clearMeta(event.getCursor()));
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (WorldGroup.get(event.getPlayer().getWorld()) != WorldGroup.CREATIVE) return;
		if (!event.getPlayer().hasPermission("rank.guest")) return;

		if (NO_META_ALLOWED.isTagged(event.getItemInHand().getType()))
			event.getPlayer().getInventory().setItem(event.getHand(), clearMeta(event.getItemInHand()));
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (WorldGroup.get(event.getPlayer().getWorld()) != WorldGroup.CREATIVE) return;
		if (!event.getPlayer().hasPermission("rank.guest")) return;

		if (event.getItem() == null) return;
		if (NO_META_ALLOWED.isTagged(event.getItem().getType()))
			event.getItem().setItemMeta(Bukkit.getItemFactory().getItemMeta(event.getItem().getType()));
	}

	private ItemStack clearMeta(ItemStack item) {
		return new ItemStack(item.getType());
	}

}

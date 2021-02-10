package me.pugabyte.nexus.features.radar;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.commands.staff.admin.MaterialTagCommand.MaterialTagMaterialsMenu;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

@NoArgsConstructor
public class CreativeFilterCommand extends CustomCommand implements Listener {
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
			);

	public CreativeFilterCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("list")
	void list() {
		new MaterialTagMaterialsMenu(NO_META_ALLOWED).open(player());
	}

	private boolean shouldFilterItems(HumanEntity whoClicked) {
		return WorldGroup.get(whoClicked.getWorld()) == WorldGroup.CREATIVE && whoClicked.hasPermission("rank.guest");
	}

	private void filter(Supplier<HumanEntity> player, Supplier<ItemStack> getter, Consumer<ItemStack> setter) {
		if (!shouldFilterItems(player.get()))
			return;

		ItemStack item = getter.get();
		if (item != null && NO_META_ALLOWED.isTagged(item.getType()))
			setter.accept(new ItemStack(item.getType(), item.getAmount()));
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		filter(event::getWhoClicked, event::getCurrentItem, event::setCurrentItem);
		filter(event::getWhoClicked, event::getCursor, event::setCursor);
	}

	@EventHandler
	public void onInventoryCreative(InventoryCreativeEvent event) {
		filter(event::getWhoClicked, event::getCurrentItem, event::setCurrentItem);
		filter(event::getWhoClicked, event::getCursor, event::setCursor);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		filter(event::getPlayer, event::getItemInHand, item -> event.getPlayer().getInventory().setItem(event.getHand(), item));
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		filter(event::getPlayer, event::getItem, item -> item.setItemMeta(Bukkit.getItemFactory().getItemMeta(item.getType())));
	}

}

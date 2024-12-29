package gg.projecteden.nexus.features.legacy.listeners;

import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.legacy.Legacy;
import gg.projecteden.nexus.features.listeners.events.fake.FakePlayerInteractEvent;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.SmartInvsPlugin;
import gg.projecteden.nexus.features.menus.api.TemporaryMenuListener;
import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.SerializationUtils.Json;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LegacyShulkerBoxes implements Listener {

	@EventHandler
	public void onClickShulkerBox(InventoryClickEvent event) {
		if (WorldGroup.of(event.getWhoClicked()) != WorldGroup.LEGACY)
			return;

		if (!isShulkerBox(event.getCurrentItem()))
			return;

		if (!event.getClick().isRightClick())
			return;

		if (!(event.getClickedInventory() instanceof PlayerInventory))
			return;

		if (!(event.getWhoClicked() instanceof Player player))
			return;

		Optional<SmartInventory> smartInv = SmartInvsPlugin.manager().getInventory(player);
		if (smartInv.isPresent() && !smartInv.get().isCloseable())
			return;

		event.setCancelled(true);
		player.closeInventory();
		openShulkerBox(player, event.getCurrentItem());
	}

	@EventHandler
	public void onPlaceShulkerBox(PlayerInteractEvent event) {
		if (WorldGroup.of(event.getPlayer()) != WorldGroup.LEGACY)
			return;

		if (!ActionGroup.RIGHT_CLICK.applies(event))
			return;

		if (!isShulkerBox(event.getItem()))
			return;

		if (event instanceof FakePlayerInteractEvent)
			return;

		event.setCancelled(true);

		openShulkerBox(event.getPlayer(), event.getItem());
	}

	public void openShulkerBox(Player player, ItemStack shulkerBox) {
		new SoundBuilder(Sound.BLOCK_SHULKER_BOX_OPEN).receiver(player).volume(.3f).play();
		new LegacyShulkerBoxMenu(player, shulkerBox);
	}

	public static final String NBT_KEY = "ShulkerBoxId";

	private static boolean isShulkerBox(ItemStack item) {
		if (gg.projecteden.nexus.utils.Nullables.isNullOrAir(item))
			return false;

		if (Backpacks.isBackpack(item))
			return false;

		return !Nullables.isNullOrEmpty(new NBTItem(item).getString(NBT_KEY));
	}

	public static String getShulkerBoxId(ItemStack item) {
		if (!isShulkerBox(item))
			return null;

		return new NBTItem(item).getString(NBT_KEY);
	}

	@NoArgsConstructor
	public static class LegacyShulkerBoxMenu implements TemporaryMenuListener {
		@Getter
		private Player player;
		private ItemStack shulkerBox;
		private List<ItemStack> originalItems;

		@Getter
		private final LegacyShulkerBoxHolder inventoryHolder = new LegacyShulkerBoxHolder();

		public LegacyShulkerBoxMenu(Player player, ItemStack shulkerBox) {
			this.player = player;
			this.shulkerBox = shulkerBox;
			this.originalItems = new ItemBuilder(shulkerBox).shulkerBoxContents();

			try {
				verifyInventory(player);
				open(3, originalItems);
			} catch (Exception ex) {
				ex.printStackTrace();
				PlayerUtils.send(player, Legacy.PREFIX + "&c" + ex.getMessage());
			}
		}

		public static class LegacyShulkerBoxHolder extends CustomInventoryHolder {}

		@Override
		public String getTitle() {
			final String displayName = shulkerBox.getItemMeta().getDisplayName();
			if (!Nullables.isNullOrEmpty(displayName))
				return displayName;

			return "Shulker Box";
		}

		@EventHandler
		public void onDropShulkerBox(PlayerDropItemEvent event) {
			if (player != event.getPlayer())
				return;

			if (!isShulkerBox(event.getItemDrop().getItemStack()))
				return;

			event.setCancelled(true);
			player.getInventory().setItem(player.getInventory().getHeldItemSlot(), shulkerBox);
		}

		@EventHandler
		public void onClickShulkerBox(InventoryClickEvent event) {
			if (player != event.getWhoClicked())
				return;

			if (event.getClickedInventory() == null)
				return;

			ItemStack item = event.getClickedInventory().getItem(event.getSlot());
			if (event.getClick() == ClickType.NUMBER_KEY)
				item = player.getInventory().getContents()[event.getHotbarButton()];

			if (!MaterialTag.SHULKER_BOXES.isTagged(item))
				return;

			event.setCancelled(true);
		}

		@Override
		public boolean keepAirSlots() {
			return true;
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			if (shulkerBox == null || !(shulkerBox.getItemMeta() instanceof BlockStateMeta)) {
				handleError(contents);
				return;
			}

			shulkerBox.setItemMeta(new ItemBuilder(shulkerBox).clearShulkerBox().shulkerBox(contents).build().getItemMeta());

			player.updateInventory();
			Tasks.wait(1, player::updateInventory);
		}

		private void handleError(List<ItemStack> contents) {
			Nexus.warn("There was an error while saving ShulkerBox contents for " + player.getName());
			Nexus.warn("Below is a serialized paste of the original and new contents in the shulker box:");
			Nexus.warn("Old Contents: " + StringUtils.paste(Json.toString(Json.serialize(originalItems))));
			Nexus.warn("New Contents: " + StringUtils.paste(Json.toString(Json.serialize(contents))));
			PlayerUtils.send(player, Legacy.PREFIX + "&cThere was an error while saving your shulker box items. Please report this to staff to retrieve your lost items.");
		}

		private static void verifyInventory(Player player) {
			List<String> ids = new ArrayList<>();
			for (ItemStack item : player.getInventory().getContents()) {
				if (!isShulkerBox(item))
					continue;

				final String id = getShulkerBoxId(item);
				if (ids.contains(id))
					throw new InvalidInputException("Duplicate shulker boxes found, please contact staff");
				ids.add(id);
			}
		}
	}

}

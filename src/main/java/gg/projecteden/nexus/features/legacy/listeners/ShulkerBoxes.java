package gg.projecteden.nexus.features.legacy.listeners;

import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.listeners.TemporaryMenuListener;
import gg.projecteden.nexus.features.listeners.events.FakePlayerInteractEvent;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.SmartInvsPlugin;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SerializationUtils.Json;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static gg.projecteden.nexus.features.legacy.Legacy.PREFIX;
import static gg.projecteden.nexus.features.recipes.functionals.Backpacks.isBackpack;
import static gg.projecteden.nexus.utils.ItemUtils.find;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.utils.StringUtils.paste;

public class ShulkerBoxes implements Listener {

	@EventHandler
	public void onClickBackpack(InventoryClickEvent event) {
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
	public void onPlaceBackpack(PlayerInteractEvent event) {
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
		new ShulkerBoxMenu(player, shulkerBox);
	}

	private static final String SHULKER_BOX_NBT_KEY = "ShulkerBoxId";

	private static boolean isShulkerBox(ItemStack item) {
		if (isNullOrAir(item))
			return false;

		if (isBackpack(item))
			return false;

		return !isNullOrEmpty(new NBTItem(item).getString(SHULKER_BOX_NBT_KEY));
	}

	private static boolean isShulkerBox(ItemStack item, String id) {
		if (!isShulkerBox(item))
			return false;

		return new NBTItem(item).getString(SHULKER_BOX_NBT_KEY).equals(id);
	}

	@NoArgsConstructor
	public static class ShulkerBoxMenu implements TemporaryMenuListener {
		@Getter
		private Player player;
		private ItemStack shulkerBox;
		private String shulkerBoxId;
		private ItemStack[] originalItems;

		public ShulkerBoxMenu(Player player, ItemStack shulkerBox) {
			this.player = player;
			this.shulkerBox = shulkerBox;
			this.shulkerBoxId = new NBTItem(shulkerBox.clone()).getString(SHULKER_BOX_NBT_KEY);

			BlockStateMeta shulkerBoxMeta = (BlockStateMeta) shulkerBox.getItemMeta();
			ShulkerBox shulkerBoxState = (ShulkerBox) shulkerBoxMeta.getBlockState();
			this.originalItems = shulkerBoxState.getInventory().getContents();

			try {
				Inventory inv = Bukkit.createInventory(null, 27, shulkerBox.getItemMeta().getDisplayName());
				inv.setContents(originalItems);
				player.openInventory(inv);
				Nexus.registerTemporaryListener(this);
			} catch (Exception ex) {
				ex.printStackTrace();
				PlayerUtils.send(player, PREFIX + "&c" + ex.getMessage());
			}
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

		// Cancel Moving shulker boxes While shulker box is open
		@EventHandler
		public void onClickShulkerBox(InventoryClickEvent event) {
			if (WorldGroup.of(player) != WorldGroup.LEGACY)
				return;

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
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			new SoundBuilder(Sound.BLOCK_SHULKER_BOX_CLOSE).receiver(player).volume(.3f).play();
			ItemStack[] inv = player.getInventory().getContents();
			ItemStack shulkerBox = find(inv, item -> isShulkerBox(item, this.shulkerBoxId));
			BlockStateMeta meta = null;

			if (shulkerBox != null)
				meta = (BlockStateMeta) shulkerBox.getItemMeta();

			if (meta == null) {
				handleError(contents);
				return;
			}

			ShulkerBox shulkerBoxState = (ShulkerBox) meta.getBlockState();
			shulkerBoxState.getInventory().setContents(contents.toArray(ItemStack[]::new));
			meta.setBlockState(shulkerBoxState);
			shulkerBox.setItemMeta(meta);

			player.updateInventory();
			Tasks.wait(1, player::updateInventory);
		}

		private void handleError(List<ItemStack> contents) {
			Nexus.warn("There was an error while saving ShulkerBox contents for " + player.getName());
			Nexus.warn("Below is a serialized paste of the original and new contents in the shulker box:");
			Nexus.warn("Old Contents: " + paste(Json.toString(Json.serialize(Arrays.asList(originalItems)))));
			Nexus.warn("New Contents: " + paste(Json.toString(Json.serialize(Arrays.asList(contents.toArray(ItemStack[]::new))))));
			PlayerUtils.send(player, PREFIX + "&cThere was an error while saving your shulker box items. Please report this to staff to retrieve your lost items.");
		}

	}

}

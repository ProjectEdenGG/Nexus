package gg.projecteden.nexus.features.resourcepack.decoration.store;

import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang.DecorationError;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.Decorations;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog;
import gg.projecteden.nexus.features.resourcepack.models.font.CustomTexture;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemFlags;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DecorationStoreManager implements Listener {

	@Getter
	private static final Map<DecorationStoreType, Map<UUID, TargetData>> targetDataMap = new HashMap<>();
	@Getter
	private static final List<Player> debuggers = new ArrayList<>();

	public DecorationStoreManager() {
		Nexus.registerListener(this);
		glowTask();
	}

	public static void onStop() {
		resetAllPlayerData();
	}

	public static void resetAllPlayerData() {
		for (DecorationStoreType storeRegion : DecorationStoreType.values()) {
			storeRegion.resetPlayerData();
		}
	}

	public static void debug(Player player, String message) {
		if (debuggers.contains(player))
			PlayerUtils.send(player, message);
	}

	public static void glowTask() {
		if (Nexus.getEnv() != Env.PROD)
			return;

		Tasks.repeat(0, TickTime.TICK.x(4), () -> {
			if (Decorations.isServerReloading())
				return;

			for (DecorationStoreType storeType : DecorationStoreType.values()) {
				if (storeType.getWorldName() == null)
					continue;

				if (Bukkit.getWorld(storeType.getWorldName()) == null)
					continue;

				if (storeType.getGlowRegionId() == null)
					continue;

				Map<UUID, TargetData> dataMap = targetDataMap.getOrDefault(storeType, new HashMap<>());

				for (Player player : storeType.getPlayers()) {
					TargetData data = dataMap.get(player.getUniqueId());

					// block
					Block targetBlock = DecorationStoreUtils.getTargetBlock(player);
					ItemStack targetBlockItem = DecorationStoreUtils.getTargetBlockItem(targetBlock);
					boolean isApplicableBlock = DecorationStoreUtils.isApplicableBlock(player, targetBlock, targetBlockItem, storeType);

					// entity
					Entity targetEntity = DecorationStoreUtils.getTargetEntity(player);
					ItemStack targetEntityItem = DecorationStoreUtils.getTargetEntityItem(targetEntity);
					boolean isApplicableEntity = DecorationStoreUtils.isApplicableEntity(player, targetEntity, targetEntityItem, storeType);
					//

					if (!isApplicableBlock && !isApplicableEntity) {

						if (data != null) {
							data.unglow();

							dataMap.remove(player.getUniqueId());
							targetDataMap.put(storeType, dataMap);

							if (data.getCurrentEntity() != null)
								debug(player, "---");
						}
						debug(player, "not applicable, continuing\n");
						continue;
					}

					//

					debug(player, "");

					if (data != null) {
						if (isApplicableBlock) {
							Location skullLocation = data.getCurrentSkullLocation();
							if (skullLocation != null && skullLocation.equals(targetBlock.getLocation())) {
								debug(player, "continue: same skull");

								data.getBuyableData().showPrice(player);
								continue;
							}
						}

						if (isApplicableEntity) {
							Entity oldEntity = data.getCurrentEntity();
							if (oldEntity != null && oldEntity.getUniqueId().equals(targetEntity.getUniqueId())) {
								if (!isApplicableBlock) { // If looking at block, override looking at entity
									debug(player, "continue: same entity");

									data.getBuyableData().showPrice(player);
									continue;
								}
							}
						}
					} else {
						debug(player, "data: new");
						data = new TargetData(player);
					}

					if (isApplicableBlock) {
						data.setupTargetHDB((Skull) targetBlock.getState(), targetBlockItem, storeType);
					} else {
						data.setupTargetEntity(targetEntity, targetEntityItem, storeType);
					}

					if (data.getOldEntity() != null) {
						debug(player, "old: unglow");
						data.unglowOldEntity();
					}

					dataMap.put(player.getUniqueId(), data);
					targetDataMap.put(storeType, dataMap);

					data.glowCurrentEntity();
					debug(player, "target: glowing");
					data.getBuyableData().showPrice(player);
				}
			}
		});
	}

	@EventHandler
	public void on(PlayerEnteredRegionEvent event) {
		Player player = event.getPlayer();

		DecorationStoreType storeType = DecorationStoreType.of(event.getRegion().getId());
		if (storeType == null) {
			debug(player, "Unknown store type");
			return;
		}

		if (PlayerUtils.isWGEdit(player))
			PlayerUtils.runCommand(player, "wgedit off");
	}

	@EventHandler
	public void onStorePrompt(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof ItemFrame)) return;
		if (!(event.getDamager() instanceof Player player)) return;

		if (Decorations.isServerReloading())
			return;

		DecorationStoreType storeType = DecorationStoreType.of(player);
		if (storeType == null) {
			debug(player, "Unknown store type");
			return;
		}

		if (prompt(player, storeType))
			event.setCancelled(true);
	}

	@EventHandler
	public void onStorePrompt(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!event.getAction().isLeftClick()) return;

		if (Decorations.isServerReloading())
			return;

		DecorationStoreType storeType = DecorationStoreType.of(player);
		if (storeType == null) {
			debug(player, "Unknown store type");
			return;
		}

		if (prompt(player, storeType))
			event.setCancelled(true);
	}

	private boolean prompt(Player player, DecorationStoreType storeType) {
		// TODO DECORATION: REMOVE ON RELEASE prompt
		if (!DecorationUtils.canBuyDecoration(player))
			return false;
		//

		BuyableData data = storeType.getTargetBuyable(player);
		if (data == null) {
			debug(player, "BuyableData is null");
			return false;
		}

		String itemName = data.getName();
		if (itemName == null) {
			debug(player, "Name is null");
			return false;
		}

		Integer itemPrice = data.getPrice();
		if (itemPrice == null) {
			debug(player, "Price is null");
			return false;
		}

		ItemStack displayItem = data.getItem();

		ShopGroup shopGroup;
		if (WorldGroup.of(player) != WorldGroup.SURVIVAL) {
			ConfirmationMenu.builder()
					.title(CustomTexture.GUI_CONFIRMATION_SLOT.getMenuTexture() +
							"&3Buy for &a" + StringUtils.prettyMoney(itemPrice) + " &3in which world?")
					.displayItem(displayItem)

					.cancelText("&aSurvival")
					.cancelItem(new ItemBuilder(Material.DIAMOND_PICKAXE).itemFlags(ItemFlags.HIDE_ALL).build())
					.onCancel(e -> {
						tryBuyItem(player, WorldGroup.SURVIVAL, itemPrice, displayItem, storeType.getId(), storeType);
						e.getPlayer().closeInventory();
					})

					.confirmText("&aOneBlock")
					.confirmItem(new ItemBuilder(Material.GRASS_BLOCK).itemFlags(ItemFlags.HIDE_ALL).build())
					.onConfirm(e -> {
						tryBuyItem(player, WorldGroup.SKYBLOCK, itemPrice, displayItem, storeType.getId(), storeType);
						e.getPlayer().closeInventory();
					})

					.open(player);

			return true;
		}

		WorldGroup worldGroup = WorldGroup.of(storeType.getWorldName());
		shopGroup = ShopGroup.of(worldGroup);

		if (shopGroup == null) {
			debug(player, "ShopGroup is null");
			return false;
		}

		BankerService bankerService = new BankerService();
		if (!bankerService.has(player, itemPrice, shopGroup)) {
			DecorationError.LACKING_FUNDS.send(player);
			return false;
		}

		ConfirmationMenu.builder()
				.title(CustomTexture.GUI_CONFIRMATION_SLOT.getMenuTexture() + "&3Buy for &a" + StringUtils.prettyMoney(itemPrice) + "&3?")
				.displayItem(displayItem)
				.cancelText("&cCancel")
				.confirmText("&aBuy")
				.onConfirm(e -> {
					Catalog.tryBuySurvivalItem(player, displayItem, storeType);
					e.getPlayer().closeInventory();
				})
				.open(player);

		return true;
	}

	private void tryBuyItem(Player player, WorldGroup worldGroup, double price, ItemStack item, String eventName, DecorationStoreType storeType) {
		ShopGroup shopGroup = ShopGroup.of(worldGroup);
		if (shopGroup == null) {
			debug(player, "ShopGroup is null");
		}

		BankerService bankerService = new BankerService();
		if (!bankerService.has(player, price, shopGroup)) {
			DecorationError.LACKING_FUNDS.send(player);
		}

		Catalog.tryBuyEventItem(player, item, worldGroup, shopGroup, eventName, storeType);
	}

	//

}

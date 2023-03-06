package gg.projecteden.nexus.features.survival.decorationstore;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationListener;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.survival.decorationstore.models.BuyableData;
import gg.projecteden.nexus.features.survival.decorationstore.models.TargetData;
import gg.projecteden.nexus.models.decorationstore.DecorationStoreConfig;
import gg.projecteden.nexus.models.decorationstore.DecorationStoreConfigService;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static gg.projecteden.nexus.features.resourcepack.decoration.DecorationInteractData.MAX_RADIUS;
import static gg.projecteden.nexus.features.survival.decorationstore.models.BuyableData.hasPrice;
import static gg.projecteden.nexus.utils.Nullables.isNotNullOrAir;

public class DecorationStore implements Listener {
	@Getter
	private static final DecorationStoreConfigService configService = new DecorationStoreConfigService();
	@Getter
	private static final DecorationStoreConfig config = configService.get0();

	private static final Map<UUID, TargetData> targetDataMap = new HashMap<>();

	private static final List<EntityType> glowTypes = List.of(EntityType.ITEM_FRAME);
	private static final int REACH_DISTANCE = 6;

	private static boolean isReloading = false;

	@Getter
	private static final List<Player> debuggers = new ArrayList<>();

	public DecorationStore() {
		Nexus.registerListener(this);

		new Catalog();
		new DecorationListener();
		new DecorationStoreListener();

		glowTargetTask();
	}

	public static void onStop() {
		isReloading = true;
		resetPlayerData();
	}

	public boolean isActive() {
		return config.isActive();
	}

	public static void setActive(boolean bool) {
		config.setActive(bool);
		configService.save(config);

		if (!bool)
			resetPlayerData();
	}

	public static void saveConfig() {
		configService.save(config);
	}

	public static void resetPlayerData() {
		for (UUID uuid : targetDataMap.keySet()) {
			TargetData data = targetDataMap.get(uuid);
			data.unglow();
		}

		targetDataMap.clear();
	}

	// TODO: Swap schematics, task
	public static void refresh() {
		config.setActive(false);
		configService.save(config);

		resetPlayerData();

		config.setActive(true);
		configService.save(config);
	}

	public static @Nullable BuyableData getTargetBuyable(Player player) {
		TargetData targetData = targetDataMap.get(player.getUniqueId());
		if (targetData == null)
			return null;

		return targetData.getBuyableData();
	}

	public void glowTargetTask() {
		Tasks.repeat(0, TickTime.TICK, () -> {
			if (isReloading || !config.isActive())
				return;

			for (Player player : DecorationStoreUtils.getPlayersInStore()) {

				TargetData data = targetDataMap.get(player.getUniqueId());

				Block targetBlock = player.getTargetBlockExact(REACH_DISTANCE);
				ItemStack targetBlockItem = ItemUtils.getItem(targetBlock);
				boolean isApplicableBlock = isNotNullOrAir(targetBlock) && MaterialTag.PLAYER_SKULLS.isTagged(targetBlock) && isNotNullOrAir(targetBlockItem) && hasPrice(targetBlockItem);

				Entity targetEntity = getTargetEntity(player);
				ItemStack targetEntityItem = getTargetEntityItem(targetEntity);
				boolean isApplicableEntity = targetEntity != null && glowTypes.contains(targetEntity.getType()) && isNotNullOrAir(targetEntityItem) && hasPrice(targetEntityItem);

				if (!isApplicableBlock && !isApplicableEntity) {
					if (data != null) {
						data.unglow();

						targetDataMap.remove(player.getUniqueId());

						if (data.getCurrentEntity() != null)
							debug(player, "---");
					}
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
					data.setupTargetHDB(targetBlock, targetBlockItem);
				} else {
					data.setupTargetEntity(targetEntity, targetEntityItem);
				}

				if (data.getOldEntity() != null) {
					debug(player, "old: unglow");
					data.unglowOldEntity();
				}

				targetDataMap.put(player.getUniqueId(), data);
				data.glowCurrentEntity();
				debug(player, "target: glowing");
				data.getBuyableData().showPrice(player);
			}

		});
	}

	private ItemStack getTargetEntityItem(Entity entity) {
		ItemStack itemStack = null;
		if (entity instanceof ItemFrame itemFrame) {
			itemStack = itemFrame.getItem();
		}

		return itemStack;
	}

	private Entity getTargetEntity(Player player) {
		debug(player, "getTargetEntity:");
		Entity targetEntity = player.getTargetEntity(REACH_DISTANCE, false);
		if (targetEntity != null) {
			debug(player, "found entity 1");
			return targetEntity;
		}

		targetEntity = PlayerUtils.getTargetItemFrame(player, 10, Map.of(BlockFace.DOWN, 1));
		if (targetEntity != null) {
			debug(player, "found entity 2");
			return targetEntity;
		}

		// Target Decoration
		Block block = player.getTargetBlockExact(REACH_DISTANCE);
		if (isNotNullOrAir(block)) {

			// Exact
			ItemFrame itemFrame = checkForDecoration(player, block);
			if (itemFrame == null) {
				Block inFront = block.getRelative(player.getFacing().getOppositeFace());
				if (inFront.getType().equals(Material.LIGHT)) {
					// In Front
					itemFrame = checkForDecoration(player, inFront);
				}
			}

			if (itemFrame != null) {
				debug(player, "is decoration");
				return itemFrame;
			}
		}

		debug(player, "No entities found");

		return null;
	}

	private ItemFrame checkForDecoration(Player player, Block block) {
		if (Nullables.isNullOrAir(block))
			return null;

		debug(player, "Target Block: " + block.getType());

		BlockFace facing = BlockFace.UP;
		if (block.getType().equals(Material.LIGHT))
			facing = player.getFacing();

		ItemFrame itemFrame = DecorationUtils.getItemFrame(block, MAX_RADIUS, facing, player);
		if (itemFrame == null)
			return null;

		debug(player, "found an item frame");
		DecorationConfig config = DecorationConfig.of(itemFrame);
		if (config == null)
			return null;

		return itemFrame;
	}

	public static void debug(Player player, String message) {
		if (debuggers.contains(player))
			PlayerUtils.send(player, message);
	}

}

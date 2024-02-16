package gg.projecteden.nexus.features.survival.decorationstore;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.Decorations;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.survival.Survival;
import gg.projecteden.nexus.features.survival.decorationstore.models.BuyableData;
import gg.projecteden.nexus.features.survival.decorationstore.models.TargetData;
import gg.projecteden.nexus.models.decorationstore.DecorationStoreConfig;
import gg.projecteden.nexus.models.decorationstore.DecorationStoreConfigService;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
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
import static gg.projecteden.nexus.features.survival.decorationstore.models.BuyableData.isBuyable;
import static gg.projecteden.nexus.utils.Nullables.isNotNullOrAir;

public class DecorationStore implements Listener {
	public static final String PREFIX = StringUtils.getPrefix("DecorationStore");
	@Getter
	private static final DecorationStoreConfigService configService = new DecorationStoreConfigService();
	private static DecorationStoreConfig config = configService.get();
	@Getter
	private static final List<Player> debuggers = new ArrayList<>();
	//
	@Getter
	private static final String storeRegion = "spawn_decor_store";
	@Getter
	private static final String storeRegionSchematic = storeRegion + "_schem";
	@Getter
	private static final Location warpLocation = new Location(Survival.getWorld(), 358.5, 72.00, 28.5, -90, 0);
	@Getter
	private static final Map<UUID, TargetData> targetDataMap = new HashMap<>();
	//

	private static final List<EntityType> glowTypes = List.of(EntityType.ITEM_FRAME);
	private static final int REACH_DISTANCE = 6;

	public DecorationStore() {
		Nexus.registerListener(this);

		new DecorationStoreListener();
		glowTargetTask();
	}

	public static void onStop() {
		resetPlayerData();
	}

	public static DecorationStoreConfig getConfig() {
		if (config == null)
			config = configService.get();

		return config;
	}

	public static void saveConfig() {
		configService.save(config);
	}

	public static boolean isNotActive() {
		DecorationStoreConfig config = configService.get();
		return !config.isActive();
	}

	public static void setActive(boolean bool) {
		DecorationStoreConfig config = configService.get();
		config.setActive(bool);
		saveConfig();

		if (!bool)
			resetPlayerData();
	}

	public static void resetPlayerData() {
		for (UUID uuid : targetDataMap.keySet()) {
			TargetData data = targetDataMap.get(uuid);
			data.unglow();
		}

		targetDataMap.clear();
	}

	public static List<Player> getPlayersInStore() {
		return (List<Player>) Survival.worldguard().getPlayersInRegion(storeRegionSchematic);
	}

	public static boolean isInStore(Player player) {
		return getPlayersInStore().contains(player);
	}

	public static void debug(Player player, String message) {
		if (debuggers.contains(player))
			PlayerUtils.send(player, message);
	}

	//

	public static @Nullable BuyableData getTargetBuyable(Player player) {
		TargetData targetData = targetDataMap.get(player.getUniqueId());
		if (targetData == null)
			return null;

		return targetData.getBuyableData();
	}

	public void glowTargetTask() {
		WorldGuardUtils worldguard = Survival.worldguard();

		Tasks.repeat(0, TickTime.TICK, () -> {
			if (Decorations.isServerReloading() || isNotActive())
				return;

			for (Player player : getPlayersInStore()) {

				TargetData data = targetDataMap.get(player.getUniqueId());

				// block
				Block targetBlock = player.getTargetBlockExact(REACH_DISTANCE);
				ItemStack targetBlockItem = ItemUtils.getItem(targetBlock);
				boolean isApplicableBlock =
						isNotNullOrAir(targetBlock)
								&& MaterialTag.PLAYER_SKULLS.isTagged(targetBlock)
								&& isNotNullOrAir(targetBlockItem)
								&& isBuyable(targetBlockItem)
								&& targetBlock.getState() instanceof Skull
								&& worldguard.isInRegion(targetBlock.getLocation(), storeRegionSchematic);

				// entity
				Entity targetEntity = getTargetEntity(player);
				ItemStack targetEntityItem = getTargetEntityItem(targetEntity);
				boolean isApplicableEntity =
						targetEntity != null
								&& glowTypes.contains(targetEntity.getType())
								&& isNotNullOrAir(targetEntityItem)
								&& isBuyable(targetEntityItem)
								&& worldguard.isInRegion(targetEntity.getLocation(), storeRegionSchematic);
				//

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
					data.setupTargetHDB((Skull) targetBlock.getState(), targetBlockItem);
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

		ItemFrame itemFrame = (ItemFrame) DecorationUtils.getItemFrame(block, MAX_RADIUS, facing, player, false);
		if (itemFrame == null)
			return null;

		debug(player, "found an item frame");
		DecorationConfig config = DecorationConfig.of(itemFrame);
		if (config == null)
			return null;

		return itemFrame;
	}

}

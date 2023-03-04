package gg.projecteden.nexus.features.survival.decorationstore;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationListener;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.models.decorationstore.DecorationStoreConfig;
import gg.projecteden.nexus.models.decorationstore.DecorationStoreConfigService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import org.bukkit.Location;
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

public class DecorationStore implements Listener {
	@Getter
	private static final DecorationStoreConfigService configService = new DecorationStoreConfigService();
	@Getter
	private static final DecorationStoreConfig config = configService.get0();

	private static final Map<UUID, TargetData> targetDataMap = new HashMap<>();

	private static final List<EntityType> glowTypes = List.of(EntityType.ITEM_FRAME, EntityType.PAINTING);
	private static final int REACH_DISTANCE = 5;

	private static boolean isReloading = false;

	@Getter
	private static final List<Player> debuggers = new ArrayList<>();

	public DecorationStore() {
		Nexus.registerListener(this);

		new Catalog();
		new DecorationListener();

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
			data.unglowEntity(data.currentEntity);
			data.unglowEntity(data.oldEntity);
		}

		targetDataMap.clear();
	}

	// TODO: Swap schematics
	public static void refresh() {
		config.setActive(false);
		configService.save(config);

		resetPlayerData();

		config.setActive(true);
		configService.save(config);
	}

	// TODO:
	public static @Nullable ItemStack getTargetItem(Player player) {
		return targetDataMap.get(player.getUniqueId()).getTargetItem();
	}

	/*
		TODO:
			- glowing on multiblock paintings is taking light blocks into account
			- player wall skulls need to be properly setup
	 */
	public void glowTargetTask() {
		Tasks.repeat(0, TickTime.TICK, () -> {
			if (isReloading || !config.isActive())
				return;

			for (Player player : DecorationStoreUtils.getPlayersInStore()) {
				if (!Rank.of(player).isStaff())
					continue;

				TargetData data = targetDataMap.get(player.getUniqueId());

				Block targetBlock = player.getTargetBlockExact(REACH_DISTANCE);
				ItemStack targetBlockItem = ItemUtils.getItem(targetBlock);
				boolean isApplicableBlock = Nullables.isNotNullOrAir(targetBlock) && MaterialTag.PLAYER_SKULLS.isTagged(targetBlock) && Nullables.isNotNullOrAir(targetBlockItem);

				Entity targetEntity = getTargetEntity(player);
				boolean isApplicableEntity = targetEntity != null && glowTypes.contains(targetEntity.getType());

				if (!isApplicableBlock && !isApplicableEntity) {
					if (data != null) {
						data.unglowEntity(data.getCurrentEntity());
						data.unglowEntity(data.getOldEntity());
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
							continue;
						}
					}

					if (isApplicableEntity) {
						Entity oldEntity = data.getCurrentEntity();
						if (oldEntity != null && oldEntity.getUniqueId().equals(targetEntity.getUniqueId())) {
							debug(player, "continue: same entity");
							continue;
						}
					}
				} else {
					debug(player, "data: new");
					data = new TargetData(player);
				}

				if (isApplicableBlock) // Skulls
					data.setupTargetHDB(targetBlock, targetBlockItem);
				else
					data.setupTargetEntity(targetEntity);

				if (data.getOldEntity() != null) {
					debug(player, "old: unglow");
					data.unglowEntity(data.getOldEntity());
				}

				targetDataMap.put(player.getUniqueId(), data);
				data.glowCurrentEntity();
				debug(player, "target: glowing");
			}

		});
	}

	private Entity getTargetEntity(Player player) {
		Entity targetEntity = player.getTargetEntity(REACH_DISTANCE, false);
		if (targetEntity != null)
			return targetEntity;

		targetEntity = PlayerUtils.getTargetItemFrame(player, 10, Map.of(BlockFace.UP, 1, BlockFace.DOWN, 1));
		if (targetEntity != null)
			return targetEntity;

		// Target Block
		Block block = player.getTargetBlockExact(REACH_DISTANCE);
		if (Nullables.isNotNullOrAir(block)) {
			ItemFrame itemFrame = DecorationUtils.getItemFrame(block, MAX_RADIUS, BlockFace.UP, player);
			if (itemFrame != null) {
				DecorationConfig config = DecorationConfig.of(itemFrame);
				if (config != null) {
					return itemFrame;
				}
			}
		}

		return null;
	}

	public static void debug(Player player, String message) {
		if (debuggers.contains(player))
			PlayerUtils.send(player, message);
	}

}

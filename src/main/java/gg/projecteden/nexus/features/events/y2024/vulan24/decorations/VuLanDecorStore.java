package gg.projecteden.nexus.features.events.y2024.vulan24.decorations;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2024.vulan24.VuLan24;
import gg.projecteden.nexus.features.resourcepack.decoration.Decorations;
import gg.projecteden.nexus.features.survival.decorationstore.DecorationStore;
import gg.projecteden.nexus.features.survival.decorationstore.DecorationStoreListener;
import gg.projecteden.nexus.features.survival.decorationstore.models.BuyableData;
import gg.projecteden.nexus.features.survival.decorationstore.models.TargetData;
import gg.projecteden.nexus.framework.features.Depends;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Depends(VuLan24.class)
public class VuLanDecorStore {
	public static final String PREFIX = StringUtils.getPrefix("DecorationStore");
	@Getter
	private static final String storeRegionFlorist = "vu_lan_decor_store_florist";
	@Getter
	private static final String storeRegionMarket = "vu_lan_decor_store_market";

	@Getter
	private static final List<Player> debuggers = new ArrayList<>();

	@Getter
	private static final Map<UUID, TargetData> targetDataMap = new HashMap<>();

	public VuLanDecorStore() {
		new DecorationStoreListener();
		glowTargetTask();
	}

	public static void onStop() {
		resetPlayerData();
	}

	public static void resetPlayerData() {
		for (UUID uuid : targetDataMap.keySet()) {
			unglow(uuid);
		}

		targetDataMap.clear();
	}

	public static void unglow(UUID uuid) {
		targetDataMap.get(uuid).unglow();
	}

	public static List<String> getStores() {
		return List.of(getStoreRegionFlorist(), getStoreRegionMarket());
	}

	public static List<Player> getPlayersInStore(String storeRegionId) {
		return (List<Player>) VuLan24.get().worldguard().getPlayersInRegion(storeRegionId);
	}

	public static boolean isInAStore(Player player) {
		for (String regionId : getStores()) {
			if (getPlayersInStore(regionId).contains(player))
				return true;
		}
		return false;
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
		WorldGuardUtils worldguard = VuLan24.get().worldguard();

		Tasks.repeat(0, TickTime.TICK.x(4), () -> {
			if (Decorations.isServerReloading())
				return;

			for (String regionId : getStores()) {
				for (Player player : getPlayersInStore(regionId)) {
					TargetData data = targetDataMap.get(player.getUniqueId());

					// block
					Block targetBlock = player.getTargetBlockExact(DecorationStore.REACH_DISTANCE);
					ItemStack targetBlockItem = ItemUtils.getItem(targetBlock);
					boolean isApplicableBlock = DecorationStore.isApplicableBlock(player, targetBlock, targetBlockItem, worldguard, regionId);

					// entity
					Entity targetEntity = DecorationStore.getTargetEntity(player);
					ItemStack targetEntityItem = DecorationStore.getTargetEntityItem(targetEntity);
					boolean isApplicableEntity = DecorationStore.isApplicableEntity(player, targetEntity, targetBlockItem, worldguard, regionId);
					//

					if (!isApplicableBlock && !isApplicableEntity) {

						if (data != null) {
							data.unglow();

							targetDataMap.remove(player.getUniqueId());

							if (data.getCurrentEntity() != null)
								debug(player, "---");
						}
						debug(player, "not applicable, continuing");
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

			}

		});
	}


}

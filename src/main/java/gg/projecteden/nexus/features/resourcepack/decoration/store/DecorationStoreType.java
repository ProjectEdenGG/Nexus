package gg.projecteden.nexus.features.resourcepack.decoration.store;

import gg.projecteden.nexus.features.survival.decorationstore.DecorationStore;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
public enum DecorationStoreType {
	CATALOG(null, null, null, DecorationStoreCurrencyType.MONEY, 0),
	SURVIVAL("survival", DecorationStore.getStoreRegionSchematic(), null, DecorationStoreCurrencyType.MONEY, 0),
	VU_LAN_FLORIST("vu_lan", "vu_lan_decor_store_florist", "Vu Lan", DecorationStoreCurrencyType.TOKENS, 25),
	VU_LAN_MARKET("vu_lan", "vu_lan_decor_store_market", "Vu Lan", DecorationStoreCurrencyType.TOKENS, 25),
	;

	private final @Nullable String worldName;
	private final @Nullable String glowRegionId;
	private final @Nullable String id;
	private final @NonNull DecorationStoreCurrencyType currency;
	private final double discount;

	public static @Nullable DecorationStoreType of(Player player) {
		for (DecorationStoreType storeType : values()) {
			if (storeType.getPlayers().contains(player))
				return storeType;
		}
		return null;
	}

	public static @Nullable DecorationStoreType of(String regionId) {
		for (DecorationStoreType storeType : values()) {
			if (storeType.glowRegionId != null && storeType.glowRegionId.equalsIgnoreCase(regionId))
				return storeType;
		}

		return null;
	}

	public List<Player> getPlayers() {
		if (worldName == null || glowRegionId == null)
			return new ArrayList<>();

		if (Bukkit.getWorld(worldName) == null)
			return new ArrayList<>();

		return OnlinePlayers.where()
				.world(worldName)
				.region(glowRegionId)
				.vanished(false)
				.get();
	}

	public void resetPlayerData() {
		Map<UUID, TargetData> storeDataMap = DecorationStoreManager.getTargetDataMap().getOrDefault(this, new HashMap<>());
		for (UUID uuid : storeDataMap.keySet()) {
			TargetData targetData = storeDataMap.get(uuid);
			if (targetData.getPlayer() == null || !targetData.getPlayer().isOnline())
				continue;

			targetData.unglow();
		}

		storeDataMap.clear();
	}

	public @Nullable BuyableData getTargetBuyable(Player player) {
		Map<UUID, TargetData> storeDataMap = DecorationStoreManager.getTargetDataMap().getOrDefault(this, new HashMap<>());

		TargetData targetData = storeDataMap.get(player.getUniqueId());
		if (targetData == null)
			return null;

		return targetData.getBuyableData();
	}

	public int getDiscountedPrice(int price) {
		if (this.discount <= 0)
			return price;

		return price - ((int) Math.floor((this.discount / 100) * price));
	}
}

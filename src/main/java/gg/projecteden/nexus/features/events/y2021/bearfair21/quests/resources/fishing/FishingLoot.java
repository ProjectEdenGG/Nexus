package gg.projecteden.nexus.features.events.y2021.bearfair21.quests.resources.fishing;

import gg.projecteden.nexus.features.events.y2021.bearfair21.BearFair21;
import gg.projecteden.nexus.features.events.y2021.bearfair21.quests.npcs.Merchants;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MerchantBuilder.TradeBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.features.events.y2021.bearfair21.quests.resources.fishing.FishingLoot.FishingLootCategory.FISH;
import static gg.projecteden.nexus.features.events.y2021.bearfair21.quests.resources.fishing.FishingLoot.FishingLootCategory.JUNK;
import static gg.projecteden.nexus.features.events.y2021.bearfair21.quests.resources.fishing.FishingLoot.FishingLootCategory.TREASURE;
import static gg.projecteden.nexus.features.events.y2021.bearfair21.quests.resources.fishing.FishingLoot.FishingLootCategory.UNIQUE;
import static gg.projecteden.nexus.features.events.y2021.bearfair21.quests.resources.fishing.FishingLoot.FishingLootTime.BOTH;
import static gg.projecteden.nexus.features.events.y2021.bearfair21.quests.resources.fishing.FishingLoot.FishingLootTime.DAY;
import static gg.projecteden.nexus.features.events.y2021.bearfair21.quests.resources.fishing.FishingLoot.FishingLootTime.NIGHT;

@Getter
@AllArgsConstructor
public enum FishingLoot {
	// Fish
	CARP(FISH, 2, CustomMaterial.FISHING_LOOT_CARP, "Carp", 40),
	SALMON(FISH, 2, CustomMaterial.FISHING_LOOT_SALMON, "Salmon", 30),
	TROPICAL_FISH(FISH, 2, CustomMaterial.FISHING_LOOT_TROPICAL_FISH, "Tropical Fish", 20),
	PUFFERFISH(FISH, 2, CustomMaterial.FISHING_LOOT_PUFFERFISH, "Pufferfish", 10),
	BULLHEAD(FISH, 2, CustomMaterial.FISHING_LOOT_BULLHEAD, "Bullhead", 10),
	STURGEON(FISH, 2, CustomMaterial.FISHING_LOOT_STURGEON, "Sturgeon", 10),
	WOODSKIP(FISH, 2, CustomMaterial.FISHING_LOOT_WOODSKIP, "Woodskip", 10),
	VOID_SALMON(FISH, 2, CustomMaterial.FISHING_LOOT_VOID_SALMON, "Void Salmon", 10),
	RED_SNAPPER(FISH, 2, CustomMaterial.FISHING_LOOT_RED_SNAPPER, "Red Snapper", 10),
	RED_MULLET(FISH, 2, CustomMaterial.FISHING_LOOT_RED_MULLET, "Red Mullet", 10),
	// Junk
	OLD_BOOTS(JUNK, 1, CustomMaterial.FISHING_LOOT_OLD_BOOTS, "Old Boots", 10),
	RUSTY_SPOON(JUNK, 0, CustomMaterial.FISHING_LOOT_RUSTY_SPOON, "Rusty Spoon", 10),
	BROKEN_CD(JUNK, 0, CustomMaterial.FISHING_LOOT_BROKEN_CD, "Broken CD", 10),
	LOST_BOOK(JUNK, 1, CustomMaterial.FISHING_LOOT_LOST_BOOK, "Lost Book", 10),
	SOGGY_NEWSPAPER(JUNK, 0, CustomMaterial.FISHING_LOOT_SOGGY_NEWSPAPER, "Soggy Newspaper", 10),
	DRIFTWOOD(JUNK, 0, CustomMaterial.FISHING_LOOT_DRIFTWOOD, "Driftwood", 10),
	SEAWEED(JUNK, 0, CustomMaterial.FISHING_LOOT_SEAWEED, "Seaweed", 10),
	// Treasure
	GOLD_NUGGET(TREASURE, 1, Material.GOLD_NUGGET, 15),
	UNBREAKING(TREASURE, 5, Material.ENCHANTED_BOOK, 10),
	EFFICIENCY(TREASURE, 5, Material.ENCHANTED_BOOK, 10),
	FORTUNE(TREASURE, 6, Material.ENCHANTED_BOOK, 8),
	LURE(TREASURE, 6, Material.ENCHANTED_BOOK, 8),
	DIAMOND(TREASURE, 7, Material.DIAMOND, 6),
	NAUTILUS_SHELL(TREASURE, 7, Material.NAUTILUS_SHELL, 6),
	TREASURE_CHEST(TREASURE, 8, CustomMaterial.FISHING_LOOT_TREASURE_CHEST, "Treasure Chest", 5),
	// Unique
	MIDNIGHT_CARP(UNIQUE, 8, CustomMaterial.FISHING_LOOT_MIDNIGHT_CARP, "Midnight Carp", 50, "main", NIGHT),
	SUNFISH(UNIQUE, 8, CustomMaterial.FISHING_LOOT_SUNFISH, "Sunfish", 50, "main", DAY),
	STONEFISH(UNIQUE, 10, CustomMaterial.FISHING_LOOT_STONEFISH, "Stonefish", 100, "main", 120),
	TIGER_TROUT(UNIQUE, 8, CustomMaterial.FISHING_LOOT_TIGER_TROUT, "Tiger Trout", 50, "minigamenight"),
	SEA_CUCUMBER(UNIQUE, 8, CustomMaterial.FISHING_LOOT_SEA_CUCUMBER, "Sea Cucumber", 50, "minigamenight"),
	GLACIERFISH(UNIQUE, 10, CustomMaterial.FISHING_LOOT_GLACIERFISH, "Glacierfish", 100, "pugmas"),
	CRIMSONFISH(UNIQUE, 10, CustomMaterial.FISHING_LOOT_CRIMSONFISH, "Crimsonfish", 100, "halloween"),
	BLOBFISH(UNIQUE, 10, CustomMaterial.FISHING_LOOT_BLOBFISH, "Blobfish", 100, "summerdownunder"),
	;

	private final FishingLootCategory category;
	private final int gold;
	private final Material material;
	private final int modelId;
	private final double weight;
	private final String customName;
	private final String region;
	private final FishingLootTime time;
	private final Integer maxY;

	FishingLoot(FishingLootCategory category, int gold, Material material, double weight) {
		this(category, gold, material, 0, weight, null, null, null, null);
	}

	FishingLoot(FishingLootCategory category, int gold, CustomMaterial material, String customName, double weight) {
		this(category, gold, material.getMaterial(), material.getModelId(), weight, customName, null, BOTH, null);
	}

	FishingLoot(FishingLootCategory category, int gold, CustomMaterial material, String customName, double weight, String region) {
		this(category, gold, material.getMaterial(), material.getModelId(), weight, customName, region, BOTH, null);
	}

	FishingLoot(FishingLootCategory category, int gold, CustomMaterial material, String customName, double weight, String region, FishingLootTime time) {
		this(category, gold, material.getMaterial(), material.getModelId(), weight, customName, region, time, null);
	}

	FishingLoot(FishingLootCategory category, int gold, CustomMaterial material, String customName, double weight, String region, Integer maxY) {
		this(category, gold, material.getMaterial(), material.getModelId(), weight, customName, region, BOTH, maxY);
	}

	public static List<FishingLoot> of(FishingLootCategory category) {
		List<FishingLoot> result = new ArrayList<>();
		for (FishingLoot loot : values()) {
			if (loot.getCategory().equals(category))
				result.add(loot);
		}
		return result;
	}

	public static boolean isTrash(ItemStack itemStack) {
		for (FishingLoot loot : of(JUNK)) {
			if (ItemUtils.isFuzzyMatch(loot.getItem(), itemStack))
				return true;
		}

		return false;
	}

	public double getChance() {
		double sum = 0;
		FishingLootCategory category = this.getCategory();
		for (FishingLoot loot : values())
			if (category.equals(loot.getCategory()))
				sum += loot.getWeight();

		return (weight / sum) * 100;
	}

	public boolean applies(Player player) {
		return this.timeApplies(player) && this.regionApplies(player) && this.yValueApplies(player);
	}

	private boolean timeApplies(Player player) {
		World world = player.getWorld();
		FishingLootTime time = this.getTime();
		if (time == BOTH)
			return true;

		boolean isDay = world.isDayTime();
		if (isDay && time == DAY)
			return true;
		else
			return !isDay && time == NIGHT;
	}

	private boolean regionApplies(Player player) {
		Location location = player.getLocation();
		if (this.getRegion() == null)
			return true;

		return BearFair21.worldguard().isInRegion(location, BearFair21.getRegion() + "_" + this.getRegion());
	}

	private boolean yValueApplies(Player player) {
		Location location = player.getLocation();
		if (this.getMaxY() == null)
			return true;

		return location.getBlockY() <= this.getMaxY();
	}

	public ItemStack getItem() {
		return getItemBuilder().amount(1).build();
	}

	public ItemBuilder getItemBuilder() {
		Material material = this.getMaterial();
		ItemBuilder result = new ItemBuilder(material);

		if (this.getCustomName() != null)
			result.name(this.getCustomName());

		if (this.getModelId() != 0)
			result.modelId(this.getModelId());

		if (material.equals(Material.ENCHANTED_BOOK)) {
			if (this.equals(UNBREAKING))
				result.enchant(Enchant.UNBREAKING, 1);

			if (this.equals(EFFICIENCY)) {
				if (RandomUtils.chanceOf(20))
					result.enchant(Enchant.EFFICIENCY, 3);
				else
					result.enchant(Enchant.EFFICIENCY, 2);
			}

			if (this.equals(FORTUNE))
				result.enchant(Enchant.FORTUNE, 1);

			if (this.equals(LURE))
				result.enchant(Enchant.LURE, 1);
		}

		return result.lore(getLore());
	}

	public String getLore() {
		return switch (category) {
			case FISH -> "&7Fish";
			case UNIQUE -> "&7Unique";
			case TREASURE -> null;
			case JUNK -> "&7Trash";
		};
	}

	public static List<TradeBuilder> getTrades() {
		return new ArrayList<>() {{
			for (FishingLoot loot : values()) {
				if (loot.equals(EFFICIENCY)) {
					add(new TradeBuilder()
						.result(Merchants.goldNugget.clone().amount(loot.getGold()).build())
						.ingredient(loot.getItemBuilder().enchant(Enchant.EFFICIENCY, 2))
					);
					add(new TradeBuilder()
						.result(Merchants.goldNugget.clone().amount(loot.getGold()).build())
						.ingredient(loot.getItemBuilder().enchant(Enchant.EFFICIENCY, 3))
					);
				} else {
					add(new TradeBuilder()
						.result(Merchants.goldNugget.clone().amount(loot.getGold()).build())
						.ingredient(loot.getItem())
					);
				}
			}
		}};
	}

	@Getter
	@AllArgsConstructor
	public enum FishingLootCategory {
		FISH(50),
		JUNK(JunkWeight.MAX.getWeight()),
		UNIQUE(17),
		TREASURE(8);

		private final double weight;

		public double getChance() {
			double sum = 0;
			for (FishingLootCategory category : values())
				sum += category.getWeight();

			return (weight / sum) * 100;
		}
	}

	public enum FishingLootTime {
		DAY,
		NIGHT,
		BOTH
	}

	@Getter
	@AllArgsConstructor
	public enum JunkWeight {
		MAX(25, 0),
		EIGHTY_PERCENT(20, 25),    // +25
		SIXTY_PERCENT(15, 100),    // +75
		MIN(10, 225);            // +125

		private final double weight;
		private final int amount;

		public JunkWeight update(int amount) {
			if (amount >= MIN.getAmount())
				return MIN;
			else if (amount >= SIXTY_PERCENT.getAmount())
				return SIXTY_PERCENT;
			else if (amount >= EIGHTY_PERCENT.getAmount())
				return SIXTY_PERCENT;
			else if (amount >= MAX.getAmount())
				return MAX;

			return this;
		}
	}

}

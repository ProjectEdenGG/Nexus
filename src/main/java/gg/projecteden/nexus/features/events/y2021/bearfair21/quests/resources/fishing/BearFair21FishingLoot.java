package gg.projecteden.nexus.features.events.y2021.bearfair21.quests.resources.fishing;

import gg.projecteden.nexus.features.events.y2021.bearfair21.BearFair21;
import gg.projecteden.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21Merchants;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
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

@Getter
@AllArgsConstructor
public enum BearFair21FishingLoot {
	// Fish
	CARP(FishingLootCategory.FISH, 2, ItemModelType.FISHING_LOOT_CARP, "Carp", 40),
	SALMON(FishingLootCategory.FISH, 2, ItemModelType.FISHING_LOOT_SALMON, "Salmon", 30),
	TROPICAL_FISH(FishingLootCategory.FISH, 2, ItemModelType.FISHING_LOOT_TROPICAL_FISH, "Tropical Fish", 20),
	PUFFERFISH(FishingLootCategory.FISH, 2, ItemModelType.FISHING_LOOT_PUFFERFISH, "Pufferfish", 10),
	BULLHEAD(FishingLootCategory.FISH, 2, ItemModelType.FISHING_LOOT_BULLHEAD, "Bullhead", 10),
	STURGEON(FishingLootCategory.FISH, 2, ItemModelType.FISHING_LOOT_STURGEON, "Sturgeon", 10),
	WOODSKIP(FishingLootCategory.FISH, 2, ItemModelType.FISHING_LOOT_WOODSKIP, "Woodskip", 10),
	VOID_SALMON(FishingLootCategory.FISH, 2, ItemModelType.FISHING_LOOT_VOID_SALMON, "Void Salmon", 10),
	RED_SNAPPER(FishingLootCategory.FISH, 2, ItemModelType.FISHING_LOOT_RED_SNAPPER, "Red Snapper", 10),
	RED_MULLET(FishingLootCategory.FISH, 2, ItemModelType.FISHING_LOOT_RED_MULLET, "Red Mullet", 10),
	// Junk
	OLD_BOOTS(FishingLootCategory.JUNK, 1, ItemModelType.FISHING_LOOT_OLD_BOOTS, "Old Boots", 10),
	RUSTY_SPOON(FishingLootCategory.JUNK, 0, ItemModelType.FISHING_LOOT_RUSTY_SPOON, "Rusty Spoon", 10),
	BROKEN_CD(FishingLootCategory.JUNK, 0, ItemModelType.FISHING_LOOT_BROKEN_CD, "Broken CD", 10),
	LOST_BOOK(FishingLootCategory.JUNK, 1, ItemModelType.FISHING_LOOT_LOST_BOOK, "Lost Book", 10),
	SOGGY_NEWSPAPER(FishingLootCategory.JUNK, 0, ItemModelType.FISHING_LOOT_SOGGY_NEWSPAPER, "Soggy Newspaper", 10),
	DRIFTWOOD(FishingLootCategory.JUNK, 0, ItemModelType.FISHING_LOOT_DRIFTWOOD, "Driftwood", 10),
	SEAWEED(FishingLootCategory.JUNK, 0, ItemModelType.FISHING_LOOT_SEAWEED, "Seaweed", 10),
	// Treasure
	GOLD_NUGGET(FishingLootCategory.TREASURE, 1, Material.GOLD_NUGGET, 15),
	UNBREAKING(FishingLootCategory.TREASURE, 5, Material.ENCHANTED_BOOK, 10),
	EFFICIENCY(FishingLootCategory.TREASURE, 5, Material.ENCHANTED_BOOK, 10),
	FORTUNE(FishingLootCategory.TREASURE, 6, Material.ENCHANTED_BOOK, 8),
	LURE(FishingLootCategory.TREASURE, 6, Material.ENCHANTED_BOOK, 8),
	DIAMOND(FishingLootCategory.TREASURE, 7, Material.DIAMOND, 6),
	NAUTILUS_SHELL(FishingLootCategory.TREASURE, 7, Material.NAUTILUS_SHELL, 6),
	TREASURE_CHEST(FishingLootCategory.TREASURE, 8, ItemModelType.FISHING_LOOT_TREASURE_CHEST, "Treasure Chest", 5),
	// Unique
	MIDNIGHT_CARP(FishingLootCategory.UNIQUE, 8, ItemModelType.FISHING_LOOT_MIDNIGHT_CARP, "Midnight Carp", 50, "main", FishingLootTime.NIGHT),
	SUNFISH(FishingLootCategory.UNIQUE, 8, ItemModelType.FISHING_LOOT_SUNFISH, "Sunfish", 50, "main", FishingLootTime.DAY),
	STONEFISH(FishingLootCategory.UNIQUE, 10, ItemModelType.FISHING_LOOT_STONEFISH, "Stonefish", 100, "main", 120),
	TIGER_TROUT(FishingLootCategory.UNIQUE, 8, ItemModelType.FISHING_LOOT_TIGER_TROUT, "Tiger Trout", 50, "minigamenight"),
	SEA_CUCUMBER(FishingLootCategory.UNIQUE, 8, ItemModelType.FISHING_LOOT_SEA_CUCUMBER, "Sea Cucumber", 50, "minigamenight"),
	GLACIERFISH(FishingLootCategory.UNIQUE, 10, ItemModelType.FISHING_LOOT_GLACIERFISH, "Glacierfish", 100, "pugmas"),
	CRIMSONFISH(FishingLootCategory.UNIQUE, 10, ItemModelType.FISHING_LOOT_CRIMSONFISH, "Crimsonfish", 100, "halloween"),
	BLOBFISH(FishingLootCategory.UNIQUE, 10, ItemModelType.FISHING_LOOT_BLOBFISH, "Blobfish", 100, "summerdownunder"),
	;

	private final FishingLootCategory category;
	private final int gold;
	private final Material material;
	private final String modelId;
	private final double weight;
	private final String customName;
	private final String region;
	private final FishingLootTime time;
	private final Integer maxY;

	BearFair21FishingLoot(FishingLootCategory category, int gold, Material material, double weight) {
		this(category, gold, material, null, weight, null, null, null, null);
	}

	BearFair21FishingLoot(FishingLootCategory category, int gold, ItemModelType itemModelType, String customName, double weight) {
		this(category, gold, itemModelType.getMaterial(), itemModelType.getModel(), weight, customName, null, FishingLootTime.BOTH, null);
	}

	BearFair21FishingLoot(FishingLootCategory category, int gold, ItemModelType itemModelType, String customName, double weight, String region) {
		this(category, gold, itemModelType.getMaterial(), itemModelType.getModel(), weight, customName, region, FishingLootTime.BOTH, null);
	}

	BearFair21FishingLoot(FishingLootCategory category, int gold, ItemModelType itemModelType, String customName, double weight, String region, FishingLootTime time) {
		this(category, gold, itemModelType.getMaterial(), itemModelType.getModel(), weight, customName, region, time, null);
	}

	BearFair21FishingLoot(FishingLootCategory category, int gold, ItemModelType itemModelType, String customName, double weight, String region, Integer maxY) {
		this(category, gold, itemModelType.getMaterial(), itemModelType.getModel(), weight, customName, region, FishingLootTime.BOTH, maxY);
	}

	public static List<BearFair21FishingLoot> of(FishingLootCategory category) {
		List<BearFair21FishingLoot> result = new ArrayList<>();
		for (BearFair21FishingLoot loot : values()) {
			if (loot.getCategory().equals(category))
				result.add(loot);
		}
		return result;
	}

	public static boolean isTrash(ItemStack itemStack) {
		for (BearFair21FishingLoot loot : of(FishingLootCategory.JUNK)) {
			if (ItemUtils.isFuzzyMatch(loot.getItem(), itemStack))
				return true;
		}

		return false;
	}

	public double getChance() {
		double sum = 0;
		FishingLootCategory category = this.getCategory();
		for (BearFair21FishingLoot loot : values())
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
		if (time == FishingLootTime.BOTH)
			return true;

		boolean isDay = world.isDayTime();
		if (isDay && time == FishingLootTime.DAY)
			return true;
		else
			return !isDay && time == FishingLootTime.NIGHT;
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

		if (this.getModelId() != null)
			result.model(this.getModelId());

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
			for (BearFair21FishingLoot loot : values()) {
				if (loot.equals(EFFICIENCY)) {
					add(new TradeBuilder()
						.result(BearFair21Merchants.goldNugget.clone().amount(loot.getGold()).build())
						.ingredient(loot.getItemBuilder().enchant(Enchant.EFFICIENCY, 2))
					);
					add(new TradeBuilder()
						.result(BearFair21Merchants.goldNugget.clone().amount(loot.getGold()).build())
						.ingredient(loot.getItemBuilder().enchant(Enchant.EFFICIENCY, 3))
					);
				} else {
					add(new TradeBuilder()
						.result(BearFair21Merchants.goldNugget.clone().amount(loot.getGold()).build())
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

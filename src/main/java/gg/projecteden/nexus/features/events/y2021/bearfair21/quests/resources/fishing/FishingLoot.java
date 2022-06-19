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
	CARP(FISH, 2, CustomMaterial.BEARFAIR21_CARP, 40, "Carp"),
	SALMON(FISH, 2, CustomMaterial.BEARFAIR21_SALMON, 30, "Salmon"),
	TROPICAL_FISH(FISH, 2, CustomMaterial.BEARFAIR21_TROPICAL_FISH, 20, "Tropical Fish"),
	PUFFERFISH(FISH, 2, CustomMaterial.BEARFAIR21_PUFFERFISH, 10, "Pufferfish"),
	BULLHEAD(FISH, 2, CustomMaterial.BEARFAIR21_BULLHEAD, 10, "Bullhead"),
	STURGEON(FISH, 2, CustomMaterial.BEARFAIR21_STURGEON, 10, "Sturgeon"),
	WOODSKIP(FISH, 2, CustomMaterial.BEARFAIR21_WOODSKIP, 10, "Woodskip"),
	VOID_SALMON(FISH, 2, CustomMaterial.BEARFAIR21_VOID_SALMON, 10, "Void Salmon"),
	RED_SNAPPER(FISH, 2, CustomMaterial.BEARFAIR21_RED_SNAPPER, 10, "Red Snapper"),
	RED_MULLET(FISH, 2, CustomMaterial.BEARFAIR21_RED_MULLET, 10, "Red Mullet"),
	// Junk
	OLD_BOOTS(JUNK, 1, CustomMaterial.BEARFAIR21_OLD_BOOTS, 10, "Old Boots"),
	RUSTY_SPOON(JUNK, 0, CustomMaterial.BEARFAIR21_RUSTY_SPOON, 10, "Rusty Spoon"),
	BROKEN_CD(JUNK, 0, CustomMaterial.BEARFAIR21_BROKEN_CD, 10, "Broken CD"),
	LOST_BOOK(JUNK, 1, CustomMaterial.BEARFAIR21_LOST_BOOK, 10, "Lost Book"),
	SOGGY_NEWSPAPER(JUNK, 0, CustomMaterial.BEARFAIR21_SOGGY_NEWSPAPER, 10, "Soggy Newspaper"),
	DRIFTWOOD(JUNK, 0, CustomMaterial.BEARFAIR21_DRIFTWOOD, 10, "Driftwood"),
	SEAWEED(JUNK, 0, CustomMaterial.BEARFAIR21_SEAWEED, 10, "Seaweed"),
	// Treasure
	GOLD_NUGGET(TREASURE, 1, Material.GOLD_NUGGET, 15),
	UNBREAKING(TREASURE, 5, Material.ENCHANTED_BOOK, 10),
	EFFICIENCY(TREASURE, 5, Material.ENCHANTED_BOOK, 10),
	FORTUNE(TREASURE, 6, Material.ENCHANTED_BOOK, 8),
	LURE(TREASURE, 6, Material.ENCHANTED_BOOK, 8),
	DIAMOND(TREASURE, 7, Material.DIAMOND, 6),
	NAUTILUS_SHELL(TREASURE, 7, Material.NAUTILUS_SHELL, 6),
	TREASURE_CHEST(TREASURE, 8, CustomMaterial.BEARFAIR21_TREASURE_CHEST, 5, "Treasure Chest"),
	// Unique
	MIDNIGHT_CARP(UNIQUE, 8, CustomMaterial.BEARFAIR21_MIDNIGHT_CARP, 50, "Midnight Carp", "main", NIGHT),
	SUNFISH(UNIQUE, 8, CustomMaterial.BEARFAIR21_SUNFISH, 50, "Sunfish", "main", DAY),
	STONEFISH(UNIQUE, 10, CustomMaterial.BEARFAIR21_STONEFISH, 100, "Stonefish", "main", 120),
	TIGER_TROUT(UNIQUE, 8, CustomMaterial.BEARFAIR21_TIGER_TROUT, 50, "Tiger Trout", "minigamenight"),
	SEA_CUCUMBER(UNIQUE, 8, CustomMaterial.BEARFAIR21_SEA_CUCUMBER, 50, "Sea Cucumber", "minigamenight"),
	GLACIERFISH(UNIQUE, 10, CustomMaterial.BEARFAIR21_GLACIERFISH, 100, "Glacierfish", "pugmas"),
	CRIMSONFISH(UNIQUE, 10, CustomMaterial.BEARFAIR21_CRIMSONFISH, 100, "Crimsonfish", "halloween"),
	BLOBFISH(UNIQUE, 10, CustomMaterial.BEARFAIR21_BLOBFISH, 100, "Blobfish", "summerdownunder"),
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

	FishingLoot(FishingLootCategory category, int gold, CustomMaterial material, double weight, String customName) {
		this(category, gold, material.getMaterial(), material.getModelId(), weight, customName, null, BOTH, null);
	}

	FishingLoot(FishingLootCategory category, int gold, CustomMaterial material, double weight, String customName, String region) {
		this(category, gold, material.getMaterial(), material.getModelId(), weight, customName, region, BOTH, null);
	}

	FishingLoot(FishingLootCategory category, int gold, CustomMaterial material, double weight, String customName, String region, FishingLootTime time) {
		this(category, gold, material.getMaterial(), material.getModelId(), weight, customName, region, time, null);
	}

	FishingLoot(FishingLootCategory category, int gold, CustomMaterial material, double weight, String customName, String region, Integer maxY) {
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
			result.customModelData(this.getModelId());

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

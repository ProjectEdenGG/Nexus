package me.pugabyte.nexus.features.events.y2021.bearfair21.quests.fishing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.features.resourcepack.ResourcePack;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.nexus.features.events.y2021.bearfair21.quests.fishing.FishingLoot.FishingLootCategory.FISH;
import static me.pugabyte.nexus.features.events.y2021.bearfair21.quests.fishing.FishingLoot.FishingLootCategory.JUNK;
import static me.pugabyte.nexus.features.events.y2021.bearfair21.quests.fishing.FishingLoot.FishingLootCategory.TREASURE;
import static me.pugabyte.nexus.features.events.y2021.bearfair21.quests.fishing.FishingLoot.FishingLootCategory.UNIQUE;
import static me.pugabyte.nexus.features.events.y2021.bearfair21.quests.fishing.FishingLoot.FishingLootTime.BOTH;
import static me.pugabyte.nexus.features.events.y2021.bearfair21.quests.fishing.FishingLoot.FishingLootTime.DAY;
import static me.pugabyte.nexus.features.events.y2021.bearfair21.quests.fishing.FishingLoot.FishingLootTime.NIGHT;


@Getter
@AllArgsConstructor
public enum FishingLoot {

	// Fish
	CARP(FISH, 1, Material.COD, 40.0, "Carp", 1),
	SALMON(FISH, 1, Material.COD, Material.SALMON, 30.0, "Salmon", 2),
	TROPICAL_FISH(FISH, 1, Material.COD, Material.TROPICAL_FISH, 20.0, "Tropical Fish", 3),
	PUFFERFISH(FISH, 1, Material.COD, Material.PUFFERFISH, 10.0, "Pufferfish", 4),
	BULLHEAD(FISH, 1, Material.COD, Material.COOKED_SALMON, 10.0, "Bullhead", 5),
	STURGEON(FISH, 1, Material.COD, 10.0, "Sturgeon", 6),
	WOODSKIP(FISH, 1, Material.COD, 10.0, "Woodskip", 7),
	VOID_SALMON(FISH, 1, Material.COD, Material.SALMON, 10.0, "Void Salmon", 8),
	RED_SNAPPER(FISH, 1, Material.COD, Material.SALMON, 10.0, "Red Snapper", 9),
	RED_MULLET(FISH, 1, Material.COD, Material.SALMON, 10.0, "Red Mullet", 10),
	// Junk
	OLD_BOOTS(JUNK, 1, Material.LEATHER_BOOTS, 10.0, "Old Boots", 1),
	RUSTY_SPOON(JUNK, 1, Material.IRON_SHOVEL, 10.0, "Rusty Spoon", 1),
	BROKEN_CD(JUNK, 1, Material.MUSIC_DISC_11, 10.0, "Broken CD", 1),
	LOST_BOOK(JUNK, 1, Material.BOOK, 10.0, "Lost Book", 1),
	SOGGY_NEWSPAPER(JUNK, 1, Material.PAPER, 10.0, "Soggy Newspaper", 1),
	DRIFTWOOD(JUNK, 1, Material.STICK, 10.0, "Driftwood", 47),
	SEAWEED(JUNK, 1, Material.KELP, 10.0, "Seaweed", 1),
	// Treasure
	CORAL_BRAIN(TREASURE, 1, Material.BRAIN_CORAL, 15.0),
	CORAL_HORN(TREASURE, 1, Material.HORN_CORAL, 15.0),
	CORAL_TUBE(TREASURE, 1, Material.TUBE_CORAL, 15.0),
	CORAL_FIRE(TREASURE, 1, Material.FIRE_CORAL, 15.0),
	CORAL_BUBBLE(TREASURE, 1, Material.BUBBLE_CORAL, 15.0),
	NAUTILUS_SHELL(TREASURE, 1, Material.NAUTILUS_SHELL, 5.0),
	TREASURE_CHEST(TREASURE, 1, Material.CHEST, 1.0, "Treasure Chest", 1),
	// Unique
	MIDNIGHT_CARP(UNIQUE, 1, Material.COD, Material.TROPICAL_FISH, 50.0, "Midnight Carp", 11, "main", NIGHT),
	SUNFISH(UNIQUE, 1, Material.COD, Material.TROPICAL_FISH, 50.0, "Sunfish", 12, "main", DAY),
	STONEFISH(UNIQUE, 1, Material.COD, Material.COD, 100.0, "Stonefish", 13, "main", 120),
	TIGER_TROUT(UNIQUE, 1, Material.COD, Material.COOKED_SALMON, 50.0, "Tiger Trout", 14, "minigamenight"),
	SEA_CUCUMBER(UNIQUE, 1, Material.SEA_PICKLE, 50.0, "Sea Cucumber", 0, "minigamenight"),
	GLACIERFISH(UNIQUE, 1, Material.COD, 100.0, "Glacierfish", 15, "pugmas"),
	CRIMSONFISH(UNIQUE, 1, Material.COD, Material.SALMON, 100.0, "Crimsonfish", 16, "halloween"),
	BLOBFISH(UNIQUE, 1, Material.COD, Material.PUFFERFISH, 100.0, "Blobfish", 17, "summerdownunder"),
	;

	FishingLootCategory category;
	int gold;
	Material resourcepack;
	Material backup;
	double weight;
	String customName;
	int customModelData;
	String region;
	FishingLootTime time;
	Integer maxY;

	FishingLoot(FishingLootCategory category, int gold, Material resourcepack, double weight) {
		this.category = category;
		this.gold = gold;
		this.resourcepack = resourcepack;
		this.backup = null;
		this.weight = weight;
		this.customName = null;
		this.customModelData = 0;
		this.region = null;
		this.time = BOTH;
	}

	FishingLoot(FishingLootCategory category, int gold, Material resourcepack, double weight, String customName, int customModelData) {
		this.category = category;
		this.gold = gold;
		this.resourcepack = resourcepack;
		this.backup = null;
		this.weight = weight;
		this.customName = customName;
		this.customModelData = customModelData;
		this.region = null;
		this.time = BOTH;
	}

	FishingLoot(FishingLootCategory category, int gold, Material resourcepack, double weight, String customName, int customModelData, String region) {
		this.category = category;
		this.gold = gold;
		this.resourcepack = resourcepack;
		this.backup = null;
		this.weight = weight;
		this.customName = customName;
		this.customModelData = customModelData;
		this.region = region;
		this.time = BOTH;
	}

	FishingLoot(FishingLootCategory category, int gold, Material resourcepack, Material backup, double weight, String customName, int customModelData) {
		this.category = category;
		this.gold = gold;
		this.resourcepack = resourcepack;
		this.backup = backup;
		this.weight = weight;
		this.customName = customName;
		this.customModelData = customModelData;
		this.region = null;
		this.time = BOTH;
	}

	FishingLoot(FishingLootCategory category, int gold, Material resourcepack, Material backup, double weight, String customName, int customModelData, String region) {
		this.category = category;
		this.gold = gold;
		this.resourcepack = resourcepack;
		this.backup = backup;
		this.weight = weight;
		this.customName = customName;
		this.customModelData = customModelData;
		this.region = region;
		this.time = BOTH;
	}

	FishingLoot(FishingLootCategory category, int gold, Material resourcepack, Material backup, double weight, String customName, int customModelData, String region, FishingLootTime time) {
		this.category = category;
		this.gold = gold;
		this.resourcepack = resourcepack;
		this.backup = backup;
		this.weight = weight;
		this.customName = customName;
		this.customModelData = customModelData;
		this.region = region;
		this.time = time;
		this.maxY = null;
	}

	FishingLoot(FishingLootCategory category, int gold, Material resourcepack, Material backup, double weight, String customName, int customModelData, String region, Integer maxY) {
		this.category = category;
		this.gold = gold;
		this.resourcepack = resourcepack;
		this.backup = backup;
		this.weight = weight;
		this.customName = customName;
		this.customModelData = customModelData;
		this.region = region;
		this.time = BOTH;
		this.maxY = maxY;
	}

	public static List<FishingLoot> of(FishingLootCategory category) {
		List<FishingLoot> result = new ArrayList<>();
		for (FishingLoot loot : values()) {
			if (loot.getCategory().equals(category))
				result.add(loot);
		}
		return result;
	}

	public double getChance() {
		double sum = 0.0;
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

		return BearFair21.getWGUtils().isInRegion(location, this.getRegion());
	}

	private boolean yValueApplies(Player player) {
		Location location = player.getLocation();
		if (this.getMaxY() == null)
			return true;

		return location.getBlockY() <= this.getMaxY();
	}

	public Material getMaterial(Player player) {
		if (!ResourcePack.isEnabledFor(player) && backup != null)
			return backup;

		return resourcepack;
	}

	public ItemStack getItem(Player player) {
		ItemBuilder result = new ItemBuilder(this.getMaterial(player));

		if (this.getCustomName() != null)
			result.name(this.getCustomName());
		if (this.getCustomModelData() != 0)
			result.customModelData(this.getCustomModelData());

		return result.build();
	}

	@Getter
	@AllArgsConstructor
	public enum FishingLootCategory {
		FISH(50.0),
		JUNK(25.0),
		UNIQUE(15.0),
		TREASURE(10.0);

		double weight;

		public double getChance() {
			double sum = 0.0;
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

}

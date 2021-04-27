package me.pugabyte.nexus.features.events.y2021.bearfair21.quests.fishing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.features.resourcepack.ResourcePack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

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
	CARP(FISH, Material.COD, 40.0, "Carp", 1),
	SALMON(FISH, Material.COD, Material.SALMON, 30.0, "Salmon", 2),
	TROPICAL_FISH(FISH, Material.COD, Material.TROPICAL_FISH, 20.0, "Tropical Fish", 3),
	PUFFERFISH(FISH, Material.COD, Material.PUFFERFISH, 10.0, "Pufferfish", 4),
	BULLHEAD(FISH, Material.COD, Material.COOKED_SALMON, 10.0, "Bullhead", 5),
	STURGEON(FISH, Material.COD, 10.0, "Sturgeon", 6),
	WOODSKIP(FISH, Material.COD, 10.0, "Woodskip", 7),
	VOID_SALMON(FISH, Material.COD, Material.SALMON, 10.0, "Void Salmon", 8),
	RED_SNAPPER(FISH, Material.COD, Material.SALMON, 10.0, "Red Snapper", 9),
	RED_MULLET(FISH, Material.COD, Material.SALMON, 10.0, "Red Mullet", 10),
	// Junk
	OLD_BOOTS(JUNK, Material.LEATHER_BOOTS, 10.0, "Old Boots", 0),
	RUSTY_SPOON(JUNK, Material.IRON_SHOVEL, 10.0, "Rusty Spoon", 0),
	BROKEN_CD(JUNK, Material.MUSIC_DISC_11, 10.0, "Broken CD", 0),
	LOST_BOOK(JUNK, Material.BOOK, 10.0, "Lost Book", 0),
	SOGGY_NEWSPAPER(JUNK, Material.PAPER, 10.0, "Soggy Newspaper", 0),
	DRIFTWOOD(JUNK, Material.STICK, 10.0, "Driftwood", 0),
	SEAWEED(JUNK, Material.KELP, 10.0, "Seaweed", 0),
	// Treasure
	CORAL_BRAIN(TREASURE, Material.BRAIN_CORAL, 15.0),
	CORAL_HORN(TREASURE, Material.HORN_CORAL, 15.0),
	CORAL_TUBE(TREASURE, Material.TUBE_CORAL, 15.0),
	CORAL_FIRE(TREASURE, Material.FIRE_CORAL, 15.0),
	CORAL_BUBBLE(TREASURE, Material.BUBBLE_CORAL, 15.0),
	NAUTILUS_SHELL(TREASURE, Material.NAUTILUS_SHELL, 5.0),
	CHEST(TREASURE, Material.CHEST, 1.0, "Old Chest", 0),
	// Unique
	MIDNIGHT_CARP(UNIQUE, Material.COD, Material.TROPICAL_FISH, 50.0, "Midnight Carp", 11, "main", NIGHT),
	SUNFISH(UNIQUE, Material.COD, Material.TROPICAL_FISH, 50.0, "Sunfish", 12, "main", DAY),
	TIGER_TROUT(UNIQUE, Material.COD, Material.COOKED_SALMON, 50.0, "Tiger Trout", 13, "minigamenight"),
	SEA_CUCUMBER(UNIQUE, Material.SEA_PICKLE, 50.0, "Sea Cucumber", 0, "minigamenight"),
	GLACIERFISH(UNIQUE, Material.COD, 100.0, "Glacierfish", 14, "pugmas"),
	CRIMSONFISH(UNIQUE, Material.COD, Material.SALMON, 100.0, "Crimsonfish", 15, "halloween"),
	BLOBFISH(UNIQUE, Material.COD, Material.PUFFERFISH, 100.0, "Blobfish", 16, "summerdownunder"),
	;

	FishingLootCategory category;
	Material resourcepack;
	Material backup;
	double weight;
	String customName;
	int customModelData;
	String region;
	FishingLootTime time;

	FishingLoot(FishingLootCategory category, Material resourcepack, double weight) {
		this.category = category;
		this.resourcepack = resourcepack;
		this.backup = null;
		this.weight = weight;
		this.customName = null;
		this.customModelData = 0;
		this.region = null;
		this.time = BOTH;
	}

	FishingLoot(FishingLootCategory category, Material resourcepack, double weight, String customName, int customModelData) {
		this.category = category;
		this.resourcepack = resourcepack;
		this.backup = null;
		this.weight = weight;
		this.customName = customName;
		this.customModelData = customModelData;
		this.region = null;
		this.time = BOTH;
	}

	FishingLoot(FishingLootCategory category, Material resourcepack, double weight, String customName, int customModelData, String region) {
		this.category = category;
		this.resourcepack = resourcepack;
		this.backup = null;
		this.weight = weight;
		this.customName = customName;
		this.customModelData = customModelData;
		this.region = region;
		this.time = BOTH;
	}

	FishingLoot(FishingLootCategory category, Material resourcepack, Material backup, double weight, String customName, int customModelData) {
		this.category = category;
		this.resourcepack = resourcepack;
		this.backup = backup;
		this.weight = weight;
		this.customName = customName;
		this.customModelData = customModelData;
		this.region = null;
		this.time = BOTH;
	}

	FishingLoot(FishingLootCategory category, Material resourcepack, Material backup, double weight, String customName, int customModelData, String region) {
		this.category = category;
		this.resourcepack = resourcepack;
		this.backup = backup;
		this.weight = weight;
		this.customName = customName;
		this.customModelData = customModelData;
		this.region = region;
		this.time = BOTH;
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
		for (FishingLoot loot : values())
			sum += loot.getWeight();

		return (weight / sum) * 100;
	}

	public boolean timeApplies(World world) {
		FishingLootTime time = this.getTime();
		if (time == BOTH)
			return true;

		boolean isDay = world.isDayTime();
		if (isDay && time == DAY)
			return true;
		else
			return !isDay && time == NIGHT;
	}

	public boolean regionApplies(Location location) {
		if (this.getRegion() == null)
			return true;

		return BearFair21.getWGUtils().isInRegion(location, this.getRegion());
	}

	public Material getMaterial(Player player) {
		if (!ResourcePack.isEnabledFor(player) && backup != null)
			return backup;

		return resourcepack;
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

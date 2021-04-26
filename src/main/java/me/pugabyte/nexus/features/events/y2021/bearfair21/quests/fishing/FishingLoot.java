package me.pugabyte.nexus.features.events.y2021.bearfair21.quests.fishing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.nexus.features.events.y2021.bearfair21.quests.fishing.FishingLoot.FishingLootCategory.FISH;
import static me.pugabyte.nexus.features.events.y2021.bearfair21.quests.fishing.FishingLoot.FishingLootCategory.JUNK;
import static me.pugabyte.nexus.features.events.y2021.bearfair21.quests.fishing.FishingLoot.FishingLootCategory.TREASURE;


@Getter
@AllArgsConstructor
public enum FishingLoot {

	// Fish
	COD(FISH, Material.COD, 40.0),
	SALMON(FISH, Material.SALMON, 30.0),
	TROPICAL(FISH, Material.TROPICAL_FISH, 20.0),
	PUFFER(FISH, Material.PUFFERFISH, 10.0),
	BULLHEAD(FISH, Material.COD, 10.0, "Bullhead", 901),
	STURGEON(FISH, Material.COD, 10.0, "Sturgeon", 902),
	WOODSKIP(FISH, Material.COD, 10.0, "Woodskip", 903),
	VOID_SALMON(FISH, Material.SALMON, 10.0, "Void Salmon", 901),
	RED_SNAPPER(FISH, Material.SALMON, 10.0, "Red Snapper", 902),
	RED_MULLET(FISH, Material.SALMON, 10.0, "Red Mullet", 903),
	// Junk
	OLD_BOOTS(JUNK, Material.LEATHER_BOOTS, 10.0, "Old Boots"),
	RUSTY_SPOON(JUNK, Material.IRON_SHOVEL, 10.0, "Rusty Spoon"),
	BROKEN_CD(JUNK, Material.MUSIC_DISC_11, 10.0, "Broken CD"),
	LOST_BOOK(JUNK, Material.BOOK, 10.0, "Lost Book"),
	SOGGY_NEWSPAPER(JUNK, Material.PAPER, 10.0, "Soggy Newspaper"),
	DRIFTWOOD(JUNK, Material.STICK, 10.0, "Driftwood"),
	SEAWEED(JUNK, Material.KELP, 10.0, "Seaweed"),
	// Treasure
	CORAL_BRAIN(TREASURE, Material.BRAIN_CORAL, 15.0),
	CORAL_HORN(TREASURE, Material.HORN_CORAL, 15.0),
	CORAL_TUBE(TREASURE, Material.TUBE_CORAL, 15.0),
	CORAL_FIRE(TREASURE, Material.FIRE_CORAL, 15.0),
	CORAL_BUBBLE(TREASURE, Material.BUBBLE_CORAL, 15.0),
	NAUTILUS_SHELL(TREASURE, Material.NAUTILUS_SHELL, 5.0),
	;

	FishingLootCategory category;
	Material material;
	double weight;
	String customName;
	int customModelData;
	String region;
	FishingLootTime time;

	FishingLoot(FishingLootCategory category, Material material, double weight) {
		this.category = category;
		this.material = material;
		this.weight = weight;
		this.customName = null;
		this.customModelData = 0;
		this.region = null;
		this.time = FishingLootTime.BOTH;
	}

	FishingLoot(FishingLootCategory category, Material material, double weight, String customName) {
		this.category = category;
		this.material = material;
		this.weight = weight;
		this.customName = customName;
		this.customModelData = 0;
		this.region = null;
		this.time = FishingLootTime.BOTH;
	}

	FishingLoot(FishingLootCategory category, Material material, double weight, String customName, int customModelData) {
		this.category = category;
		this.material = material;
		this.weight = weight;
		this.customName = customName;
		this.customModelData = customModelData;
		this.region = null;
		this.time = FishingLootTime.BOTH;
	}

	FishingLoot(FishingLootCategory category, Material material, double weight, String customName, int customModelData, String region) {
		this.category = category;
		this.material = material;
		this.weight = weight;
		this.customName = customName;
		this.customModelData = customModelData;
		this.region = region;
		this.time = FishingLootTime.BOTH;
	}

	FishingLoot(FishingLootCategory category, Material material, double weight, String customName, int customModelData, FishingLootTime time) {
		this.category = category;
		this.material = material;
		this.weight = weight;
		this.customName = customName;
		this.customModelData = customModelData;
		this.region = null;
		this.time = time;
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

	@Getter
	@AllArgsConstructor
	public enum FishingLootCategory {
		FISH(60.0),
		JUNK(30.0),
		TREASURE(10.0),
		;

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

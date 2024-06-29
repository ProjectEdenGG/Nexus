package gg.projecteden.nexus.features.events.models;

import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Predicate;

import static gg.projecteden.nexus.features.events.models.EventFishingLoot.EventFishingLootCategory.FISH;
import static gg.projecteden.nexus.features.events.models.EventFishingLoot.EventFishingLootCategory.JUNK;
import static gg.projecteden.nexus.features.events.models.EventFishingLoot.EventFishingLootCategory.TREASURE;
import static gg.projecteden.nexus.features.events.models.EventFishingLoot.EventFishingLootTime.BOTH;
import static gg.projecteden.nexus.features.events.models.EventFishingLoot.EventFishingLootTime.DAY;
import static gg.projecteden.nexus.features.events.models.EventFishingLoot.EventFishingLootTime.NIGHT;

public class EventFishingLoot {

	@Getter
	@AllArgsConstructor
	public enum EventDefaultFishingLoot {
		// Fish
		CARP(FISH, CustomMaterial.FISHING_LOOT_CARP, "Carp", 40),
		SALMON(FISH, CustomMaterial.FISHING_LOOT_SALMON, "Salmon", 30),
		TROPICAL_FISH(FISH, CustomMaterial.FISHING_LOOT_TROPICAL_FISH, "Tropical Fish", 20),
		PUFFERFISH(FISH, CustomMaterial.FISHING_LOOT_PUFFERFISH, "Pufferfish", 10),
		BULLHEAD(FISH, CustomMaterial.FISHING_LOOT_BULLHEAD, "Bullhead", 10),
		STURGEON(FISH, CustomMaterial.FISHING_LOOT_STURGEON, "Sturgeon", 10),
		WOODSKIP(FISH, CustomMaterial.FISHING_LOOT_WOODSKIP, "Woodskip", 10),
		VOID_SALMON(FISH, CustomMaterial.FISHING_LOOT_VOID_SALMON, "Void Salmon", 10),
		RED_SNAPPER(FISH, CustomMaterial.FISHING_LOOT_RED_SNAPPER, "Red Snapper", 10),
		RED_MULLET(FISH, CustomMaterial.FISHING_LOOT_RED_MULLET, "Red Mullet", 10),
		TIGER_TROUT(FISH, CustomMaterial.FISHING_LOOT_TIGER_TROUT, "Tiger Trout", 10),
		SEA_CUCUMBER(FISH, CustomMaterial.FISHING_LOOT_SEA_CUCUMBER, "Sea Cucumber", 10),
		GLACIERFISH(FISH, CustomMaterial.FISHING_LOOT_GLACIERFISH, "Glacierfish", 10),
		CRIMSONFISH(FISH, CustomMaterial.FISHING_LOOT_CRIMSONFISH, "Crimsonfish", 10),
		BLOBFISH(FISH, CustomMaterial.FISHING_LOOT_BLOBFISH, "Blobfish", 10),
		MIDNIGHT_CARP(FISH, CustomMaterial.FISHING_LOOT_MIDNIGHT_CARP, "Midnight Carp", 40, NIGHT),
		SUNFISH(FISH, CustomMaterial.FISHING_LOOT_SUNFISH, "Sunfish", 40, DAY),
		STONEFISH(FISH, CustomMaterial.FISHING_LOOT_STONEFISH, "Stonefish", 40, 120),
		// Junk
		OLD_BOOTS(JUNK, CustomMaterial.FISHING_LOOT_OLD_BOOTS, "Old Boots", 10),
		RUSTY_SPOON(JUNK, CustomMaterial.FISHING_LOOT_RUSTY_SPOON, "Rusty Spoon", 10),
		BROKEN_CD(JUNK, CustomMaterial.FISHING_LOOT_BROKEN_CD, "Broken CD", 10),
		LOST_BOOK(JUNK, CustomMaterial.FISHING_LOOT_LOST_BOOK, "Lost Book", 10),
		SOGGY_NEWSPAPER(JUNK, CustomMaterial.FISHING_LOOT_SOGGY_NEWSPAPER, "Soggy Newspaper", 10),
		DRIFTWOOD(JUNK, CustomMaterial.FISHING_LOOT_DRIFTWOOD, "Driftwood", 10),
		SEAWEED(JUNK, CustomMaterial.FISHING_LOOT_SEAWEED, "Seaweed", 10),
		// Treasure
		GOLD_NUGGET(TREASURE, Material.GOLD_NUGGET, 15),
		UNBREAKING(TREASURE, Material.ENCHANTED_BOOK, "Unbreaking", 10),
		EFFICIENCY(TREASURE, Material.ENCHANTED_BOOK, "Efficiency", 10),
		FORTUNE(TREASURE, Material.ENCHANTED_BOOK, "Fortune", 8),
		LURE(TREASURE, Material.ENCHANTED_BOOK, "Lure", 8),
		DIAMOND(TREASURE, Material.DIAMOND, 6),
		NAUTILUS_SHELL(TREASURE, Material.NAUTILUS_SHELL, 6),
		TREASURE_CHEST(TREASURE, CustomMaterial.FISHING_LOOT_TREASURE_CHEST, "Treasure Chest", 5),
		;

		private final EventFishingLootCategory category;
		private final Material material;
		private final int modelId;
		private final double weight;
		private final String customName;
		private final EventFishingLootTime time;
		private final Integer maxY;

		EventDefaultFishingLoot(EventFishingLootCategory category, Material material, double weight) {
			this(category, material, 0, weight, null, null, null);
		}

		EventDefaultFishingLoot(EventFishingLootCategory category, Material material, String customName, double weight) {
			this(category, material, 0, weight, customName, null, null);
		}

		EventDefaultFishingLoot(EventFishingLootCategory category, CustomMaterial material, String customName, double weight) {
			this(category, material.getMaterial(), material.getModelId(), weight, customName, BOTH, null);
		}

		EventDefaultFishingLoot(EventFishingLootCategory category, CustomMaterial material, String customName, double weight, EventFishingLootTime time) {
			this(category, material.getMaterial(), material.getModelId(), weight, customName, time, null);
		}

		EventDefaultFishingLoot(EventFishingLootCategory category, CustomMaterial material, String customName, double weight, Integer maxY) {
			this(category, material.getMaterial(), material.getModelId(), weight, customName, BOTH, maxY);
		}

		public FishingLoot build() {
			return new FishingLoot(category, material, modelId, weight, customName, time, maxY, null);
		}

	}

	@Data
	@AllArgsConstructor
	@Accessors(chain = true)
	public static class FishingLoot {
		private EventFishingLootCategory category;
		private Material material;
		private int modelId;
		private double weight;
		private String customName;
		private EventFishingLootTime time;
		private Integer maxY;
		private Predicate<Player> predicate;

		FishingLoot(EventFishingLootCategory category, Material material, double weight) {
			this(category, material, 0, weight, null, null, null, null);
		}

		FishingLoot(EventFishingLootCategory category, Material material, String customName, double weight) {
			this(category, material, 0, weight, customName, null, null, null);
		}

		FishingLoot(EventFishingLootCategory category, CustomMaterial material, String customName, double weight) {
			this(category, material.getMaterial(), material.getModelId(), weight, customName, BOTH, null, null);
		}

		FishingLoot(EventFishingLootCategory category, CustomMaterial material, String customName, double weight, EventFishingLootTime time) {
			this(category, material.getMaterial(), material.getModelId(), weight, customName, time, null, null);
		}

		FishingLoot(EventFishingLootCategory category, CustomMaterial material, String customName, double weight, Integer maxY) {
			this(category, material.getMaterial(), material.getModelId(), weight, customName, BOTH, maxY, null);
		}

//		public double getChance() {
//			double sum = 0;
//			FishingLootCategory category = this.getCategory();
//			for (FishingLoot loot : values())
//				if (category.equals(loot.getCategory()))
//					sum += loot.getWeight();
//
//			return (weight / sum) * 100;
//		}

		public boolean applies(Player player) {
			return this.timeApplies(player) && this.yValueApplies(player) && (predicate == null || predicate.test(player));
		}

		private boolean timeApplies(Player player) {
			World world = player.getWorld();
			EventFishingLootTime time = this.getTime();
			if (time == BOTH)
				return true;

			boolean isDay = world.isDayTime();
			if (isDay && time == DAY)
				return true;
			else
				return !isDay && time == NIGHT;
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

			if (this.getCustomName() != null && !material.equals(Material.ENCHANTED_BOOK))
				result.name(this.getCustomName());

			if (this.getModelId() != 0)
				result.modelId(this.getModelId());

			if (material.equals(Material.ENCHANTED_BOOK)) {
				if (customName.equals("Unbreaking"))
					result.enchant(Enchant.UNBREAKING, 1);

				if (customName.equals("Efficiency")) {
					if (RandomUtils.chanceOf(20))
						result.enchant(Enchant.EFFICIENCY, 3);
					else
						result.enchant(Enchant.EFFICIENCY, 2);
				}

				if (customName.equals("Fortune"))
					result.enchant(Enchant.FORTUNE, 1);

				if (customName.equals("Lure"))
					result.enchant(Enchant.LURE, 1);
			}

			return result.lore(getLore());
		}

		public String getLore() {
			return switch (category) {
				case FISH -> "&7Fish";
				case TREASURE -> null;
				case JUNK -> "&7Trash";
			};
		}
	}

	@Getter
	@AllArgsConstructor
	public enum EventFishingLootCategory {
		FISH(50),
		JUNK(25),
		TREASURE(8);

		private final double weight;

		public double getChance() {
			double sum = 0;
			for (EventFishingLootCategory category : values())
				sum += category.getWeight();

			return (weight / sum) * 100;
		}
	}

	public enum EventFishingLootTime {
		DAY,
		NIGHT,
		BOTH
	}


}

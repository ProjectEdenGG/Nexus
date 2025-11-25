package gg.projecteden.nexus.features.events.models;

import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class EventFishingLoot {

	@Getter
	@AllArgsConstructor
	public enum EventDefaultFishingLoot {
		// Fish
		CARP(EventFishingLootCategory.FISH, ItemModelType.FISHING_LOOT_CARP, "Carp", 40),
		SALMON(EventFishingLootCategory.FISH, ItemModelType.FISHING_LOOT_SALMON, "Salmon", 30),
		TROPICAL_FISH(EventFishingLootCategory.FISH, ItemModelType.FISHING_LOOT_TROPICAL_FISH, "Tropical Fish", 20),
		PUFFERFISH(EventFishingLootCategory.FISH, ItemModelType.FISHING_LOOT_PUFFERFISH, "Pufferfish", 10),
		BULLHEAD(EventFishingLootCategory.FISH, ItemModelType.FISHING_LOOT_BULLHEAD, "Bullhead", 10),
		STURGEON(EventFishingLootCategory.FISH, ItemModelType.FISHING_LOOT_STURGEON, "Sturgeon", 10),
		WOODSKIP(EventFishingLootCategory.FISH, ItemModelType.FISHING_LOOT_WOODSKIP, "Woodskip", 10),
		VOID_SALMON(EventFishingLootCategory.FISH, ItemModelType.FISHING_LOOT_VOID_SALMON, "Void Salmon", 10),
		RED_SNAPPER(EventFishingLootCategory.FISH, ItemModelType.FISHING_LOOT_RED_SNAPPER, "Red Snapper", 10),
		RED_MULLET(EventFishingLootCategory.FISH, ItemModelType.FISHING_LOOT_RED_MULLET, "Red Mullet", 10),
		TIGER_TROUT(EventFishingLootCategory.FISH, ItemModelType.FISHING_LOOT_TIGER_TROUT, "Tiger Trout", 10),
		SEA_CUCUMBER(EventFishingLootCategory.FISH, ItemModelType.FISHING_LOOT_SEA_CUCUMBER, "Sea Cucumber", 10),
		GLACIERFISH(EventFishingLootCategory.FISH, ItemModelType.FISHING_LOOT_GLACIERFISH, "Glacierfish", 10),
		CRIMSONFISH(EventFishingLootCategory.FISH, ItemModelType.FISHING_LOOT_CRIMSONFISH, "Crimsonfish", 10),
		BLOBFISH(EventFishingLootCategory.FISH, ItemModelType.FISHING_LOOT_BLOBFISH, "Blobfish", 10),
		MIDNIGHT_CARP(EventFishingLootCategory.FISH, ItemModelType.FISHING_LOOT_MIDNIGHT_CARP, "Midnight Carp", 40, EventFishingLootTime.NIGHT),
		SUNFISH(EventFishingLootCategory.FISH, ItemModelType.FISHING_LOOT_SUNFISH, "Sunfish", 40, EventFishingLootTime.DAY),
		STONEFISH(EventFishingLootCategory.FISH, ItemModelType.FISHING_LOOT_STONEFISH, "Stonefish", 40, 120),
		// Junk
		OLD_BOOTS(EventFishingLootCategory.JUNK, ItemModelType.FISHING_LOOT_OLD_BOOTS, "Old Boots", 10),
		RUSTY_SPOON(EventFishingLootCategory.JUNK, ItemModelType.FISHING_LOOT_RUSTY_SPOON, "Rusty Spoon", 10),
		BROKEN_CD(EventFishingLootCategory.JUNK, ItemModelType.FISHING_LOOT_BROKEN_CD, "Broken CD", 10),
		LOST_BOOK(EventFishingLootCategory.JUNK, ItemModelType.FISHING_LOOT_LOST_BOOK, "Lost Book", 10),
		SOGGY_NEWSPAPER(EventFishingLootCategory.JUNK, ItemModelType.FISHING_LOOT_SOGGY_NEWSPAPER, "Soggy Newspaper", 10),
		DRIFTWOOD(EventFishingLootCategory.JUNK, ItemModelType.FISHING_LOOT_DRIFTWOOD, "Driftwood", 10),
		SEAWEED(EventFishingLootCategory.JUNK, ItemModelType.FISHING_LOOT_SEAWEED, "Seaweed", 10),
		// Treasure
		GOLD_NUGGET(EventFishingLootCategory.TREASURE, Material.GOLD_NUGGET, 15),
		UNBREAKING(EventFishingLootCategory.TREASURE, Material.ENCHANTED_BOOK, "Unbreaking", 10),
		EFFICIENCY(EventFishingLootCategory.TREASURE, Material.ENCHANTED_BOOK, "Efficiency", 10),
		FORTUNE(EventFishingLootCategory.TREASURE, Material.ENCHANTED_BOOK, "Fortune", 8),
		LURE(EventFishingLootCategory.TREASURE, Material.ENCHANTED_BOOK, "Lure", 8),
		DIAMOND(EventFishingLootCategory.TREASURE, Material.DIAMOND, 6),
		NAUTILUS_SHELL(EventFishingLootCategory.TREASURE, Material.NAUTILUS_SHELL, 6),
		TREASURE_CHEST(EventFishingLootCategory.TREASURE, ItemModelType.FISHING_LOOT_TREASURE_CHEST, "Treasure Chest", 5),
		// Pugmas25
		;

		private final EventFishingLootCategory category;
		private final Material material;
		private final String modelId;
		private final double weight;
		private final String customName;
		private final EventFishingLootTime time;
		private final Integer maxY;

		EventDefaultFishingLoot(EventFishingLootCategory category, Material material, double weight) {
			this(category, material, null, weight, null, null, null);
		}

		EventDefaultFishingLoot(EventFishingLootCategory category, Material material, String customName, double weight) {
			this(category, material, null, weight, customName, null, null);
		}

		EventDefaultFishingLoot(EventFishingLootCategory category, ItemModelType itemModelType, String customName, double weight) {
			this(category, itemModelType.getMaterial(), itemModelType.getModel(), weight, customName, EventFishingLootTime.BOTH, null);
		}

		EventDefaultFishingLoot(EventFishingLootCategory category, ItemModelType itemModelType, String customName, double weight, EventFishingLootTime time) {
			this(category, itemModelType.getMaterial(), itemModelType.getModel(), weight, customName, time, null);
		}

		EventDefaultFishingLoot(EventFishingLootCategory category, ItemModelType itemModelType, String customName, double weight, Integer maxY) {
			this(category, itemModelType.getMaterial(), itemModelType.getModel(), weight, customName, EventFishingLootTime.BOTH, maxY);
		}

		public FishingLoot build() {
			return new FishingLoot(name(), category, material, modelId, weight, customName, null, time, maxY, null);
		}

	}

	@Data
	@AllArgsConstructor
	@Accessors(chain = true)
	public static class FishingLoot {
		private String id;
		private EventFishingLootCategory category;
		private Material material;
		private String modelId;
		private double weight;
		private String customName;
		private List<String> customLore;
		private EventFishingLootTime time;
		private Integer maxY;
		private Predicate<Player> predicate;

		public FishingLoot(String name, EventFishingLootCategory category, Material material, String modelId, int weight,
						   String customName, String customLore, EventFishingLootTime time, Integer maxY, Predicate<Player> predicate) {
			this.id = name;
			this.category = category;
			this.material = material;
			this.modelId = modelId;
			this.weight = weight;
			this.customName = customName;
			this.customLore = Collections.singletonList(customLore);
			this.time = time;
			this.maxY = maxY;
			this.predicate = predicate;
		}


		public boolean applies(Player player) {
			return this.timeApplies(player) && this.yValueApplies(player) && (predicate == null || predicate.test(player));
		}

		private boolean timeApplies(Player player) {
			World world = player.getWorld();
			EventFishingLootTime time = this.getTime();
			if (time == EventFishingLootTime.BOTH)
				return true;

			boolean isDay = world.isDayTime();
			if (isDay && time == EventFishingLootTime.DAY)
				return true;
			else
				return !isDay && time == EventFishingLootTime.NIGHT;
		}

		private boolean yValueApplies(Player player) {
			Location location = player.getLocation();
			if (this.getMaxY() == null)
				return true;

			return location.getBlockY() <= this.getMaxY();
		}

		public ItemStack getItem() {
			return getItemBuilder().itemFlags(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES).amount(1).build();
		}

		public ItemBuilder getItemBuilder() {
			Material material = this.getMaterial();
			ItemBuilder result = new ItemBuilder(material);

			if (this.getCustomName() != null && !material.equals(Material.ENCHANTED_BOOK))
				result.name(this.getCustomName());

			if (this.getModelId() != null)
				result.model(this.getModelId());

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

			if (this.getCustomLore() != null)
				result.lore(this.getCustomLore());
			else
				result.lore(getLore());

			return result;
		}

		public String getLore() {
			return switch (category) {
				case FISH -> "&7Fish";
				case JUNK -> "&7Trash";
				default -> null;
			};
		}
	}

	@Getter
	@AllArgsConstructor
	public enum EventFishingLootCategory {
		FISH(50.0),
		JUNK(25.0),
		TREASURE(8.0),
		SPECIAL(5.0),
		;

		private final Double weight;

		public double getChance() {
			double sum = 0;
			for (EventFishingLootCategory category : values()) {
				Double weight = category.getWeight();
				if (weight != null)
					sum += weight;
			}

			return (weight / sum) * 100;
		}
	}

	public enum EventFishingLootTime {
		DAY,
		NIGHT,
		BOTH
	}


}

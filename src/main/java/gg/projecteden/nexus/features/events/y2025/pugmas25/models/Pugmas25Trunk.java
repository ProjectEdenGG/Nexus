package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestItem;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum Pugmas25Trunk {
	DIAMOND(ItemModelType.EVENT_TRUNK_DIAMOND, new HashMap<>() {{
		put(new ItemBuilder(Material.POTION).potionEffect(PotionEffectType.NIGHT_VISION, (int) TickTime.SECOND.x(60 * 8)).name("Potion of Night Vision").build(), 15d);
		put(new ItemBuilder(Material.POTION).potionEffect(PotionEffectType.SPEED, (int) TickTime.SECOND.x(60 * 8)).name("Potion of Swiftness").build(), 15d);
		put(new ItemBuilder(Material.POTION).potionEffect(PotionEffectType.REGENERATION, (int) TickTime.SECOND.x(60 * 8)).name("Potion of Regeneration").build(), 15d);
		put(new ItemBuilder(Material.POTION).potionEffect(PotionEffectType.STRENGTH, (int) TickTime.SECOND.x(60 * 8)).name("Potion of Strength").build(), 15d);
		put(new ItemBuilder(Material.POTION).potionEffect(PotionEffectType.FIRE_RESISTANCE, (int) TickTime.SECOND.x(60 * 8)).name("Potion of Fire Resistance").build(), 15d);
		put(new ItemBuilder(Material.POTION).potionEffect(PotionEffectType.RESISTANCE, (int) TickTime.SECOND.x(60 * 8)).name("Potion of Resistance").build(), 15d);
		put(new ItemBuilder(Material.POTION).potionEffect(PotionEffectType.HEALTH_BOOST, (int) TickTime.SECOND.x(60 * 8)).name("Potion of Healing").build(), 15d);
		put(new ItemBuilder(Material.POTION).potionEffect(PotionEffectType.SATURATION, (int) TickTime.SECOND.x(60 * 8)).name("Potion of Saturation").build(), 15d);
		put(new ItemBuilder(Material.POTION).potionEffect(PotionEffectType.ABSORPTION, (int) TickTime.SECOND.x(60 * 8)).name("Potion of Absorption").build(), 15d);
		put(new ItemBuilder(Material.DIAMOND).build(), 10d);
		put(new ItemBuilder(Material.EMERALD).build(), 8d);
		put(new ItemBuilder(Material.NETHERITE_SCRAP).build(), 5d);
		put(new ItemBuilder(Material.NETHERITE_INGOT).build(), 2d);
		put(Pugmas25QuestItem.LUCKY_HORSESHOE.get(), 0.5d);
	}}),
	GOLD(ItemModelType.EVENT_TRUNK_GOLDEN, new HashMap<>() {{
		put(new ItemBuilder(Material.POTION).potionEffect(PotionEffectType.NIGHT_VISION, (int) TickTime.SECOND.x(60 * 5)).name("Potion of Night Vision").build(), 10d);
		put(new ItemBuilder(Material.POTION).potionEffect(PotionEffectType.SPEED, (int) TickTime.SECOND.x(60 * 5)).name("Potion of Swiftness").build(), 10d);
		put(new ItemBuilder(Material.POTION).potionEffect(PotionEffectType.REGENERATION, (int) TickTime.SECOND.x(60 * 5)).name("Potion of Regeneration").build(), 10d);
		put(new ItemBuilder(Material.POTION).potionEffect(PotionEffectType.STRENGTH, (int) TickTime.SECOND.x(60 * 5)).name("Potion of Strength").build(), 10d);
		put(new ItemBuilder(Material.POTION).potionEffect(PotionEffectType.FIRE_RESISTANCE, (int) TickTime.SECOND.x(60 * 5)).name("Potion of Fire Resistance").build(), 10d);
		put(new ItemBuilder(Material.POTION).potionEffect(PotionEffectType.RESISTANCE, (int) TickTime.SECOND.x(60 * 5)).name("Potion of Resistance").build(), 10d);
		put(new ItemBuilder(Material.POTION).potionEffect(PotionEffectType.HEALTH_BOOST, (int) TickTime.SECOND.x(60 * 5)).name("Potion of Healing").build(), 10d);
		put(new ItemBuilder(Material.LAPIS_LAZULI).build(), 8d);
		put(new ItemBuilder(Material.IRON_INGOT).build(), 5d);
		put(new ItemBuilder(Material.DIAMOND).build(), 5d);
		put(new ItemBuilder(Material.NETHERITE_SCRAP).build(), 1d);
	}}),
	IRON(ItemModelType.EVENT_TRUNK_IRON, new HashMap<>() {{
		put(new ItemBuilder(Material.COAL).build(), 25d);
		put(new ItemBuilder(Material.RAW_COPPER).build(), 20d);
		put(new ItemBuilder(Material.RAW_GOLD).build(), 18d);
		put(new ItemBuilder(Material.RAW_IRON).build(), 15d);
		put(new ItemBuilder(Material.GOLD_NUGGET).build(), 12d);
		put(new ItemBuilder(Material.GOLD_INGOT).build(), 10d);
		put(Pugmas25QuestItem.SUSPICIOUS_DEBRIS.get(), 10d);
		put(new ItemBuilder(Material.COPPER_INGOT).build(), 8d);
		put(new ItemBuilder(Material.IRON_NUGGET).build(), 5d);
		put(new ItemBuilder(Material.IRON_INGOT).build(), 3d);
		put(new ItemBuilder(Material.DIAMOND).build(), 1d);
	}})
	;

	private final ItemModelType model;
	private final Map<ItemStack, Double> items;

	public static Pugmas25Trunk of(ItemModelType model) {
		for (Pugmas25Trunk trunk : values())
			if (trunk.getModel().equals(model))
				return trunk;
		return null;
	}

	public List<ItemStack> getRandomItems(Player player) {
		int rolls = Pugmas25.getLuckyHorseshoeAmount(player, 3, 7);

		List<ItemStack> result = new ArrayList<>();
		for (int i = 0; i < rolls; i++) {
			ItemStack item = RandomUtils.getWeightedRandom(items);
			int amount = 1;
			if (item.getType() != Material.POTION)
				amount = RandomUtils.randomInt(1, 5);

			item.setAmount(amount);
			result.add(item);
		}

		return result;
	}
}

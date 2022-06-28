package gg.projecteden.nexus.features.listeners;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import kotlin.Pair;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

import static gg.projecteden.api.common.utils.StringUtils.camelCase;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class Food implements Listener {

	private static final Map<CustomMaterial, Pair<Integer, Double>> FOOD = Map.of(
		CustomMaterial.FOOD_BEETROOT_SOUP, new Pair<>(6, 7.2d),
		CustomMaterial.FOOD_MUSHROOM_STEW, new Pair<>(6, 7.2d),
		CustomMaterial.FOOD_RABBIT_STEW, new Pair<>(10, 12d)
	);

	@EventHandler
	public void onEat(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();

		switch (event.getItem().getType()) {
			case GLOW_BERRIES -> player.addPotionEffect(new PotionEffectBuilder(PotionEffectType.GLOWING).duration(TickTime.MINUTE.x(1.5)).build());
			case COOKIE -> {
				final CustomMaterial customMaterial = CustomMaterial.of(event.getItem());
				if (customMaterial == null)
					break;

				if (!FOOD.containsKey(customMaterial))
					break;

				PlayerUtils.giveItem(player, Material.BOWL);
				player.setFoodLevel(Math.min(20, player.getFoodLevel() + FOOD.get(customMaterial).getFirst() - 2));
				player.setSaturation((float) Math.min(player.getFoodLevel(), player.getSaturation() + FOOD.get(customMaterial).getSecond() - 0.4));
			}
		}
	}

	@EventHandler
	public void on(PrepareItemCraftEvent event) {
		if (isNullOrAir(event.getInventory().getResult()))
			return;

		for (CustomMaterial customMaterial : FOOD.keySet()) {
			final Material material = Material.getMaterial(customMaterial.name().replace("FOOD_", ""));
			if (material == null)
				continue;

			if (event.getInventory().getResult().getType() != material)
				continue;

			event.getInventory().setResult(new ItemBuilder(customMaterial)
				.name(camelCase(material.getKey().getKey()))
				.amount(event.getInventory().getResult().getAmount())
				.build());
		}
	}

}

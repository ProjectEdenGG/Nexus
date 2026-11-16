package gg.projecteden.nexus.features.mcmmo.mastery.skills.herbalism;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.FoodProperties;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;

public class HealthierFood implements Listener {

	public static final String PERMISSION = "nexus.mcmmo.herbalism.healthier_food";

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (!(event.getEntity() instanceof Player player) || !player.hasPermission(PERMISSION)) return;

		ItemStack item = event.getItem();
		if (item == null) return;

		FoodProperties food = item.getData(DataComponentTypes.FOOD);
		if (food == null) return;

		int currentFood = player.getFoodLevel();
		int foodGain = event.getFoodLevel() - currentFood;
		if (foodGain < 0) return;

		int boostedFood = Math.min(20, currentFood + Math.round(foodGain * 1.5f));
		event.setFoodLevel(boostedFood);

		float bonusSaturation = food.saturation() * 0.5f;
		player.setSaturation(Math.min(boostedFood, player.getSaturation() + bonusSaturation));
	}

}

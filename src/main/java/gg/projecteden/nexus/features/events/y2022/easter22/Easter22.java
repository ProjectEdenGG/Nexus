package gg.projecteden.nexus.features.events.y2022.easter22;

import gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22QuestItem;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shapeless;

@NoArgsConstructor
public class Easter22 extends Feature implements Listener {
	public static final int TOTAL_EASTER_EGGS = 20;

	public static boolean isAtEasterIsland(Player player) {
		return new WorldGuardUtils(player).isInRegion(player, "event");
	}
	
	@EventHandler
	public void on(ResourcePackUpdateCompleteEvent event) {
		shapeless()
			.add(Easter22QuestItem.PAINTBRUSH.get())
			.add(Easter22QuestItem.PRISTINE_EGG.get())
			.toMake(Easter22QuestItem.PAINTED_EGG.get())
			.build()
			.register();
	}

	

}

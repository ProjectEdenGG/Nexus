package gg.projecteden.nexus.features.events.y2022.easter22;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.NullEventEffects;
import gg.projecteden.nexus.features.events.y2022.easter22.Easter22Command.Easter22StoreProvider;
import gg.projecteden.nexus.features.events.y2022.easter22.quests.*;
import gg.projecteden.nexus.features.quests.QuestConfig;
import gg.projecteden.nexus.features.quests.interactable.instructions.Dialog;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import gg.projecteden.nexus.framework.annotations.Date;
import gg.projecteden.nexus.framework.features.Features;
import gg.projecteden.nexus.models.quests.Quester;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.utils.Nullables;
import lombok.NoArgsConstructor;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;

@QuestConfig(
	quests = Easter22Quest.class,
	tasks = Easter22QuestTask.class,
	npcs = Easter22NPC.class,
	entities = Easter22Entity.class,
	items = Easter22QuestItem.class,
	rewards = Easter22QuestReward.class,
	effects = NullEventEffects.class,
	start = @Date(m = 4, d = 10, y = 2022),
	end = @Date(m = 4, d = 25, y = 2022),
	world = "server",
	region = "event",
	warpType = WarpType.EASTER22
)
@NoArgsConstructor
@Disabled
public class Easter22 extends EdenEvent {
	public static final int TOTAL_EASTER_EGGS = 20;

	public static Easter22 get() {
		return Features.get(Easter22.class);
	}

	@EventHandler
	public void on(ResourcePackUpdateCompleteEvent event) {
		RecipeBuilder.shapeless()
			.add(Easter22QuestItem.PAINTBRUSH.get())
			.add(Easter22QuestItem.PRISTINE_EGG.get())
			.toMake(Easter22QuestItem.PAINTED_EGG.get())
			.build()
			.register();
	}

//	@EventHandler
//	public void on(PlayerEnteredRegionEvent event) {
//		final Player player = event.getPlayer();
//		if (!shouldHandle(player))
//			return;
//
//		final Quest quest = Easter22User.of(player).getQuest();
//		if (quest == null)
//			Quest.builder()
//				.tasks(Easter22QuestTask.MAIN)
//				.assign(player)
//				.start();
//	}

	@EventHandler(priority = EventPriority.LOW)
	public void on(NPCRightClickEvent event) {
		final Player player = event.getClicker();
		if (!shouldHandle(player))
			return;

		final Easter22NPC npc = interactableOf(event.getNPC());
		if (npc != Easter22NPC.BASIL)
			return;

		event.setCancelled(true);
		new Easter22StoreProvider().open(event.getClicker());
		Dialog.genericGreeting(Quester.of(player), npc);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void on(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		if (!shouldHandle(player))
			return;

		if (!Nullables.isNullOrAir(event.getItem()) && event.getItem().getType() == Material.EGG)
			event.setCancelled(true);
	}

}

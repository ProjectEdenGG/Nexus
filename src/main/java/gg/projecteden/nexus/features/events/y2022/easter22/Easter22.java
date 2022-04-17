package gg.projecteden.nexus.features.events.y2022.easter22;

import gg.projecteden.nexus.features.events.y2022.easter22.Easter22Command.Easter22StoreProvider;
import gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22Entity;
import gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22NPC;
import gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22QuestItem;
import gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22QuestTask;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.easter22.Easter22User;
import gg.projecteden.nexus.models.quests.Quest;
import gg.projecteden.nexus.models.quests.QuesterService;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.NoArgsConstructor;
import me.lexikiq.HasLocation;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22NPC.BASIL;
import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shapeless;

@NoArgsConstructor
public class Easter22 extends Feature implements Listener {
	public static final int TOTAL_EASTER_EGGS = 20;
	public static final LocalDateTime START = LocalDate.of(2022, 4, 10).atStartOfDay();
	public static final LocalDateTime END = LocalDate.of(2022, 4, 25).atStartOfDay();

	public static boolean isAtEasterIsland(HasLocation location) {
		return new WorldGuardUtils(location.getLocation()).isInRegion(location.getLocation(), "event");
	}

	public static boolean isActive() {
		final LocalDateTime now = LocalDateTime.now();
		return now.isAfter(START) && now.isBefore(END);
	}

	private boolean isEasterEvent(Player player) {
		return isActive() && Easter22.isAtEasterIsland(player);
	}

	@EventHandler
	public void on(PlayerEnteredRegionEvent event) {
		final Player player = event.getPlayer();
		if (!isEasterEvent(player))
			return;

		final Quest quest = Easter22User.of(player).getQuest();
		if (quest == null)
			Quest.builder()
				.tasks(Easter22QuestTask.MAIN)
				.assign(player)
				.start();
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

	@EventHandler
	public void on(NPCRightClickEvent event) {
		final Player player = event.getClicker();
		if (!isEasterEvent(player))
			return;

		final Easter22NPC npc = Easter22NPC.of(event.getNPC());
		if (npc == null)
			return;

		event.setCancelled(true);

		if (npc == BASIL)
			new Easter22StoreProvider().open(event.getClicker());
		else
			new QuesterService().edit(event.getClicker(), quester -> quester.interact(npc, event));
	}

	@EventHandler
	public void on(PlayerInteractAtEntityEvent event) {
		final Player player = event.getPlayer();
		if (!isEasterEvent(player))
			return;

		final Easter22Entity entity = Easter22Entity.of(event.getRightClicked());
		if (entity == null)
			return;

		event.setCancelled(true);
		new QuesterService().edit(event.getPlayer(), quester -> quester.interact(entity, event));
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		if (!isEasterEvent(player))
			return;

		if (!Nullables.isNullOrAir(event.getItem()) && event.getItem().getType() == Material.EGG)
			event.setCancelled(true);

		new QuesterService().edit(player, quester -> quester.interact(event));
	}


}

package gg.projecteden.nexus.features.events.y2022.easter22;

import gg.projecteden.nexus.features.events.y2022.easter22.Easter22Command.Easter22StoreProvider;
import gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22Entity;
import gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22NPC;
import gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22QuestItem;
import gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22QuestTask;
import gg.projecteden.nexus.features.quests.interactable.instructions.Dialog;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.easter22.Easter22User;
import gg.projecteden.nexus.models.quests.Quest;
import gg.projecteden.nexus.models.quests.QuesterService;
import gg.projecteden.nexus.models.scheduledjobs.jobs.BlockRegenJob;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.NoArgsConstructor;
import me.lexikiq.HasLocation;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22NPC.BASIL;
import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shapeless;
import static gg.projecteden.nexus.utils.PlayerUtils.giveItem;
import static gg.projecteden.nexus.utils.PlayerUtils.playerHas;
import static gg.projecteden.nexus.utils.RandomUtils.randomInt;
import static gg.projecteden.utils.RandomUtils.chanceOf;

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

	@EventHandler
	public void on(PlayerEnteredRegionEvent event) {
		final Player player = event.getPlayer();
		if (!isAtEasterIsland(player))
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
		if (!isActive())
			return;

		final Easter22NPC npc = Easter22NPC.of(event.getNPC());
		if (npc == null)
			return;

		event.setCancelled(true);

		if (npc == BASIL)
			new Easter22StoreProvider().open(event.getClicker());
		else
			new QuesterService().edit(event.getClicker(), quester -> {
				if (!quester.interact(npc, event))
					if (npc.isAlive())
						Dialog.genericGreeting(quester, npc);
			});
	}

	@EventHandler
	public void on(PlayerInteractAtEntityEvent event) {
		if (!isActive())
			return;

		final Easter22Entity entity = Easter22Entity.of(event.getRightClicked());
		if (entity == null)
			return;

		event.setCancelled(true);
		new QuesterService().edit(event.getPlayer(), quester -> {
			if (!quester.interact(entity, event))
				if (entity.isAlive())
					Dialog.genericGreeting(quester, entity);
		});
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		if (!Easter22.isAtEasterIsland(player))
			return;

		if (!Nullables.isNullOrAir(event.getItem()) && event.getItem().getType() == Material.EGG)
			event.setCancelled(true);

		final Block block = event.getClickedBlock();
		if (Nullables.isNullOrAir(block))
			return;

		switch (event.getAction()) {
			case LEFT_CLICK_BLOCK:
				switch (block.getType()) {
					case OXEYE_DAISY, CORNFLOWER -> {
						new BlockRegenJob(block.getLocation(), block.getType()).schedule(randomInt(3 * 60, 6 * 60));
						block.breakNaturally();
					}
				}
				break;
			case RIGHT_CLICK_BLOCK:
				switch (block.getType()) {
					case OAK_LEAVES, JUNGLE_LEAVES -> {
						final Quest quest = Easter22User.of(player).getQuest();
						final int step = quest.getTaskProgress().getStep();
						if (quest.getTask() == 0 && (step == 2 || step == 3))
							if (!playerHas(player, Material.STICK)) {
								if (!new CooldownService().check(player, "easter22-stick-" + StringUtils.getFlooredCoordinateString(block.getLocation()).replace(" ", "-"), TickTime.MINUTE))
									return;

								if (chanceOf(20)) {
									giveItem(player, Material.STICK);
									new SoundBuilder(Sound.ITEM_BONE_MEAL_USE).receiver(player).play();
								} else {
									PlayerUtils.send(player, "&7Hmm... no sticks in this bush");
									new SoundBuilder(Sound.ENTITY_VILLAGER_NO).receiver(player).volume(.15).play();
								}
							}
					}
				}
				break;
		}
	}
	

}

package gg.projecteden.nexus.features.safecracker.menus;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.anvilgui.AnvilGUI;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.safecracker.NPCHandler;
import gg.projecteden.nexus.models.safecracker.SafeCrackerEvent;
import gg.projecteden.nexus.models.safecracker.SafeCrackerEventService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Material;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Disabled
@Title("SafeCracker Admin")
public class SafeCrackerAdminProvider extends InventoryProvider {
	private final SafeCrackerEventService service = new SafeCrackerEventService();
	private final SafeCrackerEvent.SafeCrackerGame game = service.getActiveEvent();

	@Override
	public void init() {
		if (game == null) {
			MenuUtils.openAnvilMenu(viewer, "New Game...", (player1, response) -> {
				service.get0().getGames().put(response, new SafeCrackerEvent.SafeCrackerGame(response, true, LocalDateTime.now(), "", "", new HashMap<>()));
				service.save(service.get0());
				new SafeCrackerAdminProvider().open(viewer);

				return AnvilGUI.Response.text(response);
			}, (player1) -> viewer.closeInventory());
		}

		addCloseItem();

		contents.set(0, 8, ClickableItem.of(new ItemBuilder(Material.ENCHANTED_BOOK).name("&e" + game.getName())
				.lore("&7Click to change the").lore("&7active game").build(), e -> {
			new SafeCrackerGameSelector().open(viewer);

		}));

		contents.set(0, 6, ClickableItem.of(new ItemBuilder(Material.POLAR_BEAR_SPAWN_EGG).name("&eSpawn NPCs").build(), e -> {
			for (SafeCrackerEvent.SafeCrackerNPC npc : game.getNpcs().values()) {
				NPC npcEntity = CitizensAPI.getNPCRegistry().getById(npc.getId());
				npcEntity.spawn(npcEntity.getStoredLocation());
			}
		}));

		contents.set(0, 7, ClickableItem.of(new ItemBuilder(Material.LAVA_BUCKET).name("&eDespawn NPCs").build(), e -> {
			for (SafeCrackerEvent.SafeCrackerNPC npc : game.getNpcs().values()) {
				NPC npcEntity = CitizensAPI.getNPCRegistry().getById(npc.getId());
				npcEntity.despawn(DespawnReason.PLUGIN);
			}
		}));

		contents.set(0, 4, ClickableItem.of(new ItemBuilder(Material.EMERALD_BLOCK).name("&aNew NPC").build(), e -> {
			MenuUtils.openAnvilMenu(viewer, "New NPC...", (player1, response) -> {
				int id = NPCHandler.createNPC(response, viewer.getLocation());
				SafeCrackerEvent.SafeCrackerNPC npc = new SafeCrackerEvent.SafeCrackerNPC(id, response, "", new ArrayList<>(), "");
				game.getNpcs().put(response, npc);
				service.save(service.get0());
				new SafeCrackerNPCEditProvider(npc).open(viewer);

				return AnvilGUI.Response.text(response);
			}, (player1) -> new SafeCrackerAdminProvider().open(viewer));
		}));

		contents.set(0, 3, ClickableItem.of(new ItemBuilder(Material.PAPER).name("&eRiddle Answer").lore("&3" + game.getAnswer()).build(), e -> {
			MenuUtils.openAnvilMenu(viewer, game.getAnswer(), (player1, response) -> {
				game.setAnswer(response);
				service.save(service.get0());
				new SafeCrackerAdminProvider().open(viewer);

				return AnvilGUI.Response.text(response);
			}, (player1) -> new SafeCrackerAdminProvider().open(viewer));
		}));

		contents.set(0, 2, ClickableItem.of(new ItemBuilder(Material.BOOK).name("&eFinal Riddle").lore("&3" + game.getRiddle()).build(), e -> {
			viewer.closeInventory();
			PlayerUtils.send(viewer, new JsonBuilder("&e&lClick here to set the Final Riddle").suggest("/safecracker riddle "));
		}));

		int row = 1;
		int column = 0;

		Map<String, SafeCrackerEvent.SafeCrackerNPC> npcs = new HashMap<>();
		for (SafeCrackerEvent.SafeCrackerNPC npc : game.getNpcs().values()) {
			try {
				PlayerUtils.getPlayer(npc.getName()).hasPlayedBefore();
				npcs.put(npc.getName(), npc);
			} catch (Exception ignore) {
			}
		}
		game.setNpcs(npcs);
		service.save(service.get0());

		for (SafeCrackerEvent.SafeCrackerNPC npc : game.getNpcs().values()) {
			ItemBuilder builder = new ItemBuilder(Material.PLAYER_HEAD).skullOwner(PlayerUtils.getPlayer(npc.getName()))
					.loreize(true)
					.name("&e" + npc.getName())
					.lore("&3Id: &e" + npc.getId())
					.lore("&3Question: &e" + npc.getQuestion())
					.lore("&3Answers:");
			if (npc.getAnswers() != null) {
				for (String answer : npc.getAnswers()) {
					builder.lore("&e - " + answer);
				}
			}
			builder.lore("&3Hint: &e" + npc.getRiddle()).lore("").lore("&7&oClick me to Edit");
			contents.set(row, column, ClickableItem.of(builder.build(),
				e -> new SafeCrackerNPCEditProvider(npc).open(viewer)));

			if (column == 8) {
				column = 0;
				row++;
			} else
				column++;
		}

	}
}

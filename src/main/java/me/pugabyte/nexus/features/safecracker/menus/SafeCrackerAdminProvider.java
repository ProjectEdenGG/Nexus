package me.pugabyte.nexus.features.safecracker.menus;

import eden.annotations.Disabled;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.safecracker.NPCHandler;
import me.pugabyte.nexus.models.safecracker.SafeCrackerEvent;
import me.pugabyte.nexus.models.safecracker.SafeCrackerEventService;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Disabled
public class SafeCrackerAdminProvider extends MenuUtils implements InventoryProvider {

	SafeCrackerEventService service = new SafeCrackerEventService();
	SafeCrackerEvent.SafeCrackerGame game = service.getActiveEvent();

	@Override
	public void init(Player player, InventoryContents contents) {
		if (game == null) {
			openAnvilMenu(player, "New Game...", (player1, response) -> {
				service.get().getGames().put(response, new SafeCrackerEvent.SafeCrackerGame(response, true, LocalDateTime.now(), "", "", new HashMap<>()));
				service.save(service.get());
				SafeCrackerInventories.openAdminMenu(player);
				return AnvilGUI.Response.text(response);
			}, (player1) -> player.closeInventory());
		}

		addCloseItem(contents);

		contents.set(0, 8, ClickableItem.from(new ItemBuilder(Material.ENCHANTED_BOOK).name("&e" + game.getName())
				.lore("&7Click to change the").lore("&7active game").build(), e -> {
			SafeCrackerInventories.openGameSelectorMenu(player);
		}));

		contents.set(0, 6, ClickableItem.from(new ItemBuilder(Material.POLAR_BEAR_SPAWN_EGG).name("&eSpawn NPCs").build(), e -> {
			for (SafeCrackerEvent.SafeCrackerNPC npc : game.getNpcs().values()) {
				NPC npcEntity = CitizensAPI.getNPCRegistry().getById(npc.getId());
				npcEntity.spawn(npcEntity.getStoredLocation());
			}
		}));

		contents.set(0, 7, ClickableItem.from(new ItemBuilder(Material.LAVA_BUCKET).name("&eDespawn NPCs").build(), e -> {
			for (SafeCrackerEvent.SafeCrackerNPC npc : game.getNpcs().values()) {
				NPC npcEntity = CitizensAPI.getNPCRegistry().getById(npc.getId());
				npcEntity.despawn(DespawnReason.PLUGIN);
			}
		}));

		contents.set(0, 4, ClickableItem.from(new ItemBuilder(Material.EMERALD_BLOCK).name("&aNew NPC").build(), e -> {
			openAnvilMenu(player, "New NPC...", (player1, response) -> {
				int id = NPCHandler.createNPC(response, player.getLocation());
				SafeCrackerEvent.SafeCrackerNPC npc = new SafeCrackerEvent.SafeCrackerNPC(id, response, "", new ArrayList<>(), "");
				game.getNpcs().put(response, npc);
				service.save(service.get());
				SafeCrackerInventories.openNPCEditMenu(player, npc);
				return AnvilGUI.Response.text(response);
			}, (player1) -> SafeCrackerInventories.openAdminMenu(player));
		}));

		contents.set(0, 3, ClickableItem.from(new ItemBuilder(Material.PAPER).name("&eRiddle Answer").lore("&3" + game.getAnswer()).build(), e -> {
			openAnvilMenu(player, game.getAnswer(), (player1, response) -> {
				game.setAnswer(response);
				service.save(service.get());
				SafeCrackerInventories.openAdminMenu(player);
				return AnvilGUI.Response.text(response);
			}, (player1) -> SafeCrackerInventories.openAdminMenu(player));
		}));

		contents.set(0, 2, ClickableItem.from(new ItemBuilder(Material.BOOK).name("&eFinal Riddle").lore("&3" + game.getRiddle()).build(), e -> {
			player.closeInventory();
			PlayerUtils.send(player, new JsonBuilder("&e&lClick here to set the Final Riddle").suggest("/safecracker riddle "));
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
		service.save(service.get());

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
			contents.set(row, column, ClickableItem.from(builder.build(),
					e -> SafeCrackerInventories.openNPCEditMenu(player, npc)));

			if (column == 8) {
				column = 0;
				row++;
			} else
				column++;
		}

	}
}

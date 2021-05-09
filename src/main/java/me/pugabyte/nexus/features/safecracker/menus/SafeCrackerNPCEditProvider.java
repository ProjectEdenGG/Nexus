package me.pugabyte.nexus.features.safecracker.menus;

import eden.annotations.Disabled;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.safecracker.SafeCracker;
import me.pugabyte.nexus.models.safecracker.SafeCrackerEvent;
import me.pugabyte.nexus.models.safecracker.SafeCrackerEventService;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@Disabled
public class SafeCrackerNPCEditProvider extends MenuUtils implements InventoryProvider {

	SafeCrackerEventService service = new SafeCrackerEventService();
	SafeCrackerEvent.SafeCrackerNPC npc;

	public SafeCrackerNPCEditProvider(SafeCrackerEvent.SafeCrackerNPC npc) {
		this.npc = npc;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> SafeCrackerInventories.openAdminMenu(player));

		contents.set(0, 4, ClickableItem.empty(new ItemBuilder(Material.BOOK).name("&e" + npc.getName()).lore("&3Id: &e" + npc.getId()).build()));

		contents.set(0, 8, ClickableItem.from(new ItemBuilder(Material.TNT).name("&cDelete NPC").build(), e -> {
			MenuUtils.ConfirmationMenu.builder()
					.onConfirm(event -> {
						SafeCrackerEvent.SafeCrackerGame game = service.getActiveEvent();
						NPC npcEntity = CitizensAPI.getNPCRegistry().getById(npc.getId());
						npcEntity.destroy();
						game.getNpcs().remove(npc.getName());
						service.save(service.get());
						SafeCrackerInventories.openAdminMenu(player);
					})
					.onCancel(event -> SafeCrackerInventories.openNPCEditMenu(player, npc))
					.open(player);
		}));

		contents.set(1, 2, ClickableItem.from(new ItemBuilder(Material.WRITABLE_BOOK).name("&eQuestion").loreize(true)
				.lore("&3" + npc.getQuestion()).build(), e -> {
			player.closeInventory();
			SafeCracker.adminQuestionMap.put(player, npc.getName());
			PlayerUtils.send(player, new JsonBuilder("&e&lClick here to set " + npc.getName() + "'s question").suggest("/safecracker question "));
		}));

		ItemBuilder builder = new ItemBuilder(Material.PAPER).name("&eAnswers:");
		if (npc.getAnswers() != null)
			npc.getAnswers().forEach(answer -> builder.lore("&3 - " + answer));

		contents.set(1, 4, ClickableItem.from(builder.build(), e -> {
			openAnvilMenu(player, "Add Answer", (player1, response) -> {
				if (npc.getAnswers() == null)
					npc.setAnswers(new ArrayList<>());
				npc.getAnswers().add(response);
				service.save(service.get());
				SafeCrackerInventories.openNPCEditMenu(player, npc);
				return AnvilGUI.Response.text(response);
			}, (player1) -> SafeCrackerInventories.openNPCEditMenu(player, npc));
		}));

		contents.set(1, 6, ClickableItem.from(new ItemBuilder(Material.DIAMOND).name("&eRiddle").loreize(true).lore("&3" + npc.getRiddle()).build(), e -> {
			openAnvilMenu(player, npc.getRiddle(), (player1, response) -> {
				npc.setRiddle(response);
				service.save(service.get());
				SafeCrackerInventories.openNPCEditMenu(player, npc);
				return AnvilGUI.Response.text(response);
			}, (player1) -> SafeCrackerInventories.openNPCEditMenu(player, npc));
		}));

	}
}

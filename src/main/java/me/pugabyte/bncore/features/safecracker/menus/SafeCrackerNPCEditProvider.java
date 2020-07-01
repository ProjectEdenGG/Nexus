package me.pugabyte.bncore.features.safecracker.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.models.safecracker.SafeCrackerEvent;
import me.pugabyte.bncore.models.safecracker.SafeCrackerEventService;
import me.pugabyte.bncore.utils.ItemBuilder;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

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
						game.getNpcs().remove(npc.getName());
						service.save(service.get());
						SafeCrackerInventories.openAdminMenu(player);
					})
					.onCancel(event -> SafeCrackerInventories.openNPCEditMenu(player, npc))
					.open(player);
		}));

		contents.set(1, 2, ClickableItem.from(new ItemBuilder(Material.WRITABLE_BOOK).name("&eQuestion").loreize(true)
				.lore("&3" + npc.getQuestion()).build(), e -> {
			openAnvilMenu(player, npc.getQuestion(), (player1, response) -> {
				npc.setQuestion(response);
				service.save(service.get());
				SafeCrackerInventories.openNPCEditMenu(player, npc);
				return AnvilGUI.Response.text(response);
			}, (player1) -> SafeCrackerInventories.openNPCEditMenu(player, npc));
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

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}
}

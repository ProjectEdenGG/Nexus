package gg.projecteden.nexus.features.safecracker.menus;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.anvilgui.AnvilGUI;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.safecracker.SafeCracker;
import gg.projecteden.nexus.models.safecracker.SafeCrackerEvent;
import gg.projecteden.nexus.models.safecracker.SafeCrackerEventService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.RequiredArgsConstructor;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Material;

import java.util.ArrayList;

@Rows(3)
@Disabled
@RequiredArgsConstructor
public class SafeCrackerNPCEditProvider extends InventoryProvider {
	private final SafeCrackerEventService service = new SafeCrackerEventService();
	private final SafeCrackerEvent.SafeCrackerNPC npc;

	@Override
	public String getTitle() {
		return "SafeCracker Admin - " + npc.getName();
	}

	@Override
	public void init() {
		addBackItem(e -> new SafeCrackerAdminProvider().open(viewer));

		contents.set(0, 4, ClickableItem.empty(new ItemBuilder(Material.BOOK).name("&e" + npc.getName()).lore("&3Id: &e" + npc.getId()).build()));

		contents.set(0, 8, ClickableItem.of(new ItemBuilder(Material.TNT).name("&cDelete NPC").build(), e -> {
			MenuUtils.ConfirmationMenu.builder()
				.onConfirm(event -> {
					SafeCrackerEvent.SafeCrackerGame game = service.getActiveEvent();
					NPC npcEntity = CitizensAPI.getNPCRegistry().getById(npc.getId());
					npcEntity.destroy();
					game.getNpcs().remove(npc.getName());
					service.save(service.get0());
					new SafeCrackerAdminProvider().open(viewer);

				})
				.onCancel(event -> new SafeCrackerNPCEditProvider(npc).open(viewer))
				.open(viewer);
		}));

		contents.set(1, 2, ClickableItem.of(new ItemBuilder(Material.WRITABLE_BOOK).name("&eQuestion").loreize(true)
				.lore("&3" + npc.getQuestion()).build(), e -> {
			viewer.closeInventory();
			SafeCracker.adminQuestionMap.put(viewer, npc.getName());
			PlayerUtils.send(viewer, new JsonBuilder("&e&lClick here to set " + npc.getName() + "'s question").suggest("/safecracker question "));
		}));

		ItemBuilder builder = new ItemBuilder(Material.PAPER).name("&eAnswers:");
		if (npc.getAnswers() != null)
			npc.getAnswers().forEach(answer -> builder.lore("&3 - " + answer));

		contents.set(1, 4, ClickableItem.of(builder.build(), e -> {
			MenuUtils.openAnvilMenu(viewer, "Add Answer", (player1, response) -> {
				if (npc.getAnswers() == null)
					npc.setAnswers(new ArrayList<>());
				npc.getAnswers().add(response);
				service.save(service.get0());
				new SafeCrackerNPCEditProvider(npc).open(viewer);

				return AnvilGUI.Response.text(response);
			}, (player1) -> new SafeCrackerNPCEditProvider(npc).open(viewer));
		}));

		contents.set(1, 6, ClickableItem.of(new ItemBuilder(Material.DIAMOND).name("&eRiddle").loreize(true).lore("&3" + npc.getRiddle()).build(), e -> {
			MenuUtils.openAnvilMenu(viewer, npc.getRiddle(), (player1, response) -> {
				npc.setRiddle(response);
				service.save(service.get0());
				new SafeCrackerNPCEditProvider(npc).open(viewer);

				return AnvilGUI.Response.text(response);
			}, (player1) -> new SafeCrackerNPCEditProvider(npc).open(viewer));
		}));

	}
}

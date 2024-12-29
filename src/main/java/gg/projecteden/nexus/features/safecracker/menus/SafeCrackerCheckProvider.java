package gg.projecteden.nexus.features.safecracker.menus;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.safecracker.SafeCrackerEvent;
import gg.projecteden.nexus.models.safecracker.SafeCrackerEventService;
import gg.projecteden.nexus.models.safecracker.SafeCrackerPlayer;
import gg.projecteden.nexus.models.safecracker.SafeCrackerPlayerService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Disabled
@Title("SafeCracker")
public class SafeCrackerCheckProvider extends InventoryProvider {
	private final SafeCrackerPlayerService service = new SafeCrackerPlayerService();
	private final SafeCrackerEvent.SafeCrackerGame game = new SafeCrackerEventService().getActiveEvent();

	@Override
	public void init() {
		addCloseItem();

		SafeCrackerPlayer safeCrackerPlayer = service.get(viewer);

		int row = 1;
		int column = 0;
		int found = 0;

		for (SafeCrackerEvent.SafeCrackerNPC npc : game.getNpcs().values()) {
			ItemStack item = new ItemBuilder(Material.PLAYER_HEAD).skullOwner(PlayerUtils.getPlayer(npc.getName())).name("&e???").build();
			if (safeCrackerPlayer.getGames().get(game.getName()).getNpcs().containsKey(npc.getName())) {
				++found;
				SafeCrackerPlayer.SafeCrackerPlayerNPC playerNPC = safeCrackerPlayer.getGames().get(game.getName()).getNpcs().get(npc.getName());
				item = new ItemBuilder(Material.PLAYER_HEAD).skullOwner(PlayerUtils.getPlayer(npc.getName())).name("&e" + npc.getName())
						.lore("&3Found: &e" + TimeUtils.shortDateTimeFormat(playerNPC.getFound()))
						.lore("&3Question: &e" + npc.getQuestion())
						.lore("&3Answer: &e" + playerNPC.getAnswer())
						.lore("")
						.lore("&3Hint: &e" + npc.getRiddle())
						.build();
			}
			contents.set(row, column, ClickableItem.empty(item));

			if (column == 8) {
				column = 0;
				row++;
			} else
				column++;
		}

		boolean foundAll = found == game.getNpcs().size();

		ItemBuilder builder = new ItemBuilder(Material.BOOK)
				.name("&eFinal Riddle")
				.lore("&3" + game.getRiddle());

		if (!foundAll)
			builder.lore("", "&cFind all the NPCs before you solve the riddle!");
		else
			builder.lore("", "&eReturn to the excavation site and click the safe to solve the riddle");

		contents.set(0, 4, ClickableItem.empty(builder.build()));
	}
}

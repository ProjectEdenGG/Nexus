package me.pugabyte.bncore.features.safecracker.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.models.safecracker.SafeCrackerEvent;
import me.pugabyte.bncore.models.safecracker.SafeCrackerEventService;
import me.pugabyte.bncore.models.safecracker.SafeCrackerPlayer;
import me.pugabyte.bncore.models.safecracker.SafeCrackerPlayerService;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SafeCrackerCheckProvider extends MenuUtils implements InventoryProvider {

	SafeCrackerPlayerService service = new SafeCrackerPlayerService();
	SafeCrackerEvent.SafeCrackerGame game = new SafeCrackerEventService().getActiveEvent();

	@Override
	public void init(Player player, InventoryContents contents) {
		addCloseItem(contents);

		SafeCrackerPlayer safeCrackerPlayer = service.get(player);

		int row = 1;
		int column = 0;
		int found = 0;

		for (SafeCrackerEvent.SafeCrackerNPC npc : game.getNpcs().values()) {
			ItemStack item = new ItemBuilder(Material.PLAYER_HEAD).skullOwner(Utils.getPlayer(npc.getName())).name("&e???").build();
			if (safeCrackerPlayer.getGames().get(game.getName()).getNpcs().containsKey(npc.getName())) {
				++found;
				SafeCrackerPlayer.SafeCrackerPlayerNPC playerNPC = safeCrackerPlayer.getGames().get(game.getName()).getNpcs().get(npc.getName());
				item = new ItemBuilder(Material.PLAYER_HEAD).skullOwner(Utils.getPlayer(npc.getName())).name("&e" + npc.getName())
						.lore("&3Found: &e" + StringUtils.shortDateTimeFormat(playerNPC.getFound()))
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

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}
}

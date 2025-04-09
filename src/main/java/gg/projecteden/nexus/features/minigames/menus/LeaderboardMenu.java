package gg.projecteden.nexus.features.minigames.menus;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.matchdata.CheckpointData;
import gg.projecteden.nexus.models.checkpoint.CheckpointService;
import gg.projecteden.nexus.models.checkpoint.RecordTotalTime;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

@Rows(6)
public class LeaderboardMenu extends InventoryProvider {

	private static final CheckpointService service = new CheckpointService();
	private final Arena arena;

	public LeaderboardMenu(Arena arena) {
		this.arena = arena;
	}

	@Override
	public String getTitle() {
		return arena.getDisplayName() + " Leaderboard";
	}

	@Override
	public void init() {
		List<RecordTotalTime> records = service.getBestTotalTimes(arena);
		List<ClickableItem> items = new ArrayList<>();

		for (RecordTotalTime record : records) {
			List<ComponentLike> lore = new ArrayList<>();
			lore.add(new JsonBuilder("Total Time: ", NamedTextColor.GOLD)
				.next(CheckpointData.formatChatTime(record.getTime(), null, null), NamedTextColor.YELLOW));
			// TODO add split times

			ItemBuilder item = new ItemBuilder(Material.PLAYER_HEAD)
				.skullOwner(record)
				.name(new JsonBuilder((items.size() + 1) + ". ", NamedTextColor.GRAY)
					.next(record.getNickname(), NamedTextColor.DARK_AQUA))
				.componentLore(lore);

			items.add(ClickableItem.empty(item));
		}

		addCloseItem();
		contents.set(0, 8, ClickableItem.empty(new ItemBuilder(Material.BOOK).name("&eInformation")
			.loreize(true).lore("&3This is the leaderboard for the fastest completions of the minigame &6&o" + arena.getDisplayName() + "&3.")));
		paginate(items);
	}
}

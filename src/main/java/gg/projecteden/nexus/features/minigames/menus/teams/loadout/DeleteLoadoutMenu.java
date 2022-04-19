package gg.projecteden.nexus.features.minigames.menus.teams.loadout;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.menus.teams.TeamsMenu;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Loadout;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class DeleteLoadoutMenu extends InventoryProvider {
	private final Arena arena;
	private final Team team;

	@Override
	public void open(Player player, int page) {
		SmartInventory.builder()
			.provider(this)
			.title("Delete Loadout?")
			.rows(3)
			.build()
			.open(player, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		ItemBuilder cancel = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("&7Cancel");
		contents.fillRect(0, 0, 2, 8, ClickableItem.of(cancel, e -> new TeamsMenu(arena).open(player)));
		contents.fillRect(1, 1, 1, 7, ClickableItem.of(cancel, e -> new TeamsMenu(arena).open(player)));

		contents.set(1, 4, ClickableItem.of(Material.TNT, "&4&lDELETE LOADOUT", "&7This cannot be undone.", e -> {
			team.setLoadout(new Loadout());
			arena.write();
			new LoadoutMenu(arena, team).open(player);

		}));
	}

}

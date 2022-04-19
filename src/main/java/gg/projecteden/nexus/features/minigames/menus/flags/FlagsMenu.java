package gg.projecteden.nexus.features.minigames.menus.flags;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import static gg.projecteden.nexus.features.minigames.Minigames.menus;

public class FlagsMenu extends MenuUtils implements InventoryProvider {
	Arena arena;

	public FlagsMenu(@NonNull Arena arena) {
		this.arena = arena;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(0, 0, ClickableItem.of(backItem(), e -> menus.openArenaMenu(player, arena)));

		ColorType color = arena.isWhitelist() ? ColorType.WHITE : ColorType.BLACK;
		contents.set(1, 0, ClickableItem.of(new ItemBuilder(color.getWool()).name("&eUsable Block List").lore(
			"&7Click me to set the block list",
			"&7that players can use",
			"",
			"&3Current Setting: &e" + color
		), e -> menus.blockListMenu(arena).open(player)));

		ItemBuilder lateJoinItem = new ItemBuilder(Material.IRON_DOOR)
			.name("&eLate Join")
			.lore(
				"&7Set if players can join after",
				"&7the game has started",
				"",
				"&3Allowed:",
				"&e" + arena.canJoinLate()
			)
			.glow(arena.canJoinLate());

		contents.set(1, 1, ClickableItem.of(lateJoinItem, e -> {
			arena.canJoinLate(!arena.canJoinLate());
			arena.write();
			menus.openFlagsMenu(player, arena);
		}));
	}

}

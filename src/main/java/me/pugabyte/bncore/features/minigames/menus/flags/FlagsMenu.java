package me.pugabyte.bncore.features.minigames.menus.flags;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NonNull;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.utils.ColorType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.pugabyte.bncore.features.minigames.Minigames.menus;

public class FlagsMenu extends MenuUtils implements InventoryProvider {
	Arena arena;

	public FlagsMenu(@NonNull Arena arena) {
		this.arena = arena;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(0, 0, ClickableItem.from(backItem(), e -> menus.openArenaMenu(player, arena)));

		ColorType whitelist = ColorType.WHITE;
		if (!arena.isWhitelist()) whitelist = ColorType.BLACK;
		contents.set(1, 0, ClickableItem.from(nameItem(whitelist.getItemStack(Material.WOOL), "&eUsable Block List",
				"&7Click me to set the block list||&7that players can use|| ||&3Current Setting: &e" + whitelist), e -> {
			menus.blockListMenu(arena).open(player);
		}));

		ItemStack lateJoinItem = nameItem(Material.IRON_DOOR, "&eLate Join",
				"&7Set if players can join after||&7the game has started|| ||&3Allowed:||&e" + arena.canJoinLate());
		if (arena.canJoinLate())
			addGlowing(lateJoinItem);

		contents.set(1, 1, ClickableItem.from(lateJoinItem, e -> {
			arena.canJoinLate(!arena.canJoinLate());
			arena.write();
			menus.openFlagsMenu(player, arena);
		}));

		ItemStack scoreboardItem = nameItem(Material.SIGN, "&eScoreboard",
				"&7Set if the arena has||&7a visible scoreboard|| ||&3Current Setting:||&e" + arena.hasScoreboard());
		if (arena.hasScoreboard())
			addGlowing(scoreboardItem);

		contents.set(1, 2, ClickableItem.from(scoreboardItem, e -> {
			arena.hasScoreboard(!arena.hasScoreboard());
			arena.write();
			menus.openFlagsMenu(player, arena);
		}));
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {
	}

}

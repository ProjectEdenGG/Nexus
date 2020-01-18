package me.pugabyte.bncore.features.minigames.menus.flags;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FlagsMenu extends MenuUtils implements InventoryProvider {

	Arena arena;
	public FlagsMenu(Arena arena) {
		this.arena = arena;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(0, 0, ClickableItem.from(backItem(), e-> Minigames.menus.openArenaMenu(player, arena)));

		//Allow Late Join Toggle Item
		ItemStack lateJoinItem = nameItem(new ItemStack(Material.IRON_DOOR),
				"&eLate Join", "&7Set if players can join after||&7the game has started|| ||&3Allowed:||&e" + arena.canJoinLate());
		if(arena.canJoinLate()){
			addGlowing(lateJoinItem);
		}
		contents.set(1, 0, ClickableItem.of(lateJoinItem, e ->{
			if(arena.canJoinLate()){
				arena.canJoinLate(false);
			}
			else {
				arena.canJoinLate(true);
			}
			ArenaManager.write(arena);
			ArenaManager.add(arena);
			Minigames.menus.openFlagsMenu(player, arena);
		}));

		//Scoreboard Toggle Item
		ItemStack scoreboardItem = nameItem(new ItemStack(Material.SIGN),
				"&eScoreboard", "&7Set if the arena has||&7a visible scoreboard|| ||&3Current Setting:||&e" + arena.hasScoreboard());
		if(arena.hasScoreboard()){
			addGlowing(scoreboardItem);
		}
		contents.set(1, 1, ClickableItem.of(scoreboardItem, e ->{
			if(arena.hasScoreboard()){
				arena.hasScoreboard(false);
			}
			else {
				arena.hasScoreboard(true);
			}
			ArenaManager.write(arena);
			ArenaManager.add(arena);
			Minigames.menus.openFlagsMenu(player, arena);
		}));

		String whitelist = "Whitelisted";
		if(arena.isWhitelist()) whitelist = "Blacklisted";
		if(!arena.isWhitelist()) whitelist = "Blacklisted";
		//Block List Item
		contents.set(1, 2, ClickableItem.of(nameItem(new ItemStack(Material.DIAMOND_PICKAXE),
				"&eBreakable Block List", "&7Click me to set the block list||&7that players can break|| ||&3Current Setting:||&e" + whitelist), e->{
			Minigames.menus.blockListMenu(arena).open(player);
		}));

	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}
}

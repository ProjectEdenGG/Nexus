package me.pugabyte.bncore.features.minigames.menus.custom;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.menus.annotations.CustomMechanicSettings;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.arenas.GoldRushArena;
import me.pugabyte.bncore.features.minigames.models.mechanics.MechanicType;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.utils.Utils;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiFunction;

import static me.pugabyte.bncore.features.minigames.Minigames.PREFIX;
import static me.pugabyte.bncore.features.minigames.Minigames.menus;

@CustomMechanicSettings(MechanicType.GOLD_RUSH)
public class GoldRushMenu extends MenuUtils implements InventoryProvider {
	GoldRushArena arena;

	public GoldRushMenu(Arena arena){
		this.arena = (GoldRushArena) ArenaManager.convert(arena, GoldRushArena.class);
		this.arena.write();
	}

	static void openAnvilMenu(Player player, Arena arena, String text, BiFunction<Player, String, AnvilGUI.Response> onComplete) {
		openAnvilMenu(player, text, onComplete, p -> Utils.wait(1, () -> menus.openCustomSettingsMenu(player, arena)));
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(0, 0, ClickableItem.from(backItem(), e-> Minigames.getMenus().openArenaMenu(player, arena)));

		String currentValue = (arena.getMineStackHeight() > 0) ? "" + arena.getMineStackHeight() : "null";

		contents.set(1, 4, ClickableItem.from(nameItem(new ItemStack(Material.LADDER), "&eMine Stack Height", "&eCurrent value:||&3"),
				e -> {
					openAnvilMenu(player, arena, currentValue, (Player p, String text) -> {
						if(!Utils.isInt(text)) {
							AnvilGUI.Response.close();
							throw new InvalidInputException(PREFIX + "You must use an integer for Mine Stack Height.");
						}
						arena.setMineStackHeight(Integer.parseInt(text));
						ArenaManager.write(arena);
						menus.openCustomSettingsMenu(player, arena);
						return AnvilGUI.Response.text(text);
				});
		}));
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}
}

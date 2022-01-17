package gg.projecteden.nexus.features.minigames.menus.custom;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.sabotage.ArenaTasksMenu;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.mechanics.Sabotage;
import gg.projecteden.nexus.features.minigames.menus.annotations.CustomMechanicSettings;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.arenas.SabotageArena;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import static gg.projecteden.nexus.features.minigames.Minigames.menus;

@CustomMechanicSettings(Sabotage.class)
public class SabotageMenu extends MenuUtils implements InventoryProvider {
	SabotageArena arena;

	public SabotageMenu(Arena arena) {
		this.arena = ArenaManager.convert(arena, SabotageArena.class);
	}

	@Override
	public void open(Player player, int page) {
		Minigames.getMenus().openCustomSettingsMenu(player, arena);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(0, 0, ClickableItem.from(backItem(), e -> menus.openArenaMenu(player, arena)));

		contents.set(1, 0, ClickableItem.from(new ItemBuilder(Material.CLOCK).name("&eKill Cooldown").lore("", "&eCurrent value: &3" + arena.getKillCooldown() + " seconds").build(),
				click -> new AnvilMenu.IntegerBuilder().positiveChecker().click(click).getter(arena::getKillCooldown).setter(arena::setKillCooldown).writer(arena::write).open()));

		contents.set(1, 1, ClickableItem.from(new ItemBuilder(Material.CLOCK).name("&eMeeting Cooldown").lore("", "&eCurrent value: &3" + arena.getMeetingCooldown() + " seconds").build(),
				click -> new AnvilMenu.IntegerBuilder().positiveChecker().click(click).getter(arena::getMeetingCooldown).setter(arena::setMeetingCooldown).writer(arena::write).open()));

		contents.set(1, 2, ClickableItem.from(new ItemBuilder(Material.PAPER).name("&eShort Tasks").lore("", "&eCurrent value: &3" + arena.getShortTasks()).build(),
				click -> new AnvilMenu.IntegerBuilder().nonNegativeChecker().click(click).getter(arena::getShortTasks).setter(arena::setShortTasks).writer(arena::write).open()));

		contents.set(1, 3, ClickableItem.from(new ItemBuilder(Material.PAPER).name("&eLong Tasks").lore("", "&eCurrent value: &3" + arena.getLongTasks()).build(),
				click -> new AnvilMenu.IntegerBuilder().nonNegativeChecker().click(click).getter(arena::getLongTasks).setter(arena::setLongTasks).writer(arena::write).open()));

		contents.set(1, 4, ClickableItem.from(new ItemBuilder(Material.PAPER).name("&eCommon Tasks").lore("", "&eCurrent value: &3" + arena.getCommonTasks()).build(),
				click -> new AnvilMenu.IntegerBuilder().nonNegativeChecker().click(click).getter(arena::getCommonTasks).setter(arena::setCommonTasks).writer(arena::write).open()));

		contents.set(1, 5, ClickableItem.from(new ItemBuilder(Material.REDSTONE).name("&eEnabled Tasks").build(), $ -> new ArenaTasksMenu(arena).open(player)));
	}
}

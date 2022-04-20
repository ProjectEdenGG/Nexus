package gg.projecteden.nexus.features.minigames.menus.custom;

import gg.projecteden.nexus.features.menus.MenuUtils.AnvilMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.sabotage.ArenaTasksMenu;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.mechanics.Sabotage;
import gg.projecteden.nexus.features.minigames.menus.ArenaMenu;
import gg.projecteden.nexus.features.minigames.menus.annotations.CustomMechanicSettings;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.arenas.SabotageArena;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;

@CustomMechanicSettings(Sabotage.class)
public class SabotageMenu extends ICustomMechanicMenu {
	SabotageArena arena;

	public SabotageMenu(Arena arena) {
		this.arena = ArenaManager.convert(arena, SabotageArena.class);
	}

	@Override
	public void init() {
		addBackItem(e -> new ArenaMenu(arena).open(player));

		contents.set(1, 0, ClickableItem.of(new ItemBuilder(Material.CLOCK).name("&eKill Cooldown").lore("", "&eCurrent value: &3" + arena.getKillCooldown() + " seconds").build(),
				click -> new AnvilMenu.IntegerBuilder().positiveChecker().click(click).getter(arena::getKillCooldown).setter(arena::setKillCooldown).writer(arena::write).open()));

		contents.set(1, 1, ClickableItem.of(new ItemBuilder(Material.CLOCK).name("&eMeeting Cooldown").lore("", "&eCurrent value: &3" + arena.getMeetingCooldown() + " seconds").build(),
				click -> new AnvilMenu.IntegerBuilder().positiveChecker().click(click).getter(arena::getMeetingCooldown).setter(arena::setMeetingCooldown).writer(arena::write).open()));

		contents.set(1, 2, ClickableItem.of(new ItemBuilder(Material.PAPER).name("&eShort Tasks").lore("", "&eCurrent value: &3" + arena.getShortTasks()).build(),
				click -> new AnvilMenu.IntegerBuilder().nonNegativeChecker().click(click).getter(arena::getShortTasks).setter(arena::setShortTasks).writer(arena::write).open()));

		contents.set(1, 3, ClickableItem.of(new ItemBuilder(Material.PAPER).name("&eLong Tasks").lore("", "&eCurrent value: &3" + arena.getLongTasks()).build(),
				click -> new AnvilMenu.IntegerBuilder().nonNegativeChecker().click(click).getter(arena::getLongTasks).setter(arena::setLongTasks).writer(arena::write).open()));

		contents.set(1, 4, ClickableItem.of(new ItemBuilder(Material.PAPER).name("&eCommon Tasks").lore("", "&eCurrent value: &3" + arena.getCommonTasks()).build(),
				click -> new AnvilMenu.IntegerBuilder().nonNegativeChecker().click(click).getter(arena::getCommonTasks).setter(arena::setCommonTasks).writer(arena::write).open()));

		contents.set(1, 5, ClickableItem.of(new ItemBuilder(Material.REDSTONE).name("&eEnabled Tasks").build(), $ -> new ArenaTasksMenu(arena).open(player)));
	}
}

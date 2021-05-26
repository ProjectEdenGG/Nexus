package me.pugabyte.nexus.features.minigames.menus.custom;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.features.minigames.managers.ArenaManager;
import me.pugabyte.nexus.features.minigames.mechanics.Sabotage;
import me.pugabyte.nexus.features.minigames.menus.annotations.CustomMechanicSettings;
import me.pugabyte.nexus.features.minigames.models.Arena;
import me.pugabyte.nexus.features.minigames.models.arenas.SabotageArena;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import static me.pugabyte.nexus.features.minigames.Minigames.menus;

@CustomMechanicSettings(Sabotage.class)
public class SabotageMenu extends MenuUtils implements InventoryProvider {
	SabotageArena arena;

	public SabotageMenu(Arena arena) {
		this.arena = ArenaManager.convert(arena, SabotageArena.class);
	}

	@Override
	public void open(Player viewer, int page) {
		Minigames.getMenus().openCustomSettingsMenu(viewer, arena);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(0, 0, ClickableItem.from(backItem(), e -> menus.openArenaMenu(player, arena)));

		ItemBuilder meetingItem = new ItemBuilder(Material.RED_CONCRETE).name("&eMeeting Location").lore("&3Stationary location where players should be teleported to during meetings and for the ejection cutscene", "");
		meetingItem.lore(arena.getMeetingLocation() == null ? "&eUnset" : getLocationLore(arena.getMeetingLocation()));
		contents.set(1, 0, ClickableItem.from(meetingItem.build(), $ -> {
			arena.setMeetingLocation(player.getLocation());
			arena.write();
			init(player, contents);
		}));

		contents.set(1, 1, ClickableItem.from(new ItemBuilder(Material.CLOCK).name("&eKill Cooldown").lore("", "&eCurrent value: &3" + arena.getKillCooldown() + " seconds").build(),
				$ -> openPositiveIntAnvilMenu($, arena::getKillCooldown, arena::setKillCooldown, arena::write, "Kill cooldown must be a positive integer")));

		contents.set(1, 2, ClickableItem.from(new ItemBuilder(Material.CLOCK).name("&eMeeting Cooldown").lore("", "&eCurrent value: &3" + arena.getMeetingCooldown() + " seconds").build(),
				$ -> openPositiveIntAnvilMenu($, arena::getMeetingCooldown, arena::setMeetingCooldown, arena::write, "Meeting cooldown must be a positive integer")));
	}
}

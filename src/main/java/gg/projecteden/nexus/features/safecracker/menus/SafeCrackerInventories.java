package gg.projecteden.nexus.features.safecracker.menus;

import gg.projecteden.annotations.Disabled;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.models.safecracker.SafeCrackerEvent;
import org.bukkit.entity.Player;

@Disabled
public class SafeCrackerInventories {

	public static void openCheckMenu(Player player) {
		SmartInventory.builder()
				.size(6, 9)
				.title("SafeCracker")
				.provider(new SafeCrackerCheckProvider())
				.build().open(player);
	}

	public static void openAdminMenu(Player player) {
		SmartInventory.builder()
				.size(6, 9)
				.title("SafeCracker Admin")
				.provider(new SafeCrackerAdminProvider())
				.build().open(player);
	}

	public static void openNPCEditMenu(Player player, SafeCrackerEvent.SafeCrackerNPC npc) {
		SmartInventory.builder()
				.size(3, 9)
				.title("SafeCracker Admin - " + npc.getName())
				.provider(new SafeCrackerNPCEditProvider(npc))
				.build().open(player);
	}

	public static void openGameSelectorMenu(Player player) {
		SmartInventory.builder()
				.size(6, 9)
				.title("SafeCracker Game Selector")
				.provider(new SafeCrackerGameSelector())
				.build().open(player);
	}
}

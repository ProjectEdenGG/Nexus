package me.pugabyte.bncore.features.votes.vps;

import fr.minuskube.inv.SmartInventory;
import org.bukkit.entity.Player;

public class VPS {
	public static void open(Player player, String type, int page) {
		VPSMenu menu = getMenu(type, page);
		SmartInventory.builder()
				.provider(new VPSProvider(menu))
				.size(6, 9)
				.title("&3Vote Point Store")
				.build()
				.open(player);
	}

	public static VPSMenu getMenu(String type, int page) {
		return VPSMenu.valueOf(type.toUpperCase());
	}

}

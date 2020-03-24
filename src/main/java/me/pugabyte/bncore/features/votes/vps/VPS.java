package me.pugabyte.bncore.features.votes.vps;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.features.votes.vps.VPSMenu.VPSPage;
import org.bukkit.entity.Player;

public class VPS {
	public static void open(Player player, String type, int page) {
		VPSPage vpsPage = getMenu(type).getPage(page);
		SmartInventory.builder()
				.provider(new VPSProvider(vpsPage))
				.size(vpsPage.getRows(), 9)
				.title("&3Vote Point Store")
				.build()
				.open(player);
	}

	public static VPSMenu getMenu(String type) {
		return VPSMenu.valueOf(type.toUpperCase());
	}

}

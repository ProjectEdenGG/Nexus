package gg.projecteden.nexus.features.votes.vps;

import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.votes.vps.VPSMenu.VPSPage;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.WorldGroup;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class VPS {
	public static final String PREFIX = StringUtils.getPrefix("VotePoints");

	public static void open(Player player, int page) {
		open(player, getMenu(player), page);
	}

	public static void open(Player player, VPSMenu menu, int page) {
		VPSPage vpsPage = menu.getPage(page);
		SmartInventory.builder()
				.provider(new VPSProvider(menu, vpsPage))
				.size(vpsPage.getRows(), 9)
				.title(ChatColor.DARK_AQUA + "Vote Point Store")
				.build()
				.open(player);
	}

	public static VPSMenu getMenu(Player player) {
		return getMenu(WorldGroup.of(player).name());
	}

	public static VPSMenu getMenu(String type) {
		try {
			return VPSMenu.valueOf(type.toUpperCase());
		} catch (IllegalArgumentException ex) {
			throw new InvalidInputException("There is no VPS for this world");
		}
	}

}

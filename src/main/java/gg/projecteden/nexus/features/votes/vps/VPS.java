package gg.projecteden.nexus.features.votes.vps;

import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.WorldGroup;
import org.bukkit.entity.Player;

public class VPS {
	public static final String PREFIX = StringUtils.getPrefix("VotePoints");

	public static void open(Player player, int page) {
		open(player, getMenu(player), page);
	}

	public static void open(Player player, VPSMenu menu, int page) {
		new VPSProvider(menu, menu.getPage(page)).open(player, page);
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

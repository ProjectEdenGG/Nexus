package me.pugabyte.bncore.features.votes.vps;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.features.votes.vps.VPSMenu.VPSPage;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.ChatColor;
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
		return getMenu(WorldGroup.get(player).name());
	}

	public static VPSMenu getMenu(String type) {
		try {
			return VPSMenu.valueOf(type.toUpperCase());
		} catch (IllegalArgumentException ex) {
			throw new InvalidInputException("There is no VPS for this world");
		}
	}

}

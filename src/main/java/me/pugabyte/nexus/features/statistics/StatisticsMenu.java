package me.pugabyte.nexus.features.statistics;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class StatisticsMenu {

	public enum StatsMenus {
		MAIN,
		GENERAL,
		BLOCKS,
		ITEMS,
		MOBS;

		public int getSize() {
			if (this.equals(MAIN))
				return 3;
			return 6;
		}
	}

	public static void open(Player player, StatsMenus menu, int page, OfflinePlayer targetPlayer) {
		SmartInventory.builder()
				.provider(new StatisticsMenuProvider(menu, targetPlayer))
				.size(menu.getSize(), 9)
				.title(StringUtils.colorize(Nickname.of(targetPlayer) + "'s Statistics - " + StringUtils.camelCase(menu.name())))
				.build().open(player, page);
	}

	public static void open(Player player, StatsMenus menu, OfflinePlayer targetPlayer, int startIndex) {
		SmartInventory.builder()
				.provider(new StatisticsMenuProvider(menu, targetPlayer, startIndex))
				.size(menu.getSize(), 9)
				.title(StringUtils.colorize(Nickname.of(targetPlayer) + "'s Statistics - " + StringUtils.camelCase(menu.name())))
				.build().open(player);
	}

}

package gg.projecteden.nexus.features.statistics;

import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.StringUtils;
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
				.rows(menu.getSize())
				.title(StringUtils.colorize(Nickname.of(targetPlayer) + "'s Statistics - " + StringUtils.camelCase(menu.name())))
				.build().open(player, page);
	}

	public static void open(Player player, StatsMenus menu, OfflinePlayer targetPlayer, int startIndex) {
		SmartInventory.builder()
				.provider(new StatisticsMenuProvider(menu, targetPlayer, startIndex))
				.rows(menu.getSize())
				.title(StringUtils.colorize(Nickname.of(targetPlayer) + "'s Statistics - " + StringUtils.camelCase(menu.name())))
				.build().open(player);
	}

}

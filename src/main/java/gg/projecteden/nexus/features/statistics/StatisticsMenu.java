package gg.projecteden.nexus.features.statistics;

import static gg.projecteden.api.common.utils.StringUtils.camelCase;

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

}

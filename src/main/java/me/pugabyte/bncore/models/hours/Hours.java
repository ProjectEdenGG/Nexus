package me.pugabyte.bncore.models.hours;

import lombok.Data;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.OfflinePlayer;

@Data
public class Hours {
	private String uuid;
	private int total = 0;
	private int monthly = 0;
	private int weekly = 0;
	private int daily = 0;
	private int lastMonth = 0;
	private int lastWeek = 0;
	private int yesterday = 0;

	public OfflinePlayer getPlayer() {
		return Utils.getPlayer(uuid);
	}

	public void increment() {
		++total;
		++daily;
		++weekly;
		++monthly;
	}

	public int get(HoursService.HoursType type) {
		switch (type) {
			case TOTAL:
				return getTotal();
			case MONTHLY:
				return getMonthly();
			case WEEKLY:
				return getWeekly();
			case DAILY:
				return getDaily();
			case LAST_MONTH:
				return getLastMonth();
			case LAST_WEEK:
				return getLastWeek();
			case YESTERDAY:
				return getYesterday();
		}

		return 0;
	}

}

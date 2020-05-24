package me.pugabyte.bncore.models.hours;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.OfflinePlayer;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Hours {
	@NonNull
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
		increment(1);
	}

	public void increment(int amount) {
		total += amount;
		daily += amount;
		weekly += amount;
		monthly += amount;
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

	public void set(HoursService.HoursType type, int amount) {
		switch (type) {
			case TOTAL:
				total = amount;
				break;
			case MONTHLY:
				monthly = amount;
				break;
			case WEEKLY:
				weekly = amount;
				break;
			case DAILY:
				daily = amount;
				break;
			case LAST_MONTH:
				lastMonth = amount;
				break;
			case LAST_WEEK:
				lastWeek = amount;
				break;
			case YESTERDAY:
				yesterday = amount;
				break;
		}
	}

}

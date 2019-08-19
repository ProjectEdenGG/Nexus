package me.pugabyte.bncore.models.dailyrewards;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.BaseService;

import java.util.List;

public class DailyRewardsService extends BaseService {
	@Override
	public DailyRewards get(String uuid) {
		return database.where("uuid = ?", uuid).first(DailyRewards.class);
	}

	public List<DailyRewards> getPage(int page) {
		return database.sql("select * from dailyrewards order by streak desc limit 10 offset " + ((page - 1) * 10))
				.results(DailyRewards.class);
	}

	public List<DailyRewards> getAll() {
		return database.results(DailyRewards.class);
	}

	public void save(DailyRewards dailyRewards) {
		BNCore.async(() -> database.upsert(dailyRewards).execute());
	}
}

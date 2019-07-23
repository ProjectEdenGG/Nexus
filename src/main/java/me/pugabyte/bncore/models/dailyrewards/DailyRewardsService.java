package me.pugabyte.bncore.models.dailyrewards;

import com.dieselpoint.norm.Database;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.BaseService;
import me.pugabyte.bncore.models.persistence.BearNationDatabase;
import me.pugabyte.bncore.models.persistence.Persistence;

public class DailyRewardsService extends BaseService {
	private Database database = Persistence.getConnection(BearNationDatabase.BEARNATION);

	@Override
	public DailyRewards get(String uuid) {
		return database.where("uuid = ?", uuid).first(DailyRewards.class);
	}

	public void save(DailyRewards dailyRewards) {
		BNCore.async(() -> database.upsert(dailyRewards).execute());
	}
}

package me.pugabyte.nexus.models.dailyreward;

import me.pugabyte.nexus.models.MySQLService;

import java.util.List;

public class DailyRewardService extends MySQLService {

	@Override
	public DailyReward get(String uuid) {
		DailyReward dailyReward = database.where("uuid = ? and active = 1", uuid).first(DailyReward.class);
		if (dailyReward.getUuid() == null)
			dailyReward = new DailyReward(uuid);
		return dailyReward;
	}

	public List<DailyReward> getPage(int page) {
		return database.where("active = 1").orderBy("streak desc").limit(10).offset((page - 1) * 10).results(DailyReward.class);
	}

	public List<DailyReward> getAll() {
		return database.where("active = 1").results(DailyReward.class);
	}

	public List<DailyReward> getAllNotEarnedToday() {
		return database.where("active = 1 and earnedToday = 0").results(DailyReward.class);
	}

	public void save(DailyReward dailyReward) {
		if (dailyReward.getStreak() > 0)
			super.save(dailyReward);
	}

}

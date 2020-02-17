package me.pugabyte.bncore.models.vote;

import lombok.Data;
import me.pugabyte.bncore.models.MySQLService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class VoteService extends MySQLService {
	private final static Map<String, Voter> cache = new HashMap<>();

	public void clearCache() {
		cache.clear();
	}

	@Override
	public Voter get(String uuid) {
//		cache.computeIfAbsent(uuid, $ -> {
			int votes = database.select("count(*)").where("uuid = ?", uuid).first(Double.class).intValue();
			List<Vote> activeVotes = database.where("uuid = ?", uuid).and("expired = 0").results(Vote.class);
			return new Voter(uuid, votes, activeVotes);
//		});

//		return cache.get(uuid);
	}




}

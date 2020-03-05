package me.pugabyte.bncore.models.vote;

import com.dieselpoint.norm.Query;
import lombok.Data;
import me.pugabyte.bncore.models.MySQLService;

import java.util.List;

@Data
public class VoteService extends MySQLService {

	@Override
	public Voter get(String uuid) {
		return new Voter(uuid, getTotalVotes(uuid), getActiveVotes(uuid));
	}

	public int getTotalVotes() {
		return getTotalVotes(null);
	}

	public int getTotalVotes(String uuid) {
		Query query = database.table("vote").select("count(*)");
		if (uuid != null)
			query.where("uuid = ?", uuid);
		return query.first(Long.class).intValue();
	}

	public List<Vote> getActiveVotes() {
		return getActiveVotes(null);
	}

	public List<Vote> getActiveVotes(String uuid) {
		Query query = database.where("expired = 0");
		if (uuid != null)
			query.and("uuid = ?", uuid);
		return query.results(Vote.class);
	}

}

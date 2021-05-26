package me.pugabyte.nexus.models.vote;

import com.dieselpoint.norm.Query;
import lombok.Data;
import me.pugabyte.nexus.models.MySQLService;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

	public List<Vote> getRecentVotes() {
		Query query = database.where("expired = 0").orderBy("timestamp desc");
		return query.results(Vote.class);
	}

	public Map<LocalDate, Integer> getVotesByDay() {
		Query query = database.sql("select date(timestamp) as date, count(*) as count from vote group by 1 having count >= 100 order by count desc");
		List<LinkedHashMap> results = query.results(LinkedHashMap.class);
		LinkedHashMap<LocalDate, Integer> map = new LinkedHashMap<>();
		for (Map result : results)
			map.put(((Date) result.get("date")).toLocalDate(), ((Long) result.get("count")).intValue());

		return map;
	}

	public List<TopVoter> getTopVoters(Month month) {
		LocalDateTime first = LocalDateTime.now().withMonth(month.getValue()).withDayOfMonth(1);
		if (first.isAfter(LocalDateTime.now()))
			first = first.minusYears(1);

		return database.sql(
				"select uuid, count(*) as count " +
				"from vote where MONTH(timestamp) = ? AND YEAR(timestamp) = ? " +
				"group by 1 " +
				"order by count desc")
				.args(month.getValue(), first.getYear())
				.results(TopVoter.class);
	}

	public List<TopVoter> getTopVoters() {
		return database.sql(
				"select uuid, count(*) as count " +
				"from vote " +
				"group by 1 " +
				"order by count desc " +
				"limit 50")
				.results(TopVoter.class);
	}

}

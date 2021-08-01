package gg.projecteden.nexus.models.voter;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;
import gg.projecteden.nexus.models.voter.Voter.Vote;
import gg.projecteden.nexus.utils.Tasks;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@PlayerClass(Voter.class)
public class VoterService extends MongoService<Voter> {
	private final static Map<UUID, Voter> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, Voter> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	static {
		Tasks.async(() -> new VoterService().cacheAll());
	}

	@NotNull
	private Stream<Vote> getAllVotes() {
		return getCache().values().stream()
			.map(Voter::getVotes)
			.flatMap(Collection::stream);
	}

	public Map<LocalDate, Integer> getVotesByDay() {
		return getVotesByDay(YearMonth.now());
	}

	public Map<LocalDate, Integer> getVotesByDay(YearMonth yearMonth) {
		return new HashMap<>() {{
			for (Vote vote : getMonthsVotes(yearMonth)) {
				final LocalDate date = vote.getTimestamp().toLocalDate();
				put(date, getOrDefault(date, 0) + 1);
			}
		}};
	}

	public Map<YearMonth, List<Vote>> getVotesByMonth() {
		return new HashMap<>() {{
			getAllVotes().forEach(vote -> computeIfAbsent(YearMonth.from(vote.getTimestamp()), $ -> new ArrayList<>()).add(vote));
		}};
	}

	public List<Vote> getMonthsVotes() {
		return getMonthsVotes(YearMonth.now());
	}

	public List<Vote> getMonthsVotes(YearMonth yearMonth) {
		return getAllVotes()
			.filter(vote -> yearMonth.equals(YearMonth.from(vote.getTimestamp())))
			.toList();
	}

	public List<TopVoter> getTopVoters() {
		return getCache().values().stream()
			.sorted(Comparator.comparingInt(Voter::getCount).reversed())
			.map(voter -> new TopVoter(voter, null, voter.getVotes()))
			.toList();
	}

	public List<TopVoter> getTopVoters(Month month) {
		YearMonth yearMonth = YearMonth.now().with(month);
		if (yearMonth.isAfter(YearMonth.now()))
			yearMonth = yearMonth.minusYears(1);

		return getTopVoters(yearMonth);
	}

	public List<TopVoter> getTopVoters(YearMonth yearMonth) {
		return getCache().values().stream()
			.map(voter -> new TopVoter(voter, yearMonth, voter.getVotes().stream()
				.filter(vote -> yearMonth.equals(YearMonth.from(vote.getTimestamp())))
				.toList()
			))
			.sorted(Comparator.comparingInt(TopVoter::getCount).reversed())
			.toList();
	}

	public List<Vote> getActiveVotes() {
		return getAllVotes()
			.filter(Vote::isActive)
			.toList();
	}

}

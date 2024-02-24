package gg.projecteden.nexus.models.voter;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.features.votes.party.VoteParty;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;
import gg.projecteden.nexus.models.voter.Voter.Vote;
import gg.projecteden.nexus.utils.Tasks;
import org.jetbrains.annotations.NotNull;

import java.time.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@ObjectClass(Voter.class)
public class VoterService extends MongoPlayerService<Voter> {
	private final static Map<UUID, Voter> cache = new ConcurrentHashMap<>();

	public Map<UUID, Voter> getCache() {
		return cache;
	}

	static {
		Tasks.async(() -> {
			new VoterService().cacheAll();
			VoteParty.process();
		});
	}

	@NotNull
	private Stream<Vote> getAllVotes() {
		return getCache().values().stream()
			.map(Voter::getVotes)
			.flatMap(Collection::stream);
	}

	public Map<LocalDate, Integer> getAllVotesByDay() {
		return new HashMap<>() {{
			for (Vote vote : getAllVotes().toList()) {
				final LocalDate date = vote.getTimestamp().toLocalDate();
				put(date, getOrDefault(date, 0) + 1);
			}
		}};
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

	public Map<LocalDate, Integer> getVotesByDay(Year year) {
		return new HashMap<>() {{
			for (Vote vote : getYearsVotes(year)) {
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

	public List<Vote> getYearsVotes() {
		return getMonthsVotes(YearMonth.now());
	}

	public List<Vote> getYearsVotes(Year year) {
		return getAllVotes()
			.filter(vote -> year.equals(Year.from(vote.getTimestamp())))
			.toList();
	}

	public List<Vote> getVotesAfter(LocalDateTime date) {
		return getAllVotes()
			.filter(vote -> vote.getTimestamp().isAfter(date))
			.toList();
	}

	public List<TopVoter> getTopVoters() {
		return getCache().values().stream()
			.filter(voter -> voter.getCount() > 0)
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
			.filter(topVoter -> topVoter.getCount() > 0)
			.sorted(Comparator.comparingInt(TopVoter::getCount).reversed())
			.toList();
	}

	public List<Vote> getActiveVotes() {
		return getAllVotes()
			.filter(Vote::isActive)
			.toList();
	}

}

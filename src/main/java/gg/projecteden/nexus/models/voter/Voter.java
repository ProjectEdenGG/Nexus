package gg.projecteden.nexus.models.voter;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
@Entity(value = "voter", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class Voter implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<Vote> votes = new ArrayList<>();
	private int points;
	private boolean reminders = true;
	private int headCoupons;

	public void vote(Vote vote) {
		votes.add(vote);
	}

	public List<Vote> getActiveVotes() {
		return votes.stream().filter(Vote::isActive).toList();
	}

	public Optional<Vote> getActiveVote(VoteSite site) {
		return getActiveVotes().stream().filter(vote -> vote.getSite() == site).findFirst();
	}

	public List<Vote> getTodaysVotes() {
		return getVotes(LocalDate.now());
	}

	public List<Vote> getVotes(LocalDate date) {
		return votes.stream().filter(vote -> date.equals(vote.getTimestamp().toLocalDate())).toList();
	}

	public int getCount() {
		return votes.size();
	}

	public void takePoints(int points) {
		setPoints(getPoints() - points);
	}

	public void givePoints(int points) {
		setPoints(getPoints() + points);
	}

	public void setPoints(int points) {
		if (points < 0)
			throw new InvalidInputException("You do not have enough vote points"); // TODO NegativeBalanceException?

		this.points = points;
	}

	public boolean hasNotVotedFor60Days() {
		return votes.stream().noneMatch(vote -> vote.getTimestamp().plusDays(60).isAfter(LocalDateTime.now()));
	}

	@Data
	@NoArgsConstructor
	public static class Vote implements PlayerOwnedObject {
		@NonNull
		private UUID uuid;
		@NonNull
		private VoteSite site;
		private int extra;
		@NonNull
		private LocalDateTime timestamp;
		private boolean active;

		public Voter getVoter() {
			return new VoterService().get(this);
		}

		public LocalDateTime getExpiration() {
			return timestamp.plusHours(site.getExpirationHours());
		}

		public Vote(@NonNull UUID uuid, @NonNull VoteSite site, int extra, @NonNull LocalDateTime timestamp) {
			this.uuid = uuid;
			this.site = site;
			this.extra = extra;
			this.timestamp = timestamp;
			this.active = true;
		}

	}

}

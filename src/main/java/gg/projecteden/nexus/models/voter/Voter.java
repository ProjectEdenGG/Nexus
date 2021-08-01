package gg.projecteden.nexus.models.voter;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
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

	public List<Vote> getTodaysVotes() {
		final LocalDate now = LocalDate.now();
		return votes.stream().filter(vote -> now.equals(vote.getTimestamp().toLocalDate())).toList();
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

		public Vote(@NonNull UUID uuid, @NonNull VoteSite site, int extra, @NonNull LocalDateTime timestamp) {
			this.uuid = uuid;
			this.site = site;
			this.extra = extra;
			this.timestamp = timestamp;
			this.active = true;
		}

	}

}

package gg.projecteden.nexus.models.vote;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Voter {
	@NonNull
	private String uuid;
	private int votes;
	private List<Vote> activeVotes = new ArrayList<>();

	public Voter(OfflinePlayer player) {
		this.uuid = player.getUniqueId().toString();
	}

	public Voter(UUID uuid) {
		this.uuid = uuid.toString();
	}

	public int getPoints() {
		VotePointsService service = new VotePointsService();
		VotePoints votePoints = service.get(UUID.fromString(uuid));
		return votePoints.getPoints();
	}

	public void takePoints(int points) {
		setPoints(getPoints() - points);
	}

	public void givePoints(int points) {
		setPoints(getPoints() + points);
	}

	public void setPoints(int points) {
		VotePointsService service = new VotePointsService();
		VotePoints votePoints = service.get(UUID.fromString(uuid));
		votePoints.setPoints(points);
		new VotePointsService().save(votePoints);
	}

}

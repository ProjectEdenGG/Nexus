package me.pugabyte.bncore.models.vote;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Voter {
	@NonNull
	private String uuid;
	private int votes;
	private List<Vote> activeVotes = new ArrayList<>();
	private int points;

	public void takePoints(int points) {
		setPoints(this.points - points);
	}

	public void setPoints(int points) {
		this.points = points;
		new VoteService().setPoints(uuid, points);
	}

}

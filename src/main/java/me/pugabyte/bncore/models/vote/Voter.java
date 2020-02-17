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
	public String uuid;
	int votes;
	List<Vote> activeVotes = new ArrayList<>();
}

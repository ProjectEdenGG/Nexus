package me.pugabyte.bncore.features.votes;

import com.vexsoftware.votifier.model.VotifierEvent;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.Vote;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class VotesListener implements Listener {
	Map<Integer, Integer> extras = new HashMap<>();

	public VotesListener() {
		extras.put(1500, 50);
		extras.put(500, 25);
		extras.put(200, 15);
		extras.put(100, 10);
		extras.put(50, 5);

		BNCore.registerListener(this);
	}

	private int randomNumberBelow(int max) {
		return new Random().nextInt(max - 1) + 1;
	}

	private int extraVotePoints() {
		for (Map.Entry<Integer, Integer> pair : extras.entrySet()) {
			int random = randomNumberBelow(pair.getKey());
			if (random == 1) {
				return pair.getValue();
			}
		}
		return 0;
	}

	@EventHandler
	public void onVote(VotifierEvent event) {
		String service = event.getVote().getServiceName();
		int extra = extraVotePoints();
		LocalDateTime timestamp = BNCore.timestamp(event.getVote().getLocalTimestamp());

		Vote vote = new Vote(service, extra, timestamp);
		Votes.save(vote);
	}
}

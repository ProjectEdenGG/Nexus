package me.pugabyte.bncore.features.votes;

import com.vexsoftware.votifier.model.VotifierEvent;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.votes.vps.VPS;
import me.pugabyte.bncore.models.vote.Vote;
import me.pugabyte.bncore.models.vote.VoteService;
import me.pugabyte.bncore.models.vote.VoteSite;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static me.pugabyte.bncore.utils.Utils.epochSecond;
import static me.pugabyte.bncore.utils.Utils.randomInt;

public class Votes implements Listener {

	public Votes() {
		BNCore.registerListener(this);
		scheduler();
		new VPS();
	}

	private void scheduler() {
		Tasks.repeatAsync(Time.SECOND.x(5), Time.SECOND.x(10), () -> {
			VoteService service = new VoteService();
			service.getActiveVotes().forEach(vote -> {
				LocalDateTime expiration = vote.getTimestamp().plusHours(vote.getSite().getExpirationHours());
				if (!expiration.isBefore(LocalDateTime.now())) return;

				BNCore.log("Vote expired: " + vote);

				vote.setExpired(true);
				service.save(vote);
			});
		});
	}

	@EventHandler
	public void onVote(VotifierEvent event) {
		OfflinePlayer player = Utils.getPlayer(event.getVote().getUsername());
		VoteSite site = VoteSite.getFromId(event.getVote().getServiceName());
		int extra = extraVotePoints();
		LocalDateTime timestamp = epochSecond(event.getVote().getTimeStamp());

		Vote vote = new Vote(player.getUniqueId().toString(), site, extra, timestamp);
		new VoteService().save(vote);

		// Announce if PMC
		// Give points
	}

	Map<Integer, Integer> extras = new HashMap<Integer, Integer>() {{
		put(1500, 50);
		put(500, 25);
		put(200, 15);
		put(100, 10);
		put(50, 5);
	}};

	private int extraVotePoints() {
		for (Map.Entry<Integer, Integer> pair : extras.entrySet())
			if (randomInt(pair.getKey()) == 1)
				return pair.getValue();
		return 0;
	}

}

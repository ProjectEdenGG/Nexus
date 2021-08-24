package gg.projecteden.nexus.models.safecracker;

import gg.projecteden.annotations.Disabled;
import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(SafeCrackerPlayer.class)
@Disabled
public class SafeCrackerPlayerService extends MongoService<SafeCrackerPlayer> {
	private final static Map<UUID, SafeCrackerPlayer> cache = new ConcurrentHashMap<>();

	public Map<UUID, SafeCrackerPlayer> getCache() {
		return cache;
	}

	public LinkedHashMap<UUID, Integer> getScores(SafeCrackerEvent.SafeCrackerGame game) {
		LinkedHashMap<UUID, Integer> scores = new LinkedHashMap<>();
		List<SafeCrackerPlayer> temp = new ArrayList<>();
		List<SafeCrackerPlayer> players = getAll();
		players.forEach(player -> {
			if (player.getGames().containsKey(game.getName()))
				if (player.getGames().get(game.getName()).isFinished())
					temp.add(player);
		});
		temp.sort(Comparator.comparing(player -> player.getGames().get(game.getName()).getScore()));
		temp.forEach(player -> scores.put(player.getUuid(), player.getGames().get(game.getName()).getScore()));
		return scores;
	}


}

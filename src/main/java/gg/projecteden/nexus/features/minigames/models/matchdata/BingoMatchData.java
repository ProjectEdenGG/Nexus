package gg.projecteden.nexus.features.minigames.models.matchdata;

import gg.projecteden.nexus.features.minigames.mechanics.Bingo;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.Challenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IChallengeProgress;
import gg.projecteden.nexus.utils.LocationUtils;
import lombok.Data;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@MatchDataFor(Bingo.class)
public class BingoMatchData extends MatchData {
	private static final int SIZE = 5;
	private Map<UUID, BingoMinigamerData> players = new HashMap<>();
	private Challenge[][] challenges = new Challenge[SIZE][SIZE];

	@Data
	public static class BingoMinigamerData {
		private final Minigamer minigamer;
		private Location spawnpoint;
		private Boolean[][] completed = new Boolean[SIZE][SIZE];
		private Set<BingoLine> bingos = new HashSet<>();
		private final Map<Class<? extends IChallengeProgress>, IChallengeProgress> progress = new HashMap<>();

		public BingoMinigamerData(Minigamer minigamer) {
			this.minigamer = minigamer;
			for (Boolean[] array : completed)
				Arrays.fill(array, false);
		}

		public boolean hasBingo(BingoLine line) {
			return bingos.contains(line);
		}

		public void setCompleted(Challenge challenge, boolean completed) {
			final Challenge[][] challenges = minigamer.getMatch().<BingoMatchData>getMatchData().getChallenges();
			for (int i = 0; i < SIZE; i++)
				for (int j = 0; j < SIZE; j++)
					if (challenge == challenges[i][j])
						this.completed[i][j] = completed;
		}
	}

	public BingoMatchData(Match match) {
		super(match);

		determineChallenges();
	}

	public void determineChallenges() {
		Iterator<Challenge> iterator = Challenge.shuffle().iterator();

		for (int i = 0; i < SIZE; i++)
			for (int j = 0; j < SIZE; j++)
				challenges[i][j] = iterator.next();
	}

	public BingoMinigamerData getData(Minigamer minigamer) {
		return players.computeIfAbsent(minigamer.getUniqueId(), $ -> new BingoMinigamerData(minigamer));
	}

	public Set<Challenge> getAllChallenges() {
		return new LinkedHashSet<>() {{
			for (Challenge[] array : challenges)
				this.addAll(Set.of(array));
		}};
	}

	public Set<Challenge> getAllChallenges(Class<? extends IChallenge> challengeType) {
		return new LinkedHashSet<>() {{
			for (Challenge challenge : getAllChallenges())
				if (challengeType.isAssignableFrom(challenge.getChallenge().getClass()))
					add(challenge);
		}};
	}

	public void setSpawnpoint(Minigamer minigamer, Location location) {
		location = LocationUtils.getCenteredLocation(location.clone().add(0, 2, 0));
		getData(minigamer).setSpawnpoint(location);
	}

	public <T extends IChallengeProgress> T getProgress(Minigamer minigamer, Challenge challenge) {
		return (T) getProgress(minigamer, challenge.getChallenge().getProgressClass());
	}

	public <T extends IChallengeProgress> T getProgress(Minigamer minigamer, Class<? extends T> clazz) {
		return (T) getData(minigamer).getProgress().computeIfAbsent(clazz, $ -> {
			try {
				return clazz.getDeclaredConstructor(Minigamer.class).newInstance(minigamer);
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		});
	}

	public void check(Minigamer minigamer) {
		final Boolean[][] completed = getData(minigamer).getCompleted();

		BingoMatchData matchData = minigamer.getMatch().getMatchData();

		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col++) {
				Challenge challenge = challenges[row][col];

				if (completed[row][col])
					continue;

				final IChallengeProgress progress = matchData.getProgress(minigamer, challenge);
				if (progress.isCompleted(challenge))
					completed[row][col] = true;

				var lines = Arrays.asList(BingoLine.ofRow(row), BingoLine.ofCol(col),
					BingoLine.DIAGONAL_1, BingoLine.DIAGONAL_2); // Always check these because im lazy

				lines.forEach(line -> {
					if (line.check(minigamer)) {
						match.broadcast("&e" + minigamer.getNickname() + " &3got a &6Bingo&3!");
						minigamer.scored(1);
					}
				});
			}
		}
	}

	private enum BingoLineDirection {
		ROW {
			@Override
			public boolean check(Boolean[][] completed, BingoLine line) {
				for (int col = 0; col < SIZE; col++)
					if (!completed[line.index()][col])
						return false;
				return true;
			}
		},
		COLUMN {
			@Override
			public boolean check(Boolean[][] completed, BingoLine line) {
				for (int row = 0; row < SIZE; row++)
					if (!completed[row][line.index()])
						return false;
				return true;
			}
		},
		DIAGONAL { // Again, im lazy
			@Override
			public boolean check(Boolean[][] completed, BingoLine line) {
				if (line == BingoLine.DIAGONAL_1) {
					for (int i = 0; i < SIZE; i++)
						if (!completed[i][i])
							return false;

					return true;
				}

				if (line == BingoLine.DIAGONAL_2) {
					int row = SIZE;
					for (int column = 0; column < SIZE; column++)
						if (!completed[--row][column])
							return false;

					return true;
				}

				return false;
			}
		},
		;

		abstract boolean check(Boolean[][] completed, BingoLine line);
	}

	private enum BingoLine {
		ROW_0,
		ROW_1,
		ROW_2,
		ROW_3,
		ROW_4,
		COLUMN_0,
		COLUMN_1,
		COLUMN_2,
		COLUMN_3,
		COLUMN_4,
		DIAGONAL_1,
		DIAGONAL_2,
		;

		public static BingoLine ofRow(int row) {
			return valueOf("ROW_" + row);
		}

		public static BingoLine ofCol(int row) {
			return valueOf("COLUMN_" + row);
		}

		public BingoLineDirection direction() {
			return BingoLineDirection.valueOf(name().split("_")[0]);
		}

		public int index() {
			return Integer.parseInt(name().split("_")[1]);
		}

		public boolean check(Minigamer minigamer) {
			final BingoMinigamerData data = minigamer.getMatch().<BingoMatchData>getMatchData().getData(minigamer);

			if (data.hasBingo(this))
				return false;

			if (!direction().check(data.getCompleted(), this))
				return false;

			data.getBingos().add(this);
			return true;
		}
	}

}

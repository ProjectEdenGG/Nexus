package gg.projecteden.nexus.features.tournament.models;

import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

@SuppressWarnings("deprecation")
@Data
@NoArgsConstructor
public class Tournament {
	String id;
	Location origin; // top left corner
	List<UUID> playerUUIDs = new ArrayList<>();
	Map<Integer, List<Match>> rounds = new LinkedHashMap<>();
	private int currentRound = 1;
	private int maxRounds;
	private int round1Byes;
	private final Queue<Match> matchQueue = new LinkedList<>();
	private boolean started = false;
	private boolean finished = false;
	private UUID championUUID;

	private static final double X_SPACE = 3;  // distance between rounds
	private static final double Y_SPACE = 1; // base vertical spacing

	public Tournament(@NonNull String id, Location location) {
		this.id = id;
		this.origin = location;
	}

	public void delete() {
		rounds.forEach((roundNumber, matches) -> {
			matches.forEach((match) -> {
				match.getTextDisplay().remove();
			});
		});

		matchQueue.clear();
		rounds.clear();
		playerUUIDs.clear();
		origin = null;
		id = null;
		started = false;
		finished = false;
	}

	public void start() {
		this.started = true;
		Collections.shuffle(playerUUIDs);
		spawnBracket();
	}

	public void addPlayer(OfflinePlayer player) {
		if (this.started)
			throw new InvalidInputException("Tournament already started");

		playerUUIDs.add(player.getUniqueId());
	}

	public void removePlayer(OfflinePlayer player) {
		if (this.started)
			throw new InvalidInputException("Tournament already started");

		playerUUIDs.remove(player.getUniqueId());
	}

	//

	private void spawnBracket() {
		rounds.forEach((roundNumber, matches) -> {
			matches.forEach((match) -> {
				match.getTextDisplay().remove();
			});
		});
		rounds.clear();
		matchQueue.clear();

		maxRounds = getTotalRounds();
		round1Byes = byesInRound1();
		buildTextDisplays();

		List<UUID> players = new ArrayList<>(playerUUIDs);
		Collections.shuffle(players);

		// Setup & queue round 1
		List<Match> matches = rounds.get(1);
		matches.forEach((match) -> {
			match.addPlayer(players.removeFirst());
			if (!match.isBye()) {
				match.addPlayer(players.removeFirst());
				matchQueue.add(match); // queue matches
			}

			// update text displays
			TextDisplay display = match.getTextDisplay();
			if (display != null)
				display.text(Component.text(ChatColor.translateAlternateColorCodes('&', match.getDisplay())));
		});
	}

	public void playNextMatch(OfflinePlayer winner) {
		UUID winnerUUID = winner.getUniqueId();
		Match match = matchQueue.peek();
		if (match == null)
			throw new InvalidInputException("No more matches to play");

		if (!match.isPlaying(winnerUUID)) {
			throw new InvalidInputException("Winner can either be " + Nickname.of(match.getPlayer1UUID()) + " or " + Nickname.of(match.getPlayer2UUID()));
		}

		matchQueue.poll();
		match.setWinnerUUID(winnerUUID);

		// Update current match display
		TextDisplay display = match.getTextDisplay();
		if (display != null)
			display.text(Component.text(ChatColor.translateAlternateColorCodes('&', match.getDisplay())));

		// Progress to next round?
		if (matchQueue.isEmpty()) {
			if (currentRound < maxRounds)
				setupNextRound();
			else {
				championUUID = winnerUUID;
				finished = true;
			}
		}
	}

	private void setupNextRound() {
		int lastRound = currentRound++;

		List<UUID> remainingPlayers = new ArrayList<>();
		List<Match> previousMatches = rounds.get(lastRound);
		previousMatches.forEach(_match -> {
			// update bye matches
			if (_match.isBye()) {
				_match.setWinnerUUID(_match.getPlayer1UUID());

				TextDisplay _display = _match.getTextDisplay();
				if (_display != null)
					_display.text(Component.text(ChatColor.translateAlternateColorCodes('&', _match.getDisplay())));
			}

			remainingPlayers.add(_match.getWinnerUUID());
		});

		List<Match> matches = rounds.get(currentRound);
		matches.forEach((_match) -> {
			_match.addPlayer(remainingPlayers.removeFirst());
			_match.addPlayer(remainingPlayers.removeFirst());
			matchQueue.add(_match); // queue matches

			// update text displays
			TextDisplay _display = _match.getTextDisplay();
			if (_display != null)
				_display.text(Component.text(ChatColor.translateAlternateColorCodes('&', _match.getDisplay())));
		});
	}

	private void buildTextDisplays() {
		Map<Integer, Integer> roundMatchMap = buildRoundMatches();

		roundMatchMap.forEach((roundNumber, totalMatches) -> {
			List<Match> matches = new ArrayList<>();
			for (int matchIndex = 0; matchIndex < totalMatches; matchIndex++) {
				Match match = new Match();
				TextDisplay display = getTextDisplay(roundNumber, matchIndex, match);
				match.setTextDisplay(display);
				matches.add(match);
			}

			// byes
			if (roundNumber == 1) {
				int matchCount = totalMatches;
				for (int i = 0; i < round1Byes; i++) {
					Match match = new Match();
					match.setBye(true);
					TextDisplay display = getTextDisplay(roundNumber, matchCount++, match);
					match.setTextDisplay(display);
					matches.add(match);
				}
			}

			rounds.put(roundNumber, matches);
		});
	}

	private @NotNull TextDisplay getTextDisplay(Integer roundNumber, int matchIndex, Match match) {
		double xOffset = (roundNumber - 1) * X_SPACE;
		double yOffset = getYOffset(roundNumber, matchIndex); // TODO: rounds 2+ need to be between feeder matches
		Location loc = origin.clone().add(xOffset, yOffset, 0);
		TextDisplay display = (TextDisplay) origin.getWorld().spawnEntity(loc, EntityType.TEXT_DISPLAY);
		display.text(Component.text(ChatColor.translateAlternateColorCodes('&', match.getDisplay())));
		display.setBillboard(Billboard.FIXED);
		display.setBackgroundColor(Color.fromARGB(128, 0, 0, 0));
		return display;
	}

	private double getYOffset(int roundNumber, int matchIndex) {
		if (roundNumber == 1)
			return -matchIndex * Y_SPACE;

		List<Match> previousRound = rounds.get(roundNumber - 1);

		int feederMatch1Index = matchIndex * 2;
		int feederMatch2Index = feederMatch1Index + 1;

		// Clamp indices in case of odd number of matches
		feederMatch1Index = Math.min(feederMatch1Index, previousRound.size() - 1);
		feederMatch2Index = Math.min(feederMatch2Index, previousRound.size() - 1);

		double y1 = previousRound.get(feederMatch1Index).getTextDisplay() != null
			? previousRound.get(feederMatch1Index).getTextDisplay().getLocation().getY() - origin.getY()
			: -(feederMatch1Index * Y_SPACE);

		double y2 = previousRound.get(feederMatch2Index).getTextDisplay() != null
			? previousRound.get(feederMatch2Index).getTextDisplay().getLocation().getY() - origin.getY()
			: -(feederMatch2Index * Y_SPACE);

		return (y1 + y2) / 2.0;
	}

	private int getTotalRounds() {
		int maxPlayers = playerUUIDs.size();
		if (maxPlayers < 2)
			throw new IllegalArgumentException("There must be more than 2 players in the tournament");

		// Find next power of 2
		int nextPowerOf2 = 1;
		while (nextPowerOf2 < maxPlayers) {
			nextPowerOf2 *= 2;
		}

		// log2(nextPowerOf2)
		return (int) (Math.log(nextPowerOf2) / Math.log(2));
	}

	private Map<Integer, Integer> buildRoundMatches() {
		int maxPlayers = playerUUIDs.size();
		Map<Integer, Integer> rounds = new LinkedHashMap<>();

		for (int round = 1; round <= maxRounds; round++) {
			int matches;
			if (round == 1 && round1Byes > 0) {
				// Some players skip to round 2
				matches = (maxPlayers - round1Byes) / 2;
			} else {
				matches = maxPlayers / 2;
			}

			rounds.put(round, matches);

			// Winners advance
			maxPlayers = matches + (round == 1 ? round1Byes : 0);
		}

		return rounds;
	}

	private int byesInRound1() {
		int maxPlayers = playerUUIDs.size();
		if (maxPlayers < 2) return 0;

		int nextPowerOf2 = 1;
		while (nextPowerOf2 < maxPlayers) {
			nextPowerOf2 *= 2;
		}

		return nextPowerOf2 - maxPlayers;
	}

	//

	public Match getNextMatch() {
		return matchQueue.peek();
	}

	public void printBracket(Player player) {
		PlayerUtils.send(player, "&3&lTournament " + id);
		if (rounds.isEmpty()) {
			PlayerUtils.send(player, " &3- Players:");
			for (UUID uuid : playerUUIDs) {
				PlayerUtils.send(player, "  &3- &e" + Nickname.of(uuid));
			}

			return;
		}

		for (Map.Entry<Integer, List<Match>> entry : rounds.entrySet()) {
			PlayerUtils.send(player, " &3- Round &e" + entry.getKey() + "&3:");
			for (Match match : entry.getValue())
				PlayerUtils.send(player, " &3 - " + match.getDisplay());
			PlayerUtils.send(player, "");
		}

		if (getChampionUUID() != null)
			PlayerUtils.send(player, " &3Champion: &a" + Nickname.of(getChampionUUID()));
	}

	@Getter
	@NoArgsConstructor
	public static class Match {
		private UUID player1UUID;
		private UUID player2UUID;
		@Setter
		private UUID winnerUUID;
		@Setter
		private TextDisplay textDisplay;
		@Setter
		private boolean bye = false;

		public void addPlayer(UUID uuid) {
			if (uuid == null)
				return;

			if ((player1UUID != null && player1UUID.equals(uuid)) || (player2UUID != null && player2UUID.equals(uuid)))
				throw new InvalidInputException(Nickname.of(uuid) + " is already in the match");

			if (player1UUID == null)
				this.player1UUID = uuid;
			else if (player2UUID == null)
				this.player2UUID = uuid;
			else
				throw new InvalidInputException("Cannot add " + Nickname.of(uuid) + " to match, match is full");
		}

		public boolean isPlaying(UUID uuid) {
			return uuid.equals(player1UUID) || uuid.equals(player2UUID);
		}

		public boolean isFinished() {
			return this.winnerUUID != null;
		}

		public String getDisplay() {
			if (player1UUID == null && player2UUID == null)
				return "&7TBD";

			String name1 = (player1UUID != null) ? Nickname.of(player1UUID) : "&cnull";
			String name2 = (player2UUID != null) ? Nickname.of(player2UUID) : "&cnull";

			if (this.isFinished()) {
				if (bye)
					return "&a" + name1;

				if (winnerUUID.equals(player1UUID))
					return "&a" + name1 + " &3vs &e" + name2;
				else
					return "&e" + name1 + " &3vs &a" + name2;
			}

			if (bye)
				return "&e" + name1;

			return "&e" + name1 + " &3vs &e" + name2;
		}
	}
}

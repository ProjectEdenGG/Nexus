package gg.projecteden.nexus.features.tournament.models;

import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.TextDisplay.TextAlignment;
import tech.blastmc.holograms.api.HologramsAPI;
import tech.blastmc.holograms.api.models.Hologram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
	Location origin;
	private int winningPoint = 1;
	private double X_SPACE = 2;
	private double Y_SPACE = 0.75;
	List<UUID> playerUUIDs = new ArrayList<>();
	private Match finalMatch = null;
	private Map<Integer, List<Match>> roundsMap = new HashMap<>();
	private final Queue<Match> matchQueue = new LinkedList<>();
	private int currentRound = 1;
	private Match currentMatch;
	private int totalRounds;
	private int round1Byes;
	private boolean started = false;
	private boolean finished = false;
	private UUID championUUID;

	public Tournament(@NonNull String id, Location location) {
		this.id = id;
		this.origin = location;
	}

	public Location getOrigin() {
		return origin.clone();
	}

	public void delete() {
		if (finalMatch != null)
			finalMatch.delete();
	}

	public void start() {
		if (playerUUIDs.size() < 2)
			throw new IllegalArgumentException("There must be more than 2 players in the tournament");

		this.started = true;
		Collections.shuffle(playerUUIDs);
		spawnBracket();
		currentMatch = matchQueue.poll();
	}

	public void end() {
		this.finished = true;
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
		if (finalMatch != null)
			finalMatch.delete();
		matchQueue.clear();

		totalRounds = _getTotalRounds();
		round1Byes = _byesInRound1();
		finalMatch = buildMatchTree(totalRounds);
		collectMatchesByRound(finalMatch, totalRounds, roundsMap);
		spawnTextDisplays(finalMatch, totalRounds, 0);

		List<UUID> players = new ArrayList<>(playerUUIDs);
		Collections.shuffle(players);

		List<Match> matches = roundsMap.get(1);
		int remainingByes = round1Byes;
		boolean assignBye = true; // toggle to alternate
		Iterator<UUID> playerIter = players.iterator();
		for (Match match : matches) {
			if (remainingByes > 0 && assignBye) {
				match.setBye(true);

				if (playerIter.hasNext())
					match.addPlayer(playerIter.next());

				remainingByes--;
				assignBye = false;
			} else {
				// Normal match
				if (playerIter.hasNext()) match.addPlayer(playerIter.next());
				if (playerIter.hasNext()) match.addPlayer(playerIter.next());

				matchQueue.add(match);
				assignBye = true;
			}

			match.updateHologram();
		}
	}

	private void setupNextRound() {
		int lastRound = currentRound++;

		List<UUID> remainingPlayers = new ArrayList<>();
		List<Match> previousMatches = roundsMap.get(lastRound);
		previousMatches.forEach(_match -> {
			// update bye matches
			if (_match.isBye()) {
				_match.setWinnerUUID(_match.getPlayer1UUID());

				_match.updateHologram();
			}

			remainingPlayers.add(_match.getWinnerUUID());
		});

		List<Match> matches = roundsMap.get(currentRound);
		matches.forEach((_match) -> {
			_match.addPlayer(remainingPlayers.removeFirst());
			_match.addPlayer(remainingPlayers.removeFirst());
			matchQueue.add(_match); // queue matches

			_match.updateHologram();
		});
	}

	private Match buildMatchTree(int roundNumber) {
		Match match = new Match();

		if (roundNumber > 1) {
			int previousRound = roundNumber - 1;
			match.setLeft(buildMatchTree(previousRound));
			match.setRight(buildMatchTree(previousRound));
		}

		return match;
	}

	private void collectMatchesByRound(Match match, int roundNumber, Map<Integer, List<Match>> result) {
		if (match == null) return;

		// Add match to this round
		result.computeIfAbsent(roundNumber, k -> new ArrayList<>()).add(match);

		// Recurse to children
		collectMatchesByRound(match.getLeft(), roundNumber - 1, result);
		collectMatchesByRound(match.getRight(), roundNumber - 1, result);
	}


	private double spawnTextDisplays(Match match, Integer roundNumber, int matchIndex) {
		if (match == null)
			return 0;

		double xOffset = (totalRounds - roundNumber) * X_SPACE;
		double yOffset;

		// base case: leaves (round 1)
		if (roundNumber == 1) {
			yOffset = -matchIndex * Y_SPACE;
			spawnTextDisplay(match, xOffset, yOffset);
			return yOffset;
		}

		// recurse to children first
		double leftY = spawnTextDisplays(match.getLeft(), roundNumber - 1, matchIndex * 2);
		double rightY = spawnTextDisplays(match.getRight(), roundNumber - 1, matchIndex * 2 + 1);

		// parent Y = middle of its children
		yOffset = (leftY + rightY) / 2.0;
		spawnTextDisplay(match, xOffset, yOffset);

		return yOffset;
	}

	private void spawnTextDisplay(Match match, double xOffset, double yOffset) {
		Location loc = getOrigin().add(xOffset, yOffset, 0);
		match.spawnHologram(loc);
	}

	public void addPoint(UUID uuid) {
		if (!currentMatch.isPlaying(uuid))
			throw new InvalidInputException(Nickname.of(uuid) + " is not in the match");

		currentMatch.addPointToPlayer(uuid, winningPoint);

		currentMatch.updateHologram();

		if (currentMatch.isFinished()) {
			UUID lastWinner = currentMatch.getWinnerUUID();
			currentMatch = matchQueue.peek();
			if (currentMatch == null) {
				if (currentRound < totalRounds)
					setupNextRound();
				else {
					championUUID = lastWinner;
					finished = true;
				}
			}

			currentMatch = matchQueue.poll();
		}
	}

	//

	private int _getTotalRounds() {
		// Find next power of 2
		int nextPowerOf2 = 1;
		while (nextPowerOf2 < playerUUIDs.size()) {
			nextPowerOf2 *= 2;
		}

		// log2(nextPowerOf2)
		return (int) (Math.log(nextPowerOf2) / Math.log(2));
	}

	private int _byesInRound1() {
		int maxPlayers = playerUUIDs.size();
		if (maxPlayers < 2) return 0;

		int nextPowerOf2 = 1;
		while (nextPowerOf2 < maxPlayers) {
			nextPowerOf2 *= 2;
		}

		return nextPowerOf2 - maxPlayers;
	}

	@Getter
	@NoArgsConstructor
	public static class Match {
		@Setter
		Match left;
		@Setter
		Match right;
		private UUID player1UUID;
		private int player1Score = 0;
		private UUID player2UUID;
		private int player2Score = 0;
		@Setter
		private UUID winnerUUID;
		Hologram hologram;
		@Setter
		private boolean bye = false;

		public Match(Match left, Match right) {
			this.left = left;
			this.right = right;
		}

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

		public void addPointToPlayer(UUID uuid, int winningPoint) {
			if (uuid.equals(player1UUID)) {
				if (++player1Score >= winningPoint)
					winnerUUID = player1UUID;
			} else {
				if (++player2Score >= winningPoint)
					winnerUUID = player2UUID;
			}
		}

		public boolean isPlaying(UUID uuid) {
			return uuid.equals(player1UUID) || uuid.equals(player2UUID);
		}

		public boolean isFinished() {
			return this.winnerUUID != null;
		}

		public void updateHologram() {
			if (hologram == null)
				return;

			hologram.setLines(new ArrayList<>(getDisplay()));
			hologram.update();
		}

		public List<String> getDisplay() {
			if (player1UUID == null && player2UUID == null)
				return List.of("&7TBD");

			String name1 = (player1UUID != null) ? Nickname.of(player1UUID) : "&cnull";
			String name2 = (player2UUID != null) ? Nickname.of(player2UUID) : "&cnull";

			if (bye) {
				if (isFinished())
					return List.of("&a" + name1);
				else
					return List.of("&e" + name1);
			}

			// TODO: SET MAX LENGTH TO ALL NAMES IN SAME ROUND
			int player1Length = StringUtils.DefaultFontInfo.getLength(name1);
			int player2Length = StringUtils.DefaultFontInfo.getLength(name2);
			int spaceLength = StringUtils.DefaultFontInfo.getLength(" ");

			if (player1Length < player2Length)
				name1 += " ".repeat((player2Length - player1Length) / spaceLength);
			else if (player2Length < player1Length)
				name2 += " ".repeat((player1Length - player2Length) / spaceLength);

			if (this.isFinished()) {
				if (winnerUUID.equals(player1UUID))
					return List.of(
						"&a" + name1 + " &3(&6" + player1Score + "&3)",
						"&e" + name2 + " &3(&6" + player2Score + "&3)"
					);
				else
					return List.of(
						"&e" + name1 + " &3(&6" + player1Score + "&3)",
						"&a" + name2 + " &3(&6" + player2Score + "&3)"
					);
			}

			return List.of(
				"&e" + name1 + " &3(&6" + player1Score + "&3)",
				"&e" + name2 + " &3(&6" + player2Score + "&3)"
			);
		}

		public void delete() {
			this.hologram.remove();
			if (this.left != null)
				this.left.delete();
			if (this.right != null)
				this.right.delete();
		}

		public void spawnHologram(Location location) {
			if (bye)
				return;

			hologram = HologramsAPI.builder()
				.lines(new ArrayList<>(getDisplay()))
				.billboard(Billboard.FIXED)
				.background(128, 0, 0, 0)
				.alignment(TextAlignment.CENTER)
				.location(location)
				.build();

			hologram.spawn();
		}
	}
}

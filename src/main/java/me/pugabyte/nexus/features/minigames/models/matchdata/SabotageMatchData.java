package me.pugabyte.nexus.features.minigames.models.matchdata;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.Data;
import me.lexikiq.HasUniqueId;
import me.pugabyte.nexus.features.menus.sabotage.VotingScreen;
import me.pugabyte.nexus.features.minigames.managers.PlayerManager;
import me.pugabyte.nexus.features.minigames.mechanics.Sabotage;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.MatchData;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.annotations.MatchDataFor;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.sabotage.MinigamerVoteEvent;
import me.pugabyte.nexus.features.minigames.models.sabotage.SabotageColor;
import me.pugabyte.nexus.features.minigames.models.sabotage.SabotageTeam;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.Tasks;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Data
@MatchDataFor(Sabotage.class)
public class SabotageMatchData extends MatchData {
	public SabotageMatchData(Match match) {
		super(match);
	}

	private final Map<UUID, UUID> votes = new HashMap<>();
	private final BiMap<UUID, SabotageColor> playerColors = HashBiMap.create();
	private LocalDateTime meetingStarted = LocalDateTime.of(1970, 1, 1, 0, 0);
	private int meetingTaskID = -1;
	private VotingScreen votingScreen;

	public @NotNull SabotageColor getColor(HasUniqueId player) {
		playerColors.computeIfAbsent(player.getUniqueId(), $ -> RandomUtils.randomElement(Arrays.stream(SabotageColor.values()).filter(color -> !playerColors.containsValue(color)).collect(Collectors.toList())));
		return playerColors.get(player.getUniqueId());
	}

	public @Nullable SabotageColor getColorNoCompute(HasUniqueId player) {
		return playerColors.get(player.getUniqueId());
	}

	public boolean hasVoted(HasUniqueId player) {
		return votes.containsKey(player.getUniqueId());
	}

	public @Nullable Minigamer getVote(HasUniqueId player) {
		if (!votes.containsKey(player.getUniqueId()))
			return null;
		return PlayerManager.get(votes.get(player.getUniqueId()));
	}

	public @NotNull Set<Minigamer> getVotesFor(HasUniqueId player) {
		UUID uuid = player == null ? null : player.getUniqueId();
		return votes.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), uuid)).map(entry -> PlayerManager.get(entry.getKey())).collect(Collectors.toSet());
	}

	public boolean vote(HasUniqueId voter, HasUniqueId target) {
		if (votes.containsKey(voter.getUniqueId()) || LocalDateTime.now().isBefore(meetingStarted.plusSeconds(Sabotage.VOTING_DELAY)))
			return false;
		if (new MinigamerVoteEvent(PlayerManager.get(voter), PlayerManager.get(target), votingScreen).callEvent()) {
			votes.put(voter.getUniqueId(), target == null ? null : target.getUniqueId());
			if (match.getAliveMinigamers().size() == votes.size())
				endMeeting();
			return true;
		}
		return false;
	}

	public void clearVotes() {
		votes.clear();
	}

	public boolean isMeetingActive() {
		return meetingTaskID != -1;
	}

	public void startMeeting(Minigamer origin) {
		// TODO: teleport players high above the world + give levitation?
		meetingStarted = LocalDateTime.now();
		votingScreen = new VotingScreen(origin);
		meetingTaskID = Tasks.repeat(0, 1, () -> match.getMinigamers().stream().filter(minigamer -> minigamer.getPlayer().getOpenInventory().getType() == InventoryType.CRAFTING).forEach(minigamer -> votingScreen.open(minigamer.getPlayer())));

		// TODO: fix this unhiding spectators
		// TODO: teleport to a black box instead and let players type in chat (give an item to re-open voting menu) and respawn on meeting end
		match.getMinigamers().forEach(Minigamer::respawn);
	}

	public void endMeeting() {
		match.getTasks().cancel(meetingTaskID);
		meetingTaskID = -1;
		// TODO: display vote results
		match.getMinigamers().forEach(minigamer -> minigamer.getPlayer().closeInventory(InventoryCloseEvent.Reason.PLUGIN));
		Minigamer ejected = null;
		int votes = getVotesFor(null).size();
		boolean tie = false;
		for (Minigamer minigamer : match.getAliveMinigamers()) {
			int mVotes = getVotesFor(minigamer).size();
			if (mVotes > votes) {
				ejected = minigamer;
				votes = mVotes;
				tie = false;
			} else if (mVotes == votes) {
				ejected = null;
				tie = true;
			}
		}

		SabotageTeam team = SabotageTeam.of(ejected);
		if (team == SabotageTeam.JESTER) {
			ejected.scored();
			match.end();
			return;
		}

		String ejectedName;
		if (ejected != null) {
			new MinigamerDeathEvent(ejected).callEvent();
			if (match.isEnded())
				return;
			ejectedName = ejected.getNickname();
		} else
			ejectedName = "Nobody";

		String display = ejectedName + " was ejected.";
		if (ejected == null)
			display += " (" + (tie ? "Tied" : "Skipped") + ")";

		// TODO: true animation
		match.showTitle(Title.title(Component.empty(), new JsonBuilder(display).build(), Title.Times.of(fade, Duration.ofSeconds(7), fade)));
		match.playSound(Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_SPLASH_HIGH_SPEED, Sound.Source.PLAYER, 1.0F, 1.0F));
		clearVotes();
		votingScreen = null;

		// TODO: fix "Team scored" message (said nobody won, should say player names for jester/impostors or "Crewmates")
	}

	private static final Duration fade = Duration.ofSeconds(1).dividedBy(2);
}

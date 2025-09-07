package gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer;

import gg.projecteden.api.interfaces.Named;
import gg.projecteden.api.interfaces.Nicknamed;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchStatistics;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchBeginEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEndEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import gg.projecteden.nexus.features.minigames.models.mechanics.Mechanic;
import gg.projecteden.nexus.framework.interfaces.Colored;
import gg.projecteden.nexus.models.perkowner.PerkOwner;
import gg.projecteden.nexus.models.perkowner.PerkOwnerService;
import gg.projecteden.nexus.utils.AdventureUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class MultiplayerMechanic extends Mechanic {

	@Override
	public void onDeath(@NotNull MinigamerDeathEvent event) {
		onDeath(event.getMinigamer());
		super.onDeath(event);
	}

	public void onDeath(@NotNull Minigamer victim) {
		if (victim.getLives() != 0) {
			victim.died();
			if (victim.getLives() == 0) {
				victim.setAlive(false);
				victim.stopTimeTracking();
				if (victim.getMatch().getArena().getSpectateLocation() == null)
					victim.quit();
				else if (!victim.getMatch().isEnded()) {
					victim.setSpectating(true);
					victim.toSpectate();
				}
			} else if (!victim.getMatch().isEnded()) {
				victim.respawn();
			}
		} else if (!victim.getMatch().isEnded()) {
			victim.respawn();
		}
	}

	@Override
	public void processJoin(@NotNull Minigamer minigamer) {
		Match match = minigamer.getMatch();
		if (match.isStarted()) {
			balance(minigamer);
			match.teleportIn(minigamer);
		} else {
			match.getArena().getLobby().join(minigamer);
		}
	}

	@Override
	public void onBegin(@NotNull MatchBeginEvent event) {
		super.onBegin(event);

		Match match = event.getMatch();
		Arena arena = match.getArena();

		if (arena.getTurnTime() > 0)
			nextTurn(match);
	}

	public boolean showTurnTimerInChat() {
		return true;
	}

	abstract public void nextTurn(@NotNull Match match);

	public int getMultiplier(@NotNull Match match, @NotNull Minigamer minigamer) {
		int maxScore = Utils.getMax(match.getMinigamers(), Minigamer::getContributionScore).getInteger();
		if (minigamer.getContributionScore() <= 0)
			return 0;
		return maxScore - minigamer.getContributionScore() + 1;
	}

	public final void giveRewards(@NotNull Match match) {
		PerkOwnerService service = new PerkOwnerService();

		match.getMinigamers().forEach(minigamer -> {
			PerkOwner perkOwner = service.get(minigamer.getOnlinePlayer());
			// max of 1 in 2 chance of getting a reward (dependant on score)
			int multiplier = getMultiplier(match, minigamer);
			if (multiplier == 0)
				return;
			if (RandomUtils.randomInt(1, 2 * multiplier) == 1)
				perkOwner.reward(match.getArena());
		});
	}

	@Override
	public void onEnd(@NotNull MatchEndEvent event) {
		super.onEnd(event);
		giveRewards(event.getMatch());
	}

	// moved this here because it's used by a couple "team" games (juggernaut) and copy-pasting was kinda icky
	protected final void announceTeamlessWinners(@NotNull Match match) {
		Minigames.broadcast(getTeamlessWinnersString(match));
	}

	@Nullable
	protected JsonBuilder getTeamlessWinnersString(@NotNull Match match) {
		Arena arena = match.getArena();
		Map<Minigamer, Integer> scores = new HashMap<>();

		match.getAliveMinigamers().forEach(minigamer -> scores.put(minigamer, minigamer.getScore()));
		if (scores.isEmpty()) return null;
		int winningScore = getWinningScore(scores.values());
		List<Minigamer> winners = getWinners(winningScore, scores);

		String announcement = null;
		boolean displayScoreboard = true;
		if (winningScore == 0 && winners.size() != 1) {
			announcement = "No players scored in ";
			displayScoreboard = false;
		} else if (match.getAliveMinigamers().size() == winners.size() && match.getAliveMinigamers().size() > 1) {
			announcement = "All players tied in ";
			displayScoreboard = false;
		}

		JsonBuilder builder = new JsonBuilder();
		builder.next(announcement == null ? getWinnersComponent(winners) : Component.text(announcement));
		builder.next(arena);
		if (winningScore != 0) {
			if (displayScoreboard)
				builder.next(getFinalScoresTeamless(scores));
			else
				builder.next(" (" + winningScore + ")");
		}

		return builder;
	}

	protected @NotNull List<Minigamer> getWinners(int winningScore, @NotNull Map<Minigamer, Integer> scores) {
		List<Minigamer> winners = new ArrayList<>();

		for (Minigamer minigamer : scores.keySet()) {
			if (scores.get(minigamer).equals(winningScore)) {
				winners.add(minigamer);
			}
		}

		winners.forEach(winner -> winner.getMatch().getMatchStatistics().award(MatchStatistics.WINS, winner));

		return winners;
	}

	protected @NotNull TextComponent getWinnersComponent(@NotNull List<? extends Named> winners) {
		TextComponent component = AdventureUtils.commaJoinText(winners.stream()
				.map(named -> {
					String nickname = named.getName();
					if (named instanceof Nicknamed nicknamed)
						nickname = nicknamed.getNickname();

					if (named instanceof Colored color)
						return Component.text(nickname, color.getBukkitColor());
					else
						return Component.text(nickname, NamedTextColor.YELLOW);
				})
				.collect(Collectors.toList()));

		if (winners.size() == 1)
			return component.append(Component.text(" has won "));
		else
			return component.append(Component.text(" have tied on "));
	}

	protected @NotNull final TextComponent getWinnersComponent(@NotNull Named... components) {
		return getWinnersComponent(Arrays.asList(components));
	}

	private @NonNull TextComponent getFinalScoresTeamless(Map<Minigamer, Integer> scores) {
		JsonBuilder json = new JsonBuilder(" [Scoreboard]");
		List<String> lines = new ArrayList<>();

		for (Minigamer minigamer : scores.keySet()) {
			lines.add("&3" + minigamer.getNickname() + ": &e" + scores.get(minigamer));
		}

		return json.hover(lines).build();
	}

	@NonNull
	protected TextComponent getFinalScoresTeams(Map<Team, Integer> scores, List<Team> winningTeams, Match match) {
		JsonBuilder json = new JsonBuilder(" [Scoreboard]");
		List<String> lines = new ArrayList<>();

		int count = 1;
		for (Team team : winningTeams) {
			count++;
			lines.addAll(getTeamScores(scores, team, match));

			if (count < winningTeams.size())
				lines.add("");
		}

		lines.add("");

		count = 0;
		for (Team team : scores.keySet()) {
			count++;
			if (winningTeams.contains(team))
				continue;

			lines.addAll(getTeamScores(scores, team, match));

			if (count < scores.keySet().size())
				lines.add("");
		}

		return json.hover(lines).build();
	}

	protected List<String> getTeamScores(Map<Team, Integer> scores, Team team, Match match) {
		List<String> lines = new ArrayList<>();

		lines.add(team.getChatColor() + "&l" + team.getName() + " Team&3: &e" + scores.get(team));
		for (Minigamer minigamer : team.getAliveMinigamers(match)) {
			lines.add("&3- " + team.getChatColor() + minigamer.getNickname() + "&3: &e" + minigamer.getScore());
		}

		return lines;
	}

}

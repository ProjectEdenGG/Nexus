package gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer;

import gg.projecteden.api.interfaces.Named;
import gg.projecteden.api.interfaces.Nicknamed;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
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
		super.onDeath(event);
		onDeath(event.getMinigamer());
	}

	public void onDeath(@NotNull Minigamer victim) {
		if (victim.getLives() != 0) {
			victim.died();
			if (victim.getLives() == 0) {
				victim.setAlive(false);
				if (victim.getMatch().getArena().getSpectateLocation() == null)
					victim.quit();
				else if (!victim.getMatch().isEnded())
					victim.toSpectate();
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

	public boolean shuffleTurnList() {
		return false;
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
		if (scores.size() == 0) return null;
		int winningScore = getWinningScore(scores.values());
		List<Minigamer> winners = getWinners(winningScore, scores);

		String announcement = null;
		if (winningScore == 0 && winners.size() != 1)
			announcement = "No players scored in ";
		else if (match.getAliveMinigamers().size() == winners.size() && match.getAliveMinigamers().size() > 1)
			announcement = "All players tied in ";

		JsonBuilder builder = new JsonBuilder();
		builder.next(announcement == null ? getWinnersComponent(winners) : Component.text(announcement));
		builder.next(arena);
		if (winningScore != 0)
			builder.next(" (" + winningScore + ")");
		return builder;
	}

	protected @NotNull List<Minigamer> getWinners(int winningScore, @NotNull Map<Minigamer, Integer> scores) {
		List<Minigamer> winners = new ArrayList<>();

		for (Minigamer minigamer : scores.keySet()) {
			if (scores.get(minigamer).equals(winningScore)) {
				winners.add(minigamer);
			}
		}

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

}

package gg.projecteden.nexus.features.menus.sabotage;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.minigames.managers.PlayerManager;
import gg.projecteden.nexus.features.minigames.mechanics.Sabotage;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.matchdata.SabotageMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.SabotageColor;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.SabotageTeam;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@RequiredArgsConstructor
@Title("&cWho Is The Impostor?")
public class VotingScreen extends AbstractVoteScreen {
	private final @NotNull Minigamer reporter;
	private final @Nullable SabotageColor body;

	@Override
	public void init() {
		Minigamer voter = PlayerManager.get(player);
		Match match = voter.getMatch();
		SabotageMatchData matchData = match.getMatchData();

		AtomicInteger taskId = new AtomicInteger(-1);
		taskId.set(match.getTasks().repeat(0, 2, () -> {
			if (!matchData.isMeetingActive())
				match.getTasks().cancel(taskId.get());

			if (matchData.waitingToVote()) {
				setClock("Voting starts", matchData.votingStartsIn());
			} else {
				int votingEndsIn = 1 + (int) Duration.between(LocalDateTime.now(), matchData.getMeetingStarted().plusSeconds(Sabotage.MEETING_LENGTH)).getSeconds();
				setClock("Voting ends", votingEndsIn);
			}

			contents.set(0, 2, ClickableItem.of(new ItemBuilder(Material.BARRIER).name("&eSkip Vote").build(), $ -> matchData.vote(voter, null)));
			if (!reporter.getUniqueId().equals(voter.getUniqueId()))
				contents.set(0, 4, votingItem(voter, reporter, matchData));
			contents.set(0, 6, votingItem(voter, voter, matchData));

			int row = 1;
			int col = 0;
			List<Minigamer> minigamers = match.getAllMinigamers();
			minigamers.sort(Comparator.comparing(Minigamer::isAlive).reversed());
			for (Minigamer target : minigamers) {
				if (target.equals(voter) || target.equals(reporter))
					continue;
				contents.set(row, col, votingItem(voter, target, matchData));
				col += 1;
				if (col == 9) {
					col = 0;
					row += 1;
				}
			}
		}));
	}

	private ClickableItem votingItem(Minigamer voter, Minigamer target, SabotageMatchData matchData) {
		SabotageColor targetColor = matchData.getColor(target);
		ItemBuilder builder = headItemOf(target, targetColor);
		List<ComponentLike> components = new ArrayList<>();
		components.add(SabotageTeam.render(voter, target));
		components.add(new JsonBuilder(target.isAlive() ? "&fAlive" : "&cDead"));
		if (target.getUniqueId().equals(reporter.getUniqueId()))
			if (body == null)
				components.add(new JsonBuilder("&eReporter"));
			else
				components.add(new JsonBuilder("Reporting ", NamedTextColor.YELLOW).next(body).next("'s body"));
		if (matchData.hasVoted(target))
			components.add(new JsonBuilder("&cVoted"));
		return ClickableItem.of(builder.componentLore(components).build(), $ -> matchData.vote(voter, target));
	}

}

package gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.menus;

import gg.projecteden.api.common.utils.MathUtils;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.annotations.Uncloseable;
import gg.projecteden.nexus.features.minigames.mechanics.Sabotage;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.matchdata.SabotageMatchData;
import gg.projecteden.nexus.utils.AdventureUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Uncloseable
@Title("&3Voting Results")
@Getter
@NoArgsConstructor
public class ResultsScreen extends AbstractVoteScreen {

	private ClickableItem getResultHead(ItemBuilder item, VoteWrapper wrapper) {
		item.amount(wrapper.getVoteCount());
		item.lore("", "&6&l&nVoters");
		item.lore(wrapper.getVotes().stream().map(minigamer -> AdventureUtils.asLegacyText(getColoredName(minigamer))).sorted().collect(Collectors.toList()));
		return ClickableItem.empty(item.build());
	}

	@Override
	public void init() {
		SabotageMatchData matchData = Minigamer.of(player).getMatch().getMatchData();

		List<VoteWrapper> voteWrappers = new ArrayList<>(); // this was gonna be a sorted dict but that is a nightmare to do for values apparently
		matchData.getMatch().getAliveMinigamers().forEach(minigamer -> voteWrappers.add(new VoteWrapper(minigamer, matchData.getVotesFor(minigamer).stream().map(Minigamer::of).collect(Collectors.toSet()))));
		Collections.sort(voteWrappers);
		for (VoteWrapper voteWrapper : voteWrappers) {
			int voteCount = voteWrapper.getVoteCount();
			if (voteCount == 0)
				break;

			contents.add(getResultHead(headItemOf(voteWrapper.getTarget(), matchData), voteWrapper));
		}
		VoteWrapper skippers = new VoteWrapper(null, matchData.getVotesFor(null));
		if (skippers.getVoteCount() > 0)
			contents.add(getResultHead(new ItemBuilder(Material.BARRIER).name("&eSkipped"), skippers));

		AtomicInteger taskId = new AtomicInteger(-1);
		taskId.set(matchData.getMatch().getTasks().repeat(0, TimeUtils.TickTime.SECOND, () -> {
			int sec = 1 + (int) Duration.between(LocalDateTime.now(), matchData.getMeetingEnded().plusSeconds(Sabotage.POST_MEETING_DELAY)).getSeconds();
			setClock("Game resumes", sec);
			if (sec == 1)
				matchData.getMatch().getTasks().cancel(taskId.get());
		}));
	}

	@RequiredArgsConstructor
	@EqualsAndHashCode
	@Getter
	private static class VoteWrapper implements Comparable<VoteWrapper> {
		@EqualsAndHashCode.Include
		private final @Nullable Minigamer target;
		private final @NotNull Set<Minigamer> votes;

		public int getVoteCount() {
			return votes.size();
		}

		@Override
		public int compareTo(VoteWrapper other) {
			int val = MathUtils.clamp(other.getVoteCount() - getVoteCount(), -1, 1);
			if (val != 0)
				return val;
			Validate.notNull(target, "wrappers of null minigamers (skips) cannot be compared [self]");
			Validate.notNull(other.target, "wrappers of null minigamers (skips) cannot be compared [target]");
			return target.getNickname().compareTo(other.target.getNickname());
		}

	}

}

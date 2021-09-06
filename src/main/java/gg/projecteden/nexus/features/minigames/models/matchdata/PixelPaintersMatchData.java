package gg.projecteden.nexus.features.minigames.models.matchdata;

import com.fastasyncworldedit.core.math.MutableBlockVector3;
import com.sk89q.worldedit.regions.Region;
import gg.projecteden.nexus.features.minigames.mechanics.PixelPainters;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.features.minigames.models.arenas.PixelPaintersArena;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.utils.Utils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

@Data
@MatchDataFor(PixelPainters.class)
public class PixelPaintersMatchData extends MatchData {
	private List<Minigamer> checked = new ArrayList<>();
	private List<Integer> designsPlayed = new ArrayList<>();
	@Accessors(fluent = true)
	private boolean canCheck;
	private boolean roundOver;
	private int currentRound;
	private int designCount;
	private Region designRegion;
	private long roundStart;
	private int totalFinished;
	private int roundCountdownId;
	private int timeLeft;
	private boolean animateLobby;
	private int lobbyDesign;
	private int animateLobbyId;

	public PixelPaintersMatchData(Match match) {
		super(match);
	}

	public Region getRandomLobbyDesign() {
		lobbyDesign = getRandomDesign(test -> lobbyDesign != test);
		return getDesignRegion(lobbyDesign);
	}

	public Region getRandomGameDesign() {
		final int design = getRandomDesign(test -> !designsPlayed.contains(test));
		designsPlayed.add(design);
		designRegion = getDesignRegion(design);
		return designRegion;
	}

	private Region getDesignRegion(int design) {
		final PixelPaintersArena arena = match.getArena();
		final Region designsRegion = arena.getDesignsRegion();

		int y = designsRegion.getMinimumPoint().getBlockY() - 1 + design;

		final MutableBlockVector3 min = designsRegion.getMinimumPoint().mutY(y);
		final MutableBlockVector3 max = designsRegion.getMaximumPoint().mutY(y);

		return worldedit().region(min, max);
	}

	public int getRandomDesign(Predicate<Integer> predicate) {
		AtomicInteger design = new AtomicInteger();
		Utils.attempt(designCount, () -> {
			design.set(RandomUtils.randomInt(1, designCount));
			return predicate.test(design.get());
		});

		return design.get();
	}
}

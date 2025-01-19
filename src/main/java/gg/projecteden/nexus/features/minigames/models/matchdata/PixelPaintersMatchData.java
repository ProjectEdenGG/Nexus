package gg.projecteden.nexus.features.minigames.models.matchdata;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import gg.projecteden.api.common.utils.Utils;
import gg.projecteden.nexus.features.minigames.mechanics.PixelPainters;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.features.minigames.models.arenas.PixelPaintersArena;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.RandomUtils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
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
	private LocalDateTime roundStart;
	private int totalFinished;
	private int roundCountdownId;
	private long timeLeft;
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

		World worldEditWorld = worldedit().worldEditWorld;
		int y = designsRegion.getMinimumPoint().y() - 1 + design;

		BlockVector3 min = designsRegion.getMinimumPoint().withY(y);
		BlockVector3 max = designsRegion.getMaximumPoint().withY(y);

		Dev.GRIFFIN.debug(new JsonBuilder("Design Min 1: " + min).command("//pos1 " + min.x() + "," + min.y() + "," + min.z()));
		Dev.GRIFFIN.debug(new JsonBuilder("Design Max 1: " + max).command("//pos2 " + max.x() + "," + max.y() + "," + max.z()));

		Region region = new CuboidRegion(worldEditWorld, min, max);

		min = region.getMinimumPoint();
		max = region.getMaximumPoint();

		Dev.GRIFFIN.debug(new JsonBuilder("Design Min 2: " + min).command("//pos1 " + min.x() + "," + min.y() + "," + min.z()));
		Dev.GRIFFIN.debug(new JsonBuilder("Design Max 2: " + max).command("//pos2 " + max.x() + "," + max.y() + "," + max.z()));

		return region;
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

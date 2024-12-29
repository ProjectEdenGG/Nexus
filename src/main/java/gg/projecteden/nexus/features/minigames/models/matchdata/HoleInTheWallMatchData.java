package gg.projecteden.nexus.features.minigames.models.matchdata;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.mechanics.HoleInTheWall;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.features.minigames.models.exceptions.MinigameException;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.LocationUtils.CardinalDirection;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Data;
import lombok.NonNull;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@MatchDataFor(HoleInTheWall.class)
public class HoleInTheWallMatchData extends MatchData {
	private final List<Wall> walls = new ArrayList<>();
	private final List<Track> tracks = new ArrayList<>();
	private boolean isEnding;

	private static final List<DyeColor> colors = Arrays.asList(DyeColor.ORANGE, DyeColor.MAGENTA, DyeColor.LIGHT_BLUE,
			DyeColor.YELLOW, DyeColor.LIME, DyeColor.PINK, DyeColor.CYAN, DyeColor.PURPLE, DyeColor.BLUE);

	public HoleInTheWallMatchData(Match match) {
		super(match);
		int seconds = match.getArena().getSeconds();
		long walls = (seconds / (HoleInTheWall.SKIP_BUTTON_COOLDOWN_IN_TICKS / 20)) + 1;

		for (int i = 0; i < walls; i++) {
			int empty = HoleInTheWall.BASE_EMPTY_BLOCKS + (i / HoleInTheWall.EXTRA_EMPTY_BLOCK_EVERY_X_WALLS);
			this.walls.add(new Wall(empty));
		}
	}

	public Track getTrack(Minigamer minigamer) {
		return tracks.stream().filter(track -> minigamer.equals(track.getMinigamer())).findFirst().orElse(null);
	}

	public Track getTrack(ProtectedRegion region) {
		return tracks.stream().filter(track -> Arena.getRegionNumber(region) == track.getId()).findFirst().orElse(null);
	}

	@Data
	public class Track {
		@NonNull
		private ProtectedRegion region;
		@NonNull
		private Location designHangerLocation;
		private BlockFace direction;
		private Minigamer minigamer;
		private int wallIndex = -1;
		private AtomicInteger trackIndex = new AtomicInteger(0);
		private int length;
		private BlockFace drawDesignDirection;
		private Block topLeft;
		private Block topCenter;
		private Block answerTopLeft;
		private int taskId = -1;
		private boolean validating;
		private Material wallMaterial;
		private Material buildMaterial = ColorType.of(RandomUtils.randomElement(colors)).getStainedGlass();

		public Track(@NonNull ProtectedRegion region, @NonNull Location designHangerLocation) {
			this.region = region;
			this.designHangerLocation = designHangerLocation;

			Optional<Block> cobblestone = BlockUtils.getBlocksInRadius(designHangerLocation.clone().add(0, 1, 0), 3, 1, 3).stream()
					.filter(block -> block.getType() == Material.COBBLESTONE)
					.findFirst();

			if (!cobblestone.isPresent())
				throw new MinigameException("Could not determine direction track, make sure a cobblestone block is behind the start location");

			direction = BlockUtils.getDirection(designHangerLocation.getBlock(), cobblestone.get()).getOppositeFace();

			int over = (Wall.LENGTH - 1) / 2;
			topCenter = designHangerLocation.clone().add(0, -1, 0).getBlock();
			BlockFace toTopLeftDirection = CardinalDirection.of(direction).turnRight().toBlockFace();
			drawDesignDirection = toTopLeftDirection.getOppositeFace();
			topLeft = topCenter.getRelative(toTopLeftDirection, over);

			int move = 0;
			while (length == 0) {
				Block relative = designHangerLocation.getBlock().getRelative(direction, ++move);
				if (MaterialTag.STAINED_GLASS.isTagged(relative.getType()))
					length = move;

				if (length > 100)
					throw new MinigameException("Could not determine length of track, make sure the answer board is surrounded with glass");
			}

			answerTopLeft = topLeft.getRelative(direction, length);
		}

		public int getId() {
			return Arena.getRegionNumber(region);
		}

		public void start() {
			Minigames.debug("[%s] %s start".formatted(getClass().getSimpleName(), minigamer.getNickname()));
			reset();
			nextWall();
		}

		public void end() {
			Minigames.debug("[%s] %s end".formatted(getClass().getSimpleName(), minigamer.getNickname()));
			cancelTask();
			reset();
			validate();
		}

		public void nextWall() {
			Minigames.debug("[%s] %s nextWall 1".formatted(getClass().getSimpleName(), minigamer.getNickname()));
			if (validating)
				return;

			Minigames.debug("[%s] %s nextWall 2".formatted(getClass().getSimpleName(), minigamer.getNickname()));
			if (taskId > 0)
				return;

			Minigames.debug("[%s] %s nextWall 3".formatted(getClass().getSimpleName(), minigamer.getNickname()));
			if (isEnding)
				return;

			Minigames.debug("[%s] %s nextWall 4".formatted(getClass().getSimpleName(), minigamer.getNickname()));
			final PlayerInventory inventory = minigamer.getOnlinePlayer().getInventory();
			inventory.clear();
			inventory.addItem(new ItemStack(buildMaterial, 64));

			clearAnswer();
			Wall wall = walls.get(++wallIndex);

			int delay = HoleInTheWall.BASE_TICK_SPEED - (wallIndex / HoleInTheWall.TICK_DECREASE_EVERY_X_WALLS);
			trackIndex.set(0);
			wallMaterial = ColorType.of(RandomUtils.randomElement(colors)).getConcrete();

			taskId = getMatch().getTasks().repeat(0, delay, () -> {
				Minigames.debug("[%s] %s nextWall task 1".formatted(getClass().getSimpleName(), minigamer.getNickname()));
				clearWall(trackIndex.getAndIncrement());

				if (this.designHangerLocation.getBlock().getRelative(direction, trackIndex.get()).getType() != Material.AIR) {
					Minigames.debug("[%s] %s nextWall task 2".formatted(getClass().getSimpleName(), minigamer.getNickname()));
					cancelTask();
					validate();
					return;
				}

				Minigames.debug("[%s] %s nextWall task 3".formatted(getClass().getSimpleName(), minigamer.getNickname()));
				designHangerLocation.getBlock().getRelative(direction, trackIndex.get()).setType(Material.COBBLESTONE_WALL);

				Block topLeft = this.topLeft.getRelative(direction, trackIndex.get());

				for (boolean[] row : wall.getBlocks()) {
					int columnCount = 0;
					for (boolean empty : row) {
						Block relative = topLeft.getRelative(drawDesignDirection, columnCount++);
						if (empty)
							relative.setType(Material.AIR);
						else
							relative.setType(wallMaterial);
					}
					topLeft = topLeft.getLocation().add(0, -1, 0).getBlock();
				}
			});
		}

		public void clearAnswer() {
			clearWall(length);
		}

		public void clearWall(int trackIndex) {
			Block hangar = designHangerLocation.getBlock().getRelative(direction, trackIndex);
			if (hangar.getType() == Material.COBBLESTONE_WALL)
				hangar.setType(Material.AIR);

			Location start = topLeft.getRelative(direction, trackIndex).getLocation();

			for (int i = 0; i < Wall.HEIGHT; i++) {
				for (int j = 0; j < Wall.LENGTH; j++)
					start.getBlock().getRelative(drawDesignDirection, j).setType(Material.AIR);
				start.add(0, -1, 0);
			}
		}

		public void reset() {
			cancelTask();
			for (int i = 0; i <= length; i++)
				clearWall(i);
		}

		public void cancel() {
			cancelTask();
			clearWall(trackIndex.get());
		}

		public void cancelTask() {
			getMatch().getTasks().cancel(taskId);
			taskId = -1;
		}

		public void skip() {
			if (validating) return;
			cancel();
			validate();
		}

		public void validate() {
			if (validating) return;
			validating = true;
			Wall wall = walls.get(wallIndex);

			Player player = minigamer.getOnlinePlayer();
			Location topLeft = answerTopLeft.getLocation().clone();

			int total = 0;
			int correct = 0;

			for (boolean[] row : wall.getBlocks()) {
				int columnCount = 0;
				for (boolean empty : row) {
					Block relative = topLeft.getBlock().getRelative(drawDesignDirection, columnCount++);
					if (empty) {
						++total;
						if (MaterialTag.STAINED_GLASS.isTagged(relative.getType())) {
							relative.setType(Material.GREEN_STAINED_GLASS);
							++correct;
						} else
							relative.setType(Material.RED_STAINED_GLASS);
					} else
						if (relative.getType() == Material.AIR)
							relative.setType(wallMaterial);
						else {
							--correct;
							relative.setType(Material.RED_CONCRETE);
						}
				}
				topLeft.add(0, -1, 0);
			}

			boolean allCorrect = total == correct;
			int points = (correct - 1) + (allCorrect ? 1 : 0);
			if (points > 0) {
				minigamer.scored(points);
				minigamer.tell("You earned &e" + points + " " + StringUtils.plural("point", points));

				ActionBarUtils.sendActionBar(player, allCorrect ? "&a&lCorrect" : "&c&lIncorrect");
			}

			if (allCorrect)
				player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 2F);
			else
				player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1F, 1F);

			if (!isEnding)
				getMatch().getTasks().wait(10, () -> {
					validating = false;
					nextWall();
				});
		}

	}

	@Data
	public static class Wall {
		private static final int HEIGHT = 4;
		private static final int LENGTH = 7;
		private static final int TOTAL = HEIGHT * LENGTH;
		private final boolean[][] blocks = new boolean[HEIGHT][LENGTH];

		public Wall(int empty) {
			if (empty > TOTAL)
				throw new MinigameException("Cannot generate wall with more than " + TOTAL + " empty blocks");

			for (boolean[] block : blocks)
				Arrays.fill(block, false);

			for (int i = 0; i < empty; i++) {
				int x = RandomUtils.randomInt(0, HEIGHT - 1);
				int y = RandomUtils.randomInt(0, LENGTH - 1);
				if (blocks[x][y])
					++empty;
				else
					blocks[x][y] = true;
			}
		}
	}
}

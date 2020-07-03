package me.pugabyte.bncore.features.minigames.models.matchdata;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Data;
import lombok.NonNull;
import me.pugabyte.bncore.features.minigames.mechanics.HoleInTheWall;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.MatchData;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.annotations.MatchDataFor;
import me.pugabyte.bncore.features.minigames.models.exceptions.MinigameException;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.Utils.CardinalDirection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static me.pugabyte.bncore.utils.Utils.randomInt;

@Data
@MatchDataFor(HoleInTheWall.class)
public class HoleInTheWallMatchData extends MatchData {
	public List<Wall> walls = new ArrayList<>();
	public List<Track> tracks = new ArrayList<>();

	public HoleInTheWallMatchData(Match match) {
		super(match);
		int seconds = match.getArena().getSeconds();
		int walls = (seconds / (HoleInTheWall.SKIP_BUTTON_COOLDOWN_IN_TICKS / 20)) + 1;

		for (int i = 0; i < walls; i++) {
			int empty = HoleInTheWall.BASE_EMPTY_BLOCKS + (i / HoleInTheWall.EXTRA_EMPTY_BLOCK_EVERY_X_WALLS);
			this.walls.add(new Wall(empty));
		}
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
		private int length;
		private BlockFace drawDesignDirection;
		private Block topLeft;
		private Block answerTopLeft;

		public Track(@NonNull ProtectedRegion region, @NonNull Location designHangerLocation) {
			this.region = region;
			this.designHangerLocation = designHangerLocation;

			Optional<Block> cobblestone = Utils.getBlocksInRadius(designHangerLocation.clone().add(0, 1, 0), 3, 1, 3).stream()
					.filter(block -> block.getType() == Material.COBBLESTONE)
					.findFirst();

			if (!cobblestone.isPresent())
				throw new MinigameException("Could not determine direction track, make sure a cobblestone block is behind the start location");

			direction = Utils.getDirection(designHangerLocation.getBlock(), cobblestone.get()).getOppositeFace();

			int over = (Wall.LENGTH - 1) / 2;
			Location centerOfTopRow = designHangerLocation.clone().add(0, -1, 0);
			BlockFace toTopLeftDirection = CardinalDirection.of(direction).turnRight().toBlockFace();
			drawDesignDirection = toTopLeftDirection.getOppositeFace();
			topLeft = centerOfTopRow.getBlock().getRelative(toTopLeftDirection, over);

			int move = 0;
			while (length == 0) {
				Block relative = designHangerLocation.getBlock().getRelative(direction, ++move);
				if (relative.getType() != Material.AIR)
					length = move;

				if (length > 100)
					throw new MinigameException("Could not determine length of track, make sure the answer board is surrounded with glass");
			}

			answerTopLeft = topLeft.getRelative(direction, length);
		}

		public int getId() {
			return Arena.getRegionTypeId(region);
		}

		public void start() {
			nextWall();
		}

		public void nextWall() {
			clearAnswer();
			Wall wall = walls.get(++wallIndex);

			for (boolean[] row : wall.getBlocks()) {
				int columnCount = 0;
				for (boolean empty : row) {
					Block relative = topLeft.getRelative(drawDesignDirection, columnCount++);
					if (empty)
						relative.setType(Material.AIR);
					else
						relative.setType(Material.BLACK_CONCRETE);
				}
				topLeft = topLeft.getLocation().add(0, -1, 0).getBlock();
			}
		}

		public void clearAnswer() {
			Location start = answerTopLeft.getLocation().clone();
			for (int i = 0; i < Wall.HEIGHT; i++) {
				for (int j = 0; j < Wall.LENGTH; j++)
					start.getBlock().getRelative(drawDesignDirection, j).setType(Material.AIR);
				start.add(0, -1, 0);
			}
		}

		public void validate() {
			Wall wall = walls.get(wallIndex);
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
				int x = randomInt(0, HEIGHT - 1);
				int y = randomInt(0, LENGTH - 1);
				if (blocks[x][y])
					++empty;
				else
					blocks[x][y] = true;
			}
		}
	}
}
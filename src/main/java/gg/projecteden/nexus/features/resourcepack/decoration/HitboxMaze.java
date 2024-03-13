package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.api.common.utils.RandomUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.particles.effects.DotEffect;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
class HitboxMaze {
	private static final List<BlockFace> hitboxDirections = Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN);
	private Player player;
	private Block origin;
	private int radius;
	private Block block;
	private BlockFace blockFace;
	private List<BlockFace> directionsLeft = new ArrayList<>(hitboxDirections);
	private LinkedList<Location> tempPath = new LinkedList<>();
	private LinkedList<Location> resultPath = new LinkedList<>();
	private HashMap<Location, List<BlockFace>> pathDirs = new HashMap<>();
	private Set<Location> tried = new HashSet<>();
	private int tries = 0;
	private int wait = 0;

	public HitboxMaze(Player player, Block clicked, int radius) {
		this.player = player;
		this.origin = clicked;
		this.radius = radius;
		this.block = this.origin;
		addToPath(this.origin.getLocation(), this.directionsLeft);

		this.tried.add(this.origin.getLocation());
	}

	public void incTries() {
		++this.tries;
	}

	public void addToPath(Location location, List<BlockFace> directionsLeft) {
		tempPath.add(location);
		resultPath.add(location);
		pathDirs.put(location, directionsLeft);
	}

	public void goBack() {
		Location back = tempPath.removeLast();
		setBlock(back.getBlock());
		setDirectionsLeft(pathDirs.get(back));
	}

	public void nextDirection() {
		this.setBlockFace(RandomUtils.randomElement(this.getDirectionsLeft()));
		this.getDirectionsLeft().remove(this.getBlockFace());
	}

	public void debugDot(Location location, Color color) {
		this.wait += 2;

		Tasks.wait(this.wait, () ->
				DecorationLang.debug(player, () ->
						DecorationLang.getDebuggerUUIDs().forEach(uuid -> {
							OfflinePlayer player = PlayerUtils.getPlayer(uuid);
							if (player.isOnline())
								DotEffect.debug(player.getPlayer(), location.clone().toCenterLocation(), color, TickTime.SECOND.x(1));
						})
				)
		);
	}

	public void resetDirections() {
		List<BlockFace> directions = new ArrayList<>(hitboxDirections);
		directions.remove(getBlockFace().getOppositeFace());
		setDirectionsLeft(directions);
	}
}

package me.pugabyte.bncore.features.minigames.models.matchdata;

import lombok.Data;
import lombok.NonNull;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;

@Data
public class Flag {
	// Spawn data
	@NonNull
	private Location spawnLocation;
	@NonNull
	private Material material;
	@NonNull
	private BlockData blockData;
	@NonNull
	private String[] lines;
	@NonNull
	private Team team;

	// Carrier data
	private Minigamer carrier;

	// Dropped data
	private Location currentLocation;
	private BlockState blockBelowState;
	private int taskId = -1;

	public void respawn() {
		if (currentLocation != null) {
			currentLocation.getBlock().setType(Material.AIR);
			currentLocation = null;
		}

		Block block = spawnLocation.getBlock();

		block.setType(material);
		block.setBlockData(blockData);

		Sign sign = (Sign) block.getState();

		for (int line = 0; line <= 3; line++) {
			sign.setLine(line, lines[line]);
		}

		sign.update();
	}

	public void despawn() {
		if (currentLocation != null) {
			currentLocation.getBlock().setType(Material.AIR);
			currentLocation = null;
		} else {
			spawnLocation.getBlock().setType(Material.AIR);
		}
	}

	public void drop(Location location) {
		currentLocation = getSuitableLocation(location);

		// TODO: Make sure flag is on a solid block and in an empty space

		Block block = currentLocation.getBlock();
		block.setType(Material.OAK_SIGN);

		Sign sign = (Sign) block.getState();

		for (int line = 0; line <= 3; line++) {
			sign.setLine(line, lines[line]);
		}

		sign.update();
		Match match = carrier.getMatch();
		taskId = carrier.getMatch().getTasks().wait(Time.SECOND.x(60), () -> {
			respawn();
			match.broadcast(team.getColoredName() + "&3's flag has returned");
		});
	}

	public Location getSuitableLocation(Location originalLocation) {
		Location location = originalLocation.clone();
		if (location.getBlock().isLiquid()) {
			while (location.getBlock().isLiquid()) {
				location.add(0, 1, 0);
			}
			return location;
		}
		while (location.getBlock().getType() != Material.AIR && !location.getBlock().getType().isSolid()) {
			location.add(1, 0, 0);
		}
		Block below = location.clone().subtract(0, 1, 0).getBlock();
		while ((below.getType() == Material.AIR || location.getBlock().getType() != Material.AIR) &&
				ArenaManager.getFromLocation(originalLocation).getRegion().contains(new WorldGuardUtils(Minigames.getWorld()).toBlockVector3(location))) {
			location.subtract(0, 1, 0);
			below = location.clone().subtract(0, 1, 0).getBlock();
		}
		return location;
	}

}

package gg.projecteden.nexus.features.survival.strongholds;

import com.google.gson.Gson;
import gg.projecteden.nexus.features.survival.strongholds.StrongholdFixer.OldStrongholdConfig.OldStrongholdWorld;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static gg.projecteden.nexus.utils.Distance.distance;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static java.util.Comparator.comparing;

/*
	To get all stronghold locations:
		chunkbase.com
		enter seed
		select 1.19.2
		turn off all except stronghold
		open dev tools
		search for <script> "cb3-finder"
		right click link and click "reveal in sources"
		create override
		ctrl+f onPoiDrawn
		if (a == 'stronghold') {
			console.log(d[0] + " " + d[2]);
		}
		zoom out and view all strongholds at least once
		ctrl+a ctrl+c console output
		clean up and permute->unique in sublime
*/

@NoArgsConstructor
public class StrongholdFixer extends Feature implements Listener {
	private static OldStrongholdConfig config;

	@Override
	public void onStart() {
		try {
			config = new Gson().fromJson(FileUtils.readFileToString(getFile()), OldStrongholdConfig.class);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static File getFile() {
		return IOUtils.getPluginFile(getFileName());
	}

	@NotNull
	private static String getFileName() {
		return "old-strongholds.json";
	}

	@Data
	public class OldStrongholdConfig {
		private List<OldStrongholdWorld> worlds;

		@Data
		public class OldStrongholdWorld {
			private long seed;
			private List<Vector2d> strongholds;

			@Data
			public static class Vector2d {
				private int x;
				private int z;

				public Location asLocation(World world) {
					return new Location(world, x, 0, z);
				}
			}
		}
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		if (Vanish.isVanished(player))
			return;

		if (isNullOrAir(event.getItem()))
			return;

		if (event.getItem().getType() != Material.ENDER_EYE)
			return;

		Block clickedBlock = event.getClickedBlock();
		if (Nullables.isNotNullOrAir(clickedBlock) && clickedBlock.getType() == Material.END_PORTAL_FRAME)
			return;

		OldStrongholdWorld handledWorld = null;
		final World world = player.getWorld();
		for (OldStrongholdWorld strongholdWorld : config.getWorlds())
			if (strongholdWorld.getSeed() == world.getSeed()) {
				handledWorld = strongholdWorld;
				break;
			}

		if (handledWorld == null)
			return;

		event.setCancelled(true);

		final List<Location> reachable = handledWorld.getStrongholds().stream()
			.map(vector2d -> vector2d.asLocation(world))
			.filter(location -> world.getWorldBorder().isInside(location))
			.toList();


		final Location nearest = Collections.min(reachable, comparing(stronghold -> distance(player, stronghold).get()));

		if (nearest == null) {
			PlayerUtils.send(player, "&cNo stronghold could be found");
			return;
		}

		world.spawn(player.getLocation(), EnderSignal.class, enderEye -> {
			enderEye.setTargetLocation(nearest);
			enderEye.setDropItem(RandomUtils.chanceOf(80));
		});
	}

}


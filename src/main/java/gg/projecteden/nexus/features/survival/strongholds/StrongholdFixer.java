package gg.projecteden.nexus.features.survival.strongholds;

import com.google.gson.Gson;
import gg.projecteden.nexus.features.survival.strongholds.StrongholdFixer.OldStrongholdConfig.OldStrongholdWorld;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.*;
import lombok.Data;
import lombok.NoArgsConstructor;
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
import java.nio.file.Files;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
			config = new Gson().fromJson(Files.readString(getFile().toPath()), OldStrongholdConfig.class);
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
	public static class OldStrongholdConfig {
		private List<OldStrongholdWorld> worlds;

		@Data
		public static class OldStrongholdWorld {
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
		final World world = player.getWorld();

		if (Vanish.isVanished(player))
			return;

		if (Nullables.isNullOrAir(event.getItem()))
			return;

		if (event.getItem().getType() != Material.ENDER_EYE)
			return;

		Block block = event.getClickedBlock();
		if (Nullables.isNotNullOrAir(block) && block.getType() == Material.END_PORTAL_FRAME)
			return;

		OldStrongholdWorld handledWorld = config.getWorlds().stream()
			.filter(strongholdWorld -> strongholdWorld.getSeed() == world.getSeed())
			.findFirst()
			.orElse(null);

		if (handledWorld == null)
			return;

		event.setCancelled(true);
		event.getItem().subtract();

		final List<Location> reachable = handledWorld.getStrongholds().stream()
			.map(vector2d -> vector2d.asLocation(world))
			.filter(location -> world.getWorldBorder().isInside(location))
			.toList();


		final Location nearest = Collections.min(reachable, Comparator.comparing(stronghold -> Distance.distance(player, stronghold).get()));

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


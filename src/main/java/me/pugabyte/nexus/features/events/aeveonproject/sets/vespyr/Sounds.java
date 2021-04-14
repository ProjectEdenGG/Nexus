package me.pugabyte.nexus.features.events.aeveonproject.sets.vespyr;

import me.pugabyte.nexus.features.events.aeveonproject.sets.APSetType;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Time;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static me.pugabyte.nexus.features.events.aeveonproject.AeveonProject.getWGUtils;

public class Sounds {
	private final List<Material> ignoreMaterials = Arrays.asList(Material.SNOW, Material.SNOW_BLOCK, Material.ICE, Material.PACKED_ICE,
			Material.BLUE_ICE, Material.WHITE_STAINED_GLASS_PANE);
	private static final Sound windSound = Sound.ITEM_ELYTRA_FLYING;

	public Sounds() {

		// Wind sound
		Tasks.repeatAsync(0, Time.SECOND.x(4), () -> {
			if (!APSetType.VESPYR.get().isActive())
				return;

			Tasks.sync(() -> {

				Collection<Player> players = getWGUtils().getPlayersInRegion(APSetType.VESPYR.get().getRegion());
				for (Player player : players) {
					if (player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType().equals(Material.LEATHER_HELMET))
						continue;

					Location loc = player.getLocation();
					if (isInside(player, (byte) 2))
						player.playSound(loc, windSound, SoundCategory.AMBIENT, 3F, 0.5F);
					else
						player.playSound(loc, windSound, SoundCategory.AMBIENT, 3F, 1F);
				}
			});
		});
	}


	private boolean isInside(Player player, byte blocks) {
		return player.getLocation().getBlock().getLightFromSky() < (15 - blocks);
	}
}

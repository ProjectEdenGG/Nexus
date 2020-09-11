package me.pugabyte.bncore.features.holidays.aeveonproject.sets.vespyr;

import me.pugabyte.bncore.features.holidays.aeveonproject.sets.APSetType;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static me.pugabyte.bncore.features.holidays.aeveonproject.AeveonProject.WGUtils;
import static me.pugabyte.bncore.utils.Utils.isNullOrAir;

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

				Collection<Player> players = WGUtils.getPlayersInRegion(APSetType.VESPYR.get().getRegion());
				for (Player player : players) {
					if (player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType().equals(Material.LEATHER_HELMET))
						continue;

					Location loc = player.getLocation();
					if (isInside(player))
						player.playSound(loc, windSound, SoundCategory.AMBIENT, 3F, 0.5F);
					else
						player.playSound(loc, windSound, SoundCategory.AMBIENT, 3F, 1F);
				}
			});
		});
	}


	private boolean isInside(Player player) {
		int count = 0;
		Location loc = player.getLocation();
		int playerY = loc.getBlockY() + 1;
		for (int y = playerY; y <= 255; y++) {
			Material material = loc.getBlock().getRelative(0, y - playerY, 0).getType();

			if (!ignoreMaterials.contains(material) && !isNullOrAir(material))
				++count;

			if (count >= 2)
				return true;
		}
		return false;
	}
}

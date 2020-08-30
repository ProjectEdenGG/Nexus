package me.pugabyte.bncore.features.holidays.aeveonproject.sets.sialia;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.aeveonproject.Regions;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Collection;

import static me.pugabyte.bncore.features.holidays.aeveonproject.AeveonProject.APLoc;
import static me.pugabyte.bncore.features.holidays.aeveonproject.AeveonProject.WGUtils;

public class Sounds implements Listener {
	private static final Location engineLoc = APLoc(-1294, 86, -1056);
	private static final Sound shipSound = Sound.BLOCK_BEACON_AMBIENT;
	private static final Sound engineSound = Sound.ENTITY_MINECART_RIDING;

	public Sounds() {
		BNCore.registerListener(this);

		// Engine Sound
		Tasks.repeatAsync(0, Time.TICK.x(30), () -> {
			if (!Sialia.isActive())
				return;

			Tasks.sync(() -> {
				Collection<Player> players = WGUtils.getPlayersInRegion(Regions.sialia);
				for (Player player : players) {
					if (player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType().equals(Material.LEATHER_HELMET))
						continue;

					player.playSound(engineLoc, engineSound, SoundCategory.AMBIENT, 2.5F, 1F);
				}
			});
		});

		// Ship Sound
		Tasks.repeatAsync(0, Time.SECOND.x(5), () -> {
			if (!Sialia.isActive())
				return;

			Tasks.sync(() -> {
				Collection<Player> players = WGUtils.getPlayersInRegion(Regions.sialia);
				for (Player player : players) {
					if (player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType().equals(Material.LEATHER_HELMET))
						continue;

					player.playSound(engineLoc, shipSound, SoundCategory.AMBIENT, 50F, 1F);
					Tasks.wait(Time.SECOND.x(2), () -> player.playSound(engineLoc, shipSound, SoundCategory.AMBIENT, 50F, 1F));
				}
			});
		});
	}


}

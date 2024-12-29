package gg.projecteden.nexus.features.events.aeveonproject.sets.sialiaCrashing;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.aeveonproject.APUtils;
import gg.projecteden.nexus.features.events.aeveonproject.AeveonProject;
import gg.projecteden.nexus.features.events.aeveonproject.sets.APSetType;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.Collection;

public class Sounds {
	private static final Location engineLoc = APUtils.APLoc(-823, 86, -1062);
	private static final Sound engineSound = Sound.ENTITY_MINECART_RIDING;
	private static final Sound shipSound = Sound.BLOCK_BEACON_AMBIENT;
	private static final Sound warningSound = Sound.ENTITY_ELDER_GUARDIAN_CURSE;

	public Sounds() {

		// Engine Sound
		Tasks.repeatAsync(0, TickTime.TICK.x(30), () -> {
			if (!APSetType.SIALIA_CRASHING.get().isActive())
				return;

			Tasks.sync(() -> {

				Collection<Player> players = AeveonProject.worldguard().getPlayersInRegion(APSetType.SIALIA_CRASHING.get().getRegion());
				for (Player player : players) {
					if (player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType().equals(Material.LEATHER_HELMET))
						continue;

					player.playSound(engineLoc, engineSound, SoundCategory.AMBIENT, 2.5F, 1F);
				}
			});
		});

		// Ship Sound
		Tasks.repeatAsync(0, TickTime.SECOND.x(5), () -> {
			if (!APSetType.SIALIA_CRASHING.get().isActive())
				return;

			Tasks.sync(() -> {
				Collection<Player> players = AeveonProject.worldguard().getPlayersInRegion(APSetType.SIALIA_CRASHING.get().getRegion());
				for (Player player : players) {
					if (player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType().equals(Material.LEATHER_HELMET))
						continue;

					player.playSound(engineLoc, shipSound, SoundCategory.AMBIENT, 50F, 1F);
					Tasks.wait(TickTime.SECOND.x(2), () -> player.playSound(engineLoc, shipSound, SoundCategory.AMBIENT, 50F, 1F));
				}
			});
		});

		// Alarm Sound
		Tasks.repeatAsync(0, TickTime.TICK.x(50), () -> {
			if (!APSetType.SIALIA_CRASHING.get().isActive())
				return;

			Tasks.sync(() -> {
				Collection<Player> players = AeveonProject.worldguard().getPlayersInRegion(APSetType.SIALIA_CRASHING.get().getRegion());
				for (Player player : players) {
					if (player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType().equals(Material.LEATHER_HELMET))
						continue;

					player.playSound(engineLoc, warningSound, SoundCategory.AMBIENT, 0.3F, 0.8F);
				}
			});
		});

		//Vent Sounds
		Tasks.repeatAsync(0, TickTime.TICK.x(5), () -> {
			if (!APSetType.SIALIA_CRASHING.get().isActive())
				return;

			Tasks.sync(() ->
					Particles.pipes.forEach(pipe ->
							AeveonProject.getWorld().playSound(pipe, Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 0.1F, 0.5F)));
		});
	}

}

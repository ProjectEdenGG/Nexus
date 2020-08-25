package me.pugabyte.bncore.features.holidays.aeveonproject.sets.sialiaCrashing;

import com.destroystokyo.paper.ParticleBuilder;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.event.Listener;

import static me.pugabyte.bncore.features.holidays.aeveonproject.AeveonProject.WORLD;

public class Particles implements Listener {

	private final Location gravLift_1 = new Location(WORLD, -823.0, 88.5, -1168.0);
	private final Location nautilisLoc = new Location(WORLD, -832.5, 83.5, -1172.5);
	private final Location portalLoc = new Location(WORLD, -831.5, 82.5, -1174.5);
	private final Location myceliumLoc = new Location(WORLD, -829.5, 82.5, -1176.5);
	private final Location sneeze = new Location(WORLD, -816.0, 82.0, -1164.0);

	public Particles() {
		BNCore.registerListener(this);

		Tasks.repeatAsync(0, Time.TICK.x(2), () -> {
			if (!SialiaCrashing.isActive())
				return;

			new ParticleBuilder(Particle.DOLPHIN).location(gravLift_1).count(10).offset(0.5, 4, 0.5).extra(0.1).spawn();
			new ParticleBuilder(Particle.NAUTILUS).location(nautilisLoc).count(5).offset(0.1, 0.5, 0.1).extra(0.1).spawn();
			new ParticleBuilder(Particle.PORTAL).location(portalLoc).count(5).offset(0.15, 1, 0.15).extra(0.1).spawn();
			new ParticleBuilder(Particle.TOWN_AURA).location(myceliumLoc).count(15).offset(0.15, 0.5, 0.15).extra(0.1).spawn();
			new ParticleBuilder(Particle.SNEEZE).location(sneeze).count(5).offset(0.25, 1, 0.25).extra(0.01).spawn();

		});
	}
}

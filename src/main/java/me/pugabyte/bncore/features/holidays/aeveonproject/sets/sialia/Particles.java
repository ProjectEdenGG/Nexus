package me.pugabyte.bncore.features.holidays.aeveonproject.sets.sialia;

import com.destroystokyo.paper.ParticleBuilder;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.particles.effects.LineEffect;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import static me.pugabyte.bncore.features.holidays.aeveonproject.AeveonProject.WORLD;

public class Particles implements Listener {
	private int laserTaskID = -1;
	private boolean activeLaser = false;
	private Player laserPlayer = null;
	private final Location laserStart = new Location(WORLD, -1300.5, 83.5, -1155.9);
	private final Location laserEnd = new Location(WORLD, -1300.5, 83.25, -1159.5);
	//
	private final Location nautilisLoc = new Location(WORLD, -1303.5, 83.5, -1164.5);
	private final Location portalLoc = new Location(WORLD, -1302.5, 82.5, -1166.5);
	private final Location myceliumLoc = new Location(WORLD, -1300.5, 82.5, -1168.5);
	private final Location gravLift = new Location(WORLD, -1294.0, 88.5, -1160.0);
	private final Location sneeze = new Location(WORLD, -1287.0, 82.0, -1156.0);

	public Particles() {
		BNCore.registerListener(this);

		Tasks.repeatAsync(0, Time.TICK.x(2), () -> {
			if (!Sialia.isActive() || Sialia.nearbyPlayer == null)
				return;

			new ParticleBuilder(Particle.NAUTILUS).location(nautilisLoc).count(5).offset(0.1, 0.5, 0.1).extra(0.1).spawn();
			new ParticleBuilder(Particle.PORTAL).location(portalLoc).count(5).offset(0.15, 1, 0.15).extra(0.1).spawn();
			new ParticleBuilder(Particle.TOWN_AURA).location(myceliumLoc).count(15).offset(0.15, 0.5, 0.15).extra(0.1).spawn();
			new ParticleBuilder(Particle.SNEEZE).location(sneeze).count(5).offset(0.25, 1, 0.25).extra(0.01).spawn();
			new ParticleBuilder(Particle.DOLPHIN).location(gravLift).count(10).offset(0.5, 4, 0.5).extra(0.1).spawn();

			Tasks.sync(() -> {
				//
				if (laserPlayer != null && laserPlayer != Sialia.nearbyPlayer)
					cancelTasks(); // Switch player used for laser effect

				laserPlayer = Sialia.nearbyPlayer;

				if (laserPlayer == null) {
					cancelTasks();
					return;
				}
				//

				if (!activeLaser) {
					activeLaser = true;
					laserTaskID = LineEffect.builder()
							.player(laserPlayer)
							.startLoc(laserStart)
							.endLoc(laserEnd)
							.density(0.1)
							.count(15)
							.maxLength(3.5)
							.color(ColorType.LIGHT_BLUE.getColor())
							.ticks(-1)
							.start()
							.getTaskId();
				}
			});
		});
	}

	public void cancelTasks() {
		Tasks.cancel(laserTaskID);
		activeLaser = false;
	}


}

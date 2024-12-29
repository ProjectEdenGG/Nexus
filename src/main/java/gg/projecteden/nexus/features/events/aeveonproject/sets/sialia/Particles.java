package gg.projecteden.nexus.features.events.aeveonproject.sets.sialia;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.aeveonproject.APUtils;
import gg.projecteden.nexus.features.events.aeveonproject.sets.APSetType;
import gg.projecteden.nexus.features.particles.effects.LineEffect;
import gg.projecteden.nexus.models.particle.ParticleService;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class Particles {
	private int laserTaskId = -1;
	private boolean activeLaser = false;
	private Player laserPlayer = null;
	private final Location laserStart = APUtils.APLoc(-1300.5, 83.5, -1155.9);
	private final Location laserEnd = APUtils.APLoc(-1300.5, 83.25, -1159.5);
	//
	private final Location nautilisLoc = APUtils.APLoc(-1303.5, 83.5, -1164.5);
	private final Location portalLoc = APUtils.APLoc(-1302.5, 82.5, -1166.5);
	private final Location myceliumLoc = APUtils.APLoc(-1300.5, 82.5, -1168.5);
	private final Location sneeze = APUtils.APLoc(-1287.0, 82.0, -1156.0);
	private final Location gravLift_1 = APUtils.APLoc(-1294.0, 88.5, -1160.0);
	private final Location gravLift_2 = APUtils.APLoc(-1301.0, 84.0, -1189.0);
	private final Location gravLift_3 = APUtils.APLoc(-1287.0, 84.0, -1189.0);

	public Particles() {
		Tasks.repeatAsync(0, TickTime.TICK.x(2), () -> {
			if (!APSetType.SIALIA.get().isActive() || Sialia.nearbyPlayer == null)
				return;

			new ParticleBuilder(Particle.NAUTILUS).location(nautilisLoc).count(5).offset(0.1, 0.5, 0.1).extra(0.1).spawn();
			new ParticleBuilder(Particle.PORTAL).location(portalLoc).count(5).offset(0.15, 1, 0.15).extra(0.1).spawn();
			new ParticleBuilder(Particle.TOWN_AURA).location(myceliumLoc).count(15).offset(0.15, 0.5, 0.15).extra(0.1).spawn();
			new ParticleBuilder(Particle.SNEEZE).location(sneeze).count(5).offset(0.25, 1, 0.25).extra(0.01).spawn();
			new ParticleBuilder(Particle.DOLPHIN).location(gravLift_1).count(10).offset(0.5, 4, 0.5).extra(0.1).spawn();
			new ParticleBuilder(Particle.DOLPHIN).location(gravLift_2).count(10).offset(0.75, 1.5, 0.75).extra(0.1).spawn();
			new ParticleBuilder(Particle.DOLPHIN).location(gravLift_3).count(10).offset(0.75, 1.5, 0.75).extra(0.1).spawn();

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
					laserTaskId = LineEffect.builder()
							.owner(new ParticleService().get(laserPlayer))
							.entity(laserPlayer)
							.startLoc(laserStart)
							.endLoc(laserEnd)
							.density(0.1)
							.count(15)
							.maxLength(3.5)
							.color(ColorType.LIGHT_BLUE.getBukkitColor())
							.ticks(-1)
							.start()
							.getTaskId();
				}
			});
		});
	}

	public void cancelTasks() {
		Tasks.cancel(laserTaskId);
		activeLaser = false;
	}

}

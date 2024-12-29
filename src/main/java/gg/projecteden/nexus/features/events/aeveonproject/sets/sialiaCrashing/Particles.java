package gg.projecteden.nexus.features.events.aeveonproject.sets.sialiaCrashing;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.aeveonproject.APUtils;
import gg.projecteden.nexus.features.events.aeveonproject.sets.APSetType;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.Arrays;
import java.util.List;

public class Particles {

	// Lab Particles
	private final Location gravLift_1 = APUtils.APLoc(-823.0, 88.5, -1168.0);
	private final Location nautilisLoc = APUtils.APLoc(-832.5, 83.5, -1172.5);
	private final Location portalLoc = APUtils.APLoc(-831.5, 82.5, -1174.5);
	private final Location myceliumLoc = APUtils.APLoc(-829.5, 82.5, -1176.5);
	private final Location sneeze = APUtils.APLoc(-816.0, 82.0, -1164.0);
	// Misc
	private final Location ventExit = APUtils.APLoc(-819.5, 92.9, -1112.5);
	private final Location ventSmoke1 = APUtils.APLoc(-824.5, 91.3, -1113.5);
	private final Location ventSmoke2 = APUtils.APLoc(-824.5, 91.3, -1117.5);
	private final Location ventSmoke3 = APUtils.APLoc(-820.5, 92.3, -1110.5);
	// Pipes - starting from the front of the ship
	private static final Location pipe1 = APUtils.APLoc(-824.9, 89.3, -1172.5);
	private final Location pipe1_poof = APUtils.APLoc(-821.5, 86.5, -1172.5);
	private static final Location pipe2 = APUtils.APLoc(-821.5, 89.3, -1162.5);
	private final Location pipe2_poof = APUtils.APLoc(-824.5, 86.1, -1162.5);
	private static final Location pipe3 = APUtils.APLoc(-824.5, 89.3, -1125.5);
	private final Location pipe3_poof = APUtils.APLoc(-821.5, 86.5, -1125.5);
	private static final Location pipe4 = APUtils.APLoc(-821.5, 89.3, -1124.5);
	private final Location pipe4_poof = APUtils.APLoc(-824.5, 86.1, -1124.5);
	private static final Location pipe5 = APUtils.APLoc(-824.5, 89.3, -1119.5);
	private final Location pipe5_poof = APUtils.APLoc(-821.5, 86.1, -1119.5);
	private static final Location pipe6 = APUtils.APLoc(-824.5, 89.3, -1116.5);
	private final Location pipe6_poof = APUtils.APLoc(-821.5, 86.1, -1116.5);
	private static final Location pipe7 = APUtils.APLoc(-821.5, 89.3, -1108.5);
	private final Location pipe7_poof = APUtils.APLoc(-824.5, 86.1, -1108.5);
	private static final Location pipe8 = APUtils.APLoc(-824.5, 88.3, -1092.5);
	private final Location pipe8_poof = APUtils.APLoc(-821.5, 88.3, -1092.5);
	private static final Location pipe9 = APUtils.APLoc(-821.5, 87.3, -1086.5);
	private final Location pipe9_poof = APUtils.APLoc(-824.5, 87.3, -1086.5);
	private static final Location pipe10 = APUtils.APLoc(-825.5, 87.3, -1084.5);
	private final Location pipe10_poof = APUtils.APLoc(-821.5, 87.3, -1084.5);
	private static final Location pipe11 = APUtils.APLoc(-821.5, 86.3, -1073.5);
	private final Location pipe11_poof = APUtils.APLoc(-824.5, 86.3, -1073.5);
	public static final List<Location> pipes = Arrays.asList(pipe1, pipe2, pipe3, pipe4, pipe5, pipe6, pipe7, pipe8, pipe9, pipe10, pipe11);
	// Engine Smoke
	private final Location engineSmoke1 = APUtils.APLoc(-822.5, 82, -1066.5);
	private final Location engineSmoke2 = APUtils.APLoc(-823.5, 82, -1066.5);
	private final Location engineSmoke3 = APUtils.APLoc(-822.5, 86, -1070.5);
	private final Location engineSmoke4 = APUtils.APLoc(-821.5, 86, -1068.5);
	private final Location engineSmoke5 = APUtils.APLoc(-824.5, 86, -1069.5);
	private final Location engineSmoke6 = APUtils.APLoc(-822.5, 86, -1066.5);
	private final Location engineSmoke7 = APUtils.APLoc(-827.5, 86, -1066.5);
	private final Location engineSmoke8 = APUtils.APLoc(-818.5, 83, -1060.5);
	private final Location engineSmoke9 = APUtils.APLoc(-820.5, 82, -1061.5);
	private final Location engineSmoke10 = APUtils.APLoc(-825.5, 82, -1061.5);
	private final Location engineSmoke11 = APUtils.APLoc(-827.5, 83, -1060.5);
	private final Location engineSmoke12 = APUtils.APLoc(-827.5, 83, -1066.5);
	private final Location engineSmoke13 = APUtils.APLoc(-818.5, 83, -1067.5);
	private final List<Location> engineSmoke = Arrays.asList(engineSmoke1, engineSmoke2, engineSmoke3, engineSmoke4, engineSmoke5, engineSmoke6, engineSmoke7,
			engineSmoke8, engineSmoke9, engineSmoke10, engineSmoke11, engineSmoke12, engineSmoke13);
	// Engine Lava Pop
	private final Location engineLava1 = APUtils.APLoc(-818.5, 84, -1060.5);
	private final Location engineLava2 = APUtils.APLoc(-817.5, 86, -1063.5);
	private final Location engineLava3 = APUtils.APLoc(-827.5, 83, -1063.5);
	private final Location engineLava4 = APUtils.APLoc(-826.5, 89, -1064.5);
	private final List<Location> engineLava = Arrays.asList(engineLava1, engineLava2, engineLava3, engineLava4);
	// Explosions
	private final Location explosion1 = APUtils.APLoc(-825.5, 83, -1060.5);
	private final Location explosion2 = APUtils.APLoc(-824.5, 88, -1126.5);
	private final List<Location> explosions = Arrays.asList(explosion1, explosion2);

	public Particles() {
		Tasks.repeatAsync(0, TickTime.TICK.x(6), () -> {
			if (!APSetType.SIALIA_CRASHING.get().isActive())
				return;

			new ParticleBuilder(Particle.DRIP_LAVA).location(ventExit).count(1).offset(0.15, 0, 0.15).extra(0.1).spawn();
		});

		Tasks.repeatAsync(0, TickTime.TICK.x(2), () -> {
			if (!APSetType.SIALIA_CRASHING.get().isActive())
				return;

			// Lab Particles
			new ParticleBuilder(Particle.DOLPHIN).location(gravLift_1).count(10).offset(0.5, 4, 0.5).extra(0.1).spawn();
			new ParticleBuilder(Particle.NAUTILUS).location(nautilisLoc).count(5).offset(0.1, 0.5, 0.1).extra(0.1).spawn();
			new ParticleBuilder(Particle.PORTAL).location(portalLoc).count(5).offset(0.15, 1, 0.15).extra(0.1).spawn();
			new ParticleBuilder(Particle.TOWN_AURA).location(myceliumLoc).count(15).offset(0.15, 0.5, 0.15).extra(0.1).spawn();
			new ParticleBuilder(Particle.SNEEZE).location(sneeze).count(5).offset(0.25, 1, 0.25).extra(0.01).spawn();
			// Vent Particles
			new ParticleBuilder(Particle.FLAME).location(ventSmoke1).count(0).offset(0, 0, -3).extra(0.1).spawn();
			new ParticleBuilder(Particle.SMOKE_LARGE).location(ventSmoke1).count(0).offset(0, 0, -3).extra(0.1).spawn();
			new ParticleBuilder(Particle.FLAME).location(ventSmoke2).count(0).offset(0, 0, -3).extra(0.1).spawn();
			new ParticleBuilder(Particle.SMOKE_LARGE).location(ventSmoke2).count(0).offset(0, 0, -3).extra(0.1).spawn();
			new ParticleBuilder(Particle.FLAME).location(ventSmoke3).count(0).offset(-3, 0, 0).extra(0.1).spawn();
			new ParticleBuilder(Particle.SMOKE_LARGE).location(ventSmoke3).count(0).offset(-3, 0, 0).extra(0.1).spawn();

			// Pipes
			new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe1).count(0).offset(3, -3, 0).extra(0.3).spawn();
			new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe1_poof).count(1).offset(0.2, 0, 0.2).extra(0.2).spawn();
			new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe2).count(0).offset(-3, -3, 0).extra(0.3).spawn();
			new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe2_poof).count(1).offset(0.2, 0, 0.2).extra(0.2).spawn();
			// Pipe3 extras
			new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe3).count(0).offset(3, -3, 0).extra(0.3).spawn();
			new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe3_poof).count(1).offset(0.2, 0, 0.2).extra(0.2).spawn();
			new ParticleBuilder(Particle.LAVA).location(pipe3).count(1).offset(0, 0, 0).extra(0.1).spawn();
			new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe3).count(0).offset(0, 0, 0).extra(0.1).spawn();
			//
			new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe4).count(0).offset(-3, -3, 0).extra(0.3).spawn();
			new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe4_poof).count(1).offset(0.2, 0, 0.2).extra(0.2).spawn();
			new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe5).count(0).offset(3, -3, 0).extra(0.3).spawn();
			new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe5_poof).count(1).offset(0.2, 0, 0.2).extra(0.2).spawn();
			new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe6).count(0).offset(3, -3, 0).extra(0.3).spawn();
			new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe6_poof).count(1).offset(0.2, 0, 0.2).extra(0.2).spawn();
			new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe7).count(0).offset(-3, -3, 0).extra(0.3).spawn();
			new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe7_poof).count(1).offset(0.2, 0, 0.2).extra(0.2).spawn();
			new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe8).count(0).offset(3, 0, 0).extra(0.3).spawn();
			new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe8_poof).count(1).offset(0.2, 0, 0.2).extra(0.2).spawn();
			new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe9).count(0).offset(-3, 0, 0).extra(0.3).spawn();
			new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe9_poof).count(1).offset(0.2, 0, 0.2).extra(0.2).spawn();
			new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe10).count(0).offset(3, 0, 0).extra(0.3).spawn();
			new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe10_poof).count(1).offset(0.2, 0, 0.2).extra(0.2).spawn();
			new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe11).count(0).offset(-3, 0, 0).extra(0.3).spawn();
			new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe11_poof).count(1).offset(0.2, 0, 0.2).extra(0.2).spawn();

			Tasks.waitAsync(TickTime.TICK.x(1), () -> {
				new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe1).count(0).offset(3, -3, 0).extra(0.3).spawn();
				new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe2).count(0).offset(-3, -3, 0).extra(0.3).spawn();
				new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe3).count(0).offset(3, -3, 0).extra(0.3).spawn();
				new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe4).count(0).offset(-3, -3, 0).extra(0.3).spawn();
				new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe5).count(0).offset(3, -3, 0).extra(0.3).spawn();
				new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe6).count(0).offset(3, -3, 0).extra(0.3).spawn();
				new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(pipe7).count(0).offset(-3, -3, 0).extra(0.3).spawn();
			});

			// Engine Smoke
			engineSmoke.forEach(smokeLoc ->
					new ParticleBuilder(Particle.CAMPFIRE_COSY_SMOKE).location(smokeLoc).count(0).offset(0, 3, 0).extra(0.01).spawn());

			// Engine Lava Pop
			engineLava.forEach(lavaLoc ->
					new ParticleBuilder(Particle.LAVA).location(lavaLoc).count(1).offset(0, 0, 0).extra(0.1).spawn());

			// Explosions
			explosions.forEach(explosionLoc ->
					new ParticleBuilder(Particle.EXPLOSION_LARGE).location(explosionLoc).count(1).offset(1, 1, 1).extra(0.1).spawn());
		});
	}
}

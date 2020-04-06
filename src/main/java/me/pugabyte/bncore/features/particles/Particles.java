package me.pugabyte.bncore.features.particles;

import me.pugabyte.bncore.models.particle.ParticleOwner;
import me.pugabyte.bncore.models.particle.ParticleService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Particles {

	public Particles() {
		startup();
	}

	private void startup() {
		Bukkit.getOnlinePlayers().forEach(Particles::startParticles);
	}

	protected static void startParticles(Player player) {
		ParticleOwner particleOwner = new ParticleService().get(player);
		new ArrayList<>(particleOwner.getActiveParticles()).forEach(particleType -> particleType.run(player));
	}

	protected static void stopParticles(Player player) {
		ParticleOwner particleOwner = new ParticleService().get(player);
		particleOwner.cancelTasks();
	}


}

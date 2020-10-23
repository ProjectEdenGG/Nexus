package me.pugabyte.bncore.features.particles;

import me.pugabyte.bncore.framework.features.Feature;
import me.pugabyte.bncore.models.particle.ParticleOwner;
import me.pugabyte.bncore.models.particle.ParticleService;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Particles extends Feature {

	@Override
	public void startup() {
		Tasks.async(() -> Bukkit.getOnlinePlayers().forEach(Particles::startParticles));
	}

	protected static void startParticles(Player player) {
		try {
			ParticleOwner particleOwner = new ParticleService().get(player);
			new ArrayList<>(particleOwner.getActiveParticles()).forEach(particleType -> particleType.run(player));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected static void stopParticles(Player player) {
		ParticleOwner particleOwner = new ParticleService().get(player);
		particleOwner.cancelTasks();
	}


}

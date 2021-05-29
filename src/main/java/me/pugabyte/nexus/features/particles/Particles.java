package me.pugabyte.nexus.features.particles;

import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.models.particle.ParticleOwner;
import me.pugabyte.nexus.models.particle.ParticleService;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Particles extends Feature {

	@Override
	public void onStart() {
		Tasks.async(() -> Bukkit.getOnlinePlayers().forEach(Particles::startParticles));
	}

	protected static void startParticles(Player player) {
		try {
			ParticleOwner particleOwner = new ParticleService().get(player);
			new ArrayList<>(particleOwner.getActiveParticles()).forEach(particleType -> {
				if (particleOwner.canUse(particleType))
					particleOwner.start(particleType);
				else
					particleOwner.cancel(particleType);
			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected static void stopParticles(Player player) {
		new ParticleService().get(player).cancel();
	}


}

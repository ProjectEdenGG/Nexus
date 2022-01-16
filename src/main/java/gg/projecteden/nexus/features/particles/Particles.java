package gg.projecteden.nexus.features.particles;

import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.particle.ParticleOwner;
import gg.projecteden.nexus.models.particle.ParticleService;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Particles extends Feature {

	@Override
	public void onStart() {
		OnlinePlayers.getAll().forEach(Particles::startParticles);
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

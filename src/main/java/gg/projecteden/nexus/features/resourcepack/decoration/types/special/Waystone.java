package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.nexus.features.resourcepack.decoration.common.TickableDecoration;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.FloorThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import org.bukkit.Location;
import org.bukkit.Particle;

public class Waystone extends FloorThing implements TickableDecoration {

	public Waystone(String name, CustomMaterial material) {
		super(name, material);
	}

	@Override
	public void tick(Location location) {
		new ParticleBuilder(Particle.ENCHANTMENT_TABLE).location(location.add(0, 1, 0).toCenterLocation()).spawn();
	}
}

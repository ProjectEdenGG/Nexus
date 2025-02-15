package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxFloor;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.TickableDecoration;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.FloorThing;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Location;
import org.bukkit.Particle;

public class Waystone extends FloorThing implements TickableDecoration {
	boolean activated;

	public Waystone(String name, ItemModelType itemModelType, boolean activated) {
		super(false, name, itemModelType, HitboxFloor._1x2V_WALL);
		this.activated = activated;
	}

	@Override
	public boolean shouldTick() {
		return this.activated;
	}

	@Override
	public void tick(Location location) {
		new ParticleBuilder(Particle.ENCHANT).location(location.add(0, 1, 0).toCenterLocation()).spawn();
	}
}

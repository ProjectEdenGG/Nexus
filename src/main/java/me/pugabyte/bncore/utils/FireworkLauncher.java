package me.pugabyte.bncore.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

@Getter
@Setter
@Accessors(fluent = true)
public class FireworkLauncher {
	private Location location;
	private Color color;
	private FireworkEffect.Type type;
	private Integer power;
	private Integer detonateAfter;

	public FireworkLauncher(Location location) {
		this.location = location;
	}

	public void launch() {
		Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
		FireworkEffect.Builder builder = FireworkEffect.builder();
		FireworkMeta meta = firework.getFireworkMeta();

		if (type != null)
			builder.with(type);
		if (color != null)
			builder.withColor(color);

		FireworkEffect effect = builder.build();

		meta.addEffect(effect);
		if (power != null)
			meta.setPower(0);

		firework.setFireworkMeta(meta);

		if (detonateAfter != null)
			Utils.wait(detonateAfter, firework::detonate);
	}
}

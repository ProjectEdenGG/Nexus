package me.pugabyte.nexus.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Accessors(fluent = true)
public class FireworkLauncher {
	private Location location;
	private List<Color> colors;
	private List<Color> fadeColors;
	private Boolean flickering;
	private Boolean trailing;
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
		if (colors != null)
			builder.withColor(colors);
		if (fadeColors != null)
			builder.withFade(fadeColors);

		FireworkEffect effect = builder.build();

		meta.addEffect(effect);
		if (power != null)
			meta.setPower(power);

		firework.setFireworkMeta(meta);

		if (detonateAfter != null)
			Tasks.wait(detonateAfter, firework::detonate);
	}

	public static FireworkLauncher random(Location location) {
		// Get Random Colors
		ColorType[] colorTypes = ColorType.values();
		List<Color> colorList = new ArrayList<>();
		for (ColorType colorType : colorTypes)
			if (RandomUtils.chanceOf(40))
				colorList.add(colorType.getBukkitColor());
		if (colorList.size() == 0)
			colorList.add(RandomUtils.randomElement(Arrays.asList(colorTypes)).getBukkitColor());

		// Get Random Fade Colors
		List<Color> fadeColorList = new ArrayList<>();
		for (ColorType colorType : colorTypes)
			if (RandomUtils.chanceOf(40))
				fadeColorList.add(colorType.getBukkitColor());

		// Get Random Type
		FireworkEffect.Type type = RandomUtils.randomElement(Arrays.asList(FireworkEffect.Type.values()));

		return new FireworkLauncher(location)
				.type(type)
				.colors(colorList)
				.fadeColors(fadeColorList)
				.trailing(RandomUtils.chanceOf(50))
				.flickering(RandomUtils.chanceOf(50))
				.power(RandomUtils.randomInt(1, 3));
	}
}

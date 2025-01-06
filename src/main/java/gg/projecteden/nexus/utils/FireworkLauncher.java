package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.Nexus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
	private Long detonateAfter;
	private Boolean silent;
	private Boolean damage;
	public static final String METADATA_KEY_DAMAGE = "damage";

	public FireworkLauncher(Location location) {
		this.location = location;
	}

	private static List<ColorType> ignoreColors = List.of(ColorType.BLACK, ColorType.WHITE, ColorType.GRAY, ColorType.LIGHT_GRAY);

	public FireworkLauncher rainbow() {
		List<Color> colors = new ArrayList<>();
		for (ColorType colorType : ColorType.values()) {
			if (ignoreColors.contains(colorType))
				continue;

			colors.add(colorType.getBukkitColor());
		}
		this.colors = colors;
		return this;
	}

	public FireworkLauncher color(Color color) {
		this.colors = Collections.singletonList(color);
		return this;
	}

	public FireworkLauncher fadeColor(Color color) {
		this.fadeColors = Collections.singletonList(color);
		return this;
	}

	public void launch() {
		Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK_ROCKET);
		FireworkEffect.Builder builder = FireworkEffect.builder();
		FireworkMeta meta = firework.getFireworkMeta();

		if (damage != null)
			firework.setMetadata(METADATA_KEY_DAMAGE, new FixedMetadataValue(Nexus.getInstance(), damage));
		if (silent != null)
			firework.setSilent(silent);

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
		List<Color> colorList = getRandomColors();

		// Get Random Fade Colors
		List<Color> fadeColorList = getRandomColors();

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

	public static List<Color> getRandomColors() {
		List<Color> colorList = new ArrayList<>();
		if (RandomUtils.chanceOf(50)) {
			for (ColorType colorType : ColorType.values())
				if (RandomUtils.chanceOf(40))
					colorList.add(colorType.getBukkitColor());
			if (colorList.isEmpty())
				colorList.add(ColorType.getRandom().getBukkitColor());
		} else
			colorList.add(ColorType.getRandom().getBukkitColor());

		return colorList;
	}
}

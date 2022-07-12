package gg.projecteden.nexus.features.resourcepack.playerplushies;

import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.utils.MathUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Material;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import static gg.projecteden.api.common.utils.StringUtils.camelCase;

// ====================================
// == !! ADD NEW POSES AT THE END !! ==
// ====================================

@Getter
public enum Pose {
	STANDING(Tier.TIER_1),
	WALKING(Tier.TIER_1),
	T_POSE(Tier.TIER_1),
	HANDSTAND(Tier.TIER_1),

	SITTING(Tier.TIER_2),
	DABBING(Tier.TIER_2),
	RIDING_MINECART(Tier.TIER_2),
	HOLDING_GLOBE(Tier.TIER_2),

	@Animated(frameCount = 3, frameTime = 10, frames = {0, 1, 2, 1})
	WAVING(Tier.TIER_3),
	FUNKO_POP(Tier.TIER_3),

	FUNKO_POP_ADMIN(Tier.SERVER),
	FUNKO_POP_OWNER(Tier.SERVER),
	;

	private final Tier tier;

	Pose(Tier tier) {
		this.tier = tier;
		new DecorationConfig(
			camelCase(this) + " Player Plushie",
			Material.LAPIS_LAZULI,
			getStartingIndex() + 1,
			modelId -> MathUtils.isBetween(modelId, getStartingIndex(), getEndingIndex()),
			Hitbox.NONE()
		);
	}

	public static void init() {}

	public int getStartingIndex() {
		return ordinal() * 10000;
	}

	public int getEndingIndex() {
		return ((ordinal() + 1) * 10000) - 1;
	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Animated {
		int frameCount();
		int frameTime();
		int[] frames();
	}

	@SneakyThrows
	public Field getField() {
		return getClass().getField(name());
	}

	public boolean isAnimated() {
		return getField().isAnnotationPresent(Animated.class);
	}

	public Animated getAnimationConfig() {
		return getField().getAnnotation(Animated.class);
	}

}

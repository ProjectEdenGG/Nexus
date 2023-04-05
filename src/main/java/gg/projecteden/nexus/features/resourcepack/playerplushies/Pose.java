package gg.projecteden.nexus.features.resourcepack.playerplushies;

import gg.projecteden.nexus.features.resourcepack.decoration.types.special.PlayerPlushie;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum Pose {
	// ====================================
	// == !! ADD NEW POSES AT THE END !! ==
	// ====================================

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

	public static final Map<Pose, PlayerPlushie> plushieMap = new HashMap<>();

	public static void init() {
		for (Pose pose : values()) {
			plushieMap.put(pose, new PlayerPlushie(pose));
		}
	}

	public static List<Pose> of(Tier tier) {
		return Arrays.stream(values()).filter(pose -> pose.getTier() == tier).collect(Collectors.toList());
	}

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

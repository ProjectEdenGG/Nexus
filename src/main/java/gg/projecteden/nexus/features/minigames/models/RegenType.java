package gg.projecteden.nexus.features.minigames.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

/**
 * The type of regeneration used by a mechanic.
 */
@AllArgsConstructor
public enum RegenType {
	/**
	 * The vanilla health regeneration.
	 */
	TIER_0(false, false, null),
	/**
	 * The first tier of custom health regeneration expands on {@link #TIER_0} by increasing the
	 * player's rate of regeneration when they crouch and when they stand still.
	 */
	TIER_1(true, false, null),
	/**
	 * The second tier of custom health regeneration expands on {@link #TIER_1} by healing the
	 * player by 2 hearts when they kill an enemy.
	 */
	TIER_2(true, true, null),
	/**
	 * The third tier of custom health regeneration expands on {@link #TIER_2} by additionally
	 * giving the player 15 seconds of Regeneration I (= ~3 hearts) when they kill an enemy.
	 */
	TIER_3(true, true, createRegenEffect(15, 1)),
	/**
	 * The fourth tier of custom health regeneration expands on {@link #TIER_3} by replacing
	 * tier 3's regeneration effect with a quicker one (8 seconds of Regeneration II) that heals
	 * the same amount of health.
	 */
	TIER_4(true, true, createRegenEffect(8, 2));

	@Getter @Accessors(fluent = true)
	private final boolean hasCustomRegen;
	@Getter @Accessors(fluent = true)
	private final boolean hasKillHeal;
	@Getter @Nullable
	private final PotionEffect baseKillRegen;

	private static PotionEffect createRegenEffect(int seconds, int amplifier) {
		return new PotionEffect(PotionEffectType.REGENERATION, seconds*20, amplifier-1, true, true, true);
	}

	public static final int KILL_HEAL_AMOUNT = 4;
}

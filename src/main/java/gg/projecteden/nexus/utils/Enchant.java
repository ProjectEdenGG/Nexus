package gg.projecteden.nexus.utils;

import com.google.common.base.Preconditions;
import gg.projecteden.nexus.features.customenchants.CustomEnchant;
import gg.projecteden.nexus.features.customenchants.CustomEnchants;
import gg.projecteden.nexus.features.customenchants.OldCEConverter;
import gg.projecteden.nexus.features.customenchants.enchants.AutoRepairEnchant;
import gg.projecteden.nexus.features.customenchants.enchants.DisarmingEnchant;
import gg.projecteden.nexus.features.customenchants.enchants.FireworkEnchant;
import gg.projecteden.nexus.features.customenchants.enchants.GlowingEnchant;
import gg.projecteden.nexus.features.customenchants.enchants.MagnetEnchant;
import gg.projecteden.nexus.features.customenchants.enchants.SoulboundEnchant;
import gg.projecteden.nexus.features.customenchants.enchants.ThunderingBlowEnchant;
import gg.projecteden.nexus.features.customenchants.enchants.TunnelingEnchant;
import gg.projecteden.nexus.features.customenchants.enchants.VeinMinerEnchant;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Glorified enum of enchantments but with sane names
 * <p>
 * If replacing an old CustomEnchantments enchant, please use {@link OldCEConverter.ConversionEnchant}
 */
@SuppressWarnings("unused")
public class Enchant {

	/**
	 * Provides protection against environmental damage
	 */
	public static final Enchantment PROTECTION = getEnchantment("protection");

	/**
	 * Provides protection against fire damage
	 */
	public static final Enchantment FIRE_PROTECTION = getEnchantment("fire_protection");

	/**
	 * Provides protection against fall damage
	 */
	public static final Enchantment FEATHER_FALLING = getEnchantment("feather_falling");

	/**
	 * Provides protection against explosive damage
	 */
	public static final Enchantment BLAST_PROTECTION = getEnchantment("blast_protection");

	/**
	 * Provides protection against projectile damage
	 */
	public static final Enchantment PROJECTILE_PROTECTION = getEnchantment("projectile_protection");

	/**
	 * Decreases the rate of air loss whilst underwater
	 */
	public static final Enchantment RESPIRATION = getEnchantment("respiration");

	/**
	 * Increases the speed at which a player may mine underwater
	 */
	public static final Enchantment AQUA_AFFINITY = getEnchantment("aqua_affinity");

	/**
	 * Damages the attacker
	 */
	public static final Enchantment THORNS = getEnchantment("thorns");

	/**
	 * Increases walking speed while in water
	 */
	public static final Enchantment DEPTH_STRIDER = getEnchantment("depth_strider");

	/**
	 * Freezes any still water adjacent to ice / frost which player is walking on
	 */
	public static final Enchantment FROST_WALKER = getEnchantment("frost_walker");

	/**
	 * Item cannot be removed
	 */
	public static final Enchantment BINDING_CURSE = getEnchantment("binding_curse");

	/**
	 * Increases damage against all targets
	 */
	public static final Enchantment SHARPNESS = getEnchantment("sharpness");

	/**
	 * Increases damage against undead targets
	 */
	public static final Enchantment SMITE = getEnchantment("smite");

	/**
	 * Increases damage against arthropod targets
	 */
	public static final Enchantment BANE_OF_ARTHROPODS = getEnchantment("bane_of_arthropods");

	/**
	 * All damage to other targets will knock them back when hit
	 */
	public static final Enchantment KNOCKBACK = getEnchantment("knockback");

	/**
	 * When attacking a target, has a chance to set them on fire
	 */
	public static final Enchantment FIRE_ASPECT = getEnchantment("fire_aspect");

	/**
	 * Provides a chance of gaining extra loot when killing monsters
	 */
	public static final Enchantment LOOTING = getEnchantment("looting");

	/**
	 * Increases damage against targets when using a sweep attack
	 */
	public static final Enchantment SWEEPING_EDGE = getEnchantment("sweeping");

	/**
	 * Increases the rate at which you mine/dig
	 */
	public static final Enchantment EFFICIENCY = getEnchantment("efficiency");

	/**
	 * Allows blocks to drop themselves instead of fragments (for example,
	 * stone instead of cobblestone)
	 */
	public static final Enchantment SILK_TOUCH = getEnchantment("silk_touch");

	/**
	 * Decreases the rate at which a tool looses durability
	 */
	public static final Enchantment UNBREAKING = getEnchantment("unbreaking");

	/**
	 * Provides a chance of gaining extra loot when destroying blocks
	 */
	public static final Enchantment FORTUNE = getEnchantment("fortune");

	/**
	 * Provides extra damage when shooting arrows from bows
	 */
	public static final Enchantment POWER = getEnchantment("power");

	/**
	 * Provides a knockback when an entity is hit by an arrow from a bow
	 */
	public static final Enchantment PUNCH = getEnchantment("punch");

	/**
	 * Sets entities on fire when hit by arrows shot from a bow
	 */
	public static final Enchantment FLAME = getEnchantment("flame");

	/**
	 * Provides infinite arrows when shooting a bow
	 */
	public static final Enchantment INFINITY = getEnchantment("infinity");

	/**
	 * Decreases odds of catching worthless junk
	 */
	public static final Enchantment LUCK_OF_THE_SEA = getEnchantment("luck_of_the_sea");

	/**
	 * Increases rate of fish biting your hook
	 */
	public static final Enchantment LURE = getEnchantment("lure");

	/**
	 * Causes a thrown trident to return to the player who threw it
	 */
	public static final Enchantment LOYALTY = getEnchantment("loyalty");

	/**
	 * Deals more damage to mobs that live in the ocean
	 */
	public static final Enchantment IMPALING = getEnchantment("impaling");

	/**
	 * When it is rainy, launches the player in the direction their trident is thrown
	 */
	public static final Enchantment RIPTIDE = getEnchantment("riptide");

	/**
	 * Strikes lightning when a mob is hit with a trident if conditions are
	 * stormy
	 */
	public static final Enchantment CHANNELING = getEnchantment("channeling");

	/**
	 * Shoot multiple arrows from crossbows
	 */
	public static final Enchantment MULTISHOT = getEnchantment("multishot");

	/**
	 * Charges crossbows quickly
	 */
	public static final Enchantment QUICK_CHARGE = getEnchantment("quick_charge");

	/**
	 * Crossbow projectiles pierce entities
	 */
	public static final Enchantment PIERCING = getEnchantment("piercing");

	/**
	 * Allows mending the item using experience orbs
	 */
	public static final Enchantment MENDING = getEnchantment("mending");

	/**
	 * Item disappears instead of dropping
	 */
	public static final Enchantment VANISHING_CURSE = getEnchantment("vanishing_curse");

	/**
	 * Walk quicker on soul blocks
	 */
	public static final Enchantment SOUL_SPEED = getEnchantment("soul_speed");

	/**
	 * Walk quicker while sneaking
	 */
	public static final Enchantment SWIFT_SNEAK = getEnchantment("swift_sneak");

	/**
	 * Keep item on death
	 */
	public static final CustomEnchant SOULBOUND = CustomEnchants.get(SoulboundEnchant.class);

	/**
	 * Attract dropped items
	 */
	public static final CustomEnchant MAGNET = CustomEnchants.get(MagnetEnchant.class);

	/**
	 * Gives night vision when applied to helmets
	 */
	public static final CustomEnchant GLOWING = CustomEnchants.get(GlowingEnchant.class);

	/**
	 * Passively repairs items
	 */
	public static final CustomEnchant AUTOREPAIR = CustomEnchants.get(AutoRepairEnchant.class);

	public static final CustomEnchant THUNDERINGBLOW = CustomEnchants.get(ThunderingBlowEnchant.class);

	public static final CustomEnchant FIREWORK = CustomEnchants.get(FireworkEnchant.class);

	public static final CustomEnchant DISARMING = CustomEnchants.get(DisarmingEnchant.class);

	public static final CustomEnchant VEIN_MINER = CustomEnchants.get(VeinMinerEnchant.class);

	public static final CustomEnchant TUNNELING = CustomEnchants.get(TunnelingEnchant.class);

	private static final List<Enchantment> values = new ArrayList<>();

	static {
		try {
			for (Field field : Enchant.class.getDeclaredFields())
				if (field.get(null) instanceof Enchantment enchantment)
					values.add(enchantment);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static List<Enchantment> values() {
		return values;
	}

	public static Enchantment of(String name) {
		if (name == null)
			return null;

		for (Enchantment enchantment : values)
			if (name.equalsIgnoreCase(enchantment.getName()) || name.equalsIgnoreCase(enchantment.getKey().getKey()))
				return enchantment;

		return null;
	}

	@NotNull
	private static Enchantment getEnchantment(@NotNull String key) {
		NamespacedKey namespacedKey = NamespacedKey.minecraft(key);
		Enchantment enchantment = Registry.ENCHANTMENT.get(namespacedKey);

		Preconditions.checkNotNull(enchantment, "No Enchantment found for %s. This is a bug.", namespacedKey);

		return enchantment;
	}

}

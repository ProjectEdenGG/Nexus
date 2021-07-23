package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.features.customenchants.CustomEnchant;
import gg.projecteden.nexus.features.customenchants.CustomEnchants;
import gg.projecteden.nexus.features.customenchants.enchants.MagnetEnchant;
import gg.projecteden.nexus.features.customenchants.enchants.SoulboundEnchant;
import lombok.experimental.UtilityClass;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Glorified enum of enchantments but with sane names
 */
@UtilityClass
public class Enchant {

	/**
	 * Provides protection against environmental damage
	 */
	public static final EnchantmentWrapper PROTECTION = new EnchantmentWrapper("protection");

	/**
	 * Provides protection against fire damage
	 */
	public static final EnchantmentWrapper FIRE_PROTECTION = new EnchantmentWrapper("fire_protection");

	/**
	 * Provides protection against fall damage
	 */
	public static final EnchantmentWrapper FEATHER_FALLING = new EnchantmentWrapper("feather_falling");

	/**
	 * Provides protection against explosive damage
	 */
	public static final EnchantmentWrapper BLAST_PROTECTION = new EnchantmentWrapper("blast_protection");

	/**
	 * Provides protection against projectile damage
	 */
	public static final EnchantmentWrapper PROJECTILE_PROTECTION = new EnchantmentWrapper("projectile_protection");

	/**
	 * Decreases the rate of air loss whilst underwater
	 */
	public static final EnchantmentWrapper RESPIRATION = new EnchantmentWrapper("respiration");

	/**
	 * Increases the speed at which a player may mine underwater
	 */
	public static final EnchantmentWrapper AQUA_AFFINITY = new EnchantmentWrapper("aqua_affinity");

	/**
	 * Damages the attacker
	 */
	public static final EnchantmentWrapper THORNS = new EnchantmentWrapper("thorns");

	/**
	 * Increases walking speed while in water
	 */
	public static final EnchantmentWrapper DEPTH_STRIDER = new EnchantmentWrapper("depth_strider");

	/**
	 * Freezes any still water adjacent to ice / frost which player is walking on
	 */
	public static final EnchantmentWrapper FROST_WALKER = new EnchantmentWrapper("frost_walker");

	/**
	 * Item cannot be removed
	 */
	public static final EnchantmentWrapper BINDING_CURSE = new EnchantmentWrapper("binding_curse");

	/**
	 * Increases damage against all targets
	 */
	public static final EnchantmentWrapper SHARPNESS = new EnchantmentWrapper("sharpness");

	/**
	 * Increases damage against undead targets
	 */
	public static final EnchantmentWrapper SMITE = new EnchantmentWrapper("smite");

	/**
	 * Increases damage against arthropod targets
	 */
	public static final EnchantmentWrapper BANE_OF_ARTHROPODS = new EnchantmentWrapper("bane_of_arthropods");

	/**
	 * All damage to other targets will knock them back when hit
	 */
	public static final EnchantmentWrapper KNOCKBACK = new EnchantmentWrapper("knockback");

	/**
	 * When attacking a target, has a chance to set them on fire
	 */
	public static final EnchantmentWrapper FIRE_ASPECT = new EnchantmentWrapper("fire_aspect");

	/**
	 * Provides a chance of gaining extra loot when killing monsters
	 */
	public static final EnchantmentWrapper LOOTING = new EnchantmentWrapper("looting");

	/**
	 * Increases damage against targets when using a sweep attack
	 */
	public static final EnchantmentWrapper SWEEPING_EDGE = new EnchantmentWrapper("sweeping");

	/**
	 * Increases the rate at which you mine/dig
	 */
	public static final EnchantmentWrapper EFFICIENCY = new EnchantmentWrapper("efficiency");

	/**
	 * Allows blocks to drop themselves instead of fragments (for example,
	 * stone instead of cobblestone)
	 */
	public static final EnchantmentWrapper SILK_TOUCH = new EnchantmentWrapper("silk_touch");

	/**
	 * Decreases the rate at which a tool looses durability
	 */
	public static final EnchantmentWrapper UNBREAKING = new EnchantmentWrapper("unbreaking");

	/**
	 * Provides a chance of gaining extra loot when destroying blocks
	 */
	public static final EnchantmentWrapper FORTUNE = new EnchantmentWrapper("fortune");

	/**
	 * Provides extra damage when shooting arrows from bows
	 */
	public static final EnchantmentWrapper POWER = new EnchantmentWrapper("power");

	/**
	 * Provides a knockback when an entity is hit by an arrow from a bow
	 */
	public static final EnchantmentWrapper PUNCH = new EnchantmentWrapper("punch");

	/**
	 * Sets entities on fire when hit by arrows shot from a bow
	 */
	public static final EnchantmentWrapper FLAME = new EnchantmentWrapper("flame");

	/**
	 * Provides infinite arrows when shooting a bow
	 */
	public static final EnchantmentWrapper INFINITY = new EnchantmentWrapper("infinity");

	/**
	 * Decreases odds of catching worthless junk
	 */
	public static final EnchantmentWrapper LUCK_OF_THE_SEA = new EnchantmentWrapper("luck_of_the_sea");

	/**
	 * Increases rate of fish biting your hook
	 */
	public static final EnchantmentWrapper LURE = new EnchantmentWrapper("lure");

	/**
	 * Causes a thrown trident to return to the player who threw it
	 */
	public static final EnchantmentWrapper LOYALTY = new EnchantmentWrapper("loyalty");

	/**
	 * Deals more damage to mobs that live in the ocean
	 */
	public static final EnchantmentWrapper IMPALING = new EnchantmentWrapper("impaling");

	/**
	 * When it is rainy, launches the player in the direction their trident is thrown
	 */
	public static final EnchantmentWrapper RIPTIDE = new EnchantmentWrapper("riptide");

	/**
	 * Strikes lightning when a mob is hit with a trident if conditions are
	 * stormy
	 */
	public static final EnchantmentWrapper CHANNELING = new EnchantmentWrapper("channeling");

	/**
	 * Shoot multiple arrows from crossbows
	 */
	public static final EnchantmentWrapper MULTISHOT = new EnchantmentWrapper("multishot");

	/**
	 * Charges crossbows quickly
	 */
	public static final EnchantmentWrapper QUICK_CHARGE = new EnchantmentWrapper("quick_charge");

	/**
	 * Crossbow projectiles pierce entities
	 */
	public static final EnchantmentWrapper PIERCING = new EnchantmentWrapper("piercing");

	/**
	 * Allows mending the item using experience orbs
	 */
	public static final EnchantmentWrapper MENDING = new EnchantmentWrapper("mending");

	/**
	 * Item disappears instead of dropping
	 */
	public static final EnchantmentWrapper VANISHING_CURSE = new EnchantmentWrapper("vanishing_curse");

	/**
	 * Walk quicker on soul blocks
	 */
	public static final EnchantmentWrapper SOUL_SPEED = new EnchantmentWrapper("soul_speed");

	/**
	 * Keep item on death
	 */
	public static final CustomEnchant SOULBOUND = CustomEnchants.get(SoulboundEnchant.class);

	/**
	 * Attract dropped items
	 */
	public static final CustomEnchant MAGNET = CustomEnchants.get(MagnetEnchant.class);

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
		for (Enchantment enchantment : values)
			if (enchantment.getName().equalsIgnoreCase(name) || enchantment.getKey().getKey().equalsIgnoreCase(name))
				return enchantment;
		return null;
	}

}

package gg.projecteden.nexus.utils;

import com.google.common.base.Preconditions;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customenchants.CustomEnchants;
import gg.projecteden.nexus.features.customenchants.CustomEnchantsRegistration;
import gg.projecteden.nexus.features.customenchants.enchants.AutoRepairEnchant;
import gg.projecteden.nexus.features.customenchants.enchants.BeheadingEnchant;
import gg.projecteden.nexus.features.customenchants.enchants.BountyEnchant;
import gg.projecteden.nexus.features.customenchants.enchants.ColumnQuakeEnchant;
import gg.projecteden.nexus.features.customenchants.enchants.DisarmingEnchant;
import gg.projecteden.nexus.features.customenchants.enchants.EnergizingEnchant;
import gg.projecteden.nexus.features.customenchants.enchants.FireworkEnchant;
import gg.projecteden.nexus.features.customenchants.enchants.GearsEnchant;
import gg.projecteden.nexus.features.customenchants.enchants.GlowingEnchant;
import gg.projecteden.nexus.features.customenchants.enchants.GracefulStepEnchant;
import gg.projecteden.nexus.features.customenchants.enchants.MagnetEnchant;
import gg.projecteden.nexus.features.customenchants.enchants.MidasCarrotsEnchant;
import gg.projecteden.nexus.features.customenchants.enchants.OrbseekerEnchant;
import gg.projecteden.nexus.features.customenchants.enchants.PloughEnchant;
import gg.projecteden.nexus.features.customenchants.enchants.SoulboundEnchant;
import gg.projecteden.nexus.features.customenchants.enchants.SpringsEnchant;
import gg.projecteden.nexus.features.customenchants.enchants.ThorEnchant;
import gg.projecteden.nexus.features.customenchants.enchants.TunnelingEnchant;
import gg.projecteden.nexus.features.customenchants.enchants.VeinMinerEnchant;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Glorified enum of enchantments but with sane names
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
	public static final Enchantment SWEEPING_EDGE = getEnchantment("sweeping_edge");

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
	public static final Enchantment SOULBOUND = CustomEnchants.get(SoulboundEnchant.class);

	/**
	 * Attract dropped items
	 */
	public static final Enchantment MAGNET = CustomEnchants.get(MagnetEnchant.class);

	/**
	 * Gives night vision when applied to helmets
	 */
	public static final Enchantment GLOWING = CustomEnchants.get(GlowingEnchant.class);

	/**
	 * Passively repairs items
	 */
	public static final Enchantment AUTOREPAIR = CustomEnchants.get(AutoRepairEnchant.class);

	public static final Enchantment THOR = CustomEnchants.get(ThorEnchant.class);

	public static final Enchantment FIREWORK = CustomEnchants.get(FireworkEnchant.class);

	public static final Enchantment DISARMING = CustomEnchants.get(DisarmingEnchant.class);

	public static final Enchantment ENERGIZING = CustomEnchants.get(EnergizingEnchant.class);

	public static final Enchantment VEIN_MINER = CustomEnchants.get(VeinMinerEnchant.class);

	public static final Enchantment TUNNELING = CustomEnchants.get(TunnelingEnchant.class);

	public static final Enchantment BEHEADING = CustomEnchants.get(BeheadingEnchant.class);

	public static final Enchantment COLUMN_QUAKE = CustomEnchants.get(ColumnQuakeEnchant.class);

	public static final Enchantment GEARS = CustomEnchants.get(GearsEnchant.class);

	public static final Enchantment SPRINGS = CustomEnchants.get(SpringsEnchant.class);

	public static final Enchantment GRACEFUL_STEP = CustomEnchants.get(GracefulStepEnchant.class);

	public static final Enchantment ORBSEEKER = CustomEnchants.get(OrbseekerEnchant.class);

	public static final Enchantment BOUNTY = CustomEnchants.get(BountyEnchant.class);

	public static final Enchantment PLOUGH = CustomEnchants.get(PloughEnchant.class);

	public static final Enchantment MIDAS_CARROTS = CustomEnchants.get(MidasCarrotsEnchant.class);

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
		try {
			Nexus.debug("Attempting to register " + key);
			final String registered = Registry.ENCHANTMENT.stream().filter(Objects::nonNull).map(enchantment -> enchantment.getKey().getKey()).collect(Collectors.joining(","));
			Nexus.debug("Registered so far in CraftRegistry: " + registered);

			NamespacedKey namespacedKey = NamespacedKey.minecraft(key);
			final ResourceLocation resourceLocation = CraftNamespacedKey.toMinecraft(namespacedKey);
			Nexus.debug("NMS enchant 1 %s/%s: %s".formatted(namespacedKey.toString(), resourceLocation.toString(), Registry.ENCHANTMENT.getOrThrow(namespacedKey)));
			Nexus.debug("NMS enchant 2 %s/%s: %s".formatted(namespacedKey.toString(), resourceLocation.toString(), CustomEnchantsRegistration.nmsRegistry().getOptional(resourceLocation).orElse(null)));
			Enchantment enchantment = Registry.ENCHANTMENT.get(namespacedKey);

			Preconditions.checkNotNull(enchantment, "No Enchantment found for %s. This is a bug.", namespacedKey);

			return enchantment;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

}


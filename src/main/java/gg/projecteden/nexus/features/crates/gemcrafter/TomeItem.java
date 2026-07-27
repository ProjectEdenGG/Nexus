package gg.projecteden.nexus.features.crates.gemcrafter;

import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicReference;

@Getter
@AllArgsConstructor
public class TomeItem {

	private static final String LEVEL_KEY = "TOME_LEVEL";

	private TomeType type;
	private TomeLevel level;

	public ItemStack toItemStack() {
		return new ItemBuilder(Material.PAPER)
			.maxStackSize(1)
			.name("&e" + StringUtils.camelCase(type) + " Tome")
			.lore("&3Level: &e" + StringUtils.camelCase(level))
			.model(type.getModel())
			.nbt(nbt -> nbt.setString(LEVEL_KEY, level.name()))
			.build();
	}

	public static TomeItem of(ItemStack item) {
		if (Nullables.isNullOrAir(item)) return null;

		TomeType type = TomeType.fromTome(item);
		if (type == null) return null;

		TomeLevel level = TomeLevel.fromTome(item);
		if (level == null) return null;

		return new TomeItem(type, level);
	}

	@Getter
	public enum TomeType {
		ARMOR(Enchant.PROTECTION, Enchant.PROJECTILE_PROTECTION, Enchant.BLAST_PROTECTION, Enchant.FIRE_PROTECTION, Enchant.RESPIRATION, Enchant.SWIFT_SNEAK, Enchant.THORNS),
		BOOTS(Enchant.FEATHER_FALLING, Enchant.SOUL_SPEED, Enchant.DEPTH_STRIDER),
		BOWS(Enchant.POWER, Enchant.PUNCH),
		CROSSBOWS(Enchant.QUICK_CHARGE, Enchant.MULTISHOT, Enchant.PIERCING),
		FISHING_RODS(Enchant.LUCK_OF_THE_SEA, Enchant.LURE),
		MACES(Enchant.DENSITY, Enchant.BREACH, Enchant.WIND_BURST),
		TOOLS(Enchant.EFFICIENCY, Enchant.FORTUNE),
		SPEARS(Enchant.SHARPNESS, Enchant.LOOTING, Enchant.KNOCKBACK, Enchant.FIRE_ASPECT, Enchant.LUNGE),
		MELEE(Enchant.SHARPNESS, Enchant.BANE_OF_ARTHROPODS, Enchant.SMITE, Enchant.LOOTING, Enchant.KNOCKBACK, Enchant.FIRE_ASPECT, Enchant.SWEEPING_EDGE),
		TRIDENTS(Enchant.RIPTIDE, Enchant.LOYALTY, Enchant.IMPALING),
		DURABILITY(Enchant.UNBREAKING, Enchant.MENDING)
		;

		TomeType(Enchantment... enchantments) {
			this.enchantments = enchantments;
		}

		final Enchantment[] enchantments;
		final String model = "misc/gem_crafter/tome/" + name().toLowerCase();

		public static TomeType fromTome(ItemStack tome) {
			String itemModel = new ItemBuilder(tome).model();
			for (TomeType type : values())
				if (type.getModel().equalsIgnoreCase(itemModel))
					return type;
			return null;
		}
	}

	public enum TomeLevel {
		NOVICE,
		APPRENTICE,
		ADEPT,
		EXPERT,
		MASTER
		;

		public static TomeLevel fromTome(ItemStack tome) {
			AtomicReference<String> level = new AtomicReference<>();
			new ItemBuilder(tome).nbt(nbt -> {
				if (nbt.hasTag(LEVEL_KEY))
					level.set(nbt.getString(LEVEL_KEY));
			});

			if (level.get() == null)
				return null;

			try { return valueOf(level.get().toUpperCase()); }
			catch (IllegalArgumentException ignore) { return null; }
		}
	}

}

package gg.projecteden.nexus.features.crates.gemcrafter;

import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.Model;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicReference;

@Getter
@AllArgsConstructor
public class TomeItem {

	private TomeType type;
	private TomeLevel level;

	public ItemStack toItemStack() {
		return new ItemBuilder(Material.LEATHER_HORSE_ARMOR)
			.maxStackSize(1)
			.name("&e" + StringUtils.camelCase(type) + " Tome")
			.model(type.getModel(level))
			.itemFlags(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES)
			.build();
	}

	public static TomeItem of(ItemStack item) {
		if (Nullables.isNullOrAir(item)) return null;

		TomeLevel level = TomeLevel.fromTome(item);
		if (level == null) return null;

		TomeType type = TomeType.fromTome(item, level);
		if (type == null) return null;

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
		MELEE(Enchant.SHARPNESS, Enchant.BANE_OF_ARTHROPODS, Enchant.SMITE, Enchant.LOOTING, Enchant.KNOCKBACK, Enchant.FIRE_ASPECT, Enchant.SWEEPING_EDGE, Enchant.LUNGE),
		TRIDENTS(Enchant.RIPTIDE, Enchant.LOYALTY, Enchant.IMPALING),
		DURABILITY(Enchant.UNBREAKING, Enchant.MENDING)
		;

		TomeType(Enchantment... enchantments) {
			this.enchantments = enchantments;
		}

		final Enchantment[] enchantments;

		private String getModel(TomeLevel level) {
			return "survival/gem_crafter/tomes/" + level.name().toLowerCase() + "/" + name().toLowerCase();
		}

		public static TomeType fromTome(ItemStack tome, TomeLevel level) {
			String itemModel = new ItemBuilder(tome).model();
			for (TomeType type : values())
				if (type.getModel(level).equalsIgnoreCase(itemModel))
					return type;
			return null;
		}
	}

	public enum TomeLevel {
		REGULAR,
		MASTER,
		;

		public static TomeLevel fromTome(ItemStack tome) {
			String model = Model.of(tome);
			for (TomeLevel level : values())
				if (model.contains(level.name().toLowerCase()))
					return level;
			return null;
		}
	}

}

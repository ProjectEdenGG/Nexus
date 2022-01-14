package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Seat.DyedPart;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Bench;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Block;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Chair;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Table;
import gg.projecteden.nexus.utils.ItemBuilder.CustomModelData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/*
	TODO:
		- Proper break & place detection of non-hitbox decoration
		- Add all decoration
		- If holding something and break a decoration, something breaks & only the barrier breaks
		- Chair sitability (something blocking the chair/player) detection
 */

@AllArgsConstructor
public enum DecorationType {
	// Tables
	TABLE_WOODEN_1x1(new Table("Wooden Table 1x1", 300, Table.TableSize._1x1)),
	TABLE_WOODEN_1x2(new Table("Wooden Table 1x2", 301, Table.TableSize._1x2)),
	TABLE_WOODEN_2x2(new Table("Wooden Table 2x2", 302, Table.TableSize._2x2)),
	TABLE_WOODEN_2x3(new Table("Wooden Table 2x3", 303, Table.TableSize._2x3)),
	TABLE_WOODEN_3x3(new Table("Wooden Table 3x3", 304, Table.TableSize._3x3)),
	// Chairs
	CHAIR_WOODEN_BASIC(new Chair("Wooden Chair", 400, DyedPart.WHOLE)),
	CHAIR_WOODEN_CUSHION(new Chair("Cushioned Wooden Chair", 401, DyedPart.CUSHION)),
	// Stools
	STOOL_WOODEN_BASIC(new Chair("Wooden Stool", 500, DyedPart.WHOLE)),
	STOOL_WOODEN_CUSHION(new Chair("Cushioned Wooden Stool", 501, DyedPart.CUSHION)),
	// Benches
	BENCH_WOODEN(new Bench("Wooden Bench", 450, DyedPart.WHOLE)),
	// Blocks
	DYE_STATION(new Block("Dye Station", 1, Material.CRAFTING_TABLE)),
	// Fireplaces
	// Gravestones
//	GRAVESTONE_FLOWERS(),
//	GRAVESTONE_TALL(),
//	GRAVESTONE_PLAQUE(),
//	GRAVESTONE_SMALL(),
//	GRAVESTONE_CROSS(),
//	GRAVESTONE_STACK(),
	// Food
//	PIZZA_BOX_SINGLE(),
//	PIZZA_BOX_SINGLE_OPENED(),
//	PIZZA_BOX_STACK(),
//	SOUP_MUSHROOM(),
//	SOUP_BEETROOT(),
//	SOUP_RABBIT(),
//	BREAD_LOAF(),
//	BREAD_LOAF_CUT(),
//	BROWNIES_CHOCOLATE(),
//	BROWNIES_VANILLA(),
//	COOKIES_CHOCOLATE(),
//	COOKIES_CHOCOLATE_CHIP(),
//	COOKIES_SUGAR(),
//	MILK_AND_COOKIES(),
//	MUFFINS_CHOCOLATE(),
//	MUFFINS_CHOCOLATE_CHIP(),
//	MUFFINS_LEMON(),
//	DINNER_HAM(),
//	DINNER_ROAST(),
//	DINNER_TURKEY(),
//	PUNCHBOWL_EGGNOG(), // Make dyeable
//	SIDE_CRANBERRIES(), // Make dyeable
//	SIDE_GREEN_BEAN_CASSEROLE(),
//	SIDE_MAC_N_CHEESE(),
//	SIDE_MASHED_POTATOES(),
//	SIDE_SWEET_POTATOES(),
//	SIDE_ROLLS(),
//	CAKE_BATTER(), // Make dyeable
//	CAKE_WHITE_CHOCOLATE(),
//	CAKE_BUNDT(),
//	CAKE_CHOCOLATE_DRIP(),
//	CAKE_TRIPLE(), // ???
//	PIE(), // Make dyeable
//	PIE_LATTICED(), // Make dyeable
	// Kitchenware
	// Potions
	// Misc
	;

	@Getter
	final Decoration decoration;

	public ItemStack getItem() {
		return decoration.getItem().clone();
	}

	public static DecorationType of(ItemStack tool) {
		for (DecorationType decoration : values()) {
			if (decoration.isFuzzyMatch(tool))
				return decoration;
		}

		return null;
	}

	public boolean isFuzzyMatch(ItemStack item2) {
		ItemStack item1 = this.getItem();

		if (item2 == null)
			return false;

		if (!item1.getType().equals(item2.getType()))
			return false;

		int decorModelData = CustomModelData.of(item1);
		int itemModelData = CustomModelData.of(item2);
		if (decorModelData != itemModelData)
			return false;

		return true;
	}

	private static final Set<Material> hitboxTypes = new HashSet<>();

	public static Set<Material> getHitboxTypes() {
		if (!hitboxTypes.isEmpty())
			return hitboxTypes;

		Arrays.stream(values()).forEach(decorationType ->
			hitboxTypes.addAll(decorationType.getDecoration().getHitboxes()
				.stream()
				.map(Hitbox::getMaterial)
				.toList()));

		return hitboxTypes;
	}

}

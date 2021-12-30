package gg.projecteden.nexus.utils;

import gg.projecteden.parchment.inventory.RecipeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;

@UtilityClass
public class MaterialUtils {
	public final Mineral DIAMOND = new Mineral(Material.DIAMOND);
	public final Mineral NETHERITE = Mineral.builder().mineral(Material.NETHERITE_INGOT).oreName(Material.ANCIENT_DEBRIS).build();
	public final Mineral IRON = new Mineral(Material.IRON_INGOT);
	public final Mineral GOLD = Mineral.builder().mineral(Material.GOLD_INGOT).extraBlock(Material.NETHER_GOLD_ORE).build();
	public final Mineral EMERALD = new Mineral(Material.EMERALD);
	public final Mineral QUARTZ = Mineral.builder().mineral(Material.QUARTZ).oreName(Material.NETHER_QUARTZ_ORE).build();
	public final Mineral COAL = new Mineral(Material.COAL);
	public final Mineral LAPIS = Mineral.builder().mineral(Material.LAPIS_LAZULI).prefix("LAPIS").build();
	public final Mineral REDSTONE = new Mineral(Material.REDSTONE);

	public @Nullable ItemStack oreToIngot(@NotNull World world, @NotNull Material ore) {
		return world.smeltItem(new ItemStack(ore), RecipeType.BLASTING);
	}

	public @Nullable ItemStack rawToCooked(@NotNull World world, @NotNull Material raw) {
		return world.smeltItem(new ItemStack(raw), RecipeType.CAMPFIRE_COOKING);
	}

	@Getter
	@AllArgsConstructor
	@RequiredArgsConstructor
	@EqualsAndHashCode
	@Builder
	public static class Mineral {
		private final @NotNull Material mineral;
		private @Nullable String prefix;
		private @Nullable String oreName;
		@Singular
		private final @NotNull Set<Material> extraBlocks;

		public Mineral(@NotNull Material mineral) {
			this(mineral, Collections.emptySet());
		}

		public @NotNull String getPrefix() {
			if (prefix == null)
				prefix = mineral.name().replace("_INGOT", "");
			return prefix;
		}

		public @NotNull String getOreName() {
			if (oreName == null)
				oreName = prefix + "_ORE";
			return oreName;
		}

		public boolean blockIsMineral(@NotNull Material block) {
			String blockName = block.name();
			return getOreName().equals(blockName)
				|| ("RAW_" + prefix).equals(blockName)
				|| ("RAW_" + prefix + "_BLOCK").equals(blockName)
				|| (prefix + "_BLOCK").equals(blockName)
				|| extraBlocks.contains(block);
		}

		private static class MineralBuilder {
			public MineralBuilder oreName(String oreName) {
				this.oreName = oreName;
				return this;
			}

			public MineralBuilder oreName(Material oreBlock) {
				return oreName(oreBlock.name());
			}
		}
	}
}

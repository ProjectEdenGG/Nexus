package gg.projecteden.nexus.features.resourcepack.decoration.types.toggle;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Cyclable;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.FloorThing;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.SoundBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiantCandle extends FloorThing implements Cyclable {
	public static boolean CAN_CYCLE = true;
	public static boolean CYCLE_PREVIOUS = false;
	CandleType candleType;
	boolean lit;

	public GiantCandle(String name, CandleType candleType, boolean lit) {
		super(false, name, candleType.getItemModel(lit), HitboxSingle._1x1_BARRIER);
		this.candleType = candleType;
		this.lit = lit;
	}

	@Override
	public ItemModelType getNextItemModel(Player player, ItemStack tool) {
		return candleType.getNextItemModel(lit, tool);
	}

	@Override
	public ItemModelType getPreviousItemModel(Player player, ItemStack tool) {
		return null;
	}

	@Override
	public ItemModelType getBaseItemModel() {
		return candleType.getItemModel(false);
	}

	@Override
	public boolean canCycle(Player player, ItemStack tool) {
		return CAN_CYCLE;
	}

	@Override
	public boolean canCycleToPrevious() {
		return CYCLE_PREVIOUS;
	}

	@Override
	public void playSound(@NonNull Block origin) {
		playSound(lit, origin);
	}

	@Getter
	@AllArgsConstructor
	public enum CandleType implements ICandle {
		ONE(ItemModelType.GIANT_CANDLES_ONE_LIT, ItemModelType.GIANT_CANDLES_ONE_UNLIT),
		TWO(ItemModelType.GIANT_CANDLES_TWO_LIT, ItemModelType.GIANT_CANDLES_TWO_UNLIT),
		THREE(ItemModelType.GIANT_CANDLES_THREE_LIT, ItemModelType.GIANT_CANDLES_THREE_UNLIT),
		;

		private final ItemModelType lit;
		private final ItemModelType unlit;
	}

	public static void playSound(boolean lit, Block origin) {
		if (!lit)
			return;

		new SoundBuilder(Sound.BLOCK_CANDLE_EXTINGUISH).volume(0.5).location(origin).play();
	}

	public interface ICandle {
		ItemModelType getLit();

		ItemModelType getUnlit();

		default ItemModelType getItemModel(boolean isLit) {
			return isLit ? getLit() : getUnlit();
		}

		default ItemModelType getNextItemModel(boolean lit, ItemStack tool) {
			if (Nullables.isNullOrAir(tool)) {
				return lit ? getItemModel(false) : null;
			}

			if (tool.getType() == Material.FLINT_AND_STEEL) {
				return !lit ? getItemModel(true) : null;
			}

			return null;
		}
	}


}

package gg.projecteden.nexus.features.resourcepack.decoration.types.toggle;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Cyclable;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableFloorThing;
import gg.projecteden.nexus.features.resourcepack.decoration.types.toggle.GiantCandle.ICandle;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DyeableGiantCandle extends DyeableFloorThing implements Cyclable {
	DyeableCandleType candleType;
	boolean lit;

	public DyeableGiantCandle(String name, DyeableCandleType candleType, boolean lit) {
		super(false, name, candleType.getItemModel(lit), ColorableType.DYE, HitboxSingle._1x1_BARRIER);
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
		return GiantCandle.CAN_CYCLE;
	}

	@Override
	public boolean canCycleToPrevious() {
		return GiantCandle.CYCLE_PREVIOUS;
	}

	@Override
	public void playSound(@NonNull Block origin) {
		GiantCandle.playSound(lit, origin);
	}

	@Getter
	@AllArgsConstructor
	public enum DyeableCandleType implements ICandle {
		ONE(ItemModelType.GIANT_CANDLES_ONE_LIT_DYEABLE, ItemModelType.GIANT_CANDLES_ONE_UNLIT_DYEABLE),
		TWO(ItemModelType.GIANT_CANDLES_TWO_LIT_DYEABLE, ItemModelType.GIANT_CANDLES_TWO_UNLIT_DYEABLE),
		THREE(ItemModelType.GIANT_CANDLES_THREE_LIT_DYEABLE, ItemModelType.GIANT_CANDLES_THREE_UNLIT_DYEABLE),
		;

		private final ItemModelType lit;
		private final ItemModelType unlit;
	}
}

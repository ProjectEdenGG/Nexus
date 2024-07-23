package gg.projecteden.nexus.utils;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.inventory.InventoryType;

public class InventoryUtils {

	// https://minecraft.wiki/w/Inventory#External_inventories
	@Getter
	@AllArgsConstructor
	public enum BlockInventoryType {
		CHEST(InventoryType.CHEST),
		BARREL(InventoryType.BARREL),
		SHULKER_BOX(InventoryType.SHULKER_BOX),

		FURNACE(InventoryType.FURNACE),
		BLAST_FURNACE(InventoryType.BLAST_FURNACE),
		SMOKER(InventoryType.SMOKER),

		HOPPER(InventoryType.HOPPER),
		DISPENSER(InventoryType.DISPENSER),
		DROPPER(InventoryType.DROPPER),

		BREWING_STAND(InventoryType.BREWING),
		LECTERN(InventoryType.LECTERN),
		;

		private final InventoryType inventoryType;

		public static BlockInventoryType of(String input) {
			for (BlockInventoryType value : values())
				if (value.name().equalsIgnoreCase(input))
					return value;
			return null;
		}
	}

	public static class BlockInventoryTypeFlag extends Flag<BlockInventoryType> {
		public BlockInventoryTypeFlag(String name) {
			super(name);
		}

		@Override
		public BlockInventoryType parseInput(FlagContext context) throws InvalidFlagFormat {
			BlockInventoryType blockInventoryType = BlockInventoryType.of(context.getUserInput().trim());

			if (blockInventoryType != null)
				return blockInventoryType;

			throw new InvalidFlagFormat("Unable to find the block inventory type " + context.getUserInput());
		}

		@Override
		public Object marshal(BlockInventoryType type) {
			return type.name();
		}

		@Override
		public BlockInventoryType unmarshal(Object o) {
			return BlockInventoryType.of(o.toString().trim());
		}

	}
}

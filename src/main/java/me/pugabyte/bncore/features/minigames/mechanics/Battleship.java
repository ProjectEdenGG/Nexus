package me.pugabyte.bncore.features.minigames.mechanics;

import lombok.Getter;
import me.pugabyte.bncore.features.minigames.models.annotations.Regenerating;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teams.BalancedTeamMechanic;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

/*
	Regions:
		team_<team>
		a0_(ships|pegs)_<team>
		ships
		config
		floor
		grid
 */

@Regenerating("board")
public class Battleship extends BalancedTeamMechanic {

	@Override
	public String getName() {
		return "Battleship";
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.BOAT);
	}

	enum Ship {
		CRUISER(2, ColorType.LIGHT_GREEN),
		SUBMARINE(3, ColorType.LIGHT_RED),
		DESTROYER(3, ColorType.PURPLE),
		BATTLESHIP(4, ColorType.ORANGE),
		CARRIER(5, ColorType.CYAN);

		@Getter
		private int length;
		@Getter
		private ColorType color;

		Ship(int length, ColorType color) {
			this.length = length;
			this.color = color;
		}

		@Override
		public String toString() {
			return Utils.camelCase(name());
		}

		public ItemStack getItem() {
			return color.getItemStack(Material.CONCRETE);
		}

		public static Ship get(Block block) {
			for (Ship ship : Ship.values())
				if (block.getData() == ship.getColor().getDurability())
					return ship;

			throw new IllegalArgumentException();
		}
	}

}

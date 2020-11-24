package me.pugabyte.nexus.features.events.y2020.pugmas20.quests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20;
import me.pugabyte.nexus.features.events.y2020.pugmas20.menu.AdventMenu;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.Trees.PugmasTreeType;
import me.pugabyte.nexus.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.nexus.utils.StringUtils.camelCase;

@NoArgsConstructor
public class OrnamentVendor {
	public enum Ornament {
		RED(PugmasTreeType.BLOODWOOD, -3),
		ORANGE(PugmasTreeType.MAHOGANY, -4),
		YELLOW(PugmasTreeType.EUCALYPTUS, -5),
		GREEN(PugmasTreeType.WILLOW, -6),
		CYAN(PugmasTreeType.CRYSTAL, -7),
		BLUE(PugmasTreeType.MAGIC, -8),
		PURPLE(PugmasTreeType.OAK, -9),
		MAGENTA(PugmasTreeType.TEAK, -10),
		GRAY(PugmasTreeType.MAPLE, -11),
		WHITE(PugmasTreeType.BLISTERWOOD, -12);

		@Getter
		private final PugmasTreeType treeType;
		@Getter
		private final ItemStack skull;

		Ornament(PugmasTreeType treeType, int relative) {
			this.treeType = treeType;
			ItemStack itemStack = AdventMenu.origin.getRelative(relative, 0, 0).getDrops().stream().findFirst().orElse(null);
			if (ItemUtils.isNullOrAir(itemStack))
				this.skull = null;
			else
				this.skull = Pugmas20.item(itemStack).name(camelCase(name() + " Ornament")).build();
		}
	}

	public static List<ItemStack> getOrnaments(Player player) {
		List<ItemStack> ornaments = new ArrayList<>();
		for (Ornament ornament : Ornament.values()) {
			if (player.getInventory().containsAtLeast(ornament.getSkull(), 1))
				ornaments.add(ornament.getSkull());
		}

		return ornaments;
	}
}

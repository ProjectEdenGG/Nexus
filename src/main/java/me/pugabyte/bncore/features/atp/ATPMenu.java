package me.pugabyte.bncore.features.atp;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.models.warps.WarpType;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.entity.Player;

public class ATPMenu {

	public void open(Player player) {
		SmartInventory INV = SmartInventory.builder()
				.size(5, 9)
				.title(StringUtils.colorize("&3Animal Teleport Pens"))
				.provider(new ATPMenuProvider())
				.build();
		INV.open(player);
	}

	public void openLegacy(Player player) {
		SmartInventory INV = SmartInventory.builder()
				.size(5, 9)
				.title(StringUtils.colorize("&3Animal Teleport Pens"))
				.provider(new ATPMenuProvider(WarpType.LEGACY_ATP))
				.build();
		INV.open(player);
	}

}

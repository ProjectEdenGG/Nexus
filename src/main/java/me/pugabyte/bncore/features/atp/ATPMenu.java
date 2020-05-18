package me.pugabyte.bncore.features.atp;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.features.atp.ATPMenuProvider.ATPGroup;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.entity.Player;

public class ATPMenu {

	public void open(Player player, ATPGroup group) {
		SmartInventory INV = SmartInventory.builder()
				.size(5, 9)
				.title(StringUtils.colorize("&3Animal Teleport Pens"))
				.provider(new ATPMenuProvider(group))
				.build();
		INV.open(player);
	}

}

package me.pugabyte.nexus.features.atp;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.nexus.features.atp.ATPMenuProvider.ATPGroup;
import me.pugabyte.nexus.utils.StringUtils;
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

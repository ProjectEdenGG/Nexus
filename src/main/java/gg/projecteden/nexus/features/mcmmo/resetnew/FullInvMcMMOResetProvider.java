package gg.projecteden.nexus.features.mcmmo.resetnew;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.PlayerInventoryProvider;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class FullInvMcMMOResetProvider extends PlayerInventoryProvider {

	@Override
	public void init() {
		addPlayerInvItem(5, ClickableItem.of(new ItemStack(Material.GRASS_BLOCK), e -> PlayerUtils.send(e.getPlayer(), "test")));
	}

}

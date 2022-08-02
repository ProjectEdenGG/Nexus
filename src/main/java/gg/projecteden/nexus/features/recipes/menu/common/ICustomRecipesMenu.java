package gg.projecteden.nexus.features.recipes.menu.common;

import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Rows(3)
public abstract class ICustomRecipesMenu extends InventoryProvider {
	protected ICustomRecipesMenu previousMenu;

	@Override
	public void init() {
		if (previousMenu == null)
			addCloseItem();
		else
			addBackItem(e -> previousMenu.open(player));
	}

}

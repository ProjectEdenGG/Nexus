package gg.projecteden.nexus.features.menus.api.content;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.features.resourcepack.models.font.InventoryTexture;
import gg.projecteden.nexus.utils.ItemBuilder;

public abstract class ScrollableInventoryProvider extends InventoryProvider {

	@Override
	public String getTitle(int page) {
		return InventoryTexture.getScrollTitle(getPages(), page);
	}

	@Override
	public void init() {
		final int page = contents.pagination().getPage();
		if (page > 0)
			contents.set(8, ClickableItem.of(new ItemBuilder(ItemModelType.INVISIBLE).name("&eScroll Up").build(), e -> open(viewer, page - 1)));
		else
			contents.set(8, ClickableItem.AIR);

		if (page < (getPages() - 1))
			contents.set(53, ClickableItem.of(new ItemBuilder(ItemModelType.INVISIBLE).name("&eScroll Down").build(), e -> open(viewer, page + 1)));
		else
			contents.set(53, ClickableItem.AIR);
	}

	abstract public int getPages();

}

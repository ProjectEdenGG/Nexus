package gg.projecteden.nexus.features.menus.api.content;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.FontUtils;
import gg.projecteden.nexus.utils.ItemBuilder;

import java.util.List;

public abstract class ScrollableInventoryProvider extends InventoryProvider {

	protected static final String BASE = "久";
	protected static final List<List<String>> SCROLLER_INDEXES = List.of(
		List.of("魉"),
		List.of("辆", "沩"),
		List.of("漷", "秬", "籽"),
		List.of("醭", "泽", "转", "洼"),
		List.of("髌", "泗", "穙", "邸", "甬"),
		List.of("粽", "轩", "乏", "袭", "说", "魋"),
		List.of("廋", "糠", "稿", "膑", "配", "丸", "蝻"),
		List.of("程", "磉", "暿", "飗", "毪", "轵", "浬", "腒"),
		List.of("骷", "淟", "貉", "陇", "鲌", "砵", "蚯", "涞", "轮"),
		List.of("晌", "夐", "暝", "赳", "盩", "墘", "貌", "糈", "疍", "糅")
	);

	@Override
	public String getTitle(int page) {
		return
			"&f" +
				FontUtils.minus(10) +
				BASE +
				FontUtils.minus(33) +
				SCROLLER_INDEXES.get(getPages() - 1).get(page) +
				FontUtils.minus(200);
	}

	@Override
	public void init() {
		final int page = contents.pagination().getPage();
		if (page > 0)
			contents.set(8, ClickableItem.of(new ItemBuilder(CustomMaterial.INVISIBLE).name("&eScroll Up").build(), e -> open(viewer, page - 1)));
		else
			contents.set(8, ClickableItem.AIR);

		if (page < (getPages() - 1))
			contents.set(53, ClickableItem.of(new ItemBuilder(CustomMaterial.INVISIBLE).name("&eScroll Down").build(), e -> open(viewer, page + 1)));
		else
			contents.set(53, ClickableItem.AIR);
	}

	abstract public int getPages();

}

package gg.projecteden.nexus.features.mcmmo.reset;

import gg.projecteden.nexus.features.mcmmo.reset.McMMOResetShopMenu.SkillTokenFilterType;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class McMMOResetShopRewardsMenu extends InventoryProvider {
	private final SkillTokenFilterType filter;
	private final McMMOResetShopMenu previousMenu;

	@Override
	public void init() {
		addBackItem(previousMenu);

		paginate(ResetReward.filter(filter).stream().map(reward -> {
			return ClickableItem.of(reward.buildDisplayItem(), e -> {

			});
		}).toList());
	}

}

package gg.projecteden.nexus.features.shops;

import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.shop.ShopService;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;

public class Shops extends Feature {
	public static final String PREFIX = StringUtils.getPrefix("Shops");

	@Override
	public void onStart() {
		Tasks.async(() -> {
			new ShopService().cacheAll();
			Market.load();
		});
	}

}

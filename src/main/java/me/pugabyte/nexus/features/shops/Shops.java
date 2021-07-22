package me.pugabyte.nexus.features.shops;

import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;

public class Shops extends Feature {
	public static final String PREFIX = StringUtils.getPrefix("Shops");

	@Override
	public void onStart() {
		Tasks.waitAsync(5, Market::load);
	}

}

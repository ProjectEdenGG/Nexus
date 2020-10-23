package me.pugabyte.bncore.features.homes;

import me.pugabyte.bncore.framework.features.Feature;
import me.pugabyte.bncore.utils.StringUtils;

public class HomesFeature extends Feature {
	public final static String PREFIX = StringUtils.getPrefix("Homes");
	public final static int maxHomes = 100;

	@Override
	public void startup() {
		new HomeListener();
	}

}

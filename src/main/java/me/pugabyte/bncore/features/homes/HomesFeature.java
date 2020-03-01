package me.pugabyte.bncore.features.homes;

import me.pugabyte.bncore.utils.StringUtils;

public class HomesFeature {
	public final static String PREFIX = StringUtils.getPrefix("Homes");
	public final static int maxHomes = 100;

	public HomesFeature() {
		new HomeListener();
	}

}

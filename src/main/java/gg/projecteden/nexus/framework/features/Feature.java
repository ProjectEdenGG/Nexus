package gg.projecteden.nexus.framework.features;

import gg.projecteden.nexus.utils.StringUtils;

public abstract class Feature {
	public String PREFIX = StringUtils.getPrefix(getName());
	public String DISCORD_PREFIX = StringUtils.getDiscordPrefix(getName());

	public String getName() {
		return Features.prettyName(this);
	}

	public String getPrefix() {
		return PREFIX;
	}

	public void onStart() {
	}

	public void onStop() {
	}

	public void reload() {
		onStop();
		onStart();
	}

	public boolean isUnreleased() {
		return getClass().isAnnotationPresent(Unreleased.class);
	}

}

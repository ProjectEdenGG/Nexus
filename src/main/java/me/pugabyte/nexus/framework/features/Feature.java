package me.pugabyte.nexus.framework.features;

import me.pugabyte.nexus.utils.StringUtils;

public abstract class Feature {
	public String PREFIX = StringUtils.getPrefix(getName());

	public String getName() {
		return Features.prettyName(this);
	}
	public String getPrefix() {
		return PREFIX;
	}

	public abstract void startup();

	public void shutdown() {}

}

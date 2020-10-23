package me.pugabyte.bncore.framework.features;

import me.pugabyte.bncore.utils.StringUtils;

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

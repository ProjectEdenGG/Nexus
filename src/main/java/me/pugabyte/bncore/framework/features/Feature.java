package me.pugabyte.bncore.framework.features;

public abstract class Feature {

	public String getName() {
		return Features.prettyName(this);
	}

	public abstract void startup();

	public void shutdown() {}

}

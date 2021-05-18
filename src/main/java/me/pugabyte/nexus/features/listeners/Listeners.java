package me.pugabyte.nexus.features.listeners;

import me.pugabyte.nexus.framework.features.Feature;

import static me.pugabyte.nexus.utils.Utils.registerListeners;

public class Listeners extends Feature {

	@Override
	public void onStart() {
		registerListeners(getClass().getPackage().getName());
	}

}
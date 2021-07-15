package me.pugabyte.nexus.features.ambience.managers.common;

import me.pugabyte.nexus.models.ambience.AmbienceUser;

public abstract class AmbienceManager {

	public void tick() {}

	public void update(AmbienceUser user) {}

	public void init(AmbienceUser user) {}

	public void onStart() {}

	public void onStop() {}

}

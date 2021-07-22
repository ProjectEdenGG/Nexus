package gg.projecteden.nexus.features.ambience.managers.common;

import gg.projecteden.nexus.models.ambience.AmbienceUser;

public abstract class AmbienceManager {

	public void tick() {}

	public void update(AmbienceUser user) {}

	public void init(AmbienceUser user) {}

	public void onStart() {}

	public void onStop() {}

}

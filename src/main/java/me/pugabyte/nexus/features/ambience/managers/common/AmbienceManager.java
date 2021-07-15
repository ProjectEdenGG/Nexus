package me.pugabyte.nexus.features.ambience.managers.common;

import lombok.Getter;
import me.pugabyte.nexus.models.ambience.AmbienceUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class AmbienceManager<T> {
	@Getter
	protected final Map<UUID, List<T>> activeEffects = new HashMap<>();

	public List<T> getEffects(UUID uuid) {
		return activeEffects.computeIfAbsent(uuid, $ -> new ArrayList<>());
	}

	public void addInstance(AmbienceUser user, T particleEffect) {
		getEffects(user.getUuid()).add(particleEffect);
	}

	public void tick() {}

	public void update(AmbienceUser user) {}

	public void init(AmbienceUser user) {}

	public void onStart() {}

	public void onStop() {
		getActiveEffects().clear();
	}

}

package me.pugabyte.nexus.features.ambience.managers.common;

import lombok.AllArgsConstructor;
import me.pugabyte.nexus.features.ambience.managers.ParticleEffectManager;
import me.pugabyte.nexus.models.ambience.AmbienceUser;

@AllArgsConstructor
public enum AmbienceManagers {
	PARTICLE_EFFECTS(new ParticleEffectManager()),
	;

	private final AmbienceManager<?> manager;

	public <T> AmbienceManager<T> get() {
		return (AmbienceManager<T>) manager;
	}

	public static void tick() {
		for (AmbienceManagers manager : values())
			manager.get().tick();
	}

	public static void update(AmbienceUser user) {
		for (AmbienceManagers manager : values())
			manager.get().update(user);
	}

	public static void start() {
		for (AmbienceManagers manager : values())
			manager.get().onStart();
	}

	public static void stop() {
		for (AmbienceManagers manager : values())
			manager.get().onStop();
	}

}

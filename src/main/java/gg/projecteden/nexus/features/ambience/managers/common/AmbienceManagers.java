package gg.projecteden.nexus.features.ambience.managers.common;

import gg.projecteden.nexus.features.ambience.managers.ParticleEffectManager;
import gg.projecteden.nexus.features.ambience.managers.SoundEffectManager;
import gg.projecteden.nexus.models.ambience.AmbienceUser;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AmbienceManagers {
	PARTICLES(new ParticleEffectManager()),
	SOUNDS(new SoundEffectManager()),
	;

	private final AmbienceManager manager;

	public <T extends AmbienceManager> T get() {
		return (T) manager;
	}

	public static void tick() {
		for (AmbienceManagers manager : values())
			manager.get().tick();
	}

	public static void init(AmbienceUser user) {
		for (AmbienceManagers manager : values())
			manager.get().init(user);
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

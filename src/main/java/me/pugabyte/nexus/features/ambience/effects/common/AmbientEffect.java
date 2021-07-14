package me.pugabyte.nexus.features.ambience.effects.common;

import me.pugabyte.nexus.models.ambience.AmbienceUser;

public interface AmbientEffect<T> {

	AmbienceUser getUser();

	T getEffectType();

}

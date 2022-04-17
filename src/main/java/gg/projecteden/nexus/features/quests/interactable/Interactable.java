package gg.projecteden.nexus.features.quests.interactable;

import lombok.SneakyThrows;

import java.lang.reflect.Field;

public interface Interactable {

	String getName();

	String name();

	@SneakyThrows
	private Field getField() {
		return getClass().getField(name());
	}

	default boolean isAlive() {
		return !isInanimate();
	}

	default boolean isInanimate() {
		return getField().isAnnotationPresent(Inanimate.class);
	}

}

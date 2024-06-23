package gg.projecteden.nexus.features.events.y2024.pugmas24.quests;

import gg.projecteden.nexus.features.quests.interactable.InteractableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Entity;

import java.util.UUID;
import java.util.function.Predicate;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;

@Getter
@AllArgsConstructor
public enum Pugmas24Entity implements InteractableEntity {
	// TODO
	;

	private final String name;
	private final UUID uuid;
	private final Predicate<Entity> predicate;

	Pugmas24Entity(String name, String uuid) {
		this(name, UUID.fromString(uuid), isNullOrEmpty(uuid) ? null : entity -> entity.getUniqueId().equals(UUID.fromString(uuid)));
	}

	Pugmas24Entity(String name, Predicate<Entity> predicate) {
		this(name, null, predicate);
	}
}

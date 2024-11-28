package gg.projecteden.nexus.features.events.y2025.pugmas25.quests;

import gg.projecteden.nexus.features.quests.interactable.InteractableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Entity;

import java.util.UUID;
import java.util.function.Predicate;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;

@Getter
@AllArgsConstructor
public enum Pugmas25Entity implements InteractableEntity {
	// TODO
	;

	private final String name;
	private final UUID uuid;
	private final Predicate<Entity> predicate;

	Pugmas25Entity(String name, String uuid) {
		this(name, UUID.fromString(uuid), isNullOrEmpty(uuid) ? null : entity -> entity.getUniqueId().equals(UUID.fromString(uuid)));
	}

	Pugmas25Entity(String name, Predicate<Entity> predicate) {
		this(name, null, predicate);
	}
}

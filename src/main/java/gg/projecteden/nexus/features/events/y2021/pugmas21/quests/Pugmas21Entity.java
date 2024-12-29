package gg.projecteden.nexus.features.events.y2021.pugmas21.quests;

import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.nexus.features.quests.interactable.InteractableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Entity;

import java.util.UUID;
import java.util.function.Predicate;

@Getter
@AllArgsConstructor
public enum Pugmas21Entity implements InteractableEntity {
	PENGUIN_1("Penguin", "120aa6d7-b44b-4b6d-ba48-40e1d9297e03"),
	PENGUIN_2("Penguin", "5da7b766-779d-43b3-abf0-3683bf7d909a"),
	GUARDIAN("Guardian", ""),
	FISH_PILE("Fish Pile", ""),
	;

	Pugmas21Entity(String name, String uuid) {
		this(name, Nullables.isNullOrEmpty(uuid) ? null : UUID.fromString(uuid), Nullables.isNullOrEmpty(uuid) ? null : entity -> entity.getUniqueId().equals(UUID.fromString(uuid)));
	}

	Pugmas21Entity(String name, Predicate<Entity> predicate) {
		this(name, null, predicate);
	}

	private final String name;
	private final UUID uuid;
	private final Predicate<Entity> predicate;

	public static Pugmas21Entity of(Entity entity) {
		if (entity == null)
			return null;

		return of(entity.getUniqueId());
	}

	public static Pugmas21Entity of(UUID uuid) {
		for (Pugmas21Entity entity : values()) {
			if (entity.getUuid() == null)
				continue;

			if (entity.getUuid().equals(uuid))
				return entity;
		}

		return null;
	}

}

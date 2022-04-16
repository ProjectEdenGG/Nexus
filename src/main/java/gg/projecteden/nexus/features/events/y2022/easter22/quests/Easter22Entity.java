package gg.projecteden.nexus.features.events.y2022.easter22.quests;

import gg.projecteden.nexus.features.quests.interactable.InteractableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Entity;

import java.util.UUID;

@Getter
@AllArgsConstructor
public enum Easter22Entity implements InteractableEntity {
	EASTER_BUNNY("Easter Bunny", "b061f485-969f-4294-984b-c4f4be95a724"),
	;

	Easter22Entity(String name, String entityId) {
		this(name, entityId.equals("") ? null : UUID.fromString(entityId));
	}

	private final String name;
	private final UUID entityId;

	public static Easter22Entity of(Entity entity) {
		if (entity == null)
			return null;

		return of(entity.getUniqueId());
	}

	public static Easter22Entity of(UUID uuid) {
		for (Easter22Entity entity : values()) {
			if (entity.getEntityId() == null)
				continue;

			if (entity.getEntityId().equals(uuid))
				return entity;
		}

		return null;
	}

}

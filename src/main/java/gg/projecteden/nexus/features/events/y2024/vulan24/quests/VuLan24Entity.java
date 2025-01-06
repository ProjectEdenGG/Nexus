package gg.projecteden.nexus.features.events.y2024.vulan24.quests;

import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.nexus.features.quests.interactable.InteractableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Entity;

import java.util.UUID;
import java.util.function.Predicate;

@Getter
@AllArgsConstructor
public enum VuLan24Entity implements InteractableEntity {
	;

	private final String name;
	private final UUID uuid;
	private final Predicate<Entity> predicate;

	VuLan24Entity(String name, String uuid) {
		this(name, UUID.fromString(uuid), Nullables.isNullOrEmpty(uuid) ? null : entity -> entity.getUniqueId().equals(UUID.fromString(uuid)));
	}

	VuLan24Entity(String name, Predicate<Entity> predicate) {
		this(name, null, predicate);
	}

}

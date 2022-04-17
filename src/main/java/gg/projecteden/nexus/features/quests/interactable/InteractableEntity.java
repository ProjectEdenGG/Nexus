package gg.projecteden.nexus.features.quests.interactable;

import org.bukkit.entity.Entity;

import java.util.UUID;
import java.util.function.Predicate;

public interface InteractableEntity extends Interactable {

	UUID getUuid();

	Predicate<Entity> getPredicate();

}

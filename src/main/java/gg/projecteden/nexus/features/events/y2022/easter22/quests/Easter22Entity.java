package gg.projecteden.nexus.features.events.y2022.easter22.quests;

import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.nexus.features.events.y2022.easter22.Easter22;
import gg.projecteden.nexus.features.quests.interactable.Inanimate;
import gg.projecteden.nexus.features.quests.interactable.InteractableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.Predicate;

@Getter
@AllArgsConstructor
public enum Easter22Entity implements InteractableEntity {
	EASTER_BUNNY("Easter Bunny", "b061f485-969f-4294-984b-c4f4be95a724"),
	@Inanimate
	EASTERS_PAINTBRUSH("Easter's Paintbrush", "b0041dee-f1a3-4bb7-b77f-24ff89feb49b"),
	@Inanimate
	EASTER_EGG("Easter Egg", entity -> {
		if (!Easter22.get().isAtEvent(entity))
			return false;

		if (!(entity instanceof ItemFrame itemFrame))
			return false;

		final ItemStack item = itemFrame.getItem();
		if (gg.projecteden.nexus.utils.Nullables.isNullOrAir(item))
			return false;

		if (item.getType() != Material.PAPER)
			return false;
//
//		final int modelId = Model.of(item);
//		return modelId >= 2001 && modelId <= 2020;
		return false;
	}),
	;

	private final String name;
	private final UUID uuid;
	private final Predicate<Entity> predicate;

	Easter22Entity(String name, String uuid) {
		this(name, UUID.fromString(uuid), Nullables.isNullOrEmpty(uuid) ? null : entity -> entity.getUniqueId().equals(UUID.fromString(uuid)));
	}

	Easter22Entity(String name, Predicate<Entity> predicate) {
		this(name, null, predicate);
	}

}

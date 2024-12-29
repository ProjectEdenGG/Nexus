package gg.projecteden.nexus.models.dumpster;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import lombok.*;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Data
@Entity(value = "dumpster", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, ItemStackConverter.class})
public class Dumpster implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	@Embedded
	private List<ItemStack> items = new ArrayList<>();

	public void add(ItemStack... itemStack) {
		add(Arrays.asList(itemStack));
	}

	public void add(Collection<? extends ItemStack> itemStacks) {
		if (true) return;
		itemStacks.stream()
				.filter(newItemStack -> !MaterialTag.UNOBTAINABLE.isTagged(newItemStack.getType()))
				.forEach(newItemStack -> ItemUtils.combine(items, newItemStack));
	}

}

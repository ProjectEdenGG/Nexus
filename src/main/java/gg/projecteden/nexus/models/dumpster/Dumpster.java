package gg.projecteden.nexus.models.dumpster;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.utils.MaterialTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static gg.projecteden.nexus.utils.ItemUtils.combine;

@Data
@Builder
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
				.forEach(newItemStack -> combine(items, newItemStack));
	}

}

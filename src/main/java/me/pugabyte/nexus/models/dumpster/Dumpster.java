package me.pugabyte.nexus.models.dumpster;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.utils.MaterialTag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static me.pugabyte.nexus.utils.ItemUtils.combine;

@Data
@Builder
@Entity("dumpster")
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

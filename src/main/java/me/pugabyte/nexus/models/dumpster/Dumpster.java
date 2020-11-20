package me.pugabyte.nexus.models.dumpster;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.ItemMetaConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.utils.MaterialTag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
@Builder
@Entity("dumpster")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, ItemStackConverter.class, ItemMetaConverter.class})
public class Dumpster extends PlayerOwnedObject {
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
				.filter(itemStack -> !MaterialTag.UNOBTAINABLE.isTagged(itemStack.getType()))
				.forEach(newItemStack -> {
					combine(newItemStack);

					if (newItemStack.getAmount() > 0)
						items.add(new ItemStack(newItemStack));
				});
	}

	public void combine(ItemStack newItemStack) {
		Optional<ItemStack> matching = items.stream()
				.filter(existing -> existing.isSimilar(newItemStack) && existing.getAmount() < existing.getType().getMaxStackSize())
				.findFirst();

		if (matching.isPresent()) {
			ItemStack match = matching.get();
			items.remove(match);
			int amountICanAdd = Math.min(newItemStack.getAmount(), match.getType().getMaxStackSize() - match.getAmount());
			match.setAmount(match.getAmount() + amountICanAdd);
			items.add(new ItemStack(match));

			newItemStack.setAmount(newItemStack.getAmount() - amountICanAdd);
		}
	}

}

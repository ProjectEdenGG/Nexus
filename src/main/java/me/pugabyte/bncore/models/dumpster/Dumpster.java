package me.pugabyte.bncore.models.dumpster;

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
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.ItemMetaConverter;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.ItemStackConverter;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import me.pugabyte.bncore.utils.MaterialTag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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
		itemStacks.stream()
				.filter(itemStack -> !MaterialTag.UNOBTAINABLE.isTagged(itemStack.getType()))
				.forEach(itemStack -> items.add(new ItemStack(itemStack)));
	}

}

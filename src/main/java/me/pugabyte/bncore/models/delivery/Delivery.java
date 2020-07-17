package me.pugabyte.bncore.models.delivery;

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
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity("delivery")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, ItemStackConverter.class, ItemMetaConverter.class})
public class Delivery extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	@Embedded
	private List<ItemStack> survivalItems = new ArrayList<>();
	@Embedded
	private List<ItemStack> skyblockItems = new ArrayList<>();

	public void addToSurvival(ItemStack... itemStack) {
		addToSurvival(Arrays.asList(itemStack));
	}

	public void addToSurvival(Collection<? extends ItemStack> itemStacks) {
		survivalItems.addAll(itemStacks);
	}

	public void addToSkyblock(ItemStack... itemStack) {
		addToSkyblock(Arrays.asList(itemStack));
	}

	public void addToSkyblock(Collection<? extends ItemStack> itemStacks) {
		skyblockItems.addAll(itemStacks);
	}
}

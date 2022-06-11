package gg.projecteden.nexus.models.legacy;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Entity(value = "item_transfer_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, ItemStackConverter.class})
public class ItemTransferUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<ReviewStatus, List<ItemStack>> items = new ConcurrentHashMap<>();

	public List<ItemStack> getItems(ReviewStatus status) {
		return items.computeIfAbsent(status, $ -> new ArrayList<>());
	}

	public void accept(ItemStack item) {
		getItems(ReviewStatus.PENDING).remove(item);
		getItems(ReviewStatus.ACCEPTED).add(item);
	}

	public void deny(ItemStack item) {
		getItems(ReviewStatus.PENDING).remove(item);
		getItems(ReviewStatus.DENIED).add(item);
	}

	public void acceptAll() {
		getItems(ReviewStatus.ACCEPTED).addAll(getItems(ReviewStatus.PENDING));
		getItems(ReviewStatus.PENDING).clear();
	}

	public void denyAll() {
		getItems(ReviewStatus.DENIED).addAll(getItems(ReviewStatus.PENDING));
		getItems(ReviewStatus.PENDING).clear();
	}

	public enum ReviewStatus {
		PENDING,
		ACCEPTED,
		DENIED,
	}

}

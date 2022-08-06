package gg.projecteden.nexus.models.legacy.itemtransfer;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import gg.projecteden.nexus.models.crate.CrateType;
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
@Entity(value = "legacy_item_transfer_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, ItemStackConverter.class})
public class LegacyItemTransferUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<ReviewStatus, List<ItemStack>> items = new ConcurrentHashMap<>();
	private Map<CrateType, Integer> crateKeys = new ConcurrentHashMap<>();

	public List<ItemStack> getItems(ReviewStatus status) {
		return items.computeIfAbsent(status, $ -> new ArrayList<>());
	}

	public int getCount(ReviewStatus status) {
		return getItems(status).stream().mapToInt(ItemStack::getAmount).sum();
	}

	public void accept(ItemStack item) {
		getItems(ReviewStatus.PENDING).remove(item);
		getItems(ReviewStatus.ACCEPTED).add(item);
	}

	public void deny(ItemStack item) {
		getItems(ReviewStatus.PENDING).remove(item);
		getItems(ReviewStatus.DENIED).add(item);
	}

	public void delay(ItemStack item) {
		getItems(ReviewStatus.PENDING).remove(item);
		getItems(ReviewStatus.DELAYED).add(item);
	}

	public int acceptAll() {
		return moveItems(ReviewStatus.ACCEPTED);
	}

	public int denyAll() {
		return moveItems(ReviewStatus.DENIED);
	}

	private int moveItems(ReviewStatus status) {
		int count = getCount(ReviewStatus.PENDING);
		final List<ItemStack> pending = getItems(ReviewStatus.PENDING);
		getItems(status).addAll(pending);
		pending.clear();
		return count;
	}

	public enum ReviewStatus {
		PENDING,
		ACCEPTED,
		DENIED,
		DELAYED,
	}

}

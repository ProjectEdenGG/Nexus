package me.pugabyte.bncore.models.delivery;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.features.delivery.DeliveryWorldMenu;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.ItemMetaConverter;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.ItemStackConverter;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import me.pugabyte.bncore.utils.ItemUtils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static me.pugabyte.bncore.utils.SoundUtils.playSound;

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
	private Map<WorldGroup, List<ItemStack>> items = new HashMap<>();

	@Getter
	private static final List<WorldGroup> supportedWorldGroups = Arrays.asList(WorldGroup.SURVIVAL, WorldGroup.CREATIVE, WorldGroup.SKYBLOCK);

	public void add(WorldGroup worldGroup, ItemStack... items) {
		add(worldGroup, Arrays.asList(items));
	}

	public void add(WorldGroup worldGroup, List<ItemStack> items) {
		List<ItemStack> existing = get(worldGroup);
		existing.addAll(items);
		this.items.put(worldGroup, existing);
	}

	public List<ItemStack> get(WorldGroup worldGroup) {
		return items.getOrDefault(worldGroup, new ArrayList<>());
	}

	public void setupDelivery(ItemStack item) {
		new DeliveryWorldMenu(item).open(getPlayer());
	}

	public void deliver(ItemStack item, WorldGroup worldGroup) {
		List<ItemStack> items = get(worldGroup);

		items.remove(item);
		if (items.isEmpty())
			this.items.remove(worldGroup);

		ItemUtils.giveItem(getPlayer(), item);
		playSound(getPlayer(), Sound.ENTITY_ITEM_PICKUP);
	}

}

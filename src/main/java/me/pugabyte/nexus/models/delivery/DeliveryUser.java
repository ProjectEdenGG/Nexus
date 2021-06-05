package me.pugabyte.nexus.models.delivery;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.delivery.DeliveryCommand;
import me.pugabyte.nexus.features.delivery.DeliveryWorldMenu;
import me.pugabyte.nexus.features.menus.BookBuilder.WrittenBookMenu;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Utils;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.StringUtils.asOxfordList;

@Data
@Builder
@Entity("delivery_user")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, ItemStackConverter.class})
public class DeliveryUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	@Embedded
	private Map<WorldGroup, List<Delivery>> deliveries = new HashMap<>();

	@Getter
	private static final List<WorldGroup> supportedWorldGroups = Arrays.asList(WorldGroup.SURVIVAL, WorldGroup.CREATIVE, WorldGroup.SKYBLOCK, WorldGroup.ONEBLOCK);

	public void add(WorldGroup worldGroup, Delivery delivery) {
		List<Delivery> deliveries = get(worldGroup);
		deliveries.add(delivery);
		this.deliveries.put(worldGroup, deliveries);
	}

	public List<Delivery> get(WorldGroup worldGroup) {
		return deliveries.getOrDefault(worldGroup, new ArrayList<>());
	}

	public void setupDelivery(ItemStack item) {
		new DeliveryWorldMenu(item).open(getOnlinePlayer());
	}

	public void remove(WorldGroup worldGroup, Delivery delivery) {
		List<Delivery> deliveries = get(worldGroup);
		deliveries.remove(delivery);
		if (deliveries.isEmpty())
			this.deliveries.remove(worldGroup);
		else
			this.deliveries.put(worldGroup, deliveries);
	}

	public Map<WorldGroup, List<Delivery>> getDeliveries() {
		new HashMap<>(deliveries).forEach((worldGroup, deliveries) -> {
			if (deliveries.isEmpty())
				this.deliveries.remove(worldGroup);
		});

		return deliveries;
	}

	public void sendNotification() {
		if (!isOnline())
			return;

		List<String> groups = getDeliveries().keySet().stream()
				.map(StringUtils::camelCase)
				.collect(Collectors.toList());

		if (groups.isEmpty())
			return;

		String message;
		if (groups.size() == 1)
			message = "an unclaimed delivery in &e" + groups.get(0);
		else {
			message = "unclaimed deliveries in &e" + asOxfordList(groups, "&3, &e");
		}

		sendMessage(json(DeliveryCommand.PREFIX + "&3You have " + message + "&3, use &c/delivery &3to claim it!")
				.command("/delivery")
				.hover("&eClick to view deliveries"));
	}

	@Data
	@RequiredArgsConstructor
	@NoArgsConstructor
	public static class Delivery {
		@NonNull
		private UUID fromUUID;
		private ItemStack message = null;
		private List<ItemStack> items = new ArrayList<>();

		public Delivery(@NotNull UUID fromUUID, ItemStack message, List<ItemStack> items) {
			this.fromUUID = fromUUID;
			this.items = items;
			this.message = message;
		}

		public Delivery(@NotNull UUID fromUUID, List<ItemStack> items) {
			this.fromUUID = fromUUID;
			this.items = items;
		}

		public Delivery(@NotNull UUID fromUUID, ItemStack... items) {
			this.fromUUID = fromUUID;
			this.items = Arrays.asList(items);
		}

		public Delivery(@NotNull UUID fromUUID, ItemStack message) {
			this.fromUUID = fromUUID;
			this.message = message;
		}

		public static Delivery fromServer(String message) {
			return fromServer(message, Collections.emptyList());
		}

		public static Delivery fromServer(ItemStack... items) {
			return fromServer(null, Arrays.asList(items));
		}

		public static Delivery fromServer(List<ItemStack> items) {
			return fromServer(null, items);
		}

		public static Delivery fromServer(String message, ItemStack... items) {
			return fromServer(message, Arrays.asList(items));
		}

		public static Delivery fromServer(String message, List<ItemStack> items) {
			final WrittenBookMenu builder = new WrittenBookMenu().addPage(new JsonBuilder(message));
			return new Delivery(StringUtils.getUUID0(), builder.getBook(), items);
		}

		public String getFrom() {
			String from = "Server";
			if (!this.fromUUID.equals(StringUtils.getUUID0()))
				from = Nickname.of(this.fromUUID);
			return from;
		}

		public boolean hasMessage() {
			return !ItemUtils.isNullOrAir(message);
		}

		public boolean hasItems() {
			return !Utils.isNullOrEmpty(items);
		}
	}

}

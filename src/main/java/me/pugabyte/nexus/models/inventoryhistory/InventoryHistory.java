package me.pugabyte.nexus.models.inventoryhistory;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.LocalDateTimeConverter;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.utils.PlayerUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.features.commands.staff.InventorySnapshotsCommand.PREFIX;
import static me.pugabyte.nexus.utils.ItemUtils.isInventoryEmpty;
import static me.pugabyte.nexus.utils.TimeUtils.shortDateTimeFormat;

@Data
@Builder
@Entity("inventory_history")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class InventoryHistory implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	@Embedded
	private List<InventorySnapshot> snapshots = new ArrayList<>();

	public InventorySnapshot getSnapshot(LocalDateTime timestamp) {
		Optional<InventorySnapshot> snapshot = snapshots.stream().filter(_snapshot -> _snapshot.getTimestamp().equals(timestamp)).findFirst();
		if (!snapshot.isPresent())
			throw new InvalidInputException("Snapshot from timestamp &e" + shortDateTimeFormat(timestamp) + " &3not found");
		return snapshot.get();
	}

	public InventorySnapshot takeSnapshot(SnapshotReason reason) {
		return takeSnapshot(new InventorySnapshot(getOnlinePlayer(), reason));
	}

	public InventorySnapshot takeSnapshot(InventorySnapshot snapshot) {
		snapshots.add(0, snapshot);
		return snapshot;
	}

	public void janitor() {
		snapshots = new ArrayList<>(snapshots).stream().filter(snapshot -> {
			long daysOld = ChronoUnit.DAYS.between(snapshot.getTimestamp(), LocalDateTime.now());
			return daysOld <= snapshot.getReason().getDaysToKeep();
		}).collect(Collectors.toList());
	}

	@Data
	@NoArgsConstructor
	@Converters({ItemStackConverter.class, LocationConverter.class, LocalDateTimeConverter.class})
	public static class InventorySnapshot {
		private UUID uuid;
		private LocalDateTime timestamp;
		private SnapshotReason reason;
		@Embedded
		private List<ItemStack> contents;
		private Location location;
		private int level;
		private float exp;

		public InventorySnapshot(Player player, SnapshotReason reason) {
			this.uuid = player.getUniqueId();
			this.timestamp = LocalDateTime.now();
			this.location = player.getLocation().clone();
			this.reason = reason;
			this.contents = Arrays.asList(player.getInventory().getContents().clone());
			this.level = player.getLevel();
			this.exp = player.getExp();
		}

		public void apply(Player applier, Player player) {
			if (!isInventoryEmpty(player.getInventory())) {
				PlayerUtils.send(player, PREFIX + "&cYour inventory must be empty to apply this inventory snapshot");
				if (!applier.equals(player))
					PlayerUtils.send(applier, PREFIX + "&c" + player.getName() + "'s inventory must be empty to apply this inventory snapshot");
				return;
			}

			player.setLevel(level);
			player.setExp(exp);
			player.getInventory().setContents(contents.toArray(ItemStack[]::new));
			PlayerUtils.send(player, PREFIX + "Snapshot applied");
			if (!applier.equals(player))
				PlayerUtils.send(applier, PREFIX + "Snapshot applied to " + player.getName() + "'s inventory");
		}

	}

	@Getter
	@AllArgsConstructor
	public enum SnapshotReason {
		DEATH(7, ChatColor.RED),
		MANUAL(3, ChatColor.AQUA),
		WORLD_CHANGE(1, ChatColor.GREEN);

		private final int daysToKeep;
		private final ChatColor color;
	}

}

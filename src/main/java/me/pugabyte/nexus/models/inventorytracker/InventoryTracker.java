package me.pugabyte.nexus.models.inventorytracker;

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
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@Entity("inventory_tracker")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class InventoryTracker extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	@Embedded
	private List<InventorySnapshot> snapshots;

	public InventorySnapshot takeSnapshot(SnapshotReason reason) {
		InventorySnapshot snapshot = new InventorySnapshot(getPlayer(), reason);
		snapshots.add(snapshot);
		return snapshot;
	}

	public void janitor() {
		snapshots = new ArrayList<>(snapshots).stream().filter(snapshot -> {
			long daysOld = ChronoUnit.DAYS.between(snapshot.getTimestamp(), LocalDateTime.now());
			return daysOld <= snapshot.getReason().getDaysToKeep();
		}).collect(Collectors.toList());
	}

	@Data
	@Converters({ItemStackConverter.class, LocationConverter.class})
	private static class InventorySnapshot {
		private LocalDateTime timestamp;
		private SnapshotReason reason;
		private List<ItemStack> contents;
		private Location location;

		public InventorySnapshot(Player player, SnapshotReason reason) {
			this.timestamp = LocalDateTime.now();
			this.reason = reason;
			contents = Arrays.asList(player.getInventory().getContents());
			location = player.getLocation();
		}

	}

	@Getter
	@AllArgsConstructor
	public enum SnapshotReason {
		DEATH(7),
		WORLD_CHANGE(1);

		private final int daysToKeep;
	}

}

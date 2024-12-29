package gg.projecteden.nexus.models.inventoryhistory;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.mongodb.serializers.LocalDateTimeConverter;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.commands.staff.operator.InventorySnapshotsCommand;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.parchment.HasLocation;
import lombok.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Entity(value = "inventory_history", noClassnameStored = true)
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
			throw new InvalidInputException("Snapshot from timestamp &e" + TimeUtils.shortDateTimeFormat(timestamp) + " &3not found");
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
	public static class InventorySnapshot implements HasLocation {
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
			if (!ItemUtils.isInventoryEmpty(player.getInventory())) {
				PlayerUtils.send(player, InventorySnapshotsCommand.PREFIX + "&cYour inventory must be empty to apply this inventory snapshot");
				if (!applier.equals(player))
					PlayerUtils.send(applier, InventorySnapshotsCommand.PREFIX + "&c" + Nickname.of(player) + "'s inventory must be empty to apply this inventory snapshot");
				return;
			}

			player.setLevel(level);
			player.setExp(exp);
			player.getInventory().setContents(contents.toArray(ItemStack[]::new));
			PlayerUtils.send(player, InventorySnapshotsCommand.PREFIX + "Snapshot applied");
			if (!applier.equals(player))
				PlayerUtils.send(applier, InventorySnapshotsCommand.PREFIX + "Snapshot applied to " + Nickname.of(player) + "'s inventory");
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

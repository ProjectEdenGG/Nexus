package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.utils.FakeWorldEdit.Clipboard.ClipboardBuilder;
import gg.projecteden.parchment.HasLocation;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class FakeWorldEdit {
	@NonNull
	private final World world;

	public FakeWorldEdit(@NonNull HasLocation entity) {
		this(entity.getLocation().getWorld());
	}

	public FakeWorldEdit(@NonNull String world) {
		this(Objects.requireNonNull(Bukkit.getWorld(world)));
	}

	public ClipboardBuilder clipboard() {
		return new ClipboardBuilder().world(world);
	}

	@RequiredArgsConstructor
	public static class Clipboard {
		private final World world;
		private final Location min;
		private final Location max;
		private Predicate<BlockData> filter = block -> true;

		@Getter
		private final Map<Location, BlockData> blocks = new HashMap<>();

		@Builder(buildMethodName = "copy")
		public Clipboard(World world, Location min, Location max, Predicate<BlockData> filter) {
			this.world = world;
			this.min = new Location(min.getWorld(),
				Math.min(min.getBlockX(), max.getBlockX()),
				Math.min(min.getBlockY(), max.getBlockY()),
				Math.min(min.getBlockZ(), max.getBlockZ()));
			this.max = new Location(min.getWorld(),
				Math.max(min.getBlockX(), max.getBlockX()),
				Math.max(min.getBlockY(), max.getBlockY()),
				Math.max(min.getBlockZ(), max.getBlockZ()));
			this.filter = filter == null ? block -> true : filter;
			copy();
		}

		public Clipboard copy() {
			Consumer<Location> add = coordinate -> {
				BlockData block = coordinate.getBlock().getBlockData();
				if (filter.test(block))
					blocks.put(coordinate, block);
			};

			for (int x = min.getBlockX(); x <= max.getBlockX(); x++)
				for (int y = min.getBlockY(); y <= max.getBlockY(); y++)
					for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++)
						add.accept(new Location(world, x, y, z));

			return this;
		}

		public void paste(Location at) {
			blocks.forEach((location, block) -> offset(location, at).getBlock().setBlockData(block));
		}

		@NotNull
		private Location offset(Location location, Location to) {
			return location.clone().add(
				to.getBlockX() - min.getBlockX(),
				to.getBlockY() - min.getBlockY(),
				to.getBlockZ() - min.getBlockZ()
			);
		}

	}
}

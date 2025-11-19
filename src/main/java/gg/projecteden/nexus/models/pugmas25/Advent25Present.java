package gg.projecteden.nexus.models.pugmas25;

import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Districts.Pugmas25District;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.clientside.ClientSideUser;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.parchment.HasLocation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Advent25Present implements HasLocation {
	private int day;
	private Location location;
	private List<ItemStack> contents = new ArrayList<>();
	private UUID entityUuid;

	public Advent25Present(int day, Location location, UUID entityUuid) {
		this.day = day;
		this.location = location;
		this.entityUuid = entityUuid;
	}

	public @NotNull Location getLocation() {
		return location.clone();
	}

	public ItemBuilder getItem() {
		return new ItemBuilder(ItemModelType.PUGMAS_PRESENT_ADVENT).name("Advent Present").lore("&eDay #" + day, "&f", Pugmas25.LORE);
	}

	public Pugmas25District getDistrict() {
		return Pugmas25District.of(getLocation());
	}

	public void showWaypoint(Advent25User user) {
		// TODO --> give compass showing location?
		user.sendMessage("TODO glow, day = " + day);
	}

	public void refresh(Advent25User user) {
		ClientSideUser.of(user).refresh(entityUuid);
	}

	public LocalDate getDate() {
		return LocalDate.of(2025, 12, day);
	}

	@AllArgsConstructor
	public enum Advent25PresentStatus {
		MISSED(ItemModelType.PUGMAS_PRESENT_ADVENT, ItemModelType.PUGMAS_PRESENT_OUTLINED, "&cMissed"),
		OPENED(ItemModelType.PUGMAS_PRESENT_ADVENT_OPENED, ItemModelType.PUGMAS_PRESENT_OPENED, "&aOpened"),
		@Glowing
		AVAILABLE(ItemModelType.PUGMAS_PRESENT_ADVENT, ItemModelType.PUGMAS_PRESENT_COLORED, "&6Available"),
		LOCKED(ItemModelType.PUGMAS_PRESENT_ADVENT, ItemModelType.PUGMAS_PRESENT_LOCKED, "&7Locked"),
		;

		private final ItemModelType frameModelType;
		private final ItemModelType menuModelType;
		private final String status;

		public ItemBuilder getFrameItem() {
			ItemBuilder builder = new ItemBuilder(frameModelType);
			if (isGlowing())
				builder.enchant(Enchant.INFINITY);
			return builder;
		}

		public ItemBuilder getMenuItem(Advent25Present present, Advent25User user) {
			Pugmas25District district = present.getDistrict();
			String districtName = "null";
			if (district != null) {
				districtName = district.getName();
				if (this == LOCKED && !user.hasFound(present))
					districtName = "???";
			}

			ItemBuilder itemBuilder = new ItemBuilder(menuModelType)
				.name("&3Day: &e" + present.getDay())
				.lore("&3Status: &e" + status)
				.lore("&3District: &e" + districtName);

			if (user.hasFound(present))
				itemBuilder.lore("", "&aShow Waypoint");

			return itemBuilder;
		}

		public boolean isGlowing() {
			return getField().isAnnotationPresent(Glowing.class);
		}

		@SneakyThrows
		private Field getField() {
			return getClass().getField(name());
		}

		@Target(ElementType.FIELD)
		@Retention(RetentionPolicy.RUNTIME)
		private @interface Glowing {
		}
	}
}

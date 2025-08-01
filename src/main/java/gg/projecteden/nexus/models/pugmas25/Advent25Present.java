package gg.projecteden.nexus.models.pugmas25;

import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Districts.Pugmas25District;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.clientside.ClientSideUser;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.parchment.HasLocation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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

	public void glow(Pugmas25User user) {
		// TODO
		user.sendMessage("TODO glow, day = " + day);
	}

	public void refresh(Advent25User user) {
		ClientSideUser.of(user).refresh(entityUuid);
	}

	public LocalDate getDate() {
		return LocalDate.of(2025, 12, day);
	}
}

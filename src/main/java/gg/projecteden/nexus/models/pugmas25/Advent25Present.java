package gg.projecteden.nexus.models.pugmas25;

import gg.projecteden.nexus.features.clientside.models.ClientSideItemFrame;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Districts.Pugmas25District;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.parchment.HasLocation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minecraft.world.entity.decoration.ItemFrame;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Advent25Present implements HasLocation {
	private int day;
	private Location location;
	private List<ItemStack> contents = new ArrayList<>();

	public Advent25Present(int day, Location location) {
		this.day = day;
		this.location = location;
	}

	public @NotNull Location getLocation() {
		return location.clone();
	}

	public ItemBuilder getItem() {
		return new ItemBuilder(CustomMaterial.PUGMAS_PRESENT_ADVENT).name("Advent Present").lore("&eDay #" + day, "&f", Pugmas25.LORE);
	}

	public Pugmas25District getDistrict() {
		return Pugmas25District.of(getLocation());
	}

	@NotNull
	ItemFrame sendPacket(Advent25User user) {
		final CustomMaterial material = user.hasCollected(day) ? CustomMaterial.PUGMAS_PRESENT_ADVENT_OPENED : CustomMaterial.PUGMAS_PRESENT_ADVENT;
		return ClientSideItemFrame.builder()
				.location(getLocation())
				.blockFace(BlockFace.UP)
				.content(new ItemBuilder(material).build())
				.invisible(true)
				.send(user.getOnlinePlayer())
				.entity();
	}

}

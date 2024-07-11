package gg.projecteden.nexus.models.pugmas24;

import gg.projecteden.nexus.features.clientside.models.ClientSideItemFrame;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.features.events.y2024.pugmas24.models.District;
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
public class Advent24Present implements HasLocation {
	private int day;
	private Location location;
	private List<ItemStack> contents = new ArrayList<>();

	public Advent24Present(int day, Location location) {
		this.day = day;
		this.location = location;
	}

	public @NotNull Location getLocation() {
		return location.clone();
	}

	public ItemBuilder getItem() {
		return new ItemBuilder(CustomMaterial.PUGMAS_PRESENT_ADVENT).name("Advent Present").lore("&eDay #" + day, "&f", Pugmas24.LORE);
	}

	public District getDistrict() {
		return District.of(getLocation());
	}

	@NotNull ItemFrame sendPacket(Advent24User user) {
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

package me.pugabyte.nexus.models.bearfair21;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity("bearfair21_clientside_content")
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class, ItemStackConverter.class})
public class ClientsideContent implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	//
	private List<Content> contentList = new ArrayList<>();

	public @Nullable
	Content from(Location location) {
		for (Content content : contentList) {
			if (location.equals(content.getLocation()))
				return content;
		}
		return null;
	}

	@Data
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Content {
		private Location location;
		private Material material;
		private ItemStack itemStack;
		// item frame specific
		private BlockFace blockFace;
		private Integer rotation;

		public void setRotation(Rotation rotation) {
			this.rotation = rotation.ordinal();
		}

		public Boolean isItemFrame() {
			return this.material.equals(Material.ITEM_FRAME);
		}
	}
}

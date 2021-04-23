package me.pugabyte.nexus.models.buildcontest;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Data
@Builder
@Entity("build_contest")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class BuildContest implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private int id;
	private boolean active;
	private String theme;
	private ItemStack itemStack;

}

package me.pugabyte.nexus.models.assetcompetition;

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
import org.bukkit.Location;

import java.util.UUID;

@Data
@Builder
@Entity("asset_competition")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class AssetCompetition implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Location location;

}

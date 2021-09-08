package gg.projecteden.nexus.models.worldban;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.utils.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Entity(value = "worldban", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class WorldBan implements PlayerOwnedObject {
	@Id
	@NonNull
	UUID uuid;
	List<WorldGroup> bans = new ArrayList<>();

	public List<String> getBanNames() {
		return bans.stream().map(WorldGroup::toString).collect(Collectors.toList());
	}
}

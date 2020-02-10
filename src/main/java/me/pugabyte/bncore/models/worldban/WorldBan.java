package me.pugabyte.bncore.models.worldban;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import me.pugabyte.bncore.utils.WorldGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@Entity("worldban")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class WorldBan extends PlayerOwnedObject {
	@Id
	@NonNull
	UUID uuid;
	List<WorldGroup> bans = new ArrayList<>();;

	public List<String> getBanNames() {
		return bans.stream().map(WorldGroup::toString).collect(Collectors.toList());
	}
}

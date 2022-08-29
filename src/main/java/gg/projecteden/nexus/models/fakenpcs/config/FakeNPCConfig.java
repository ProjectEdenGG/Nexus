package gg.projecteden.nexus.models.fakenpcs.config;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@Entity(value = "fake_npc_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class FakeNPCConfig implements DatabaseObject {
	@Id
	@NonNull
	private UUID uuid;
	private int currentId;

	public static FakeNPCConfig get() {
		return new FakeNPCConfigService().get0();
	}

	public static int getNextId() {
		final int nextId = get().incrementId();
		save();
		return nextId;
	}

	public static void save() {
		new FakeNPCConfigService().save(get());
	}

	public int incrementId() {
		return ++currentId;
	}

}

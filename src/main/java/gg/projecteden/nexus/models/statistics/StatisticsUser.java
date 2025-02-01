package gg.projecteden.nexus.models.statistics;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.SerializationUtils.NBT;
import gg.projecteden.nexus.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;

@Data
@Entity(value = "statistics", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class StatisticsUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<String, Map<String, Long>> stats;

	public Map<String, Map<String, Long>> loadFromFile() {
		String file = getFileFixed();
		if (isNullOrEmpty(file))
			return Collections.emptyMap();

		return this.stats = (Map<String, Map<String, Long>>) Utils.getGson().fromJson(file, Map.class).get("stats");
	}

	public String getFileFixed() {
		String file = getFile();
		if (isNullOrEmpty(file))
			return null;

		return NBT.updateStatistics(file).toString();
	}

	@SneakyThrows
	public String getFile() {
		File file = IOUtils.getFile("server/stats/" + uuid + ".json");
		if (!file.exists())
			return null;

		return Files.readString(file.toPath());
	}

}

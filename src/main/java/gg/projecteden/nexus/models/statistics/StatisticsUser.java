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

import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;

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

	public void loadFromFile() {
		this.stats = (Map<String, Map<String, Long>>) Utils.getGson().fromJson(getFileFixed(), Map.class).get("stats");
	}

	public String getFileFixed() {
		return NBT.updateStatistics(getFile()).toString();
	}

	@SneakyThrows
	public String getFile() {
		return Files.readString(IOUtils.getFile("server/stats/" + uuid + ".json").toPath());
	}

}

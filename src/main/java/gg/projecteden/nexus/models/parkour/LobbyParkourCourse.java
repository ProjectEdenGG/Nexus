package gg.projecteden.nexus.models.parkour;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.FormatType;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.TimespanBuilder;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.hub.Hub;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.parkour.LobbyParkourUser.CourseData;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import tech.blastmc.holograms.api.HologramsAPI;
import tech.blastmc.holograms.api.models.Hologram;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Data
@Entity(value = "lobby_parkour_course", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class LobbyParkourCourse implements DatabaseObject {
	@Id
	@NonNull
	private UUID uuid;
	private String name;
	private List<Location> checkpoints = new ArrayList<>();

	public LobbyParkourCourse(@NonNull UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}

	public void updateHologram() {
		final List<CourseData> data = new LobbyParkourUserService().getAll().stream()
			.map(user -> user.get(this))
			.filter(courseData -> courseData.getBestRunTime() > 0)
			.sorted(Comparator.comparing(CourseData::getBestRunTime))
			.toList();

		Hologram hologram = HologramsAPI.byId(Hub.getWorld(), "lobby_parkour_%s_leaderboard".formatted(name));
		if (hologram == null) {
			Nexus.log("Invalid hologram: lobby_parkour_%s_leaderboard".formatted(name));
			return;
		}
		for (int i = 0; i < 10; i++) {
			String line = "&6%d. &e".formatted(i + 1);
			if (data.size() < i + 1)
				line = line + "No Time";
			else {
				final CourseData run = data.get(i);
				line = line + "%s &8- &e%s".formatted(run.getNickname(), TimespanBuilder.ofMillis(run.getBestRunTime()).displayMillis().build().format(FormatType.SHORT));
			}

			hologram.setLine(i + 2, line);
		}
		hologram.save();
	}

}

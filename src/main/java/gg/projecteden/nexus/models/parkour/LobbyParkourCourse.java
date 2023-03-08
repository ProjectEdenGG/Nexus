package gg.projecteden.nexus.models.parkour;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.FormatType;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.TimespanBuilder;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.parkour.LobbyParkourUser.CourseData;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

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

		for (int i = 0; i < 10; i++) {
			String setline = "hd setline lobby_parkour_%s_leaderboard %d &6%d. &e".formatted(name, i + 3, i + 1);
			if (data.size() < i + 1)
				setline = setline + "TBD";
			else {
				final CourseData run = data.get(i);
				setline = setline + "%s &8- &e%s".formatted(run.getNickname(), TimespanBuilder.ofMillis(run.getBestRunTime()).displayMillis().build().format(FormatType.SHORT));
			}

			PlayerUtils.runCommandAsConsole(setline);
		}
	}

}
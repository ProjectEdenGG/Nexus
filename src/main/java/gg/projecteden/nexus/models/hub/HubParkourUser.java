package gg.projecteden.nexus.models.hub;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.LocalDateTimeConverter;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.TimespanConverter;
import gg.projecteden.utils.TimeUtils.Timespan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity(value = "hub_parkour_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class HubParkourUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<CourseData> courses = new ArrayList<>();

	public CourseData get(String name) {
		return courses.stream()
			.filter(data -> data.getCourse().equalsIgnoreCase(name))
			.findFirst()
			.orElseGet(() -> {
				final CourseData newData = new CourseData(uuid, name);
				courses.add(newData);
				return newData;
			});
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@RequiredArgsConstructor
	@Converters({UUIDConverter.class, TimespanConverter.class, LocalDateTimeConverter.class})
	public static class CourseData {
		@NonNull
		private UUID uuid;
		private String course;
		private List<Timespan> bestRunSplits = new ArrayList<>();
		private List<Timespan> currentRunSplits = new ArrayList<>();
		private boolean leftStartRegion;
		private int lastCheckpoint;
		private LocalDateTime lastCheckpointTime;

		public CourseData(@NonNull UUID uuid, String course) {
			this.uuid = uuid;
			this.course = course;
		}

		public long getBestRunTime() {
			return bestRunSplits.stream().mapToLong(Timespan::getOriginal).sum();
		}

		public long getCurrentRunTime() {
			return currentRunSplits.stream().mapToLong(Timespan::getOriginal).sum();
		}
	}

}

package gg.projecteden.nexus.models.parkour;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.api.mongodb.serializers.LocalDateTimeConverter;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.parkour.ParkourListener;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.TimespanConverter;
import gg.projecteden.nexus.models.mode.ModeUser.FlightMode;
import gg.projecteden.nexus.models.mode.ModeUserService;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Data
@Entity(value = "lobby_parkour_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class LobbyParkourUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<CourseData> courses = new ArrayList<>();

	public boolean isPlaying() {
		return courses.stream().anyMatch(CourseData::isPlaying);
	}

	public CourseData get(LobbyParkourCourse course) {
		return get(course.getName());
	}

	public CourseData get(String name) {
		return courses.stream()
			.filter(data -> name.equalsIgnoreCase(data.getCourse()))
			.findFirst()
			.orElseGet(() -> {
				final CourseData newData = new CourseData(uuid, name);
				courses.add(newData);
				return newData;
			});
	}

	public boolean quitAll(Consumer<CourseData> consumer) {
		boolean playing = false;
		for (CourseData courseData : courses) {
			if (courseData.isPlaying()) {
				playing = true;
				consumer.accept(courseData);
			}

		}

		if (isOnline())
			getOnlinePlayer().getInventory().remove(ParkourListener.RESET_ITEM);

		return playing;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@RequiredArgsConstructor
	@Converters({UUIDConverter.class, TimespanConverter.class, LocalDateTimeConverter.class})
	public static class CourseData implements PlayerOwnedObject {
		@NonNull
		private UUID uuid;
		private String course;
		private List<Timespan> bestRunSplits = new ArrayList<>();
		private List<Timespan> currentRunSplits = new ArrayList<>();
		private boolean playing;
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

		public void quit() {
			reset();

			if (isOnline() && getRank().isStaff()) {
				FlightMode flightMode = new ModeUserService().get(this).getFlightMode(getWorldGroup());

				PlayerUtils.setAllowFlight(getOnlinePlayer(), flightMode.isAllowFlight(), LobbyParkourUser.class);
				PlayerUtils.setFlying(getOnlinePlayer(), flightMode.isFlying(), LobbyParkourUser.class);

				getOnlinePlayer().getInventory().remove(ParkourListener.RESET_ITEM);
			}
		}

		public void reset() {
			playing = false;
			currentRunSplits.clear();
			lastCheckpoint = 0;
			lastCheckpointTime = null;
		}

	}

}

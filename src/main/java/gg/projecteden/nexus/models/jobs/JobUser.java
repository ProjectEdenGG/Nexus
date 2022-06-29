package gg.projecteden.nexus.models.jobs;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Entity(value = "job_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class JobUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private JobType currentJob;
	private List<JobType> previousJobs = new ArrayList<>();
	private Map<JobType, JobProgress> progress = new ConcurrentHashMap<>();
	private Map<UUID, Integer> stockSupplied = new ConcurrentHashMap<>();

	@Data
	public static class JobProgress {
		private int level;
		private int experience;
	}

}

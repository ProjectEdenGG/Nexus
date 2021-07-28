package gg.projecteden.nexus.models.scheduledjobs;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.LocalDateTimeConverter;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.JobConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.nexus.models.scheduledjobs.common.AbstractJob.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@Entity(value = "scheduled_jobs", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, JobConverter.class, LocalDateTimeConverter.class})
public class ScheduledJobs implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<JobStatus, Set<AbstractJob>> jobs = new HashMap<>();

	public Set<AbstractJob> get(JobStatus status) {
		return jobs.computeIfAbsent(status, $ -> new HashSet<>());
	}

	public Set<AbstractJob> getReady() {
		final LocalDateTime now = LocalDateTime.now();
		return get(JobStatus.PENDING).stream()
			.filter(job -> job.getTimestamp().isBefore(now))
			.collect(Collectors.toSet());
	}

	public <T extends AbstractJob> Set<T> get(JobStatus status, Class<T> clazz) {
		return get(status).stream()
			.filter(job -> clazz.equals(job.getClass()))
			.map(job -> (T) job)
			.collect(Collectors.toSet());
	}

	public void add(AbstractJob job) {
		add(JobStatus.PENDING, job);
	}

	public void add(JobStatus status, AbstractJob job) {
		get(status).add(job);
	}

	public void janitor() {
		final LocalDateTime threshold = LocalDateTime.now().minusDays(3);
		get(JobStatus.COMPLETED).removeIf(job -> job.getTimestamp().isBefore(threshold));
	}

}

package gg.projecteden.nexus.models.job;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import lombok.Data;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static gg.projecteden.utils.StringUtils.camelCase;

@Data
public abstract class AbstractJob {
	@NonNull
	protected UUID id;
	@NonNull
	protected LocalDateTime created;
	protected LocalDateTime timestamp;
	@NonNull
	protected JobStatus status = JobStatus.PENDING;

	public AbstractJob() {
		this.id = UUID.randomUUID();
		this.created = LocalDateTime.now();
	}

	private ScheduledJobs jobs() {
		return new ScheduledJobsService().get0();
	}

	public void setStatus(JobStatus status) {
		jobs().get(this.status).remove(this);
		this.status = status;
		jobs().get(this.status).add(this);
	}

	public void schedule(LocalDateTime timestamp) {
		this.timestamp = timestamp;
		new ScheduledJobsService().edit0(jobs -> jobs.add(this));
	}

	public void process() {
		if (status != JobStatus.PENDING)
			throw new InvalidInputException(getClass().getSimpleName() + " # " + id + " is already " + camelCase(status));

		try {
			setStatus(JobStatus.RUNNING);
			run().thenAccept(this::setStatus);
		} catch (Exception ex) {
			Nexus.severe("Error while running " + getClass().getSimpleName() + " # " + id);
			ex.printStackTrace();
			setStatus(JobStatus.ERRORED);
		}
	}

	protected CompletableFuture<JobStatus> completed() {
		final CompletableFuture<JobStatus> future = completable();
		future.complete(JobStatus.COMPLETED);
		return future;
	}

	@NotNull
	protected CompletableFuture<JobStatus> completable() {
		return new CompletableFuture<>();
	}

	protected abstract CompletableFuture<JobStatus> run();

	public enum JobStatus {
		PENDING,
		RUNNING,
		COMPLETED,
		ERRORED,
		INTERRUPTED,
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AbstractJob that = (AbstractJob) o;
		return id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}

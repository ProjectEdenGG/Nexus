package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.scheduledjobs.ScheduledJobs;
import gg.projecteden.nexus.models.scheduledjobs.ScheduledJobsService;
import gg.projecteden.nexus.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.nexus.models.scheduledjobs.common.AbstractJob.JobStatus;
import gg.projecteden.nexus.models.scheduledjobs.common.RetryIfInterrupted;
import gg.projecteden.nexus.models.scheduledjobs.jobs.Test2Job;
import gg.projecteden.nexus.models.scheduledjobs.jobs.TestJob;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.Time;
import lombok.NonNull;
import org.reflections.Reflections;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static gg.projecteden.nexus.models.scheduledjobs.common.AbstractJob.getNextExecutionTime;

@Aliases("job")
@Permission("group.admin")
public class JobsCommand extends CustomCommand {
	private static final ScheduledJobsService service = new ScheduledJobsService();
	private static final ScheduledJobs jobs = service.get0();

	public JobsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		checkInterrupted();
		processor();
		rescheduler();
	}

	private static void checkInterrupted() {
		try {
			final List<AbstractJob> interrupted = new ArrayList<>(jobs.get(JobStatus.RUNNING));
			if (!interrupted.isEmpty()) {
				for (AbstractJob job : interrupted) {
					if (job.getClass().getAnnotation(RetryIfInterrupted.class) != null) {
						job.setStatus(JobStatus.PENDING);
						Nexus.warn("[Jobs] Found interrupted job, retrying: " + job);
					} else {
						job.setStatus(JobStatus.INTERRUPTED);
						Nexus.severe("[Jobs] Found interrupted job: " + job);
					}
				}

				service.save(jobs);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void processor() {
		Tasks.repeat(0, Time.SECOND, () -> {
			for (AbstractJob job : jobs.getReady())
				job.process();

			service.save(jobs);
		});
	}

	private static void rescheduler() {
		final Reflections reflections = new Reflections(service.getClass().getPackage().getName());
		Tasks.repeatAsync(0, Time.MINUTE, () -> reflections.getSubTypesOf(AbstractJob.class).forEach(clazz -> {
			final LocalDateTime timestamp = getNextExecutionTime(clazz);
			if (timestamp == null)
				return;

			for (AbstractJob job : jobs.get(JobStatus.PENDING, clazz))
				if (job.getTimestamp().equals(timestamp))
					return;

			try {
				clazz.getConstructor().newInstance().schedule(timestamp);
			} catch (Exception ex) {
				Nexus.severe("Error rescheduling " + clazz.getSimpleName());
				ex.printStackTrace();
			}
		}));
	}

	@Path("stats")
	void stats() {
		if (jobs.getJobs().isEmpty())
			error("No jobs found");

		send(PREFIX + "Stats");
		for (JobStatus status : JobStatus.values()) {
			final Set<AbstractJob> list = jobs.get(status);
			if (list.isEmpty())
				continue;

			send("&e " + camelCase(status) + " &7- &3" + list.size());
		}
	}

	@Path("test <seconds> <message...>")
	void test(int seconds, String message) {
		new TestJob(uuid(), message).schedule(seconds);
		send(PREFIX + "Scheduled test job");
	}

	@Path("test2 <seconds> <wait> <message...>")
	void test2(int seconds, int wait, String message) {
		new Test2Job(uuid(), wait, message).schedule(seconds);
		send(PREFIX + "Scheduled test job");
	}

}

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
import gg.projecteden.nexus.models.scheduledjobs.jobs.Test2Job;
import gg.projecteden.nexus.models.scheduledjobs.jobs.TestJob;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.Time;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
	}

	private static void checkInterrupted() {
		try {
			final List<AbstractJob> interrupted = new ArrayList<>(jobs.get(JobStatus.RUNNING));
			if (!interrupted.isEmpty()) {
				for (AbstractJob job : interrupted) {
					Nexus.severe("[Jobs] Found interrupted job: " + job);
					job.setStatus(JobStatus.INTERRUPTED);
				}

				service.save(jobs);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void processor() {
		try {
			Tasks.repeat(0, Time.SECOND, () -> {
				for (AbstractJob job : jobs.getReady())
					job.process();

				service.save(jobs);
			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
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
		new TestJob(uuid(), message).schedule(LocalDateTime.now().plusSeconds(seconds));
		send(PREFIX + "Scheduled test job");
	}

	@Path("test2 <seconds> <wait> <message...>")
	void test2(int seconds, int wait, String message) {
		new Test2Job(uuid(), wait, message).schedule(LocalDateTime.now().plusSeconds(seconds));
		send(PREFIX + "Scheduled test job");
	}

}

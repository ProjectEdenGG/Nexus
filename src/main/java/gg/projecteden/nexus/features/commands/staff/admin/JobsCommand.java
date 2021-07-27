package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.job.AbstractJob;
import gg.projecteden.nexus.models.job.AbstractJob.JobStatus;
import gg.projecteden.nexus.models.job.ScheduledJobs;
import gg.projecteden.nexus.models.job.ScheduledJobsService;
import gg.projecteden.nexus.models.job.jobs.Test2Job;
import gg.projecteden.nexus.models.job.jobs.TestJob;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.Set;

@Aliases("job")
@Permission("group.admin")
public class JobsCommand extends CustomCommand {
	private final ScheduledJobsService service = new ScheduledJobsService();
	private final ScheduledJobs jobs = service.get0();

	public JobsCommand(@NonNull CommandEvent event) {
		super(event);
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

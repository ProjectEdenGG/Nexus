package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.Schedule;
import gg.projecteden.nexus.models.jobs.JobUser;
import gg.projecteden.nexus.models.jobs.JobUserService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.concurrent.CompletableFuture;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Async
@Schedule("0 1 * * 7")
@Disabled
public class JobsWeeklyJob extends AbstractJob {

	@Override
	protected CompletableFuture<JobStatus> run() {
		final JobUserService service = new JobUserService();
		for (JobUser uuid : service.getAll())
			service.edit(uuid, user -> user.setCurrentJob(null));
		// TODO Announcement?
		return completed();
	}

}


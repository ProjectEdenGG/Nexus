package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.nexus.features.votes.DailyVoteRewardsCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.concurrent.CompletableFuture;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Async
//@Schedule("0 0 * * *") // TODO 1.19
public class DailyVoteRewardsResetJob extends AbstractJob {

	@Override
	protected CompletableFuture<JobStatus> run() {
		DailyVoteRewardsCommand.dailyReset();
		return completed();
	}

}

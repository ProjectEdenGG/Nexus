package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.models.scheduledjobs.common.Schedule;
import gg.projecteden.nexus.features.dailyrewards.DailyRewardsFeature;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.concurrent.CompletableFuture;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schedule("0 0 * * *")
public class DailyRewardsResetJob extends AbstractJob {

	@Override
	protected CompletableFuture<JobStatus> run() {
		DailyRewardsFeature.dailyReset();
		return completed();
	}

}

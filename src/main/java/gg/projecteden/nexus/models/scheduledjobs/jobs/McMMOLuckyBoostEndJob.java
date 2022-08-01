package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.api.mongodb.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.RetryIfInterrupted;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.concurrent.CompletableFuture;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@RetryIfInterrupted
public class McMMOLuckyBoostEndJob extends AbstractJob {

	@Override
	protected CompletableFuture<JobStatus> run() {
		PlayerUtils.runCommandAsConsole("lp group guest permission unset mcmmo.perks.lucky.all");
		return completed();
	}

}

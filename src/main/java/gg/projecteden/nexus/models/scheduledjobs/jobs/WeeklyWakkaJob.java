package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.Schedule;
import gg.projecteden.nexus.features.survival.avontyre.weeklywakka.WeeklyWakkaFeature;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.concurrent.CompletableFuture;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Async
@Schedule("0 12 * * 0")
public class WeeklyWakkaJob extends AbstractJob {

	@Override
	protected CompletableFuture<JobStatus> run() {
		WeeklyWakkaFeature.moveNPC();
		return completed();
	}
}

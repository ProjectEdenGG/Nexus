package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.api.mongodb.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.Schedule;
import gg.projecteden.nexus.features.survival.decorationstore.DecorationStoreLayouts;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.concurrent.CompletableFuture;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schedule("0 */2 * * *")
public class DecorationStoreLayoutJob extends AbstractJob {

	@Override
	protected CompletableFuture<JobStatus> run() {
		DecorationStoreLayouts.pasteNextLayout();
		return completed();
	}
}

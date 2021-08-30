package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.models.scheduledjobs.common.Schedule;
import gg.projecteden.nexus.features.store.gallery.StoreGalleryNPCs;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.concurrent.CompletableFuture;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schedule("0 * * * *")
public class StoreGalleryNPCsJob extends AbstractJob {

	@Override
	protected CompletableFuture<JobStatus> run() {
		StoreGalleryNPCs.updateSkins();
		return completed();
	}

}

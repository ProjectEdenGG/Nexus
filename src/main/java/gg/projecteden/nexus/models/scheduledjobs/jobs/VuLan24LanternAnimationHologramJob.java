package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.FormatType;
import gg.projecteden.api.mongodb.models.scheduledjobs.ScheduledJobsService;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.Schedule;
import gg.projecteden.nexus.features.events.y2024.vulan24.VuLan24;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.event.Listener;
import tech.blastmc.holograms.api.HologramsAPI;
import tech.blastmc.holograms.api.models.Hologram;

import java.util.concurrent.CompletableFuture;

@Data
@EqualsAndHashCode(callSuper = true)
@Schedule("* * * 7,8 *")
public class VuLan24LanternAnimationHologramJob extends AbstractJob implements Listener {

	@Override
	protected CompletableFuture<JobStatus> run() {
		if (VuLan24.get().isAfterEvent())
			return completed();

		final Hologram hologram = HologramsAPI.byId(VuLan24.get().getWorld(), "vu_lan_lantern_animation_place");

		var next = new ScheduledJobsService().getApp().get(JobStatus.PENDING, VuLan24LanternAnimationJob.class);
		if (next.isEmpty())
			hologram.setLine(4, "Unknown");
		else
			hologram.setLine(4, "&e" + Timespan.of(next.iterator().next().getTimestamp()).format(FormatType.LONG));
		hologram.save();

		return completed();
	}

}

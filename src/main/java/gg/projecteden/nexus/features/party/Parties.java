package gg.projecteden.nexus.features.party;

import gg.projecteden.api.mongodb.models.scheduledjobs.ScheduledJobs;
import gg.projecteden.api.mongodb.models.scheduledjobs.ScheduledJobsService;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.AbstractJob.JobStatus;
import gg.projecteden.nexus.features.listeners.events.WorldGroupChangedEvent;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.party.Party;
import gg.projecteden.nexus.models.party.PartyManager;
import gg.projecteden.nexus.models.scheduledjobs.jobs.party.OfflineRemoverJob;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * TODO
 *      Warping
 *      Open Parties
 *      Party Size Limits
 *      XP Sharing
 */
public class Parties extends Feature implements Listener {

	public static final String PREFIX = "&8&l[&dParty&8&l] ";

	@EventHandler
	public void onPlayerEnterMinigames(WorldGroupChangedEvent event) {
		if (event.getNewWorldGroup() != WorldGroup.MINIGAMES) return;
		Party party = PartyManager.of(event.getPlayer());
		if (party == null) return;
		party.silenceChat(event.getPlayer());
	}

	@EventHandler
	public void onPlayerLeaveMinigames(WorldGroupChangedEvent event) {
		if (event.getOldWorldGroup() != WorldGroup.MINIGAMES) return;
		Party party = PartyManager.of(event.getPlayer());
		if (party == null) return;
		party.rejoinChat(event.getPlayer());
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		Party party = PartyManager.of(event.getPlayer());
		if (party == null) return;

		OfflineRemoverJob job = new OfflineRemoverJob(party.getId(), event.getPlayer().getUniqueId());
		job.schedule(LocalDateTime.now().plusSeconds(30));
		party.broadcast(Nerd.of(event.getPlayer()).getColoredName() + " &3has disconnected. They have 2 minutes to rejoin before being kicked from the party");
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Party party = PartyManager.of(event.getPlayer());
		if (party == null) return;

		ScheduledJobsService service = new ScheduledJobsService();
		ScheduledJobs app = service.getApp();
		Set<OfflineRemoverJob> jobs = app.get(JobStatus.PENDING, OfflineRemoverJob.class);
		jobs.forEach(job -> {
			if (job.getPartyId() != party.getId()) return;
			if (job.getUser() != event.getPlayer().getUniqueId()) return;
			job.setStatus(JobStatus.CANCELLED);
		});
		service.save(app);
	}

}

package gg.projecteden.nexus.features.party;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.util.player.UserManager;
import gg.projecteden.api.mongodb.models.scheduledjobs.ScheduledJobs;
import gg.projecteden.api.mongodb.models.scheduledjobs.ScheduledJobsService;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.AbstractJob.JobStatus;
import gg.projecteden.nexus.features.listeners.events.WorldGroupChangedEvent;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.party.Party;
import gg.projecteden.nexus.models.party.PartyManager;
import gg.projecteden.nexus.models.party.PartyUserService;
import gg.projecteden.nexus.models.scheduledjobs.jobs.party.OfflineRemoverJob;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.LocalDateTime;
import java.util.Set;

public class Parties extends Feature implements Listener {

	public static final String PREFIX = "&8&l[&dParty&8&l] ";
	private static final PartyUserService service = new PartyUserService();

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
		job.schedule(LocalDateTime.now().plusMinutes(2));
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


	@EventHandler
	public void onGainXP(PlayerExpChangeEvent event) {
		Party party = PartyManager.of(event.getPlayer());
		if (party == null) return;

		if (!service.get(event.getPlayer()).isXpShare()) return;
		int amount = (int) Math.max(1, event.getAmount() / 4F);
		party.getOnlineMembers().forEach(player ->  {
			if (player.getWorld() != event.getPlayer().getWorld()) return;
			if (WorldGroup.of(player) == WorldGroup.MINIGAMES) return;
			if (player.getLocation().distance(event.getPlayer().getLocation()) < 50)
				player.giveExp(amount);
		});
	}

	@EventHandler
	public void onGainMcMMOXP(McMMOPlayerXpGainEvent event) {
		if (event.getXpGainReason() == XPGainReason.COMMAND) return;

		Party party = PartyManager.of(event.getPlayer());
		if (party == null) return;

		if (!service.get(event.getPlayer()).isXpShare()) return;
		int amount = (int) Math.max(1, event.getRawXpGained() / 10F);
		Dev.BLAST.sendIfSelf(event.getPlayer(), amount);
		party.getOnlineMembers().forEach(player ->  {
			if (player.getUniqueId().equals(event.getPlayer().getUniqueId())) return;
			if (player.getWorld() != event.getPlayer().getWorld()) return;
			if (WorldGroup.of(player) == WorldGroup.MINIGAMES) return;
			if (player.getLocation().distance(event.getPlayer().getLocation()) < 50) {
				UserManager.getPlayer(player).applyXpGain(event.getSkill(), amount, XPGainReason.COMMAND, XPGainSource.COMMAND);
			}
		});
	}

}

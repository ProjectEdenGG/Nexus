package me.pugabyte.nexus.features.minigames.mechanics;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.minigames.managers.PlayerManager;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.annotations.Scoreboard;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchTimerTickEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.sabotage.MinigamerVoteEvent;
import me.pugabyte.nexus.features.minigames.models.matchdata.SabotageMatchData;
import me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import me.pugabyte.nexus.features.minigames.models.sabotage.SabotageColor;
import me.pugabyte.nexus.features.minigames.models.sabotage.SabotageTeam;
import me.pugabyte.nexus.features.minigames.models.scoreboards.MinigameScoreboard;
import me.pugabyte.nexus.utils.SoundUtils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Scoreboard(teams = false, sidebarType = MinigameScoreboard.Type.NONE)
public class Sabotage extends TeamMechanic {
	public static final int MEETING_LENGTH = 100;
	public static final int VOTING_DELAY = 10; // seconds before voting starts

	@Override
	public @NotNull String getName() {
		return "Sabotage";
	}

	@Override
	public ItemStack getMenuItem() {
		return Nexus.getHeadAPI().getItemHead("40042");
	}

	@Override
	public @NotNull String getDescription() {
		return "Try to repair your ship before aliens kill you and your fellow astronauts";
	}

	@Override
	public boolean shouldBeOver(Match match) {
		if (super.shouldBeOver(match))
			return true;

		List<Minigamer> impostors = SabotageTeam.IMPOSTOR.players(match);
		if (impostors.size() == SabotageTeam.getNonImpostors(match).size()) {
			impostors.forEach(Minigamer::scored);
			return true;
		}
		return false;
	}

	@Override
	public void onStart(MatchStartEvent event) {
		Match match = event.getMatch();
		SabotageMatchData matchData = match.getMatchData();
		match.getMinigamers().forEach(minigamer -> {
			SabotageColor color = matchData.getColor(minigamer);
			PlayerInventory inventory = minigamer.getPlayer().getInventory();
			inventory.setHelmet(color.getHead());
			inventory.setChestplate(color.getChest());
			inventory.setLeggings(color.getLegs());
			inventory.setBoots(color.getBoots());
		});
		match.getTasks().repeatAsync(0, 1, () -> {
			// TODO: packet stuff + body report checking + more goes here
		});
	}

	@EventHandler
	public void onVote(MinigamerVoteEvent event) {
		if (!event.getMatch().isMechanic(this)) return;
		if (!event.getMinigamer().isAlive() || (event.getTarget() != null && !event.getTarget().isAlive())) {
			event.setCancelled(true);
			return;
		}
		SoundUtils.Jingle.SABOTAGE_VOTE.play(event.getMatch().getMinigamers());
		event.getMatch().getMinigamers().forEach(minigamer -> {
			minigamer.getPlayer().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
			event.getVotingScreen().open(minigamer.getPlayer());
		});
	}

	@EventHandler
	public void onInteract(PlayerInteractAtEntityEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;
		if (!minigamer.isAlive()) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		Entity entity = minigamer.getPlayer().getTargetEntity(5);
		if (!(entity instanceof ItemFrame itemFrame)) return;
		if (itemFrame.getItem().getType() != Material.RED_CONCRETE) return;
		minigamer.getMatch().<SabotageMatchData>getMatchData().startMeeting(minigamer); // TODO: meeting cooldown + meeting limits
	}

	@EventHandler
	public void onTick(MatchTimerTickEvent event) {
		if (!event.getMatch().isMechanic(this)) return;
		SabotageMatchData matchData = event.getMatch().getMatchData();
		if (matchData.isMeetingActive()) {
			LocalDateTime meetingStarted = matchData.getMeetingStarted();
			if (!LocalDateTime.now().isBefore(meetingStarted.plusSeconds(VOTING_DELAY + MEETING_LENGTH)))
				matchData.endMeeting();
		}
	}
}

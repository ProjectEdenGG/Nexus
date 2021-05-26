package me.pugabyte.nexus.features.minigames.mechanics;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.chat.events.PublicChatEvent;
import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.features.minigames.managers.PlayerManager;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.annotations.Scoreboard;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchQuitEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerLoadoutEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.sabotage.MinigamerVoteEvent;
import me.pugabyte.nexus.features.minigames.models.matchdata.SabotageMatchData;
import me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import me.pugabyte.nexus.features.minigames.models.sabotage.SabotageColor;
import me.pugabyte.nexus.features.minigames.models.sabotage.SabotageTeam;
import me.pugabyte.nexus.features.minigames.models.scoreboards.MinigameScoreboard;
import me.pugabyte.nexus.framework.interfaces.Colored;
import me.pugabyte.nexus.utils.AdventureUtils;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.SoundUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Utils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Scoreboard(teams = false, sidebarType = MinigameScoreboard.Type.NONE)
public class Sabotage extends TeamMechanic {
	public static final int MEETING_LENGTH = 100;
	public static final int VOTING_DELAY = 10; // seconds before voting starts
	public static final int POST_MEETING_DELAY = 10;
	public static final Supplier<ItemStack> VOTING_ITEM = () -> new ItemBuilder(Material.NETHER_STAR).name("&eVoting Screen").build();
	public static final Supplier<ItemStack> USE_ITEM = () -> new ItemBuilder(Material.STONE_BUTTON).name("&eUse").build();

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
	public boolean usesAutoBalancing() {
		return false;
	}

	@Override
	public boolean hideTeamLoadoutColors() {
		return true;
	}

	@Override
	public boolean canMoveArmor() {
		return false;
	}

	@EventHandler
	public void onInventoryEvent(InventoryClickEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getWhoClicked().getPlayer());
		if (minigamer.isPlaying(this))
			event.setCancelled(true);
	}

	@EventHandler
	public void offhandEvent(PlayerSwapHandItemsEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (minigamer.isPlaying(this))
			event.setCancelled(true);
	}

	@Override
	public boolean shouldBeOver(Match match) {
		if (super.shouldBeOver(match))
			return true;

		List<Minigamer> impostors = SabotageTeam.IMPOSTOR.players(match);
		if (impostors.size() == SabotageTeam.getLivingNonImpostors(match).size()) {
			impostors.forEach(Minigamer::scored);
			return true;
		}
		if (impostors.size() == 0) {
			SabotageTeam.getNonImpostors(match).forEach(Minigamer::scored);
			return true;
		}
		return false;
	}

	@EventHandler(ignoreCancelled = true)
	public void onChat(PublicChatEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getChatter());
		if (!minigamer.isPlaying(this)) return;
		SabotageMatchData matchData = minigamer.getMatch().getMatchData();
		if (!event.getChannel().equals(matchData.getGameChannel())) return;
		if (!matchData.isMeetingActive()) {
			minigamer.tell("&cYou may not chat during the round");
			event.setCancelled(true);
		}
	}

	@Override
	public void onStart(MatchStartEvent event) {
		super.onStart(event);
		Match match = event.getMatch();
		SabotageMatchData matchData = match.getMatchData();
		matchData.setRoundStarted();
		match.showBossBar(matchData.getBossbar());
		match.getMinigamers().forEach(minigamer -> Chat.setActiveChannel(minigamer, matchData.getGameChannel()));
		match.getTasks().repeatAsync(0, 1, () -> {
			// TODO: packet stuff + body report checking + more goes here
		});
	}

	@Override
	public void onQuit(MatchQuitEvent event) {
		super.onQuit(event);
		event.getMinigamer().hideBossBar(event.getMatch().<SabotageMatchData>getMatchData().getBossbar());
		Chat.setActiveChannel(event.getMinigamer(), Chat.StaticChannel.MINIGAMES);
	}

	@Override
	public void onEnd(MatchEndEvent event) {
		super.onEnd(event);
		Match match = event.getMatch();
		SabotageMatchData matchData = match.getMatchData();
		match.hideBossBar(matchData.getBossbar());
		match.getMinigamers().forEach(minigamer -> Chat.setActiveChannel(minigamer, Chat.StaticChannel.MINIGAMES));
	}

	@Override
	public void onDeath(MinigamerDeathEvent event) {
		Chat.setActiveChannel(event.getMinigamer(), event.getMatch().<SabotageMatchData>getMatchData().getSpectatorChannel());
		event.setDeathMessage(null);
		super.onDeath(event);
	}

	public static void setArmor(LivingEntity entity, SabotageColor color) {
		EntityEquipment equipment = entity.getEquipment();
		if (equipment == null) return;
		equipment.setHelmet(color.getHead());
		equipment.setChestplate(color.getChest());
		equipment.setLeggings(color.getLegs());
		equipment.setBoots(color.getBoots());
	}

	@EventHandler
	public void onLoadout(MinigamerLoadoutEvent event) {
		if (!event.getMatch().isMechanic(this)) return;
		Tasks.wait(1, () -> {
			Minigamer minigamer = event.getMinigamer();
			SabotageColor color = event.getMatch().<SabotageMatchData>getMatchData().getColor(minigamer);
			setArmor(minigamer.getPlayer(), color);

//			PlayerInventory inventory = minigamer.getPlayer().getInventory();
//			SabotageTeam team = SabotageTeam.of(minigamer);
		});
	}

	@EventHandler
	public void onVote(MinigamerVoteEvent event) {
		if (!event.getMatch().isMechanic(this)) return;
		if (event.isCancelled() || !event.getMinigamer().isAlive() || (event.getTarget() != null && !event.getTarget().isAlive())) {
			event.setCancelled(true);
			SoundUtils.playSound(event.getMinigamer(), Sound.ENTITY_VILLAGER_NO, SoundCategory.VOICE, 0.8f, 1.0f);
			return;
		}
		SoundUtils.Jingle.SABOTAGE_VOTE.play(event.getMatch().getMinigamers());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;
		if (!minigamer.isAlive()) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!Utils.ActionGroup.RIGHT_CLICK.applies(event)) return;
		SabotageMatchData matchData = minigamer.getMatch().getMatchData();

		if (matchData.isMeetingActive()) {
			if (VOTING_ITEM.get().isSimilar(event.getItem()))
				matchData.getVotingScreen().open(minigamer);
			else
				event.setCancelled(true);
		} // else {}
	}

	@EventHandler
	public void onButtonInteract(PlayerInteractEntityEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;
		if (!minigamer.isAlive()) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		Entity entity = minigamer.getPlayer().getTargetEntity(5);
		if (!(entity instanceof ItemFrame itemFrame)) return;
		if (itemFrame.getItem().getType() != Material.RED_CONCRETE) return;

		SabotageMatchData matchData = minigamer.getMatch().getMatchData();
		SabotageMatchData.ButtonState state = matchData.button(minigamer);

		if (state == SabotageMatchData.ButtonState.COOLDOWN) {
			int canButtonIn = matchData.canButtonIn();
			minigamer.sendActionBar(new JsonBuilder("&cYou may call an emergency meeting in " + StringUtils.plural(canButtonIn + " second", canButtonIn)));
		}
		else if (state == SabotageMatchData.ButtonState.USED)
			minigamer.sendActionBar(new JsonBuilder("&cYou have used your emergency meeting button!"));
		else
			matchData.startMeeting(minigamer);
	}

	@Override
	public void announceWinners(Match match) {
		List<Minigamer> winners = match.getMinigamers().stream().filter(minigamer -> minigamer.getScore() > 0).collect(Collectors.toList());
		JsonBuilder builder = new JsonBuilder();
		if (winners.isEmpty())
			builder.next(new JsonBuilder("&bThe Crewmates")
								.hover(new JsonBuilder(AdventureUtils.commaJoinText(winners))
										.color(NamedTextColor.DARK_AQUA)))
					.next(" have won on ");
		else {
			builder.next(AdventureUtils.commaJoinText(winners.stream().map(minigamer -> {
				SabotageTeam team = SabotageTeam.of(minigamer);
				return new JsonBuilder(minigamer.getNickname(), (Colored) team).hover(team); // weirdly required cast
			}).collect(Collectors.toList())));
			builder.next(StringUtils.plural(" has won on ", " have won on ", winners.size()));
		}
		Minigames.broadcast(builder.next(match.getArena()));
	}
}

package me.pugabyte.nexus.features.minigames.mechanics;

import lombok.Getter;
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
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerDamageEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerLoadoutEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.sabotage.MinigamerVoteEvent;
import me.pugabyte.nexus.features.minigames.models.matchdata.SabotageMatchData;
import me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import me.pugabyte.nexus.features.minigames.models.sabotage.*;
import me.pugabyte.nexus.features.minigames.models.scoreboards.MinigameScoreboard;
import me.pugabyte.nexus.framework.interfaces.Colored;
import me.pugabyte.nexus.utils.*;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Scoreboard(teams = false, sidebarType = MinigameScoreboard.Type.NONE)
public class Sabotage extends TeamMechanic {
	public static final int MEETING_LENGTH = 100;
	public static final int VOTING_DELAY = 10; // seconds before voting starts
	public static final int POST_MEETING_DELAY = 10;
	public static final Supplier<ItemStack> VOTING_ITEM = () -> new ItemBuilder(Material.NETHER_STAR).name("&eVoting Screen").build();
	public static final Supplier<ItemStack> USE_ITEM = () -> new ItemBuilder(Material.STONE_BUTTON).name("&eUse").build();
	public static final Supplier<ItemStack> KILL_ITEM = () -> new ItemBuilder(Material.IRON_SWORD).name("&cKill").build();
	public static final Supplier<ItemStack> EMPTY_REPORT_ITEM = () -> new ItemBuilder(Material.CRIMSON_BUTTON).name("&eReport").build();
	public static final Supplier<ItemStack> REPORT_ITEM = () -> new ItemBuilder(Material.WARPED_BUTTON).name("&eReport").build();
	public static final Supplier<ItemStack> SABOTAGE_MENU = () -> new ItemBuilder(Material.STONE_BUTTON).name("&cSabotage").build();

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
			match.getMinigamers().forEach(minigamer -> {
				List<Minigamer> otherPlayers = new ArrayList<>(match.getMinigamers());
				Utils.removeEntityFrom(minigamer, otherPlayers);
				PacketUtils.sendFakeItem(minigamer, otherPlayers, new ItemStack(Material.AIR), EnumItemSlot.MAINHAND);
			});
			// TODO: body report checking + more goes here
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
			SabotageMatchData matchData = event.getMatch().getMatchData();
			SabotageColor color = matchData.getColor(minigamer);
			setArmor(minigamer.getPlayer(), color);

			PlayerInventory inventory = minigamer.getPlayer().getInventory();
			SabotageTeam team = SabotageTeam.of(minigamer);
			inventory.setItem(1, USE_ITEM.get());
			inventory.setItem(2, EMPTY_REPORT_ITEM.get());
			if (team == SabotageTeam.IMPOSTOR) {
				matchData.getKillCooldowns().put(minigamer.getUniqueId(), LocalDateTime.now());
				inventory.setItem(3, KILL_ITEM.get());
				inventory.setItem(4, SABOTAGE_MENU.get());
			}
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
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!Utils.ActionGroup.RIGHT_CLICK.applies(event)) return;
		SabotageMatchData matchData = minigamer.getMatch().getMatchData();
		ItemStack item = event.getItem();

		if (matchData.isMeetingActive()) {
			if (VOTING_ITEM.get().isSimilar(item))
				matchData.getVotingScreen().open(minigamer);
			else
				event.setCancelled(true);
		} else {
			if (USE_ITEM.get().isSimilar(event.getItem())) {

			} else if (SABOTAGE_MENU.get().isSimilar(item)) {

			} else if (minigamer.isAlive() && REPORT_ITEM.get().isSimilar(item))
				matchData.startMeeting(minigamer);
		}
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

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onHandAnimation(PlayerAnimationEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (minigamer.isPlaying(this) && event.getAnimationType() == PlayerAnimationType.ARM_SWING)
			event.setCancelled(true);
	}

	@Override
	public void onDamage(MinigamerDamageEvent event) {
		SabotageMatchData matchData = event.getMatch().getMatchData();
		LocalDateTime now = LocalDateTime.now();
		if (event.getAttacker() != null && event.getAttacker().isAlive() && SabotageTeam.of(event.getAttacker()) == SabotageTeam.IMPOSTOR
				&& now.isAfter(matchData.getKillCooldowns().get(event.getAttacker().getUniqueId()).plusSeconds(matchData.getArena().getKillCooldown()))) {
			MinigamerDeathEvent deathEvent = new MinigamerDeathEvent(event.getMinigamer(), event);
			onDeath(deathEvent);
			if (!deathEvent.isCancelled())
				matchData.getKillCooldowns().put(event.getAttacker().getUniqueId(), now);
		} else
			event.setCancelled(true);
	}

	// reflection shit

	private static final Map<TaskPart, Constructor<?>> taskPartDataMap = new HashMap<>();

	static {
		try {
			String path = Minigames.class.getPackage().getName();
			Set<Class<? extends TaskPartData>> taskPartDataTypes = new Reflections(path + ".models.sabotage.taskpartdata")
					.getSubTypesOf(TaskPartData.class);

			for (Class<?> taskPartDataType : taskPartDataTypes)
				if (taskPartDataType.getAnnotation(TaskPartDataFor.class) != null) {
					Constructor<?> constructor;
					try {
						constructor = taskPartDataType.getConstructor(TaskPart.class);
						constructor.setAccessible(true);
					} catch (NoSuchMethodException ex) {
						Nexus.warn("TaskPartData " + taskPartDataType.getSimpleName() + " has no TaskPart constructor");
						continue;
					}
					for (TaskPart taskPart : taskPartDataType.getAnnotation(TaskPartDataFor.class).value())
						taskPartDataMap.put(taskPart, constructor);
				}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Constructor<?> getTaskPartDataFor(TaskPart part) {
		try {
			return taskPartDataMap.getOrDefault(part, TaskPartData.class.getConstructor(TaskPart.class));
		} catch (Exception ex) {
			throw new RuntimeException("Unable to get TaskPartData constructor");
		}
	}

	public static <T extends TaskPartData> T createTaskPartDataFor(TaskPart part) {
		try {
			return (T) getTaskPartDataFor(part).newInstance(part);
		} catch (Exception ex) {
			throw new RuntimeException("Could not instantiate TaskPartData");
		}
	}
}

package gg.projecteden.nexus.features.minigames.mechanics;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.features.chat.events.PublicChatEvent;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.Scoreboard;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEndEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchQuitEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDamageEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerLoadoutEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.sabotage.MinigamerCompleteTaskPartEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.sabotage.MinigamerVoteEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.SabotageMatchData;
import gg.projecteden.nexus.features.minigames.models.matchdata.SabotageMatchData.ArmorStandTask;
import gg.projecteden.nexus.features.minigames.models.matchdata.SabotageMatchData.Body;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.SabotageColor;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.SabotageLight;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.SabotageTeam;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.Task;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.menus.ImpostorMenu;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import gg.projecteden.nexus.features.minigames.models.scoreboards.MinigameScoreboard;
import gg.projecteden.nexus.features.nameplates.Nameplates;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.resourcepack.ResourcePack.ResourcePackNumber;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.AdventureUtils;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.GlowUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.PacketUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.SoundUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.TitleBuilder;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.parchment.event.sound.SoundEvent;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.type.Light;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.StringUtils.camelCase;
import static gg.projecteden.nexus.utils.StringUtils.colorize;

// TODO
//  - admin table (imageonmap "api"?)
//  - cams (teleport the player around and spawn an NPC at their cams location which can be killed)
//  - ~~color menu (on interact with lobby armor stand/item frame)~~ remove colored outfits
//  - vent animation (open/close trapdoor)
//  - door sabotages
//    - create custom inventory background
//  - let impostors fix sabotages
//    - it looks like i already have some code for this; am i sure it's not working?
//  - add Darkness (1.19) potion effect to lights sabotage
//  - play sound effect when lights go out
//  - remove/replace meeting menus
//    - teleport players around the meeting table
//      - players must have direct line of sight of each other with no players blocking another
//      - players should be kept in place via teleports
//    - use Vote item while looking at a player to select who you want to vote for
//      - voting puts a client-side glow around the target player
//      - also needs a skip option
//    - display vote counts in nameplates at end of meeting?
//      - IDK where the skip count would go... maybe just a hologram above the button?
//      - alternatively, vote counts could be displayed exclusively in the sidebar with a distinct color for the eliminated person
//    - display who got voted out with glowing effect
//  - enable glow api (needed to highlight tasks)
//  - button should be encased in glass when unusable
//  - disable sprinting for all players?
@Scoreboard(teams = false, sidebarType = MinigameScoreboard.Type.MINIGAMER)
public class Sabotage extends TeamMechanic {
	public static final int MEETING_LENGTH = 100;
	public static final int VOTING_DELAY = 10; // seconds before voting starts
	public static final int POST_MEETING_DELAY = 10;
	public static final Supplier<ItemStack> VOTING_ITEM = () -> new ItemBuilder(Material.NETHER_STAR).name("&eVoting Screen").build();
	public static final Supplier<ItemStack> USE_ITEM = () -> new ItemBuilder(Material.STONE_BUTTON).name("&eUse").build();
	public static final Supplier<ItemStack> KILL_ITEM = () -> new ItemBuilder(Material.IRON_AXE).name("&cKill").build();
	public static final Supplier<ItemStack> EMPTY_REPORT_ITEM = () -> new ItemBuilder(Material.CRIMSON_BUTTON).name("&eReport").build();
	public static final Supplier<ItemStack> REPORT_ITEM = () -> new ItemBuilder(Material.WARPED_BUTTON).name("&eReport").build();
	public static final Supplier<ItemStack> SABOTAGE_MENU = () -> new ItemBuilder(Material.STONE_BUTTON).name("&cSabotage").build();

	@Override
	public @NotNull String getName() {
		return "Sabotage";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return Nexus.getHeadAPI().getItemHead("40042");
	}

	@Override
	public @NotNull String getDescription() {
		return "Try to repair your ship before impostors kill you and your fellow astronauts... unless you're the one doing the killing";
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

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInventoryCloseEvent(InventoryCloseEvent event) {
		Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (minigamer.isPlaying(this))
			event.getPlayer().setItemOnCursor(null);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInventoryEvent(InventoryClickEvent event) {
		Minigamer minigamer = Minigamer.of(event.getWhoClicked());
		if (minigamer.isPlaying(this) && event.getClickedInventory() != null && (event.getClickedInventory().getType() == InventoryType.CRAFTING || event.getClickedInventory() instanceof PlayerInventory || !event.isLeftClick()))
			event.setCancelled(true);
	}

	@EventHandler
	public void offhandEvent(PlayerSwapHandItemsEvent event) {
		Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (minigamer.isPlaying(this))
			event.setCancelled(true);
	}

	@Override
	public boolean shouldBeOver(@NotNull Match match) {
		if (super.shouldBeOver(match))
			return true;

		List<Minigamer> impostors = SabotageTeam.IMPOSTOR.players(match);
		if (impostors.size() == SabotageTeam.getLivingNonImpostors(match).size()) {
			impostors.forEach(Minigamer::scored);
			return true;
		}
		if (impostors.size() == 0)
			return true;
		SabotageMatchData matchData = match.getMatchData();
		return matchData.getProgress() == 1;
	}

	@EventHandler(ignoreCancelled = true)
	public void onChat(PublicChatEvent event) {
		Minigamer minigamer = Minigamer.of(event.getChatter());
		if (!minigamer.isPlaying(this)) return;
		SabotageMatchData matchData = minigamer.getMatch().getMatchData();
		if (!event.getChannel().equals(matchData.getGameChannel())) return;
		if (!matchData.isMeetingActive()) {
			minigamer.tell("&cYou may not chat during the round");
			event.setCancelled(true);
		}
	}

	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		super.onStart(event);
		Match match = event.getMatch();
		SabotageMatchData matchData = match.getMatchData();
		matchData.setRoundStarted();
		SabotageTeam.IMPOSTOR.players(match).forEach(matchData::putKillCooldown);
		match.showBossBar(matchData.getBossbar());
		match.getMinigamers().forEach(minigamer -> Chat.setActiveChannel(minigamer, matchData.getGameChannel()));
		match.getTasks().wait(1, () -> {
			match.sendMessage(Component.empty());
			match.sendMessage(new JsonBuilder("Players", NamedTextColor.YELLOW, TextDecoration.BOLD));
			for (SabotageTeam team : SabotageTeam.values())
				match.sendMessage(new JsonBuilder(team.players(match).size() + "x ", NamedTextColor.DARK_AQUA).next(team));
		});
		match.getTasks().wait(TimeUtils.TickTime.SECOND.x(1.5), () -> match.getMinigamers().forEach(matchData::initGlow));
		match.getTasks().repeatAsync(0, 1, () -> tick(match));

		// force faster nameplate updates
		match.getTasks().repeat(0, 5, () -> match.getMinigamers()
			.forEach(minigamer -> Nameplates.get().getNameplateManager().update(minigamer.getOnlinePlayer())));
	}

	private void tick(Match match) {
		// get match data
		SabotageMatchData matchData = match.getMatchData();

		// skip tick if meeting is active (meeting ticking is handled by another task)
		if (matchData.isMeetingActive()) return;

		// get game's current light level
		int lightLevel = matchData.lightLevel();

		// iterate through minigamers
		for (Minigamer minigamer : match.getMinigamers()) {
			// TODO: dead player handling

			// fetch variables
			Player player = minigamer.getOnlinePlayer();
			Location location = player.getLocation();
			PlayerInventory inventory = player.getInventory();

			// get all other players
			List<Player> otherPlayers = new ArrayList<>(match.getAlivePlayers());
			Utils.removeEntityFrom(minigamer, otherPlayers);

			// hide held item
			PacketUtils.sendFakeItem(minigamer.getOnlinePlayer(), otherPlayers, new ItemStack(Material.AIR), ItemSlot.MAINHAND);

			// get player's team
			SabotageTeam team = SabotageTeam.of(minigamer);
			// tick crewmates
			if (team != SabotageTeam.IMPOSTOR) {
				// send fake light block to non-impostors
				SabotageLight newLight = new SabotageLight(location, lightLevel);
				SabotageLight lastKnownLight = matchData.getLightMap().get(player.getUniqueId());
				if (!newLight.equals(lastKnownLight)) {
					if (lastKnownLight != null)
						match.getTasks().wait(1, () -> player.sendBlockChange(lastKnownLight.location(), lastKnownLight.location().getBlock().getBlockData()));
					if (location.getBlock().isReplaceable()) {
						final Light blockData = (Light) Material.LIGHT.createBlockData();
						blockData.setLevel(7);
						player.sendBlockChange(location, blockData);
					}
					matchData.getLightMap().put(player.getUniqueId(), newLight);
				}
			// tick impostors
			} else {
				// display kill cooldown
				if (KILL_ITEM.get().isSimilar(inventory.getItem(3))) {
					long killCooldown = matchData.getKillCooldown(minigamer);
					if (killCooldown != -1) {
						if (killCooldown - 1 == 0)
							matchData.getKillCooldowns().remove(minigamer.getUniqueId());
						else
							matchData.getKillCooldowns().put(minigamer.getUniqueId(), killCooldown - 1);
					}
					inventory.setItem(3, KILL_ITEM.get().asQuantity(Math.max(1, 1 + matchData.getKillCooldownAsSeconds(minigamer))));
				}
				// venting tick
				if (matchData.getVenters().containsKey(minigamer.getUniqueId())) {
					Location dest = matchData.getVenters().get(minigamer.getUniqueId());
					if (!LocationUtils.locationsEqual(location, dest))
						match.getTasks().sync(() -> minigamer.teleportAsync(dest));
					minigamer.sendActionBar(new JsonBuilder("Crouch (", NamedTextColor.RED).next(Component.keybind("key.sneak")).next(") to exit vent"));
				}
			}

			// update report item if a corpse is nearby
			ItemStack reportItem = inventory.getItem(2);
			//noinspection ConstantConditions - item name cannot be null thanks to #hasDisplayName check
			if (reportItem != null && reportItem.hasItemMeta() && reportItem.getItemMeta().hasDisplayName() && "Report".equals(AdventureUtils.asPlainText(reportItem.getItemMeta().displayName()))) {
				boolean bodyFound = false;
				if (minigamer.isAlive()) {
					for (Body body : matchData.getBodies().values()) {
						if (body.getReportBoundingBox().contains(location.toVector())) {
							inventory.setItem(2, new ItemBuilder(REPORT_ITEM.get())
								.componentLore(new JsonBuilder("Report ", NamedTextColor.DARK_AQUA).next(body.getPlayerColor()).next("'s body")).build());
							bodyFound = true;
							break;
						}
					}
				}
				if (!bodyFound)
					inventory.setItem(2, EMPTY_REPORT_ITEM.get());
			}
		}
	}

	public static final Component COMPLETED_TASK_TEXT = new JsonBuilder("Task Complete!", NamedTextColor.GREEN).build();

	@EventHandler
	public void onTaskCompletion(MinigamerCompleteTaskPartEvent event) {
		Match match = event.getMatch();
		if (!match.isMechanic(this)) return;
		SoundUtils.Jingle.SABOTAGE_VOTE.play(event.getMinigamer().getOnlinePlayer());
		event.getMinigamer().sendActionBar(COMPLETED_TASK_TEXT);
		SabotageMatchData matchData = match.getMatchData();
		match.getTasks().wait(1, () -> matchData.initGlow(event.getMinigamer()));
		if (matchData.getProgress() == 1)
			match.end();
	}

	@Override
	public void onQuit(@NotNull MatchQuitEvent event) {
		super.onQuit(event);
		UUID uuid = event.getMinigamer().getUniqueId();
		SabotageMatchData matchData = event.getMatch().getMatchData();
		event.getMinigamer().hideBossBar(matchData.getBossbar());
		Chat.setActiveChannel(event.getMinigamer(), Chat.StaticChannel.MINIGAMES);
		matchData.getVenters().remove(uuid);
		matchData.getTasks().remove(uuid);
		matchData.getPlayerColors().remove(uuid);
		final var entities = matchData.getArmorStandTasks().stream().map(ArmorStandTask::getEntity).collect(Collectors.toList());
		GlowUtils.unglow(entities).receivers(event.getMinigamer().getPlayer()).run();
	}

	@Override
	public void onEnd(@NotNull MatchEndEvent event) {
		super.onEnd(event);
		Match match = event.getMatch();
		SabotageMatchData matchData = match.getMatchData();
		match.hideBossBar(matchData.getBossbar());
		match.getMinigamers().forEach(minigamer -> Chat.setActiveChannel(minigamer, Chat.StaticChannel.MINIGAMES));
		final var entities = matchData.getArmorStandTasks().stream().map(ArmorStandTask::getEntity).collect(Collectors.toList());
		GlowUtils.unglow(entities).receivers(event.getMatch().getOnlinePlayers()).run();
	}

	@Override
	public void onDeath(@NotNull MinigamerDeathEvent event) {
		Minigamer minigamer = event.getMinigamer();
		Match match = minigamer.getMatch();
		SabotageMatchData matchData = match.getMatchData();
		Chat.setActiveChannel(minigamer, matchData.getSpectatorChannel());
		event.showDeathMessage(false);
		new SoundBuilder(Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR).receiver(minigamer).volume(1).pitch(0.9).play();

		JsonBuilder builder = new JsonBuilder();
		if (event.getAttacker() != null) {
			new SoundBuilder(Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR).receiver(event.getAttacker()).volume(.5).pitch(1.2).play();
			builder.next("You were killed by ").next(event.getAttacker().getNickname(), matchData.getColor(event.getAttacker()).colored());
		} else
			builder.next("You have been ejected");

		final Duration fade = Duration.ofSeconds(1).dividedBy(2);
		new TitleBuilder().players(minigamer).title(builder).times(fade, TimeUtils.TickTime.SECOND.duration(4), fade).send();
		minigamer.setAlive(false);
		minigamer.addPotionEffect(new PotionEffectBuilder(PotionEffectType.NIGHT_VISION).infinite().amplifier(0).ambient(true));

		final Player player = minigamer.getOnlinePlayer();
		PlayerUtils.showPlayers(player, match.getOnlinePlayers());
		PlayerUtils.showPlayer(player).to(match.getDeadOnlinePlayers());
		PlayerUtils.hidePlayer(player).from(match.getAlivePlayers());
		if (shouldBeOver(match))
			match.end();
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
		event.getMatch().getTasks().wait(1, () -> {
			Minigamer minigamer = event.getMinigamer();
			PlayerInventory inventory = minigamer.getOnlinePlayer().getInventory();
			inventory.clear();

			SabotageMatchData matchData = event.getMatch().getMatchData();
			SabotageColor color = matchData.getColor(minigamer);
			setArmor(minigamer.getOnlinePlayer(), color);

			SabotageTeam team = SabotageTeam.of(minigamer);
			inventory.setItem(1, USE_ITEM.get());
			inventory.setItem(2, EMPTY_REPORT_ITEM.get());
			if (team == SabotageTeam.IMPOSTOR) {
				inventory.setItem(3, KILL_ITEM.get());
				inventory.setItem(4, SABOTAGE_MENU.get());
			}
			if (team == SabotageTeam.IMPOSTOR || !minigamer.isAlive())
				minigamer.addPotionEffect(new PotionEffectBuilder(PotionEffectType.NIGHT_VISION).infinite().amplifier(0).ambient(true));
		});
	}

	@EventHandler
	public void onVote(MinigamerVoteEvent event) {
		if (!event.getMatch().isMechanic(this)) return;
		if (event.isCancelled() || !event.getMinigamer().isAlive() || (event.getTarget() != null && !event.getTarget().isAlive())) {
			event.setCancelled(true);
			new SoundBuilder(Sound.ENTITY_VILLAGER_NO).receiver(event.getMinigamer()).category(SoundCategory.VOICE).volume(0.8).play();
			return;
		}
		SoundUtils.Jingle.SABOTAGE_VOTE.play(event.getMatch().getOnlinePlayers());
	}

	private void giveVentItems(Minigamer minigamer, Block vent, Container container) {
		new SoundBuilder(Sound.BLOCK_IRON_TRAPDOOR_OPEN).receiver(minigamer).play();
		PlayerInventory inventory = minigamer.getOnlinePlayer().getInventory();
		inventory.clear();
		Location currentLoc = vent.getLocation();
		inventory.setItem(0, ResourcePackNumber.of(1, ColorType.RED).get().name("Crouch to Exit").lore("&f" + StringUtils.getFlooredCoordinateString(currentLoc) + " " + container.getCustomName() + " 0").loreize(false).build());
		int count = 1;
		for (ItemStack itemStack : container.getInventory()) {
			if (isNullOrAir(itemStack)) continue;
			inventory.setItem(count, ResourcePackNumber.of(1 + count, ColorType.RED).get().name("Crouch to Exit").lore(itemStack.getItemMeta().getDisplayName()).loreize(false).build());
			count += 1;
			if (count > 8)
				break;
		}
		onItemHeldEvent(new PlayerItemHeldEvent(minigamer.getOnlinePlayer(), inventory.getHeldItemSlot(), 0));
		inventory.setHeldItemSlot(0);
	}

	private Container getVentContainer(Location location) {
		Location loc = location.clone();
		loc.setY(0);
		if (!(loc.getBlock().getState() instanceof Container container)) return null;
		Component customName = container.customName();
		if (customName == null) return null;
		String containerName = AdventureUtils.asPlainText(customName);
		if (!Utils.isDouble(containerName)) return null;
		return container;
	}

	private Container getVentContainer(Block block) {
		return getVentContainer(block.getLocation());
	}

	@EventHandler
	public void onCrouch(PlayerToggleSneakEvent event) {
		Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (event.isSneaking() && minigamer.isPlaying(this)) {
			SabotageMatchData matchData = minigamer.getMatch().getMatchData();
			if (matchData.getVenters().containsKey(minigamer.getUniqueId())) {
				new SoundBuilder(Sound.BLOCK_IRON_TRAPDOOR_CLOSE).receiver(minigamer).play();
				matchData.exitVent(minigamer);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onItemHeldEvent(PlayerItemHeldEvent event) {
		Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;
		ItemStack item = event.getPlayer().getInventory().getItem(event.getNewSlot());
		if (isNullOrAir(item)) return;
		if (!item.hasItemMeta()) return;
		ItemMeta itemMeta = item.getItemMeta();
		if (!itemMeta.hasLore()) return;
		if (!itemMeta.hasDisplayName()) return;
		//noinspection ConstantConditions - item name cannot be null thanks to #hasDisplayName check
		if (!"Crouch to Exit".equals(AdventureUtils.asPlainText(itemMeta.displayName()))) return;
		//noinspection ConstantConditions - item lore cannot be null thanks to #hasLore check
		Location location = LocationUtils.parse(minigamer.getOnlinePlayer().getWorld().getName() + " " + itemMeta.getLore().get(0));
		location.add(.5, .1875-.5, .5);
		minigamer.getMatch().<SabotageMatchData>getMatchData().getVenters().put(minigamer.getUniqueId(), location);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent event) {
		Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!Utils.ActionGroup.RIGHT_CLICK.applies(event)) return;
		SabotageMatchData matchData = minigamer.getMatch().getMatchData();
		ItemStack item = event.getItem();
		SabotageTeam team = SabotageTeam.of(minigamer);

		if (matchData.isMeetingActive()) {
			if (VOTING_ITEM.get().isSimilar(item))
				matchData.getVotingScreen().open(minigamer.getOnlinePlayer());
			else
				event.setCancelled(true);
		} else {
			if (USE_ITEM.get().isSimilar(item)) {
				if (team == SabotageTeam.IMPOSTOR) {
					Block block = minigamer.getOnlinePlayer().getLocation().getBlock();
					Container container = getVentContainer(block);
					if (block.getType() == Material.IRON_TRAPDOOR && block.getRelative(0, -1, 0).getType() == Material.COAL_BLOCK && container != null) {
						giveVentItems(minigamer, block, container);
					}
				} else {
					Task task = matchData.getNearbyTask(minigamer);
					if (task != null)
						task.nextPart().instantiateMenu(task).open(minigamer.getOnlinePlayer());
				}
			} else if (SABOTAGE_MENU.get().isSimilar(item)) {
				new ImpostorMenu(matchData.getArena()).open(minigamer.getOnlinePlayer());
			} else if (minigamer.isAlive() && item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals(colorize("&eReport")) && item.getItemMeta().hasLore()) {
				//noinspection ConstantConditions - item lore cannot be null thanks to #hasLore check
				String lore = AdventureUtils.asPlainText(item.getItemMeta().lore().get(0));
				String color = lore.split(" ")[1].split("'")[0];
				matchData.startMeeting(minigamer, SabotageColor.valueOf(color.replace(' ', '_').toUpperCase()));
			}
		}
	}

	@EventHandler
	public void onButtonInteract(PlayerInteractAtEntityEvent event) {
		Player player = event.getPlayer();
		Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isPlaying(this)) return;
		if (!minigamer.isAlive()) return;
		if (!(event.getRightClicked() instanceof ArmorStand armorStand)) return;
		ItemStack helmet = armorStand.getEquipment().getHelmet();
		if (isNullOrAir(helmet) || helmet.getType() != Material.RED_CONCRETE) return;

		SabotageMatchData matchData = minigamer.getMatch().getMatchData();
		SabotageMatchData.ButtonState state = matchData.button(minigamer);

		JsonBuilder builder = switch (state) {
			case COOLDOWN -> {
				int canButtonIn = matchData.canButtonIn();
				yield new JsonBuilder("&cYou may call an emergency meeting in " + StringUtils.plural(canButtonIn + " second", canButtonIn));
			}
			case USED -> new JsonBuilder("&cYou have used your emergency meeting button");
			case SABOTAGE -> new JsonBuilder("&cMeetings cannot be called during a crisis");
			case USABLE -> null;
		};

		if (builder != null)
			minigamer.sendActionBar(builder);
		else
			matchData.startMeeting(minigamer);
	}

	@Override
	public void announceWinners(@NotNull Match match) {
		List<Minigamer> winners = match.getMinigamers().stream().filter(minigamer -> minigamer.getScore() > 0).collect(Collectors.toList());
		JsonBuilder builder = new JsonBuilder();
		if (winners.isEmpty())
			builder.group().next("&bThe Crewmates")
					.hover(new JsonBuilder(AdventureUtils.commaJoinText(winners)).color(NamedTextColor.DARK_AQUA))
					.group().color(NamedTextColor.DARK_AQUA).next(" have won on ");
		else {
			builder.next(AdventureUtils.commaJoinText(winners.stream().map(minigamer -> {
				SabotageTeam team = SabotageTeam.of(minigamer);
				return new JsonBuilder(minigamer.getNickname(), team.colored()).hover(team); // weirdly required cast
			}).collect(Collectors.toList())));
			builder.next(StringUtils.plural(" has won on ", " have won on ", winners.size()));
		}
		Minigames.broadcast(builder.next(match.getArena()));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onHandAnimation(PlayerAnimationEvent event) {
		Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (minigamer.isPlaying(this) && event.getAnimationType() == PlayerAnimationType.ARM_SWING)
			event.setCancelled(true);
	}

	@Override
	public void onDamage(@NotNull MinigamerDamageEvent event) {
		SabotageMatchData matchData = event.getMatch().getMatchData();
		if (event.getAttacker() != null && event.getAttacker().isAlive() && SabotageTeam.of(event.getAttacker()) == SabotageTeam.IMPOSTOR && SabotageTeam.of(event.getMinigamer()) != SabotageTeam.IMPOSTOR
				&& event.getAttacker().getPlayer().getInventory().getItemInMainHand().isSimilar(KILL_ITEM.get()) && matchData.getKillCooldown(event.getMinigamer()) <= 0) {
			matchData.putKillCooldown(event.getAttacker());
			matchData.spawnBody(event.getMinigamer());
			MinigamerDeathEvent deathEvent = new MinigamerDeathEvent(event.getMinigamer(), event.getAttacker(), event.getOriginalEvent());
			if (deathEvent.callEvent())
				onDeath(deathEvent);
			if (event.getOriginalEvent() instanceof Cancellable cancellable)
				cancellable.setCancelled(true);
		} else
			event.setCancelled(true);
	}

	@Override
	public @NotNull LinkedHashMap<String, Integer> getScoreboardLines(@NotNull Minigamer minigamer) {
		LinkedHashMap<String, Integer> lines = new LinkedHashMap<>();
		if (!minigamer.getMatch().isStarted())
			return lines;
		lines.put("&6Tasks", Integer.MIN_VALUE);
		SabotageMatchData matchData = minigamer.getMatch().getMatchData();
		for (Task task : matchData.getTasks(minigamer)) {
			lines.put(task.render(), Integer.MIN_VALUE);
		}
		return lines;
	}

	@EventHandler
	public void onEnterRegion(PlayerEnteredRegionEvent event) {
		Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;
		if (!event.getRegion().getId().startsWith(minigamer.getMatch().getArena().getRegionBaseName()+"_room_")) return;
		ActionBarUtils.sendActionBar(minigamer.getOnlinePlayer(), camelCase(event.getRegion().getId().split("_room_")[1]));
	}

	private static final Set<Key> BLOCKED_SOUNDS = Set.of(
		Key.key(Key.MINECRAFT_NAMESPACE, "item.armor.equip_generic"),
		Key.key(Key.MINECRAFT_NAMESPACE, "item.armor.equip_iron"),
		Key.key(Key.MINECRAFT_NAMESPACE, "item.armor.equip_diamond"),
		Key.key(Key.MINECRAFT_NAMESPACE, "item.armor.equip_netherite"),
		Key.key(Key.MINECRAFT_NAMESPACE, "item.armor.equip_turtle"),
		Key.key(Key.MINECRAFT_NAMESPACE, "item.armor.equip_chain"),
		Key.key(Key.MINECRAFT_NAMESPACE, "item.armor.equip_elytra"),
		Key.key(Key.MINECRAFT_NAMESPACE, "item.armor.equip_leather"),
		Key.key(Key.MINECRAFT_NAMESPACE, "item.armor.equip_gold"),
		Key.key(Key.MINECRAFT_NAMESPACE, "entity.player.attack.nodamage"),
		Key.key(Key.MINECRAFT_NAMESPACE, "entity.player.attack.crit"),
		Key.key(Key.MINECRAFT_NAMESPACE, "entity.player.attack.knockback"),
		Key.key(Key.MINECRAFT_NAMESPACE, "entity.player.attack.strong"),
		Key.key(Key.MINECRAFT_NAMESPACE, "entity.player.attack.sweep"),
		Key.key(Key.MINECRAFT_NAMESPACE, "entity.player.attack.weak"),
		Key.key(Key.MINECRAFT_NAMESPACE, "entity.player.hurt"),
		Key.key(Key.MINECRAFT_NAMESPACE, "entity.armor_stand.hit"),
		Key.key(Key.MINECRAFT_NAMESPACE, "entity.armor_stand.fall"),
		Key.key(Key.MINECRAFT_NAMESPACE, "entity.armor_stand.break"),
		Key.key(Key.MINECRAFT_NAMESPACE, "entity.armor_stand.place")
	);

	@EventHandler
	public void onSoundEvent(SoundEvent event) {
		Location location = event.getEmitter().location();
		if (new WorldGuardUtils(location).getRegionsLikeAt("sabotage_\\w+", location).isEmpty())
			return;

		if (BLOCKED_SOUNDS.contains(event.getSound().name()))
			event.setCancelled(true); // TODO: don't block sounds in lobby
	}

	@Override
	public boolean shouldShowNameplate(@NotNull Minigamer target, @NotNull Minigamer viewer) {
		SabotageMatchData matchData = target.getMatch().getMatchData();
		int radius = SabotageTeam.of(target) == SabotageTeam.IMPOSTOR ? (SabotageMatchData.BRIGHT_LIGHT_LEVEL * 3) : matchData.lightLevel();
		return target.getLocation().distanceSquared(viewer.getLocation()) <= (radius * radius);
	}

	@Override
	public @Nullable JsonBuilder getNameplate(@NotNull Minigamer target, @NotNull Minigamer viewer) {
		return new JsonBuilder(Nickname.of(target), SabotageTeam.render(viewer, target).colored());
	}
}

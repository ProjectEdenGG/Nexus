package me.pugabyte.nexus.features.minigames.mechanics;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.chat.events.PublicChatEvent;
import me.pugabyte.nexus.features.menus.sabotage.ImpostorMenu;
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
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.sabotage.MinigamerCompleteTaskPartEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.sabotage.MinigamerVoteEvent;
import me.pugabyte.nexus.features.minigames.models.matchdata.SabotageMatchData;
import me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import me.pugabyte.nexus.features.minigames.models.perks.Perk;
import me.pugabyte.nexus.features.minigames.models.sabotage.SabotageColor;
import me.pugabyte.nexus.features.minigames.models.sabotage.SabotageTeam;
import me.pugabyte.nexus.features.minigames.models.sabotage.Task;
import me.pugabyte.nexus.features.minigames.models.scoreboards.MinigameScoreboard;
import me.pugabyte.nexus.framework.interfaces.Colored;
import me.pugabyte.nexus.utils.AdventureUtils;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.LocationUtils;
import me.pugabyte.nexus.utils.PacketUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.SoundUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

// TODO: ensure new players can figure out how to play
// TODO: use glow API to display tasks (and maybe packets to show an ! above tasks with hidden items?)
// TODO: sabotage menu
// TODO: admin table (imageonmap "api"?)
// TODO: cams (idfk for this one, could just teleport the player around, it'd be kinda shitty tho)
// TODO: color menu
// TODO: meeting sound
// TODO: task complete sound + action bar
// TODO: emergency meeting is broken (cannot report bodies or use the button)
// TODO: venting is not working
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
	public ItemStack getMenuItem() {
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

	@Override
	public boolean usesPerk(Class<? extends Perk> perk, Minigamer minigamer) {
		return super.usesPerk(perk, minigamer);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInventoryCloseEvent(InventoryCloseEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (minigamer.isPlaying(this))
			event.getPlayer().setItemOnCursor(null);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInventoryEvent(InventoryClickEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getWhoClicked().getPlayer());
		if (minigamer.isPlaying(this) && event.getClickedInventory() != null && (event.getClickedInventory().getType() == InventoryType.CRAFTING || event.getClickedInventory() instanceof PlayerInventory || !event.isLeftClick()))
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
		if (impostors.size() == 0)
			return true;
		SabotageMatchData matchData = match.getMatchData();
		return matchData.getProgress() == 1;
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
			if (matchData.isMeetingActive()) return;
			int lightLevel = matchData.lightLevel();
			match.getMinigamers().forEach(minigamer -> {
				Player player = minigamer.getPlayer();
				Location location = player.getLocation();
				List<Minigamer> otherPlayers = new ArrayList<>(match.getAliveMinigamers());
				Utils.removeEntityFrom(minigamer, otherPlayers);
				PacketUtils.sendFakeItem(minigamer, otherPlayers, new ItemStack(Material.AIR), EnumItemSlot.MAINHAND);
				SabotageTeam team = SabotageTeam.of(minigamer);
				if (team != SabotageTeam.IMPOSTOR) {
					Tasks.sync(() -> {
								List<Minigamer> nearby = new ArrayList<>();
								location.getNearbyEntitiesByType(Player.class, lightLevel).forEach(_player -> {
									Minigamer other = PlayerManager.get(_player);
									if (!other.isAlive() || !other.isPlaying(match)) return;
									nearby.add(other);
								});
								otherPlayers.removeAll(nearby);
								PlayerUtils.hidePlayers(minigamer, otherPlayers);
								PlayerUtils.showPlayers(minigamer, nearby);
					});
					Location lastKnownLight = matchData.getLightMap().get(player.getUniqueId());
					if (!LocationUtils.blockLocationsEqual(location, lastKnownLight) || lightLevel <= 2) {
						if (lastKnownLight != null)
							player.sendBlockChange(lastKnownLight, lastKnownLight.getBlock().getBlockData());
						if (lightLevel > 2) {
							if (location.getBlock().isReplaceable())
								player.sendBlockChange(location, Material.REDSTONE_TORCH.createBlockData()); // TODO: 1.17 - use light block
							matchData.getLightMap().put(player.getUniqueId(), location);
						} else {
							player.sendBlockChange(location, location.getBlock().getBlockData());
							matchData.getLightMap().remove(player.getUniqueId());
						}
					}
				} else {
					player.getInventory().setItem(3, KILL_ITEM.get().asQuantity(Math.max(1, 1 + matchData.killCooldown(minigamer))));
					if (matchData.getVenters().containsKey(minigamer.getUniqueId())) {
						Location dest = matchData.getVenters().get(minigamer.getUniqueId());
						if (!LocationUtils.locationsEqual(location, dest))
							minigamer.teleportAsync(dest);
						minigamer.sendActionBar(new JsonBuilder("Crouch (", NamedTextColor.RED).next(Component.keybind("key.sneak")).next(") to exit vent"));
					}
				}
				boolean bodyFound = false;
				if (minigamer.isAlive()) {
					for (SabotageMatchData.Body body : matchData.getBodies().values()) {
						if (body.getReportBoundingBox().contains(location.toVector())) {
							player.getInventory().setItem(2, new ItemBuilder(REPORT_ITEM.get())
									.componentLore(new JsonBuilder("Report ", NamedTextColor.DARK_AQUA).next(body.getPlayerColor()).next("'s body")).build());
							bodyFound = true;
							break;
						}
					}
				}
				if (!bodyFound)
					player.getInventory().setItem(2, EMPTY_REPORT_ITEM.get());
			});
		});
	}

	@EventHandler
	public void onTaskCompletion(MinigamerCompleteTaskPartEvent event) {
		Match match = event.getMatch();
		if (!match.isMechanic(this)) return;
		if (match.<SabotageMatchData>getMatchData().getProgress() == 1)
			match.end();
	}

	@Override
	public void onQuit(MatchQuitEvent event) {
		super.onQuit(event);
		UUID uuid = event.getMinigamer().getUniqueId();
		SabotageMatchData matchData = event.getMatch().getMatchData();
		event.getMinigamer().hideBossBar(matchData.getBossbar());
		Chat.setActiveChannel(event.getMinigamer(), Chat.StaticChannel.MINIGAMES);
		matchData.getVenters().remove(uuid);
		matchData.getTasks().remove(uuid);
		matchData.getPlayerColors().remove(uuid);
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
		Minigamer minigamer = event.getMinigamer();
		Match match = minigamer.getMatch();
		SabotageMatchData matchData = match.getMatchData();
		Chat.setActiveChannel(minigamer, matchData.getSpectatorChannel());
		event.setDeathMessage(null);
		minigamer.setAlive(false);
		minigamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 0, true, false, false));
		PlayerUtils.showPlayers(minigamer, match.getMinigamers());
		PlayerUtils.showPlayer(minigamer).to(match.getMinigamers().stream().filter(minigamer1 -> !minigamer1.isAlive()).collect(Collectors.toList()));
		PlayerUtils.hidePlayer(minigamer).from(match.getAliveMinigamers());
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
			if (team == SabotageTeam.IMPOSTOR || !minigamer.isAlive())
				minigamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 0, true, false, false));
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

	private void giveVentItems(Minigamer minigamer, Block vent, Container container) {
		minigamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 0, true, false, false));
		PlayerInventory inventory = minigamer.getPlayer().getInventory();
		inventory.clear();
		inventory.setHeldItemSlot(0);
		inventory.setItem(0, new ItemBuilder(Material.ARROW).customModelData(2001).name("Crouch to Exit").lore(StringUtils.getFlooredCoordinateString(vent.getLocation()) + container.getCustomName()).loreize(false).build());
		int count = 1;
		for (ItemStack itemStack : container.getInventory()) {
			if (ItemUtils.isNullOrAir(itemStack)) continue;
			inventory.setItem(count, new ItemBuilder(Material.ARROW).customModelData(2000 + count).name("Crouch to Exit").lore(itemStack.getItemMeta().getDisplayName()).loreize(false).build());
			count += 1;
			if (count > 8)
				break;
		}
	}

	private Container getVentContainer(Location location) {
		Location loc = location.clone();
		loc.setY(0);
		if (loc.getBlock().getState() instanceof Container container && Utils.isDouble(container.getCustomName()))
			return container;
		return null;
	}

	private Container getVentContainer(Block block) {
		return getVentContainer(block.getLocation());
	}

	@EventHandler
	public void onCrouch(PlayerToggleSneakEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (event.isSneaking() && minigamer.isPlaying(this)) {
			SabotageMatchData matchData = minigamer.getMatch().getMatchData();
			if (matchData.getVenters().containsKey(minigamer.getUniqueId())) {
				matchData.exitVent(minigamer);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onItemHeldEvent(PlayerItemHeldEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;
		ItemStack item = event.getPlayer().getInventory().getItem(event.getNewSlot());
		if (ItemUtils.isNullOrAir(item)) return;
		if (!item.hasItemMeta()) return;
		ItemMeta itemMeta = item.getItemMeta();
		if (!itemMeta.hasLore()) return;
		if (!itemMeta.getDisplayName().equals("&fCrouch to Exit")) return;
		Location location = LocationUtils.parseOrNull(itemMeta.getLore().get(0));
		if (location == null) return;
		location.add(.5, .1875, .5);
		minigamer.getMatch().<SabotageMatchData>getMatchData().getVenters().put(minigamer.getUniqueId(), location);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!Utils.ActionGroup.RIGHT_CLICK.applies(event)) return;
		SabotageMatchData matchData = minigamer.getMatch().getMatchData();
		ItemStack item = event.getItem();
		SabotageTeam team = SabotageTeam.of(minigamer);

		if (matchData.isMeetingActive()) {
			if (VOTING_ITEM.get().isSimilar(item))
				matchData.getVotingScreen().open(minigamer);
			else
				event.setCancelled(true);
		} else {
			if (USE_ITEM.get().isSimilar(item)) {
				if (team == SabotageTeam.IMPOSTOR) {
					Block block = minigamer.getPlayerLocation().getBlock();
					Container container = getVentContainer(block);
					if (block.getType() == Material.IRON_TRAPDOOR && block.getRelative(0, -1, 0).getType() == Material.COAL_BLOCK && container != null) {
						Location setLoc = block.getLocation().add(0, .1875, 0);
						setLoc.setYaw((float) Double.parseDouble(container.getCustomName()));
						matchData.getVenters().put(minigamer.getUniqueId(), setLoc);
						giveVentItems(minigamer, block, container);
					}
				} else {
					Map<ItemStack, Task> playerTaskItems = new HashMap<>();
					for (Task task : matchData.getTasks(minigamer)) {
						if (task.nextPart() == null)
							continue;
						playerTaskItems.put(task.nextPart().getInteractionItem(), task);
					}

					Collection<ArmorStand> armorStands = minigamer.getPlayer().getLocation().getNearbyEntitiesByType(ArmorStand.class, 2);
					Task task = null;
					for (ArmorStand armorStand : armorStands) {
						ItemStack helmet = armorStand.getEquipment().getHelmet();
						if (ItemUtils.isNullOrAir(helmet))
							continue;
						task = playerTaskItems.get(helmet);
						if (task != null)
							break;
					}

					if (task != null)
						task.nextPart().instantiateMenu(task).open(minigamer);
				}
			} else if (SABOTAGE_MENU.get().isSimilar(item)) {
				new ImpostorMenu().open(minigamer);
			} else if (minigamer.isAlive() && item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(StringUtils.colorize("&eReport")) && item.getItemMeta().hasLore()) {
				String lore = AdventureUtils.asPlainText(item.getItemMeta().lore().get(0));
				String color = lore.split(" ")[1].split("'")[0];
				matchData.startMeeting(minigamer, SabotageColor.valueOf(color.replace(' ', '_').toUpperCase()));
			}
		}
	}

	@EventHandler
	public void onButtonInteract(PlayerInteractAtEntityEvent event) {
		Player player = event.getPlayer();
		Minigamer minigamer = PlayerManager.get(player);
		if (!minigamer.isPlaying(this)) return;
		if (!minigamer.isAlive()) return;
		if (!(event.getRightClicked() instanceof ArmorStand armorStand)) return;
		ItemStack helmet = armorStand.getEquipment().getHelmet();
		if (ItemUtils.isNullOrAir(helmet) || helmet.getType() != Material.RED_CONCRETE) return;

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
			builder.group().next("&bThe Crewmates")
					.hover(new JsonBuilder(AdventureUtils.commaJoinText(winners)).color(NamedTextColor.DARK_AQUA))
					.group().color(NamedTextColor.DARK_AQUA).next(" have won on ");
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
		if (event.getAttacker() != null && event.getAttacker().isAlive() && SabotageTeam.of(event.getAttacker()) == SabotageTeam.IMPOSTOR && SabotageTeam.of(event.getMinigamer()) != SabotageTeam.IMPOSTOR
				&& event.getAttacker().getPlayer().getInventory().getItemInMainHand().isSimilar(KILL_ITEM.get()) && matchData.killCooldown(event.getMinigamer()) <= 0) {
			matchData.getKillCooldowns().put(event.getAttacker().getUniqueId(), LocalDateTime.now());
			matchData.spawnBody(event.getMinigamer());
			onDeath(new MinigamerDeathEvent(event.getMinigamer(), event.getAttacker(), event.getOriginalEvent()));
			if (event.getOriginalEvent() instanceof Cancellable cancellable)
				cancellable.setCancelled(true);
		} else
			event.setCancelled(true);
	}

	@Override
	public Map<String, Integer> getScoreboardLines(Minigamer minigamer) {
		Map<String, Integer> lines = new HashMap<>();
		if (!minigamer.getMatch().isStarted())
			return lines;
		lines.put("&6Tasks", 0);
		SabotageMatchData matchData = minigamer.getMatch().getMatchData();
		int line = -1;
		for (Task task : matchData.getTasks(minigamer)) {
			lines.put(task.render(), line);
			line -= 1;
		}
		return lines;
	}
}

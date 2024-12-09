package gg.projecteden.nexus.features.minigames.mechanics;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEndEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchQuitEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.HideAndSeekMatchData;
import gg.projecteden.nexus.features.minigames.models.perks.Perk;
import gg.projecteden.nexus.features.resourcepack.models.CustomItemCooldown;
import gg.projecteden.nexus.features.resourcepack.models.CustomSound;
import gg.projecteden.nexus.models.cooldown.Cooldown;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static gg.projecteden.nexus.utils.Distance.distance;
import static gg.projecteden.nexus.utils.LocationUtils.blockLocationsEqual;
import static gg.projecteden.nexus.utils.LocationUtils.getCenteredLocation;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.StringUtils.camelCase;
import static gg.projecteden.nexus.utils.StringUtils.plural;

public class HideAndSeek extends Infection {

	public static final ItemStack SELECTOR_ITEM = new ItemBuilder(Material.NETHER_STAR).name("&3&lSelect your Block").build();
	public static final int SELECTOR_SLOT = 8;
	public static final ItemStack STUN_GRENADE = new ItemBuilder(Material.FIREWORK_STAR).name("&3&lStun Grenade").build();
	public static final int STUN_SLOT = 3;
	public static final ItemStack TAUNT = new ItemBuilder(Material.NOTE_BLOCK).name("&3&lTaunt").build();
	public static final int TAUNT_SLOT = 4;
	public static final ItemStack DECOY = new ItemBuilder(Material.STONE).name("&3&lDecoy").build();
	public static final int DECOY_SLOT = 5;
	public static final ItemStack RADAR = new ItemBuilder(Material.RECOVERY_COMPASS).name("&3&lRadar").build();
	public static final int RADAR_SLOT = 8;
	private static final long SOLIDIFY_PLAYER_AT = TickTime.SECOND.x(5);

	private static final long SELECTOR_COOLDOWN = TickTime.MINUTE.x(2.5);
	private static final CooldownService COOLDOWN_SERVICE = new CooldownService();

	@Override
	public @NotNull String getName() {
		return "Hide and Seek";
	}

	@Override
	public @NotNull String getDescription() {
		return "Disguise as a block and hide from the hunters";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.GRASS_BLOCK);
	}

	@Override
	public @NotNull GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

	@Override
	public boolean usesPerk(@NotNull Class<? extends Perk> perk, @NotNull Minigamer minigamer) {
		return isZombie(minigamer);
	}

	@Override
	public boolean canMoveArmor() {
		return false;
	}

	@Override
	public void onJoin(@NotNull MatchJoinEvent event) {
		super.onJoin(event);
		Minigamer minigamer = event.getMinigamer();
		Player player = minigamer.getOnlinePlayer();
		player.getInventory().setItem(0, SELECTOR_ITEM);
	}

	@EventHandler
	public void setPlayerBlock(PlayerInteractEvent event) {
		if (event.getItem() == null) return;
		if (!Utils.ActionGroup.RIGHT_CLICK.applies(event)) return;

		Player player = event.getPlayer();
		if (!player.getWorld().equals(Minigames.getWorld())) return;

		Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isIn(this)) return;

		Match match = minigamer.getMatch();
		if (event.getItem().getType().equals(SELECTOR_ITEM.getType())) {
			if (match.isStarted()) {
				if (!COOLDOWN_SERVICE.check(player.getUniqueId(), "hide-and-seek-selector", SELECTOR_COOLDOWN, false)) {
					return;
				}
			}
			new HideAndSeekMenu(match).open(player);
		}
		if (event.getItem().getType().equals(STUN_GRENADE.getType())) {
			Minigames.debug("Item == Stun Grenade");
			if (match.isStarted())
				StunGrenade.run(minigamer);
		}
		if (event.getItem().getType().equals(RADAR.getType())) {
			Minigames.debug("Item == Radar");
			if (match.isStarted())
				Radar.run(minigamer);
		}
		if (event.getItem().getType().equals(TAUNT.getType())) {
			if (match.isStarted())
				Taunt.run(minigamer);
		}
		if (event.getItem().getType().equals(DECOY.getType())) {
			if (match.isStarted())
				Decoy.run(minigamer);
		}
	}

	public void disguise(Minigamer minigamer, boolean midgame) {
		final Player player = minigamer.getOnlinePlayer();
		final Match match = minigamer.getMatch();
		final HideAndSeekMatchData matchData = match.getMatchData();
		final MiscDisguise disguise = new MiscDisguise(DisguiseType.FALLING_BLOCK, matchData.getBlockChoice(minigamer));
		disguise.setEntity(player);
		disguise.startDisguise();
		matchData.getDisguises().put(player.getUniqueId(), disguise);
		DisguiseAPI.setActionBarShown(player, false);
		minigamer.setImmobileTicks(0);
		applySelectorCooldown(minigamer);
		match.getScoreboard().update();
		if (midgame) {
			player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1F, 0.1F);
			player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, player.getLocation(), 50, .5, .5, .5, 0.01F);
		}
	}

	public void applySelectorCooldown(Minigamer minigamer) {
		minigamer.getMatch().getTasks().register(new CustomItemCooldown(SELECTOR_SLOT,"hide-and-seek-selector", SELECTOR_COOLDOWN)
			.onComplete(() -> minigamer.getPlayer().getInventory().setItem(SELECTOR_SLOT, SELECTOR_ITEM))
			.start(minigamer.getPlayer()));
	}

	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		super.onStart(event);
		Match match = event.getMatch();
		HideAndSeekMatchData matchData = match.getMatchData();
		if (matchData.getMapMaterials().isEmpty()) {
			error("Arena has no blocks whitelisted!", match);
			return;
		}

		for (Minigamer minigamer : match.getMinigamers())
			if (!isZombie(minigamer)) {
				minigamer.getPlayer().getInventory().setItem(SELECTOR_SLOT, SELECTOR_ITEM);
				minigamer.getPlayer().getInventory().setItem(TAUNT_SLOT, TAUNT);
				minigamer.getPlayer().getInventory().setItem(DECOY_SLOT, DECOY);
				minigamer.getPlayer().getInventory().setItem(STUN_SLOT, STUN_GRENADE);

				disguise(minigamer, false);
			}
			else
				minigamer.getPlayer().getInventory().setItem(RADAR_SLOT, RADAR);


		match.getTasks().repeat(0, 1, () -> {
			Map<Minigamer, Location> solidPlayers = matchData.getSolidPlayers();

			for (Minigamer minigamer : getHumans(match)) {
				Player player = minigamer.getOnlinePlayer();
				int immobileTicks = minigamer.getImmobileTicks();

				checkJustMoved(match, minigamer);

				Location location = player.getLocation();
				final Block down = location.getBlock().getRelative(BlockFace.DOWN);
				if (isNullOrAir(down) || down.isLiquid()) {
					sendActionBarWithTimer(minigamer, new JsonBuilder("&cYou cannot solidify here"));
				// check how long they've been still
				} else if (immobileTicks < TickTime.SECOND.x(2)) {
					sendActionBarWithTimer(minigamer, new JsonBuilder("&bYou are currently partially disguised as a ")
						.next(Component.translatable(matchData.getBlockChoice(minigamer.getUuid()))));
				} else if (immobileTicks < SOLIDIFY_PLAYER_AT) {
					// countdown until solidification
					int seconds = (int) Math.ceil((SOLIDIFY_PLAYER_AT - immobileTicks) / 20d);
					String display = String.format(plural("&dFully disguising in %d second", seconds) + "...", seconds);
					sendActionBarWithTimer(minigamer, display);
				} else {
					if (!solidPlayers.containsKey(minigamer))
						solidifyPlayer(match, minigamer);
					else
						disguisedBlockTick(minigamer);

				}
			}
		});

		match.getTasks().repeat(0, 5, () -> getZombies(match).forEach(minigamer -> {
			Block block = minigamer.getOnlinePlayer().getTargetBlockExact(4, FluidCollisionMode.NEVER);
			if (block == null) return;
			Material type = block.getType();
			if (MaterialTag.ALL_AIR.isTagged(type)) return;
			Component name = Component.translatable(type);

			JsonBuilder message = new JsonBuilder();
			if (matchData.getMapMaterials().contains(type))
				message.color(NamedTextColor.GREEN).next(name).next(" is a possible hider");
			else
				message.color(NamedTextColor.RED).next(name).next(" is not a possible hider");
			sendActionBarWithTimer(minigamer, message);
		}));

		match.getTasks().repeat(0, 1, () -> {
			HideAndSeekMatchData hideAndSeekMatchData = match.getMatchData();
			hideAndSeekMatchData.getDecoyLocations().forEach(decoy -> {
				Location location = decoy.getLocation();
				BlockData blockData = decoy.getBlockData();
				match.getMinigamers().forEach(minigamer -> minigamer.getPlayer().sendBlockChange(location, blockData));
			});
		});
	}

	private void checkJustMoved(Match match, Minigamer minigamer) {
		HideAndSeekMatchData matchData = match.getMatchData();
		Map<Minigamer, Location> solidPlayers = matchData.getSolidPlayers();
		int immobileTicks = minigamer.getImmobileTicks();
		// if player just moved, break their disguise
		if (immobileTicks < SOLIDIFY_PLAYER_AT && solidPlayers.containsKey(minigamer)) {
			blockChange(minigamer, solidPlayers.remove(minigamer), Material.AIR);
			PlayerUtils.showPlayer(minigamer.getPlayer()).to(minigamer.getMatch().getOnlinePlayers());
			if (minigamer.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY))
				minigamer.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
			FallingBlock fallingBlock = matchData.getSolidBlocks().remove(minigamer.getOnlinePlayer().getUniqueId());
			if (fallingBlock != null)
				fallingBlock.remove();
			matchData.getDisguises().get(minigamer.getUniqueId()).startDisguise();
		}
	}

	private void solidifyPlayer(Match match, Minigamer minigamer) {
		Player player = minigamer.getOnlinePlayer();
		HideAndSeekMatchData matchData = match.getMatchData();
		Map<Minigamer, Location> solidPlayers = matchData.getSolidPlayers();
		int immobileTicks = minigamer.getImmobileTicks();
		Material blockChoice = matchData.getBlockChoice(minigamer.getUuid());
		Location location = player.getLocation();

		if (immobileTicks == SOLIDIFY_PLAYER_AT && MaterialTag.ALL_AIR.isTagged(location.getBlock().getType())) {
			// save fake block location
			solidPlayers.put(minigamer, location);
			// create a falling block to render on the hider's client
			if (blockChoice.isSolid() && blockChoice.isOccluding()) {
				BlockData blockData = blockChoice.createBlockData();

				// Copy nearby block data if logs
				if (MaterialTag.LOGS.isTagged(blockChoice)) {
					for (BlockFace blockFace : BlockFace.values()) {
						final Block relative = location.getBlock().getRelative(blockFace);
						if (relative.getType() == blockChoice) {
							blockData = relative.getBlockData();
							break;
						}
					}
				}

				FallingBlock fallingBlock = player.getWorld().spawnFallingBlock(getCenteredLocation(location), blockData);
				fallingBlock.setGravity(false);
				fallingBlock.setHurtEntities(false);
				fallingBlock.setDropItem(false);
				fallingBlock.setVelocity(new Vector());
				matchData.getSolidBlocks().put(player.getUniqueId(), fallingBlock);
				// stop their disguise (as otherwise the hider sees 2 of their block)
				matchData.getDisguises().get(player.getUniqueId()).stopDisguise();
			}
			// add invisibility to hide them/their falling block disguise
			player.addPotionEffect(new PotionEffectBuilder(PotionEffectType.INVISIBILITY).infinite().ambient(true).build());
			PlayerUtils.hidePlayer(player).from(minigamer.getMatch().getOnlinePlayers());
			// run usual ticking
			disguisedBlockTick(minigamer);
		} else
			sendActionBarWithTimer(minigamer, "&cYou cannot fully disguise inside non-air blocks!");
	}

	@Override
	public void announceRelease(Match match) {
		match.broadcast(new JsonBuilder("&cThe seekers have been released!"));
	}

	private void disguisedBlockTick(Minigamer minigamer) {
		final Player player = minigamer.getOnlinePlayer();
		final HideAndSeekMatchData matchData = minigamer.getMatch().getMatchData();
		final Material blockChoice = matchData.getBlockChoice(minigamer);
		blockChange(minigamer, matchData.getSolidPlayers().get(minigamer), blockChoice);

		JsonBuilder message = new JsonBuilder("&aYou are currently fully disguised as a ").next(Component.translatable(blockChoice));
		if (matchData.getSolidBlocks().containsKey(player.getUniqueId())) {
			matchData.getSolidBlocks().get(player.getUniqueId()).setTicksLived(1);
			if (!MaterialTag.ALL_AIR.isTagged(player.getInventory().getItemInMainHand().getType()))
				message = new JsonBuilder("&cWarning: Your weapon is visible!");
		}
		sendActionBarWithTimer(minigamer, message);
	}

	protected void blockChange(Minigamer origin, Location location, Material block) {
		origin.getMatch().getMinigamers().forEach(minigamer -> {
			if (!minigamer.equals(origin))
				minigamer.getOnlinePlayer().sendBlockChange(location, block.createBlockData());
		});
	}

	@Override
	public boolean useScoreboardNumbers(Match match) {
		return match.isStarted() && match.getTimer().getTime() <= match.getArena().getSeconds()/2;
	}

	@Override
	public @NotNull LinkedHashMap<String, Integer> getScoreboardLines(@NotNull Match match) {
		if (!match.isStarted() || match.getTimer().getTime() > match.getArena().getSeconds()/2)
			return super.getScoreboardLines(match);
		HideAndSeekMatchData matchData = match.getMatchData();
		LinkedHashMap<String, Integer> lines = new LinkedHashMap<>();
		List<Minigamer> humans = getHumans(match);
		lines.put("", Integer.MIN_VALUE);
		lines.put("&3&lPlayer Count", Integer.MIN_VALUE);
		lines.put("- " + getZombieTeam(match).getVanillaColoredName(), getZombies(match).size());
		lines.put("- " + getHumanTeam(match).getVanillaColoredName(), humans.size());

		lines.put("&3&lSurviving Blocks", Integer.MIN_VALUE);
		Map<Material, Integer> blockCounts = new HashMap<>();
		humans.forEach(minigamer -> {
			Material blockChoice = matchData.getBlockChoice(minigamer);
			blockCounts.compute(blockChoice, ($, integer) -> integer == null ? 1 : integer+1);
		});
		blockCounts.forEach((material, integer) -> lines.put(camelCase(material), integer));
		return lines;
	}

	public void cleanup(Minigamer minigamer) {
		DisguiseAPI.undisguiseToAll(minigamer.getOnlinePlayer());
	}

	public void cleanup(Match match) {
		match.getMinigamers().forEach(this::cleanup);

		if (match.getMatchData() instanceof HideAndSeekMatchData hideAndSeekMatchData) {
			hideAndSeekMatchData.getSolidBlocks().forEach(($, fallingBlock) -> fallingBlock.remove());
			hideAndSeekMatchData.getFlashBangItems().forEach(Entity::remove);
			hideAndSeekMatchData.getDecoyLocations().forEach(decoy -> {
				match.getMinigamers().forEach(minigamer -> minigamer.getOnlinePlayer().sendBlockChange(decoy.getLocation(), Material.AIR.createBlockData()));
			});
		}
	}

	@Override
	public void onQuit(@NotNull MatchQuitEvent event) {
		super.onQuit(event);
		cleanup(event.getMinigamer());
	}

	@Override
	public void onEnd(@NotNull MatchEndEvent event) {
		super.onEnd(event);
		cleanup(event.getMatch());
	}

	@Override
	public void onDeath(@NotNull MinigamerDeathEvent event) {
		super.onDeath(event);
		cleanup(event.getMinigamer());
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		// this method is basically checking to see if a hunter has swung at a hider's fake block
		Minigamer minigamer = Minigamer.of(event.getPlayer());

		if (
				minigamer.isPlaying(this) &&
						isZombie(minigamer) &&
						event.getAction() == Action.LEFT_CLICK_BLOCK &&
						event.getHand() != null &&
						event.getHand().equals(EquipmentSlot.HAND)
		) {
			HideAndSeekMatchData matchData = minigamer.getMatch().getMatchData();
			Location blockLocation;
			if (event.getClickedBlock() == null)
				return;

			blockLocation = event.getClickedBlock().getLocation();
			Minigames.debug("Block Location: " + blockLocation);

			for (Map.Entry<Minigamer, Location> entry : matchData.getSolidPlayers().entrySet()) {
				Minigamer target = entry.getKey();
				Location location = entry.getValue();
				if (blockLocationsEqual(blockLocation, location)) {
					EntityDamageByEntityEvent e = new EntityDamageByEntityEvent(minigamer.getOnlinePlayer(), target.getPlayer(), EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1);
					e.callEvent();
					if (e.isCancelled()) return;

					minigamer.getOnlinePlayer().attack(target.getPlayer());
					target.setImmobileTicks(0);
					new SoundBuilder(Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK).receiver(minigamer.getOnlinePlayer()).category(SoundCategory.PLAYERS).play();
					new SoundBuilder(Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK).receiver(target.getPlayer()).category(SoundCategory.PLAYERS).play();
					return;
				}
			}

			Minigames.debug("Decoy Locatoins: " + matchData.getDecoyLocations());
			for (Decoy.DecoyInstance decoyLocation : new ArrayList<>(matchData.getDecoyLocations()))
				if (blockLocationsEqual(blockLocation, decoyLocation.getLocation()))
					decoyLocation.remove(matchData.getMatch());

		}
	}

	@Override
	public boolean canUseBlock(@NotNull Minigamer minigamer, @NotNull Block block) {
		return false;
	}

	@RequiredArgsConstructor
	@Title("&3&lSelect your Block")
	public class HideAndSeekMenu extends InventoryProvider {
		private final Match match;

		@Override
		protected int getRows(Integer page) {
			return MenuUtils.calculateRows(match.getArena().getBlockList().size(), 1);
		}

		@Override
		public void init() {
			addCloseItem();
			HideAndSeekMatchData matchData = match.getMatchData();
			List<Material> materials = matchData.getMapMaterials();
			List<ClickableItem> items = new ArrayList<>();
			materials.forEach(material -> {
				ItemStack itemStack = new ItemStack(material);
				items.add(ClickableItem.of(itemStack, e -> {
					matchData.getBlockChoices().put(viewer.getUniqueId(), material);
					viewer.closeInventory();
					if (!match.isStarted())
						PlayerUtils.send(viewer, new JsonBuilder("&3You have selected ").next(Component.translatable(material, NamedTextColor.YELLOW)));
					else
						disguise(Minigamer.of(viewer), true);

				}));
			});
			paginate(items);
		}

	}

	public static class StunGrenade {

		private static final long COOLDOWN_TIME = TickTime.SECOND.x(60);
		private static final int RANGE = 8;
		private static final PotionEffect STUN_EFFECT = new PotionEffectBuilder(PotionEffectType.BLINDNESS).particles(true).duration(TickTime.SECOND.x(5)).build();

		public static void run(Minigamer minigamer) {
			Minigames.debug("Stun#run #1");
			if (!COOLDOWN_SERVICE.check(minigamer.getUuid(), "hide-and-seek-stun", COOLDOWN_TIME, false))
				return;
			Minigames.debug("Stun#run #2");

			Match match = minigamer.getMatch();
			HideAndSeekMatchData matchData = match.getMatchData();
			HideAndSeek hideAndSeek = match.getMechanic();
			AtomicInteger iteration = new AtomicInteger(0);
			Item item = minigamer.getLocation().getWorld().spawn(minigamer.getLocation(), Item.class, _item -> {
				_item.setItemStack(new ItemStack(Material.FIREWORK_STAR));
				_item.setCanMobPickup(false);
				_item.setPickupDelay((short) 32767);
			});
			matchData.getFlashBangItems().add(item);
			int taskId = Tasks.repeat(0, 1, () -> {
				if (iteration.get() == 0) {
					item.getLocation().getWorld().playSound(item.getLocation(), Sound.ENTITY_WITCH_THROW, 1, 1);
					new SoundBuilder(CustomSound.FLASH_BANG)
						.location(item.getLocation())
						.receivers(match.getOnlinePlayers().stream().filter(p -> distance(p, item).lte(16)).toList())
						.muteMenuItem(MuteMenuItem.JOKES)
						.play();
					item.setVelocity(minigamer.getLocation().getDirection().normalize());
				}

				if (iteration.get() < 20)
					item.getLocation().getWorld().spawnParticle(Particle.SMOKE_NORMAL, item.getLocation(), 1, 0, 0, 0, 0.1);

				if (iteration.getAndIncrement() == 20) {
					item.getLocation().getWorld().spawnParticle(Particle.FLASH, item.getLocation(), 2, 0, 0, 0, 0.1);
					item.getLocation().getWorld().playSound(item.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
					Tasks.wait(1, () -> item.getLocation().getWorld().playSound(item.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1, 1));

					for (Minigamer minigamer1 : hideAndSeek.getZombies(match)) {
						if (distance(minigamer1, item).lt(RANGE)) {
							minigamer1.addPotionEffect(STUN_EFFECT);
						}
					}

					item.remove();
					matchData.getFlashBangItems().remove(item);
				}
			});
			match.getTasks().register(taskId);

			match.getTasks().register(new CustomItemCooldown(STUN_SLOT,"hide-and-seek-stun", COOLDOWN_TIME)
				.onComplete(() -> minigamer.getPlayer().getInventory().setItem(STUN_SLOT, STUN_GRENADE))
				.start(minigamer.getPlayer()));
		}

	}

	public static class Radar {

		private static final long COOLDOWN_TIME = TickTime.SECOND.x(20);
		private static final int RANGE = 20;

		public static void run(Minigamer minigamer) {
			Minigames.debug("Radar#run #1");
			if (!COOLDOWN_SERVICE.check(minigamer.getUuid(), "hide-and-seek-radar", COOLDOWN_TIME, false))
				return;
			Minigames.debug("Radar#run #2");
			
			Match match = minigamer.getMatch();
			HideAndSeek hideAndSeek = match.getMechanic();
			if (hideAndSeek.getHumans(match).stream().anyMatch(_minigamer -> distance(minigamer, _minigamer).lt(RANGE))) {
				minigamer.sendMessage(new JsonBuilder("There is a hider nearby!").color(Color.LIME));
				minigamer.getOnlinePlayer().playSound(minigamer.getOnlinePlayer().getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
			} else {
				minigamer.sendMessage(new JsonBuilder("No hiders in range").color(Color.RED));
				new SoundBuilder(CustomSound.NOTE_BUZZ).volume(2).pitch(0.1).receiver(minigamer.getOnlinePlayer()).play();
			}

			match.getTasks().register(new CustomItemCooldown(RADAR_SLOT,"hide-and-seek-radar", COOLDOWN_TIME)
				.onComplete(() -> minigamer.getPlayer().getInventory().setItem(RADAR_SLOT, RADAR))
				.start(minigamer.getPlayer()));
		}

	}


	public static class Taunt {

		private static final long COOLDOWN_TIME = TickTime.SECOND.x(30);
		private static final int SEEKER_RANGE = 15;

		public static void run(Minigamer minigamer) {
			if (!COOLDOWN_SERVICE.check(minigamer.getUuid(), "hide-and-seek-taunt", COOLDOWN_TIME, false))
				return;

			Match match = minigamer.getMatch();
			HideAndSeek hideAndSeek = match.getMechanic();

			boolean seekerInRange = hideAndSeek.getZombies(match).stream().anyMatch(seeker -> seeker.getLocation().distance(minigamer.getLocation()) <= SEEKER_RANGE);
			if (!seekerInRange) {
				new JsonBuilder("There is no seekers in range!").color(Color.RED).send(minigamer);
				return;
			}
			minigamer.getLocation().getWorld().playSound(minigamer.getLocation(), Sound.ENTITY_CAT_AMBIENT, 1, 1);

			for (int i = 0; i < 3; i++)
				new ParticleBuilder(Particle.NOTE)
					.offset(RandomUtils.randomDouble(1), RandomUtils.randomDouble(1), RandomUtils.randomDouble(1))
					.count(0)
					.allPlayers()
					.location(minigamer.getLocation().toCenterLocation().add(RandomUtils.randomDouble(-0.3, 0.3), RandomUtils.randomDouble(.75, 1.25), RandomUtils.randomDouble(-0.3, 0.3)))
					.spawn();

			lowerCooldowns(minigamer, "hide-and-seek-stun");
			lowerCooldowns(minigamer, "hide-and-seek-selector");
			lowerCooldowns(minigamer, "hide-and-seek-decoy");

			match.getTasks().register(new CustomItemCooldown(TAUNT_SLOT, "hide-and-seek-taunt", COOLDOWN_TIME)
				.onComplete(() -> minigamer.getPlayer().getInventory().setItem(TAUNT_SLOT, TAUNT))
				.start(minigamer.getPlayer()));
		}

		private static void lowerCooldowns(Minigamer minigamer, String type) {
			Cooldown cooldown = COOLDOWN_SERVICE.get(minigamer.getUuid());
			if (!cooldown.exists(type))
				return;

			LocalDateTime cooldownTime = cooldown.get(type).minusSeconds(10);
			cooldown.create(type, cooldownTime);
			COOLDOWN_SERVICE.save(cooldown);
		}

	}

	public static class Decoy {

		private static final long COOLDOWN_TIME = TickTime.SECOND.x(60);

		public static void run(Minigamer minigamer) {
			if (!COOLDOWN_SERVICE.check(minigamer.getUuid(), "hide-and-seek-decoy", COOLDOWN_TIME, false))
				return;

			final Location blockLoc = minigamer.getLocation().toBlockLocation();
			final Block down = minigamer.getLocation().getBlock().getRelative(BlockFace.DOWN);
			if (isNullOrAir(down) || down.isLiquid()) {
				minigamer.sendMessage(new JsonBuilder("&cYou cannot place a decoy here"));
				return;
			}

			HideAndSeekMatchData matchData = minigamer.getMatch().getMatchData();

			Material blockChoice = matchData.getBlockChoice(minigamer.getUuid());
			BlockData blockData = blockChoice.createBlockData();

			for (Minigamer gamer : minigamer.getMatch().getMinigamers()) {
				gamer.getPlayer().sendBlockChange(blockLoc, blockData);
			}


			DecoyInstance instance = new DecoyInstance(blockLoc, blockData);
			int taskId = minigamer.getMatch().getTasks().wait(TickTime.MINUTE.x(2), () -> instance.remove(matchData.getMatch()));
			instance.setTaskId(taskId);
			matchData.getDecoyLocations().add(instance);

			blockLoc.getWorld().playSound(blockLoc, Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);

			minigamer.getMatch().getTasks().register(new CustomItemCooldown(DECOY_SLOT, "hide-and-seek-decoy", COOLDOWN_TIME)
				.onComplete(() -> minigamer.getPlayer().getInventory().setItem(DECOY_SLOT, DECOY))
				.start(minigamer.getPlayer()));
		}

		@Data
		@RequiredArgsConstructor
		public static class DecoyInstance {

			private int taskId;
			@NonNull
			private Location location;
			@NonNull
			private BlockData blockData;

			public void remove(Match match) {
				Tasks.cancel(taskId);
				match.getMinigamers().forEach(minigamer -> minigamer.getPlayer().sendBlockChange(location, Material.AIR.createBlockData()));
				location.getWorld().playSound(location, Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
				for (int i = 0; i < 10; i++)
					new ParticleBuilder(Particle.SMOKE_NORMAL)
						.location(location.toCenterLocation())
						.allPlayers()
						.extra(.1)
						.spawn();

				HideAndSeekMatchData matchData = match.getMatchData();
				matchData.getDecoyLocations().remove(this);
			}

			@Override
			public boolean equals(Object o) {
				if (!(o instanceof DecoyInstance decoyInstance)) return false;
				return decoyInstance.taskId == taskId;
			}

		}

	}

}

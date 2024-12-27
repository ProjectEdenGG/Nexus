package gg.projecteden.nexus.features.minigames.listeners;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.managers.MatchManager;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchQuitEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerLoadoutEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.sabotage.MinigamerDisplayTimerEvent;
import gg.projecteden.nexus.features.minigames.models.mechanics.Mechanic;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.VanillaMechanic;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import gg.projecteden.nexus.features.minigames.models.perks.ParticleProjectile;
import gg.projecteden.nexus.features.minigames.models.perks.common.ParticleProjectilePerk;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.perkowner.PerkOwner;
import gg.projecteden.nexus.models.perkowner.PerkOwnerService;
import gg.projecteden.nexus.utils.BorderUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import static gg.projecteden.nexus.utils.Distance.distance;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.PlayerUtils.runCommand;
import static gg.projecteden.nexus.utils.StringUtils.getShortLocationString;

public class MatchListener implements Listener {

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (minigamer.getMatch() == null)
			return;

		if (event.getInventory().getLocation() == null)
			return;

		if (minigamer.getMatch().getMechanic().canOpenInventoryBlocks())
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onMatchStart(MatchStartEvent event) {
		event.getMatch().getOnlinePlayers().forEach(this::disableCheats);
	}

	@EventHandler
	public void onMatchJoin(MatchJoinEvent event) {
		disableCheats(event.getMinigamer().getPlayer());
	}

	public void disableCheats(Player player) {
		if (player.hasPermission("voxelsniper.sniper"))
			runCommand(player, "b paint");
		if (player.hasPermission("worldguard.region.bypass.*"))
			runCommand(player, "wgedit off");
		if (Vanish.isVanished(player))
			runCommand(player, "vanish off");
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onTeleport(PlayerTeleportEvent event) {
		Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (minigamer.getMatch() == null)
			return;
		if (minigamer.canTeleport())
			return;
		if (minigamer.getMatch().getMechanic() instanceof VanillaMechanic)
			if (event.getCause() == TeleportCause.NETHER_PORTAL)
				return;

		if (event.getFrom().getWorld().equals(event.getTo().getWorld()))
			if (distance(event.getFrom(), event.getTo()).lt(2))
				return;

		event.setCancelled(true);
		Nexus.debug("Cancelled minigamer " + minigamer.getNickname() + " teleporting from " + getShortLocationString(event.getFrom()) + " to " + getShortLocationString(event.getTo()));
		minigamer.tell("&cYou cannot teleport while in a game! &3If you are trying to leave, use &c/mgm quit");
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Minigames.debug("PlayerQuitEvent " + Nickname.of(event.getPlayer()));
		Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying())
			return;

		minigamer.quit();

		Tasks.wait(1, () -> {
			if (!minigamer.isOnline())
				minigamer.toGamelobby();
		});
	}

	@EventHandler
	public void onMatchQuit(MatchQuitEvent event) {
		MatchManager.janitor();
	}

	// TODO: Break and place events

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isPlaying())
			return;

		minigamer.getMatch().getMechanic().onPlayerInteract(minigamer, event);
	}

	@EventHandler(ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player))
			return;

		Minigamer minigamer = Minigamer.of(event.getWhoClicked());
		if (!minigamer.isPlaying())
			return;

		Mechanic mechanic = minigamer.getMatch().getMechanic();
		ItemStack item = event.getCurrentItem();

		if (event.getClickedInventory() instanceof PlayerInventory) {
			if (event.getSlotType() == InventoryType.SlotType.ARMOR && (!mechanic.canMoveArmor() || (item != null && !MaterialTag.WEARABLE.isTagged(item.getType()))))
				event.setCancelled(true);
		} else if (!mechanic.canOpenInventoryBlocks()) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onItemDrop(PlayerDropItemEvent event) {
		Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying())
			return;

		ItemStack item = event.getItemDrop().getItemStack();
		if (!minigamer.getMatch().getMechanic().canDropItem(item))
			event.setCancelled(true);
	}


	@EventHandler
	public void onItemPickup(EntityPickupItemEvent event) {
		if (!Minigames.isMinigameWorld(event.getEntity().getWorld()))
			return;

		// TODO: Entity pickups?
		if (!(event.getEntity() instanceof Player player))
			return;

		Arena arena = ArenaManager.getFromLocation(event.getItem().getLocation());
		if (arena == null)
			return;

		Match match = MatchManager.find(arena);
		if (match == null)
			return;

		Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isIn(match))
			event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		RegainReason regainReason = event.getRegainReason();
		if (regainReason != RegainReason.REGEN && regainReason != RegainReason.SATIATED)
			return;

		Minigamer minigamer = Minigamer.of(event.getEntity());
		if (!minigamer.isPlaying())
			return;

		if (!minigamer.getMatch().isStarted() || !minigamer.isAlive())
			return;

		Mechanic mechanic = minigamer.getMatch().getMechanic();

		if (mechanic.getRegenType().hasCustomRegen())
			event.setCancelled(true);
	}

	public void onShootProjectile(Player player, Projectile projectile) {
		Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isPlaying())
			return;
		Minigames.getModifier().onProjectileSpawn(projectile);
		PerkOwner owner = new PerkOwnerService().get(player);
		owner.getEnabledPerksByClass(ParticleProjectilePerk.class).forEach(perk -> new ParticleProjectile(perk, projectile, minigamer.getMatch()));
	}

	@EventHandler(ignoreCancelled = true)
	public void onBowShoot(EntityShootBowEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		if (!(event.getProjectile() instanceof Projectile))
			return;
		onShootProjectile((Player) event.getEntity(), (Projectile) event.getProjectile());
	}

	@EventHandler(ignoreCancelled = true)
	public void onProjectileFire(PlayerLaunchProjectileEvent event) {
		onShootProjectile(event.getPlayer(), event.getProjectile());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onProjectileCollideWithPlayer(ProjectileHitEvent event) {
		if (!(event.getHitEntity() instanceof Player hitPlayer))
			return;

		if (!(event.getEntity().getShooter() instanceof Player))
			return;

		Minigamer victim = Minigamer.of(hitPlayer);
		if (!victim.isPlaying())
			return;

		if (victim.isRespawning() || !victim.isAlive()) {
			event.setCancelled(true);
			return;
		}
		if (victim.getTeam() == null)
			return;

		if (!(victim.getMatch().getMechanic() instanceof TeamMechanic))
			return;

		Minigamer attacker = Minigamer.of((Player) event.getEntity().getShooter());
		if (!attacker.isPlaying(victim.getMatch()))
			return;

		if (!victim.getTeam().equals(attacker.getTeam()))
			return;

		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onProjectileCollideWithEntity(ProjectileHitEvent event) {
		Entity hitEntity = event.getHitEntity();
		if (!(hitEntity instanceof Hanging
			|| hitEntity instanceof ArmorStand
			|| hitEntity instanceof Minecart
			|| hitEntity instanceof Boat
			|| hitEntity instanceof FallingBlock))
			return;

		if (!(event.getEntity().getShooter() instanceof Player player))
			return;

		Minigamer attacker = Minigamer.of(player);
		if (!attacker.isPlaying())
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onLoadout(MinigamerLoadoutEvent event) {
		Minigames.getModifier().afterLoadout(event.getMinigamer());
	}

	@EventHandler
	public void onDisplayTimer(MinigamerDisplayTimerEvent event) {
		event.getMatch().getMechanic().onDisplayTimer(event);
	}

	@EventHandler
	public void onChangedWorlds(PlayerChangedWorldEvent event) {
		final Player player = event.getPlayer();
		if (WorldGroup.of(player) != WorldGroup.MINIGAMES)
			return;

		if (!BorderUtils.isOutsideBorder(player))
			return;

		player.sendMessage("Outside of border");
		BorderUtils.moveInsideBorder(player);
	}

	@EventHandler
	public void onJump(PlayerJumpEvent event) {
		Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying())
			return;

		boolean cancel = !Minigames.getModifier().canJump(minigamer);
		event.setCancelled(cancel);
	}

	@EventHandler
	public void on(ProjectileHitEvent event) {
		if (!(event.getHitEntity() instanceof Player hitPlayer))
			return;

		if (!(event.getEntity().getShooter() instanceof Player shooter))
			return;

		if (!Minigamer.of(shooter).isPlaying())
			return;

		if (!Minigamer.of(hitPlayer).isPlaying() || Minigamer.of(hitPlayer).isSpectating())
			event.setCancelled(true);
	}

	@EventHandler
	public void onClickSpectateCompass(PlayerInteractEvent event) {
		if (isNullOrAir(event.getItem()))
			return;
		if (!Utils.ActionGroup.RIGHT_CLICK.applies(event))
			return;

		Player player = event.getPlayer();
		if (!player.getWorld().equals(Minigames.getWorld()))
			return;

		Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isSpectating())
			return;

		if (!event.getItem().getType().equals(Minigamer.SPECTATING_COMPASS.getType()))
			return;

		event.setCancelled(true);
		minigamer.getMatch().getMechanic().getSpectateMenu(minigamer.getMatch()).open(player);
	}

}

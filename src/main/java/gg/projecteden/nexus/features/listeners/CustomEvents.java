package gg.projecteden.nexus.features.listeners;

import gg.projecteden.nexus.features.listeners.events.FirstSubWorldGroupVisitEvent;
import gg.projecteden.nexus.features.listeners.events.FirstWorldGroupVisitEvent;
import gg.projecteden.nexus.features.listeners.events.GolemBuildEvent.IronGolemBuildEvent;
import gg.projecteden.nexus.features.listeners.events.GolemBuildEvent.SnowGolemBuildEvent;
import gg.projecteden.nexus.features.listeners.events.LivingEntityDamageByPlayerEvent;
import gg.projecteden.nexus.features.listeners.events.LivingEntityKilledByPlayerEvent;
import gg.projecteden.nexus.features.listeners.events.PlayerChangingWorldEvent;
import gg.projecteden.nexus.features.listeners.events.PlayerDamageByPlayerEvent;
import gg.projecteden.nexus.features.listeners.events.PlayerInteractHeadEvent;
import gg.projecteden.nexus.features.listeners.events.SubWorldGroupChangedEvent;
import gg.projecteden.nexus.features.listeners.events.WorldGroupChangedEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import gg.projecteden.nexus.utils.worldgroup.SubWorldGroup;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class CustomEvents implements Listener {

	@EventHandler
	public void onChangingWorlds(PlayerTeleportEvent event) {
		final World fromWorld = event.getFrom().getWorld();
		final World toWorld = event.getTo().getWorld();
		if (fromWorld.equals(toWorld))
			return;

		final var changingWorldsEvent = new PlayerChangingWorldEvent(event.getPlayer(), fromWorld, toWorld);
		if (changingWorldsEvent.callEvent())
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		final Player player = event.getPlayer();
		final Nerd nerd = Nerd.of(player);

		final WorldGroup oldWorldGroup = WorldGroup.of(event.getFrom());
		final WorldGroup newWorldGroup = WorldGroup.of(player);

		final SubWorldGroup oldSubWorldGroup = SubWorldGroup.of(event.getFrom());
		final SubWorldGroup newSubWorldGroup = SubWorldGroup.of(player);

		if (oldWorldGroup != newWorldGroup) {
			new WorldGroupChangedEvent(player, oldWorldGroup, newWorldGroup).callEvent();

			if (!nerd.getVisitedWorldGroups().contains(newWorldGroup)) {
				new FirstWorldGroupVisitEvent(player, newWorldGroup).callEvent();
				nerd.getVisitedWorldGroups().add(newWorldGroup);
				new NerdService().save(nerd);
			}
		}

		if (oldSubWorldGroup != newSubWorldGroup) {
			new SubWorldGroupChangedEvent(player, oldSubWorldGroup, newSubWorldGroup).callEvent();

			if (!nerd.getVisitedSubWorldGroups().contains(newSubWorldGroup)) {
				new FirstSubWorldGroupVisitEvent(player, newSubWorldGroup).callEvent();
				nerd.getVisitedSubWorldGroups().add(newSubWorldGroup);
				new NerdService().save(nerd);
			}
		}
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent event) {
		Player attacker = null;
		if (event.getDamager() instanceof Player) {
			attacker = (Player) event.getDamager();
		} else if (event.getDamager() instanceof Projectile projectile) {
			if (projectile.getShooter() instanceof Player)
				attacker = (Player) projectile.getShooter();
		}

		if (attacker == null)
			return;

		if (event.getEntity() instanceof Player player)
			new PlayerDamageByPlayerEvent(player, attacker, event).callEvent();
		else if (event.getEntity() instanceof LivingEntity livingEntity) {
			new LivingEntityDamageByPlayerEvent(livingEntity, attacker, event).callEvent();

			if (livingEntity.getHealth() - event.getFinalDamage() <= 0)
				new LivingEntityKilledByPlayerEvent(livingEntity, attacker, event).callEvent();
		}
	}

	@EventHandler
	public void onSpawnIronGolem(BlockPlaceEvent event) {
		final Block block = event.getBlock();
		if (!block.getWorld().getName().toLowerCase().startsWith("bingo"))
			return;

		Player player = event.getPlayer();
		if (!MaterialTag.ALL_PUMPKINS.isTagged(block))
			return;

		Block HEAD = block;

		Block TORSO = block.getRelative(0, -1, 0);
		Block LEGS = block.getRelative(0, -2, 0);

		Block ARM1_X = block.getRelative(-1, -1, 0);
		Block ARM2_X = block.getRelative(1, -1, 0);

		Block ARM1_Z = block.getRelative(0, -1, -1);
		Block ARM2_Z = block.getRelative(0, -1, 1);

		if (!Material.IRON_BLOCK.equals(TORSO.getType()) || !Material.IRON_BLOCK.equals(LEGS.getType()))
			return;

		if (Material.IRON_BLOCK.equals(ARM1_X.getType()) && Material.IRON_BLOCK.equals(ARM2_X.getType())) {
			ARM1_X.setType(Material.AIR);
			ARM2_X.setType(Material.AIR);
		} else if (Material.IRON_BLOCK.equals(ARM1_Z.getType()) && Material.IRON_BLOCK.equals(ARM2_Z.getType())) {
			ARM1_Z.setType(Material.AIR);
			ARM2_Z.setType(Material.AIR);
		} else
			return;

		event.setCancelled(true);
		HEAD.setType(Material.AIR);
		TORSO.setType(Material.AIR);
		LEGS.setType(Material.AIR);

		Location location = TORSO.getLocation().toCenterLocation();
		final IronGolem golem = location.getWorld().spawn(location, IronGolem.class, SpawnReason.BUILD_IRONGOLEM);
		if (!golem.isValid())
			return;

		new IronGolemBuildEvent(player, golem).callEvent();
	}

	@EventHandler
	public void onSpawnSnowGolem(BlockPlaceEvent event) {
		final Block block = event.getBlock();
		if (!block.getWorld().getName().contains("bingo"))
			return;

		Player player = event.getPlayer();
		if (!List.of(Material.CARVED_PUMPKIN, Material.JACK_O_LANTERN).contains(block.getType()))
			return;

		Block HEAD = block;
		Block TORSO = block.getRelative(0, -1, 0);
		Block LEGS = block.getRelative(0, -2, 0);

		if (!Material.SNOW_BLOCK.equals(TORSO.getType()) || !Material.SNOW_BLOCK.equals(LEGS.getType()))
			return;

		event.setCancelled(true);
		HEAD.setType(Material.AIR);
		TORSO.setType(Material.AIR);
		LEGS.setType(Material.AIR);

		Location location = TORSO.getLocation().toCenterLocation();
		final Snowman golem = location.getWorld().spawn(location, Snowman.class, SpawnReason.BUILD_SNOWMAN);
		if (!golem.isValid())
			return;

		new SnowGolemBuildEvent(player, golem).callEvent();
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND)
			return;

		if (!ActionGroup.CLICK_BLOCK.applies(event))
			return;

		final Block block = event.getClickedBlock();
		if (isNullOrAir(block) || block.getType() != Material.PLAYER_HEAD)
			return;

		new PlayerInteractHeadEvent(event.getPlayer(), block).callEvent();
	}

}

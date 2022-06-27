package gg.projecteden.nexus.features.listeners;

import gg.projecteden.nexus.features.listeners.events.FirstSubWorldGroupVisitEvent;
import gg.projecteden.nexus.features.listeners.events.FirstWorldGroupVisitEvent;
import gg.projecteden.nexus.features.listeners.events.GolemBuildEvent.IronGolemBuildEvent;
import gg.projecteden.nexus.features.listeners.events.GolemBuildEvent.SnowGolemBuildEvent;
import gg.projecteden.nexus.features.listeners.events.LivingEntityDamageByPlayerEvent;
import gg.projecteden.nexus.features.listeners.events.PlayerDamageByPlayerEvent;
import gg.projecteden.nexus.features.listeners.events.SubWorldGroupChangedEvent;
import gg.projecteden.nexus.features.listeners.events.WorldGroupChangedEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.utils.worldgroup.SubWorldGroup;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.Location;
import org.bukkit.Material;
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

import java.util.List;

public class CustomEvents implements Listener {

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
		else if (event.getEntity() instanceof LivingEntity livingEntity)
			new LivingEntityDamageByPlayerEvent(livingEntity, attacker, event).callEvent();
	}

	@EventHandler
	public void onSpawnIronGolem(BlockPlaceEvent event) {
		if (!event.getBlock().getWorld().getName().contains("bingo"))
			return;

		Player player = event.getPlayer();
		if (event.getBlock().getType().equals(Material.PUMPKIN)) {
			Location HEAD = event.getBlock().getLocation();

			Location TORSO = new Location(HEAD.getWorld(), HEAD.getX(), HEAD.getY() - 1, HEAD.getZ());
			Location LEGS = new Location(HEAD.getWorld(), HEAD.getX(), HEAD.getY() - 2, HEAD.getZ());

			Location ARM1_X = new Location(HEAD.getWorld(), HEAD.getX() - 1, HEAD.getY() - 1, HEAD.getZ());
			Location ARM2_X = new Location(HEAD.getWorld(), HEAD.getX() + 1, HEAD.getY() - 1, HEAD.getZ());

			Location ARM1_Z = new Location(HEAD.getWorld(), HEAD.getX(), HEAD.getY() - 1, HEAD.getZ() - 1);
			Location ARM2_Z = new Location(HEAD.getWorld(), HEAD.getX(), HEAD.getY() - 1, HEAD.getZ() + 1);

			if (Material.IRON_BLOCK.equals(TORSO.getBlock().getType()) && Material.IRON_BLOCK.equals(LEGS.getBlock().getType())) {
				if (Material.IRON_BLOCK.equals(ARM1_X.getBlock().getType()) && Material.IRON_BLOCK.equals(ARM2_X.getBlock().getType())) {
					event.setCancelled(true);
					HEAD.getBlock().setType(Material.AIR);
					TORSO.getBlock().setType(Material.AIR);
					LEGS.getBlock().setType(Material.AIR);
					ARM1_X.getBlock().setType(Material.AIR);
					ARM2_X.getBlock().setType(Material.AIR);

					Location location = event.getBlock().getLocation().add(0, -1, 0).toCenterLocation();
					final IronGolem golem = location.getWorld().spawn(location, IronGolem.class);
					if (golem.isValid())
						new IronGolemBuildEvent(player, golem).callEvent();
				} else if (Material.IRON_BLOCK.equals(ARM1_Z.getBlock().getType()) && Material.IRON_BLOCK.equals(ARM2_Z.getBlock().getType())) {
					event.setCancelled(true);
					HEAD.getBlock().setType(Material.AIR);
					TORSO.getBlock().setType(Material.AIR);
					LEGS.getBlock().setType(Material.AIR);
					ARM1_Z.getBlock().setType(Material.AIR);
					ARM2_Z.getBlock().setType(Material.AIR);

					Location location = event.getBlock().getLocation().add(0, -1, 0).toCenterLocation();
					final IronGolem golem = location.getWorld().spawn(location, IronGolem.class, SpawnReason.BUILD_IRONGOLEM);
					if (golem.isValid())
						new IronGolemBuildEvent(player, golem).callEvent();
				}
			}
		}
	}

	@EventHandler
	public void onSpawnSnowGolem(BlockPlaceEvent event) {
		if (!event.getBlock().getWorld().getName().contains("bingo"))
			return;

		Player player = event.getPlayer();
		if (List.of(Material.CARVED_PUMPKIN, Material.JACK_O_LANTERN).contains(event.getBlock().getType())) {
			Location HEAD = event.getBlock().getLocation();

			Location TORSO = new Location(HEAD.getWorld(), HEAD.getX(), HEAD.getY() - 1, HEAD.getZ());
			Location LEGS = new Location(HEAD.getWorld(), HEAD.getX(), HEAD.getY() - 2, HEAD.getZ());

			if (Material.SNOW_BLOCK.equals(TORSO.getBlock().getType()) && Material.SNOW_BLOCK.equals(LEGS.getBlock().getType())) {
				event.setCancelled(true);
				HEAD.getBlock().setType(Material.AIR);
				TORSO.getBlock().setType(Material.AIR);
				LEGS.getBlock().setType(Material.AIR);

				Location location = event.getBlock().getLocation().add(0, -1, 0).toCenterLocation();
				final Snowman golem = location.getWorld().spawn(location, Snowman.class, SpawnReason.BUILD_IRONGOLEM);
				if (golem.isValid())
					new SnowGolemBuildEvent(player, golem).callEvent();
			}
		}
	}

}

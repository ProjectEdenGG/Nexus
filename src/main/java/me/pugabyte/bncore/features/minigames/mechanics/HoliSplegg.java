package me.pugabyte.bncore.features.minigames.mechanics;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.annotations.Regenerating;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchTimerTickEvent;
import me.pugabyte.bncore.features.minigames.models.matchdata.HoliSpleggMatchData;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.BlockIterator;

@Regenerating("floor")
public final class HoliSplegg extends TeamlessMechanic {

	@Override
	public String getName() {
		return "Holi Splegg";
	}

	@Override
	public String getDescription() {
		return "Shoot blocks with eggs to break them and extinguish the armor stand.";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.FLINT_AND_STEEL);
	}

	@Override
	public void onStart(MatchStartEvent event) {
		super.onStart(event);
		HoliSpleggMatchData matchData = event.getMatch().getMatchData();
		matchData.setArmorStand(summonArmorStand(event.getMatch().getArena()));

		event.getMatch().getTasks().repeat(0, 20, () -> {
			if (matchData.getArmorStand().getLocation().clone().subtract(0, 1, 0).getBlock().getType() == Material.WATER)
				event.getMatch().end();
		});
	}

	@EventHandler
	public void onTick(MatchTimerTickEvent event) {
		if (!event.getMatch().isMechanic(this)) return;

		event.getMatch().getMinigamers().forEach(Minigamer::scored);
	}

	@Override
	public void onEnd(MatchEndEvent event) {
		super.onEnd(event);
		HoliSpleggMatchData matchData = event.getMatch().getMatchData();
		matchData.getArmorStand().remove();
	}

	private ArmorStand summonArmorStand(Arena arena) {
		Region region = arena.getRegion("floor_1");
		ArmorStand armorStand = Minigames.getGameworld().spawn(new Location(Minigames.getGameworld(), 2548, 29, 710), ArmorStand.class);
		armorStand.setInvulnerable(true);
		armorStand.setFireTicks(9999999);
		armorStand.setCustomNameVisible(true);
		armorStand.setCustomName("temp");
		Utils.blast(armorStand.getLocation().toString());
		return armorStand;
	}

	@Override
	public void onPlayerInteract(Minigamer minigamer, PlayerInteractEvent event) {
		super.onPlayerInteract(minigamer, event);

		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!event.getAction().name().contains("RIGHT_CLICK")) return;

		Material hand = minigamer.getPlayer().getInventory().getItemInMainHand().getType();
		// TODO: 1.13 material tags
		if (hand.name().contains("SPADE") || hand.name().contains("SHOVEL"))
			throwEgg(minigamer);
	}

	private void throwEgg(Minigamer minigamer) {
		Location location = minigamer.getPlayer().getLocation().add(0, 1.5, 0);
		location.add(minigamer.getPlayer().getLocation().getDirection());
		Egg egg = (Egg) minigamer.getPlayer().getWorld().spawnEntity(location, EntityType.EGG);
		egg.setVelocity(location.getDirection().multiply(1.75));
		egg.setShooter(minigamer.getPlayer());
		minigamer.getPlayer().playSound(minigamer.getPlayer().getLocation(), Sound.ENTITY_BLAZE_SHOOT, 0.5F, 2F);
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		if (!(projectile instanceof Egg)) return;

		ProjectileSource source = projectile.getShooter();
		if (!(source instanceof Player)) return;

		Minigamer minigamer = PlayerManager.get((Player) source);
		if (!minigamer.isPlaying(this)) return;

		projectile.remove();
		BlockIterator blockIter = new BlockIterator(projectile.getWorld(), projectile.getLocation().toVector(), projectile.getVelocity().normalize(), 0, 4);
		Block blockHit = null;

		while (blockIter.hasNext()) {
			blockHit = blockIter.next();
			if (blockHit.getType() != Material.AIR) break;
		}

		if (blockHit == null) return;

		breakBlock(minigamer.getMatch(), blockHit.getLocation());
	}

	public boolean breakBlock(Match match, Location location) {
		for (ProtectedRegion region : WGUtils.getRegionsAt(location.clone().add(0, .1, 0))) {
			if (!match.getArena().ownsRegion(region.getId(), "floor")) continue;

			Material type = location.getBlock().getType();
			if (!match.getArena().canUseBlock(type))
				return false;

			playBlockBreakSound(location);
			location.getBlock().setType(Material.AIR);

			return true;
		}
		return false;
	}

	public void playBlockBreakSound(Location location) {
		location.getWorld().playSound(location, Sound.ENTITY_CHICKEN_EGG, 1.0F, 0.7F);
	}


}

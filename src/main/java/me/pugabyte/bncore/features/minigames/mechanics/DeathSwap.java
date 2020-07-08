package me.pugabyte.bncore.features.minigames.mechanics;

import com.sk89q.worldedit.bukkit.paperlib.PaperLib;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchInitializeEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.bncore.features.minigames.models.exceptions.MinigameException;
import me.pugabyte.bncore.features.minigames.models.matchdata.DeathSwapMatchData;
import me.pugabyte.bncore.features.minigames.models.matchdata.DeathSwapMatchData.Swap;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.utils.ActionBarUtils;
import me.pugabyte.bncore.utils.RandomUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class DeathSwap extends TeamlessMechanic {
	@Override
	public String getName() {
		return "Death Swap";
	}

	@Override
	public String getDescription() {
		return "Trap players by swapping with them!";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.ENDER_PEARL);
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

	@Override
	public boolean canOpenInventoryBlocks() {
		return true;
	}

	public int radius = 1000;
	public String world = "deathswap";

	@Override
	public void onInitialize(MatchInitializeEvent event) {
		super.onInitialize(event);
		if (getWorld() == null)
			throw new MinigameException("DeathSwap world not created");
		getWorld().getWorldBorder().reset();
	}

	@Override
	public void onStart(MatchStartEvent event) {
		super.onStart(event);

		setWorldBorder(getWorld().getHighestBlockAt(RandomUtils.randomInt(-5000, 5000), RandomUtils.randomInt(-5000, 5000)).getLocation());

		event.getMatch().getTasks().wait(1, () -> spreadPlayers(event.getMatch()));

		event.getMatch().getTasks().wait(Time.SECOND.x(30), () -> delay(event.getMatch()));
	}

	@Override
	public void onDeath(MinigamerDeathEvent event) {
		if (!event.getMinigamer().isAlive()) return;
		event.getMinigamer().setAlive(false);

		if (event.getAttacker() != null) {
			event.getAttacker().scored();
		} else {
			DeathSwapMatchData matchData = event.getMatch().getMatchData();
			Minigamer killer = matchData.getKiller(event.getMinigamer());
			if (killer != null) {
				event.setDeathMessage(event.getMinigamer().getColoredName() + " &3was killed by " + killer.getColoredName());
				killer.scored();
			}
		}

		Tasks.wait(1, () -> super.onDeath(event));
	}

	@Override
	public void onEnd(MatchEndEvent event) {
		super.onEnd(event);
		getWorld().getWorldBorder().reset();
	}

	public World getWorld() {
		return Bukkit.getWorld(this.world);
	}

	private void spreadPlayers(Match match) {
		for (Minigamer minigamer : match.getMinigamers()) {
			minigamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Time.SECOND.x(20), 10, false, false));
			minigamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Time.SECOND.x(5), 10, false, false));
			minigamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, Time.SECOND.x(5), 255, false, false));
			minigamer.getPlayer().setVelocity(new Vector(0, 0, 0));
			Tasks.async(() -> randomTeleport(minigamer));
		}
	}

	private void randomTeleport(Minigamer minigamer) {
		Location center = getWorld().getWorldBorder().getCenter();
		int x = RandomUtils.randomInt(-radius / 2, radius / 2);
		int z = RandomUtils.randomInt(-radius / 2, radius / 2);
		Location random = new Location(getWorld(), x, 0, z).add(center.getX(), 0, center.getZ());
		PaperLib.getChunkAtAsync(random, true).thenRun(() -> {
			Location location = getWorld().getHighestBlockAt(random).getLocation();
			if (location.getBlock().getType().isSolid()) {
				minigamer.canTeleport(true);
				PaperLib.teleportAsync(minigamer.getPlayer(), location.clone().add(0, 2, 0))
						.thenRun(() -> minigamer.canTeleport(false));
			} else
				randomTeleport(minigamer);
		});
	}

	private void setWorldBorder(Location center) {
		WorldBorder border = getWorld().getWorldBorder();
		border.setCenter(center);
		border.setSize(radius);
		border.setDamageAmount(0);
		border.setWarningDistance(1);
	}

	public void swap(Match match) {
		List<Minigamer> swappingList = new ArrayList<>(match.getMinigamers().stream().filter(Minigamer::isAlive).collect(Collectors.toList()));
		swappingList.forEach(player -> {
			player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 1));
			ActionBarUtils.sendActionBar(player.getPlayer(), "&3SWAPPING");
		});

		if (match.getMinigamers().size() % 2 != 0) {
			Minigamer playerOne = RandomUtils.randomElement(swappingList);
			swappingList.remove(playerOne);
			Minigamer playerTwo = RandomUtils.randomElement(swappingList);
			swappingList.remove(playerTwo);
			Minigamer playerThree = RandomUtils.randomElement(swappingList);
			swappingList.remove(playerThree);

			Swap one = new Swap(playerOne);
			Swap two = new Swap(playerTwo);
			Swap three = new Swap(playerThree);
			one.with(playerThree);
			two.with(playerOne);
			three.with(playerTwo);
		}
		while (swappingList.size() > 0) {
			Minigamer playerOne = RandomUtils.randomElement(swappingList);
			swappingList.remove(playerOne);
			Minigamer playerTwo = RandomUtils.randomElement(swappingList);
			swappingList.remove(playerTwo);

			Swap one = new Swap(playerOne);
			Swap two = new Swap(playerTwo);
			one.with(playerTwo);
			two.with(playerOne);
		}
		aggroMobs(match);
		delay(match);
	}

	public void aggroMobs(Match match) {
		match.getMinigamers().stream().filter(Minigamer::isAlive).collect(Collectors.toList()).forEach(player ->
				Utils.getNearbyEntities(player.getPlayer().getLocation(), 10).keySet().stream()
						.filter(entity -> entity instanceof Mob)
						.map(entity -> (Mob) entity)
						.filter(mob -> mob.getTarget() instanceof Player)
						.forEach(mob -> mob.setTarget(player.getPlayer())));
	}

	public void delay(Match match) {
		if (match.getMinigamers().size() <= 1) {
			match.end();
			return;
		}
		match.getTasks().wait(Time.SECOND.x(RandomUtils.randomInt(30, 120)), () -> swap(match));
	}

	@EventHandler
	public void onCraftItem(CraftItemEvent event) {
		Minigamer minigamer = PlayerManager.get((Player) event.getView().getPlayer());
		if (!minigamer.isPlaying(this)) return;

		if (event.getRecipe().getResult().getType() != Material.LADDER) return;

		event.setCancelled(true);
		minigamer.tell("You cannot use ladders in this game! (Too OP)");
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		if (event.getBlock().getType() != Material.LADDER) return;

		event.setCancelled(true);
		minigamer.tell("You cannot use ladders in this game! (Too OP)");
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		if (event.getBlock().getType() != Material.LADDER && event.getBlock().getRelative(BlockFace.UP).getType() != Material.LADDER) return;

		event.setCancelled(true);
		minigamer.tell("You cannot use ladders in this game! (Too OP)");
	}

}

package me.pugabyte.nexus.features.minigames.mechanics;

import com.sk89q.worldedit.bukkit.paperlib.PaperLib;
import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchInitializeEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.nexus.features.minigames.models.exceptions.MinigameException;
import me.pugabyte.nexus.features.minigames.models.matchdata.BingoMatchData;
import me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TitleUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;
import static me.pugabyte.nexus.utils.WorldUtils.getRandomLocationInBorder;

public final class Bingo extends TeamlessMechanic {
	@Override
	public @NotNull String getName() {
		return "Bingo";
	}

	@Override
	public @NotNull String getDescription() {
		return "TODO";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.CRAFTING_TABLE);
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

	@Override
	public boolean canOpenInventoryBlocks() {
		return true;
	}

	@Override
	public boolean canDropItem(ItemStack item) {
		return true;
	}

	public int matchRadius = 3000;
	public int worldRadius = 7000;
	public String world = "bingo";

	public World getWorld() {
		return Bukkit.getWorld(world);
	}

	@Override
	public void onInitialize(MatchInitializeEvent event) {
		super.onInitialize(event);
		if (getWorld() == null)
			throw new MinigameException("Bingo world not created");
		getWorld().getWorldBorder().reset();
	}

	@Override
	public void onStart(MatchStartEvent event) {
		super.onStart(event);

		getWorld().setTime(0);

		setWorldBorder(getWorld().getHighestBlockAt(RandomUtils.randomInt(-worldRadius, worldRadius), RandomUtils.randomInt(-worldRadius, worldRadius)).getLocation());

		event.getMatch().getTasks().wait(1, () -> spreadPlayers(event.getMatch()));
	}

	@Override
	public void onEnd(MatchEndEvent event) {
		super.onEnd(event);
		getWorld().getWorldBorder().reset();
	}

	@Override
	public void onDeath(MinigamerDeathEvent event) {
		final Player player = event.getMinigamer().getPlayer();

		for (ItemStack itemStack : player.getInventory())
			if (!isNullOrAir(itemStack))
				player.getWorld().dropItemNaturally(player.getLocation(), itemStack);

		super.onDeath(event);
	}

	@Override
	public void onDeath(Minigamer victim) {
		victim.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Time.SECOND.x(3), 10, false, false));
		TitleUtils.sendTitle(victim.getPlayer(), "&cYou died!");

		final Location bed = victim.getPlayer().getBedSpawnLocation();
		if (bed != null && getWorld().equals(bed.getWorld()))
			victim.teleport(bed);
		else
			victim.teleport(victim.getMatch().<BingoMatchData>getMatchData().getSpawnpoints().get(victim.getUniqueId()));
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
		Location random = getRandomLocationInBorder(getWorld());
		PaperLib.getChunkAtAsync(random, true).thenRun(() -> {
			Location location = getWorld().getHighestBlockAt(random).getLocation();
			if (location.getBlock().getType().isSolid())
				minigamer.getMatch().<BingoMatchData>getMatchData().spawnpoint(minigamer, location);
			else
				randomTeleport(minigamer);
		});
	}

	private void setWorldBorder(Location center) {
		WorldBorder border = getWorld().getWorldBorder();
		border.setCenter(center);
		border.setSize(matchRadius);
		border.setDamageAmount(0);
		border.setWarningDistance(1);
	}

}

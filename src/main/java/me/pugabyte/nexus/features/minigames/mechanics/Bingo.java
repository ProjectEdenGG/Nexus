package me.pugabyte.nexus.features.minigames.mechanics;

import com.sk89q.worldedit.bukkit.paperlib.PaperLib;
import de.tr7zw.nbtapi.NBTItem;
import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.features.listeners.Misc.FixedCraftItemEvent;
import me.pugabyte.nexus.features.listeners.Misc.LivingEntityDamageByPlayerEvent;
import me.pugabyte.nexus.features.minigames.managers.PlayerManager;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchInitializeEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.nexus.features.minigames.models.exceptions.MinigameException;
import me.pugabyte.nexus.features.minigames.models.matchdata.BingoMatchData;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.BreakChallengeProgress;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.CraftChallengeProgress;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.KillChallengeProgress;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.ObtainChallengeProgress;
import me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TitleUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;
import static me.pugabyte.nexus.utils.WorldUtils.getRandomLocationInBorder;
import static org.bukkit.Material.CRAFTING_TABLE;

public final class Bingo extends TeamlessMechanic {

	private static final String NBT_KEY = "nexus.bingo.obtained";

	@Override
	public @NotNull String getName() {
		return "Bingo";
	}

	@Override
	public @NotNull String getDescription() {
		return "Fill out your Bingo board from doing unique survival challenges";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(CRAFTING_TABLE);
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

	@EventHandler
	public void onBlock(BlockBreakEvent event) {
		final Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final BreakChallengeProgress progress = matchData.getProgress(minigamer, BreakChallengeProgress.class);

		progress.getItems().add(new ItemStack(event.getBlock().getType(), 1));
	}

	@EventHandler
	public void onObtain(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;

		final Minigamer minigamer = PlayerManager.get(player);
		if (!minigamer.isPlaying(this)) return;

		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final ObtainChallengeProgress progress = matchData.getProgress(minigamer, ObtainChallengeProgress.class);

		final ItemStack itemStack = event.getItem().getItemStack();
		final NBTItem nbtItem = new NBTItem(itemStack);
		if (nbtItem.hasKey(NBT_KEY)) return;

		nbtItem.setBoolean(NBT_KEY, true);
		progress.getItems().add(nbtItem.getItem());
	}

	@EventHandler
	public void onCraft(FixedCraftItemEvent event) {
		final Minigamer minigamer = PlayerManager.get(event.getWhoClicked());
		if (!minigamer.isPlaying(this)) return;

		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final CraftChallengeProgress progress = matchData.getProgress(minigamer, CraftChallengeProgress.class);

		final ItemStack result = event.getResultItemStack();
		progress.getItems().add(result);
	}

	@EventHandler
	public void onKill(LivingEntityDamageByPlayerEvent event) {
		final Minigamer minigamer = PlayerManager.get(event.getAttacker());
		if (!minigamer.isPlaying(this)) return;

		final LivingEntity entity = (LivingEntity) event.getOriginalEvent().getEntity();
		if (entity.getHealth() - event.getOriginalEvent().getFinalDamage() > 0)
			return;

		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final KillChallengeProgress progress = matchData.getProgress(minigamer, KillChallengeProgress.class);

		progress.getKills().add(event.getEntity().getType());
	}

	// Breaking
	// Placing
	// Crafting
	// Enchanting
	// Brewing
	// Cooking
	// Obtaining
	// Killing
	// Eating
	// Biome
	// Distance
	// Breeding
	// Taming
	// Advancement

	// Villager trade
	// Piglin trade
	// Exp level
	// Spawning
	//

}

package me.pugabyte.nexus.features.minigames.mechanics;

import eden.utils.TimeUtils.Time;
import eden.utils.Utils.MinMaxResult;
import io.papermc.paper.event.player.PlayerTradeEvent;
import lombok.Getter;
import me.pugabyte.nexus.features.listeners.Misc.FixedCraftItemEvent;
import me.pugabyte.nexus.features.listeners.Misc.IronGolemBuildEvent;
import me.pugabyte.nexus.features.listeners.Misc.LivingEntityDamageByPlayerEvent;
import me.pugabyte.nexus.features.listeners.Misc.SnowGolemBuildEvent;
import me.pugabyte.nexus.features.minigames.managers.MatchManager;
import me.pugabyte.nexus.features.minigames.managers.PlayerManager;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MinigamerQuitEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.nexus.features.minigames.models.matchdata.BingoMatchData;
import me.pugabyte.nexus.features.minigames.models.mechanics.MechanicType;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.Challenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.CustomChallenge.CustomTask;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.StructureChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.BiomeChallengeProgress;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.BreakChallengeProgress;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.ConsumeChallengeProgress;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.CraftChallengeProgress;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.CustomChallengeProgress;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.DimensionChallengeProgress;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.KillChallengeProgress;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.ObtainChallengeProgress;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.PlaceChallengeProgress;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.StructureChallengeProgress;
import me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessVanillaMechanic;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.MaterialUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TitleBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.StructureType;
import org.bukkit.block.Biome;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;

public final class Bingo extends TeamlessVanillaMechanic {

	private static final String NBT_KEY = "nexus.bingo.obtained";

	@Override
	public @NotNull String getName() {
		return "Bingo";
	}

	@Override
	public @NotNull String getDescription() {
		return "Fill out your &c/bingo &eboard from doing unique survival challenges";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.CRAFTING_TABLE);
	}

	@Getter
	public final int worldDiameter = 7000;
	@Getter
	public final String worldName = "bingo";

	@Override
	public void onDeath(@NotNull MinigamerDeathEvent event) {
		final Player player = event.getMinigamer().getPlayer();

		for (ItemStack itemStack : player.getInventory())
			if (!isNullOrAir(itemStack))
				player.getWorld().dropItemNaturally(player.getLocation(), itemStack);

		super.onDeath(event);
	}

	@Override
	public void onDeath(@NotNull Minigamer victim) {
		victim.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Time.SECOND.x(3), 10, false, false));
		new TitleBuilder().players(victim).title("&cYou died!").stay(150).send();

		final Location bed = victim.getPlayer().getBedSpawnLocation();
		if (bed != null && getWorld().equals(bed.getWorld()))
			victim.teleport(bed);
		else
			victim.teleport(victim.getMatch().<BingoMatchData>getMatchData().getData(victim).getSpawnpoint());
	}

	@Override
	public void onJoin(@NotNull MatchJoinEvent event) {
		removeBedSpawnLocation(event.getMinigamer().getOfflinePlayer());
	}

	@Override
	public void onQuit(@NotNull MinigamerQuitEvent event) {
		removeBedSpawnLocation(event.getMinigamer().getOfflinePlayer());
	}

	@Override
	public void onEnd(@NotNull MatchEndEvent event) {
		for (Player player : event.getMatch().getPlayers())
			removeBedSpawnLocation(player);
	}

	private void removeBedSpawnLocation(OfflinePlayer offlinePlayer) {
		if (!offlinePlayer.isOnline() || offlinePlayer.getPlayer() == null)
			return;

		final Player player = offlinePlayer.getPlayer();
		if (player.getBedSpawnLocation() == null)
			return;

		if (!player.getBedSpawnLocation().getWorld().equals(getWorld()))
			return;

		player.setBedSpawnLocation(null, true);
	}

	@Override
	public @NotNull CompletableFuture<Void> onRandomTeleport(@NotNull Match match, @NotNull Minigamer minigamer, @NotNull Location location) {
		super.onRandomTeleport(match, minigamer, location);
		return minigamer.getMatch().<BingoMatchData>getMatchData().spawnpoint(minigamer, location);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onItemDrop(BlockDropItemEvent event) {
		if (!event.getPlayer().getWorld().getName().startsWith(worldName))
			return;

		for (Item item : event.getItems()) {
			ItemStack stack = item.getItemStack();
			Material ingot = MaterialUtils.oreToIngot(stack.getType());
			if (ingot == null)
				continue;

			stack.setType(ingot);
			item.setItemStack(stack);
			break;
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onEntityKill(EntityDeathEvent event) {
		if (!event.getEntity().getWorld().getName().equals(worldName)) return;
		for (ItemStack item : event.getDrops()) {
			Material cooked = MaterialUtils.rawToCooked(item.getType());
			if (cooked == null)
				continue;
			item.setType(cooked);
			return;
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		final Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this))
			return;

		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final BreakChallengeProgress progress = matchData.getProgress(minigamer, BreakChallengeProgress.class);

		progress.getItems().add(new ItemStack(event.getBlock().getType(), 1));
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		final Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this))
			return;

		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final PlaceChallengeProgress progress = matchData.getProgress(minigamer, PlaceChallengeProgress.class);

		progress.getItems().add(new ItemStack(event.getBlock().getType(), 1));
	}

	@EventHandler
	public void onObtain(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		final Minigamer minigamer = PlayerManager.get(player);
		if (!minigamer.isPlaying(this))
			return;

		final ItemStack itemStack = event.getItem().getItemStack();
		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final ObtainChallengeProgress progress = matchData.getProgress(minigamer, ObtainChallengeProgress.class);

		progress.getItems().add(itemStack);

		/* TODO Figure out why this is buggy
		final NBTItem nbtItem = new NBTItem(itemStack);
		if (nbtItem.hasKey(NBT_KEY))
			return;

		nbtItem.setBoolean(NBT_KEY, true);
		progress.getItems().add(nbtItem.getItem());
		 */
	}

	@EventHandler
	public void onObtain(InventoryCloseEvent event) {
		final Player player = (Player) event.getPlayer();
		final Minigamer minigamer = PlayerManager.get(player);
		if (!minigamer.isPlaying(this))
			return;

		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final ObtainChallengeProgress progress = matchData.getProgress(minigamer, ObtainChallengeProgress.class);

		for (ItemStack itemStack : player.getInventory().getContents()) {
			if (ItemUtils.isNullOrAir(itemStack))
				continue;

			progress.getItems().add(itemStack);

			/* TODO Figure out why this is buggy
			final NBTItem nbtItem = new NBTItem(itemStack, true);
			if (nbtItem.hasKey(NBT_KEY))
				continue;

			nbtItem.setBoolean(NBT_KEY, true);
			progress.getItems().add(nbtItem.getItem());
			 */
		}
	}

	@EventHandler
	public void onCraft(FixedCraftItemEvent event) {
		final Minigamer minigamer = PlayerManager.get(event.getWhoClicked());
		if (!minigamer.isPlaying(this))
			return;

		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final CraftChallengeProgress progress = matchData.getProgress(minigamer, CraftChallengeProgress.class);

		final ItemStack result = event.getResultItemStack();
		progress.getItems().add(result);
	}

	@EventHandler
	public void onKill(LivingEntityDamageByPlayerEvent event) {
		final Minigamer minigamer = PlayerManager.get(event.getAttacker());
		if (!minigamer.isPlaying(this))
			return;

		final LivingEntity entity = (LivingEntity) event.getOriginalEvent().getEntity();
		if (entity.getHealth() - event.getOriginalEvent().getFinalDamage() > 0)
			return;

		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final KillChallengeProgress progress = matchData.getProgress(minigamer, KillChallengeProgress.class);

		progress.getKills().add(event.getEntity().getType());
	}

	@EventHandler
	public void onConsume(PlayerItemConsumeEvent event) {
		final Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this))
			return;

		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final ConsumeChallengeProgress progress = matchData.getProgress(minigamer, ConsumeChallengeProgress.class);

		if (event.getItem().getItemMeta() instanceof PotionMeta meta)
			if (meta.getCustomEffects().isEmpty())
				return;

		progress.getItems().add(ItemBuilder.oneOf(event.getItem()).build());
	}

	private static List<Minigamer> getActiveBingoMinigamers() {
		return new ArrayList<>() {{
			for (Match match : MatchManager.getAll()) {
				if (!match.isStarted())
					continue;
				if (match.getArena().getMechanicType() != MechanicType.BINGO)
					continue;

				addAll(match.getAliveMinigamers());
			}
		}};
	}

	static {
		Tasks.repeat(Time.SECOND.x(10), Time.SECOND.x(5), () -> {
			for (Minigamer minigamer : getActiveBingoMinigamers()) {
				final Match match = minigamer.getMatch();
				final BingoMatchData matchData = match.getMatchData();
				final BiomeChallengeProgress progress = matchData.getProgress(minigamer, BiomeChallengeProgress.class);
				final Biome biome = minigamer.getPlayer().getLocation().getBlock().getBiome();
				progress.getBiomes().add(biome);
			}
		});
	}

	@EventHandler
	public void onDimensionChange(PlayerChangedWorldEvent event) {
		final Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this))
			return;

		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final DimensionChallengeProgress progress = matchData.getProgress(minigamer, DimensionChallengeProgress.class);

		progress.getDimensions().add(event.getPlayer().getWorld().getEnvironment());
	}

	static {
		Tasks.repeat(Time.SECOND.x(10), Time.SECOND.x(15), () -> {
			for (Minigamer minigamer : getActiveBingoMinigamers()) {
				final Match match = minigamer.getMatch();
				final BingoMatchData matchData = match.getMatchData();
				for (Challenge challenge : matchData.getAllChallenges(StructureChallenge.class)) {
					final StructureChallenge structureChallenge = challenge.getChallenge();
					final StructureType structureType = structureChallenge.getStructureType();
					final Location location = minigamer.getPlayer().getLocation();
					final Location found = location.getWorld().locateNearestStructure(location, structureType, 2, false);

					if (found == null)
						continue;

					found.setY(location.getY());

					if (structureType == StructureType.NETHER_FORTRESS) {
						if (found.distance(location) > 100)
							continue;
					} else {
						if (found.distance(location) > 32)
							continue;
					}

					final StructureChallengeProgress progress = matchData.getProgress(minigamer, StructureChallengeProgress.class);
					progress.getStructures().add(structureType);
				}
			}
		});
	}

	@EventHandler
	public void onIronGolemBuild(IronGolemBuildEvent event) {
		final Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this))
			return;

		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final CustomChallengeProgress progress = matchData.getProgress(minigamer, CustomChallengeProgress.class);

		progress.complete(Challenge.SPAWN_AN_IRON_GOLEM, CustomTask.SPAWN_AN_IRON_GOLEM);
	}

	@EventHandler
	public void onSnowGolemBuild(SnowGolemBuildEvent event) {
		final Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this))
			return;

		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final CustomChallengeProgress progress = matchData.getProgress(minigamer, CustomChallengeProgress.class);

		progress.complete(Challenge.SPAWN_A_SNOW_GOLEM, CustomTask.SPAWN_A_SNOW_GOLEM);
	}

	static {
		Tasks.repeat(Time.SECOND.x(10), Time.SECOND.x(3), () -> {
			for (Minigamer minigamer : getActiveBingoMinigamers()) {
				final Match match = minigamer.getMatch();
				final BingoMatchData matchData = match.getMatchData();
				final double y = minigamer.getOnlinePlayer().getLocation().getY();
				final CustomChallengeProgress progress = matchData.getProgress(minigamer, CustomChallengeProgress.class);

				if (matchData.getAllChallenges().contains(Challenge.CLIMB_TO_BUILD_HEIGHT)) {
					if (y >= 256)
						progress.complete(Challenge.CLIMB_TO_BUILD_HEIGHT, CustomTask.CLIMB_TO_BUILD_HEIGHT);
				}

				if (matchData.getAllChallenges().contains(Challenge.DIG_TO_BEDROCK)) {
					if (y <= 5)
						progress.complete(Challenge.DIG_TO_BEDROCK, CustomTask.DIG_TO_BEDROCK);
				}
			}
		});
	}

	@EventHandler
	public void onPlayerTrade(PlayerTradeEvent event) {
		final Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this))
			return;

		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final CustomChallengeProgress progress = matchData.getProgress(minigamer, CustomChallengeProgress.class);

		progress.complete(Challenge.TRADE_WITH_A_VILLAGER, CustomTask.TRADE_WITH_A_VILLAGER);
	}

	@EventHandler
	public void onPiglinBarter(PiglinBarterEvent event) {
		final MinMaxResult<Player> nearestPlayer = PlayerUtils.getNearestPlayer(event.getEntity().getLocation());
		final Player player = nearestPlayer.getObject();
		if (player == null)
			return;

		final Minigamer minigamer = PlayerManager.get(player);
		if (!minigamer.isPlaying(this))
			return;

		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final CustomChallengeProgress progress = matchData.getProgress(minigamer, CustomChallengeProgress.class);

		progress.complete(Challenge.TRADE_WITH_A_PIGLIN, CustomTask.TRADE_WITH_A_PIGLIN);
	}

}

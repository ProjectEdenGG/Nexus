package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.Utils.MinMaxResult;
import gg.projecteden.nexus.features.listeners.events.FixedCraftItemEvent;
import gg.projecteden.nexus.features.listeners.events.GolemBuildEvent.IronGolemBuildEvent;
import gg.projecteden.nexus.features.listeners.events.GolemBuildEvent.SnowGolemBuildEvent;
import gg.projecteden.nexus.features.listeners.events.LivingEntityDamageByPlayerEvent;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEndEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchQuitEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.BingoMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.Challenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.CustomChallenge.CustomTask;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.StructureChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.BiomeChallengeProgress;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.BreakChallengeProgress;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.BreedChallengeProgress;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.ConsumeChallengeProgress;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.CraftChallengeProgress;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.CustomChallengeProgress;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.DeathChallengeProgress;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.DimensionChallengeProgress;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.KillChallengeProgress;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.PlaceChallengeProgress;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.StructureChallengeProgress;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.TameChallengeProgress;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessVanillaMechanic;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.TitleBuilder;
import io.papermc.paper.event.player.PlayerTradeEvent;
import lombok.Getter;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.StructureType;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static gg.projecteden.nexus.utils.Distance.distance;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

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
	public final int worldDiameter = 10000;
	@Getter
	public final String worldName = "bingo";

	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		super.onStart(event);
		event.getMatch().getArena().getLobby().getLocation().getWorld().setDifficulty(Difficulty.NORMAL);

		Match match = event.getMatch();

		biomes(match);
		structures(match);
		yLevel(match);
		misc(match);
	}

	@Override
	public void onDeath(@NotNull Minigamer victim) {
		final Player player = victim.getOnlinePlayer();
		final BingoMatchData matchData = victim.getMatch().getMatchData();

		player.addPotionEffect(new PotionEffectBuilder(PotionEffectType.BLINDNESS).duration(TickTime.SECOND.x(3)).amplifier(10).build());
		new TitleBuilder().players(victim).title("&cYou died!").stay(150).send();

		for (ItemStack itemStack : player.getInventory())
			if (!isNullOrAir(itemStack))
				player.getWorld().dropItemNaturally(player.getLocation(), itemStack);

		victim.clearInventory();

		final Location bed = player.getBedSpawnLocation();
		if (bed != null && getWorld().equals(bed.getWorld()))
			victim.teleportAsync(bed);
		else
			victim.teleportAsync(matchData.getData(victim).getSpawnpoint());

		victim.getTeam().getLoadout().apply(victim);
		player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
	}

	@Override
	public void onJoin(@NotNull MatchJoinEvent event) {
		removeBedSpawnLocation(event.getMinigamer().getPlayer());
		super.onJoin(event);
	}

	@Override
	public void onQuit(@NotNull MatchQuitEvent event) {
		removeBedSpawnLocation(event.getMinigamer().getPlayer());
		super.onQuit(event);
	}

	@Override
	public void onEnd(@NotNull MatchEndEvent event) {
		for (Player player : event.getMatch().getOnlinePlayers())
			removeBedSpawnLocation(player);
	}

	private void removeBedSpawnLocation(Player player) {
		if (player == null)
			return;

		if (player.getBedSpawnLocation() == null)
			return;

		if (!player.getBedSpawnLocation().getWorld().equals(getWorld()))
			return;

		player.setBedSpawnLocation(null, true);
	}

	@Override
	public @NotNull CompletableFuture<Boolean> onRandomTeleport(@NotNull Match match, @NotNull Minigamer minigamer, @NotNull Location location) {
		final CompletableFuture<Boolean> teleport = super.onRandomTeleport(match, minigamer, location);
		minigamer.getMatch().<BingoMatchData>getMatchData().setSpawnpoint(minigamer, location);
		return teleport;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onItemDrop(BlockDropItemEvent event) {
		if (!event.getPlayer().getWorld().getName().startsWith(worldName))
			return;
		World world = event.getPlayer().getWorld();

		for (Item item : event.getItems()) {
			ItemStack stack = item.getItemStack();
			ItemStack ingot = MaterialUtils.oreToIngot(world, stack.getType());
			if (ingot == null)
				continue;

			stack.setType(ingot.getType());
			item.setItemStack(stack);
			break;
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onEntityKill(EntityDeathEvent event) {
		if (!event.getEntity().getWorld().getName().equals(worldName))
			return;
		World world = event.getEntity().getWorld();

		for (ItemStack item : event.getDrops()) {
			ItemStack cooked = MaterialUtils.rawToCooked(world, item.getType());
			if (cooked == null)
				continue;
			item.setType(cooked.getType());
			return;
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		final Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying(this))
			return;

		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final BreakChallengeProgress progress = matchData.getProgress(minigamer, BreakChallengeProgress.class);

		progress.getItems().add(new ItemStack(event.getBlock().getType(), 1));
		matchData.check(minigamer);
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		final Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying(this))
			return;

		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final PlaceChallengeProgress progress = matchData.getProgress(minigamer, PlaceChallengeProgress.class);

		progress.getItems().add(new ItemStack(event.getBlock().getType(), 1));
		matchData.check(minigamer);
	}

	@EventHandler
	public void onObtain(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		final Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isPlaying(this))
			return;

		minigamer.getMatch().<BingoMatchData>getMatchData().check(minigamer);
	}

	@EventHandler
	public void onObtain(InventoryCloseEvent event) {
		final Player player = (Player) event.getPlayer();
		final Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isPlaying(this))
			return;

		minigamer.getMatch().<BingoMatchData>getMatchData().check(minigamer);
	}

	@EventHandler
	public void onCraft(FixedCraftItemEvent event) {
		final Minigamer minigamer = Minigamer.of(event.getWhoClicked());
		if (!minigamer.isPlaying(this))
			return;

		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final CraftChallengeProgress progress = matchData.getProgress(minigamer, CraftChallengeProgress.class);

		final ItemStack result = event.getResultItemStack();
		progress.getItems().add(result);
		matchData.check(minigamer);
	}

	@EventHandler
	public void onKill(LivingEntityDamageByPlayerEvent event) {
		final Minigamer minigamer = Minigamer.of(event.getAttacker());
		if (!minigamer.isPlaying(this))
			return;

		final LivingEntity entity = (LivingEntity) event.getOriginalEvent().getEntity();
		if (entity.getHealth() - event.getOriginalEvent().getFinalDamage() > 0)
			return;

		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final KillChallengeProgress progress = matchData.getProgress(minigamer, KillChallengeProgress.class);

		progress.getProgress().add(event.getEntity().getType());
		matchData.check(minigamer);
	}

	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		final Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isPlaying(this))
			return;

		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final DeathChallengeProgress progress = matchData.getProgress(minigamer, DeathChallengeProgress.class);
		final CustomChallengeProgress customChallengeProgress = matchData.getProgress(minigamer, CustomChallengeProgress.class);

		final EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();
		if (damageEvent == null)
			return;

		progress.getDamageCauses().add(damageEvent.getCause());

//		if (damageEvent.getCause() == DamageCause.ENTITY_ATTACK)
//			customChallengeProgress.complete(Challenge.DIE_BY_PUFFERFISH_POISON, CustomTask.DIE_BY_PUFFERFISH_POISON);

		matchData.check(minigamer);
	}

	@EventHandler
	public void onConsume(PlayerItemConsumeEvent event) {
		final Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying(this))
			return;

		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final ConsumeChallengeProgress progress = matchData.getProgress(minigamer, ConsumeChallengeProgress.class);

		ItemStack item = event.getItem();

		if (item.getItemMeta() instanceof PotionMeta meta)
			if (meta.getCustomEffects().isEmpty())
				return;

		final CustomMaterial customMaterial = CustomMaterial.of(item);
		if (customMaterial != null)
			item = new ItemStack(Material.valueOf(customMaterial.name().replace("FOOD_", "")), item.getAmount());

		progress.getItems().add(ItemBuilder.oneOf(item).build());

		final CustomChallengeProgress customChallengeProgress = matchData.getProgress(minigamer, CustomChallengeProgress.class);
//		if (item.getType() == Material.PUFFERFISH)
//			customChallengeProgress.complete(Challenge.DIE_BY_PUFFERFISH_POISON, CustomTask.CONSUME_A_PUFFERFISH);

		matchData.check(minigamer);
	}

	@EventHandler
	public void onTame(EntityTameEvent event) {
		if (!(event.getOwner() instanceof Player player))
			return;

		final Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isPlaying(this))
			return;

		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final TameChallengeProgress progress = matchData.getProgress(minigamer, TameChallengeProgress.class);

		progress.getProgress().add(event.getEntityType());
		matchData.check(minigamer);
	}

	@EventHandler
	public void onBreed(EntityBreedEvent event) {
		if (!(event.getBreeder() instanceof Player player))
			return;

		final Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isPlaying(this))
			return;

		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final BreedChallengeProgress progress = matchData.getProgress(minigamer, BreedChallengeProgress.class);

		progress.getProgress().add(event.getMother().getType());
		progress.getProgress().add(event.getFather().getType());
		matchData.check(minigamer);
	}

	@EventHandler
	public void onDimensionChange(PlayerChangedWorldEvent event) {
		final Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying(this))
			return;

		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final DimensionChallengeProgress progress = matchData.getProgress(minigamer, DimensionChallengeProgress.class);

		progress.getDimensions().add(event.getPlayer().getWorld().getEnvironment());
		matchData.check(minigamer);
	}

	private void biomes(Match match) {
		match.getTasks().repeat(TickTime.SECOND.x(10), TickTime.SECOND.x(5), () -> {
			for (Minigamer minigamer : match.getAliveMinigamers()) {
				final BingoMatchData matchData = match.getMatchData();
				final BiomeChallengeProgress progress = matchData.getProgress(minigamer, BiomeChallengeProgress.class);
				final Biome biome = minigamer.getOnlinePlayer().getLocation().getBlock().getBiome();
				progress.getBiomes().add(biome);
				matchData.check(minigamer);
			}
		});
	}

	private void structures(Match match) {
		match.getTasks().repeat(TickTime.SECOND.x(10), TickTime.SECOND.x(15), () -> {
			for (Minigamer minigamer : match.getAliveMinigamers()) {
				final BingoMatchData matchData = match.getMatchData();
				for (Challenge challenge : matchData.getAllChallenges(StructureChallenge.class)) {
					final StructureChallenge structureChallenge = challenge.getChallenge();
					final StructureType structureType = structureChallenge.getStructureType();
					final Location location = minigamer.getOnlinePlayer().getLocation();
					final Location found = location.getWorld().locateNearestStructure(location, structureType, 2, false);

					if (found == null)
						continue;

					found.setY(location.getY());

					if (structureType == StructureType.NETHER_FORTRESS) {
						if (distance(found, location).gt(100))
							continue;
					} else {
						if (distance(found, location).gt(32))
							continue;
					}

					final StructureChallengeProgress progress = matchData.getProgress(minigamer, StructureChallengeProgress.class);
					progress.getStructures().add(structureType);
				}
			}
		});
	}

	private void yLevel(Match match) {
		match.getTasks().repeat(TickTime.SECOND.x(10), TickTime.SECOND.x(3), () -> {
			for (Minigamer minigamer : match.getAliveMinigamers()) {
				final Player player = minigamer.getOnlinePlayer();
				final BingoMatchData matchData = match.getMatchData();
				final double y = player.getLocation().getY();
				final CustomChallengeProgress progress = matchData.getProgress(minigamer, CustomChallengeProgress.class);

				if (matchData.getAllChallenges().contains(Challenge.CLIMB_TO_BUILD_HEIGHT)) {
					if (y >= match.getWorld().getMaxHeight())
						progress.complete(Challenge.CLIMB_TO_BUILD_HEIGHT, CustomTask.CLIMB_TO_BUILD_HEIGHT);
				}

				if (matchData.getAllChallenges().contains(Challenge.DIG_TO_BEDROCK)) {
					if (y <= player.getWorld().getMinHeight() + 5)
						progress.complete(Challenge.DIG_TO_BEDROCK, CustomTask.DIG_TO_BEDROCK);
				}
			}
		});
	}

	private void misc(Match match) {
		match.getTasks().repeat(TickTime.SECOND.x(10), TickTime.SECOND.x(2), () -> {
			for (Minigamer minigamer : match.getAliveMinigamers()) {
				final Player player = minigamer.getOnlinePlayer();
				checkRidingHorse(player);
			}
		});
	}

	@EventHandler
	public void onIronGolemBuild(IronGolemBuildEvent event) {
		final Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying(this))
			return;

		Minigames.debug("[Bingo] IronGolemBuildEvent(" + minigamer.getNickname() + ")");
		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final CustomChallengeProgress progress = matchData.getProgress(minigamer, CustomChallengeProgress.class);

		progress.complete(Challenge.SPAWN_AN_IRON_GOLEM, CustomTask.SPAWN_AN_IRON_GOLEM);
	}

	@EventHandler
	public void onSnowGolemBuild(SnowGolemBuildEvent event) {
		final Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying(this))
			return;

		Minigames.debug("[Bingo] SnowGolemBuildEvent(" + minigamer.getNickname() + ")");
		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final CustomChallengeProgress progress = matchData.getProgress(minigamer, CustomChallengeProgress.class);

		progress.complete(Challenge.SPAWN_A_SNOW_GOLEM, CustomTask.SPAWN_A_SNOW_GOLEM);
	}

	@EventHandler
	public void onPlayerTrade(PlayerTradeEvent event) {
		final Minigamer minigamer = Minigamer.of(event.getPlayer());
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

		final Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isPlaying(this))
			return;

		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final CustomChallengeProgress progress = matchData.getProgress(minigamer, CustomChallengeProgress.class);

		progress.complete(Challenge.TRADE_WITH_A_PIGLIN, CustomTask.TRADE_WITH_A_PIGLIN);
	}

	@EventHandler
	public void onPotionEffect(EntityPotionEffectEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		final Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isPlaying(this))
			return;

		if (event.getNewEffect() == null)
			return;

		if (event.getNewEffect().getType() != PotionEffectType.DOLPHINS_GRACE)
			return;

		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final CustomChallengeProgress progress = matchData.getProgress(minigamer, CustomChallengeProgress.class);

		progress.complete(Challenge.OBTAIN_DOLPHINS_GRACE, CustomTask.OBTAIN_DOLPHINS_GRACE);
	}

	@EventHandler
	public void onMount(EntityMountEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		checkRidingHorse(player);
	}

	private static void checkRidingHorse(Player player) {
		final Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isPlaying(Bingo.class))
			return;

		if (!(player.getVehicle() instanceof Horse horse))
			return;

		if (isNullOrAir(horse.getInventory().getSaddle()))
			return;

		final BingoMatchData matchData = minigamer.getMatch().getMatchData();
		final CustomChallengeProgress progress = matchData.getProgress(minigamer, CustomChallengeProgress.class);

		progress.complete(Challenge.RIDE_A_HORSE, CustomTask.RIDE_A_HORSE);
	}

}

package me.pugabyte.nexus.features.minigames.mechanics;

import com.sk89q.worldedit.bukkit.paperlib.PaperLib;
import eden.utils.TimeUtils.Time;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchInitializeEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.nexus.features.minigames.models.exceptions.MinigameException;
import me.pugabyte.nexus.features.minigames.models.matchdata.BingoMatchData;
import me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TitleUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;
import static me.pugabyte.nexus.utils.WorldUtils.getRandomLocationInBorder;
import static org.bukkit.Material.*;

public final class Bingo extends TeamlessMechanic {

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

	@Getter
	@AllArgsConstructor
	public enum Challenge {
		// Breaking
		BREAK_32_COAL_ORE(new BreakChallenge(new FuzzyItemStack(COAL_ORE, 32))),
		BREAK_16_IRON_ORE(new BreakChallenge(new FuzzyItemStack(IRON_ORE, 16))),
		BREAK_5_DIAMOND_ORE(new BreakChallenge(new FuzzyItemStack(DIAMOND_ORE, 5))),
		BREAK_2_EMERALD_ORE(new BreakChallenge(new FuzzyItemStack(EMERALD_ORE, 2))),
		BREAK_192_STONE(new BreakChallenge(new FuzzyItemStack(STONE, 192))),
		BREAK_16_SUGAR_CANE(new BreakChallenge(new FuzzyItemStack(SUGAR_CANE, 16))),
		BREAK_192_NETHERRACK(new BreakChallenge(new FuzzyItemStack(NETHERRACK, 192))),
		BREAK_128_MAGMA_BLOCKS(new BreakChallenge(new FuzzyItemStack(MAGMA_BLOCK, 128))),
		BREAK_64_NETHER_BRICKS(new BreakChallenge(new FuzzyItemStack(NETHER_BRICKS, 64))),
		BREAK_64_BASALT(new BreakChallenge(new FuzzyItemStack(BASALT, 64))),
		BREAK_64_BLACKSTONE(new BreakChallenge(new FuzzyItemStack(BLACKSTONE, 64))),
		BREAK_32_PODZOL(new BreakChallenge(new FuzzyItemStack(PODZOL, 32))),
		BREAK_32_GRAVEL(new BreakChallenge(new FuzzyItemStack(GRAVEL, 32))),
		BREAK_3_OF_EACH_TULIP(new BreakChallenge(FuzzyItemStack.ofEach(MaterialTag.TULIPS, 1))),
		BREAK_1_MONSTER_SPAWNER(new BreakChallenge(new FuzzyItemStack(SPAWNER, 1))),
		BREAK_32_BAMBOO(new BreakChallenge(new FuzzyItemStack(BAMBOO, 32))),
		BREAK_1_OF_EVERY_ORE(new BreakChallenge(FuzzyItemStack.ofEach(MaterialTag.MINERAL_ORES, 1))),
		BREAK_64_OF_COMMON_BLOCKS(new BreakChallenge(FuzzyItemStack.ofEach(new MaterialTag(SAND, GRAVEL, STONE, ANDESITE, DIORITE, GRANITE, NETHERRACK), 64))),
		BREAK_1_SPONGE(new BreakChallenge(new FuzzyItemStack(Set.of(SPONGE, WET_SPONGE), 1))),

		// Crafting
		CRAFT_16_FENCE_GATES(new CraftChallenge(new FuzzyItemStack(MaterialTag.FENCE_GATES, 16))),
		CRAFT_16_FENCES(new CraftChallenge(new FuzzyItemStack(MaterialTag.FENCES, 16))),
		CRAFT_IRON_ARMOR(new CraftChallenge(FuzzyItemStack.ofEach(MaterialTag.ARMOR_IRON, 1))),
		CRAFT_32_WALLS(new CraftChallenge(new FuzzyItemStack(MaterialTag.WALLS, 32))),
		CRAFT_32_POLISHED_BLACKSTONE_BRICKS(new CraftChallenge(new FuzzyItemStack(POLISHED_BLACKSTONE_BRICKS, 32))),

		// Obtaining
		OBTAIN_4_OBSIDIAN(new ObtainChallenge(new FuzzyItemStack(OBSIDIAN, 4))),
		OBTAIN_CROPS(new ObtainChallenge(FuzzyItemStack.ofEach(new MaterialTag(BEETROOT, CARROT, WHEAT, POTATO, APPLE), 1))),
		CATCH_8_FISH(new ObtainChallenge(new FuzzyItemStack(MaterialTag.RAW_FISH, 8))),
		// TODO prevent placing them back?
		CATCH_1_FISH_WITH_A_BUCKET(new ObtainChallenge(new FuzzyItemStack(MaterialTag.FISH_BUCKETS, 1))),
		CATCH_16_FISH_WITH_A_BUCKET(new ObtainChallenge(new FuzzyItemStack(MaterialTag.FISH_BUCKETS, 16))),
		OBTAIN_1_OF_EVERY_DYE(new ObtainChallenge(FuzzyItemStack.ofEach(MaterialTag.DYES, 1))),
		OBTAIN_1_NETHERITE_INGOT(new ObtainChallenge(new FuzzyItemStack(NETHERITE_INGOT, 1))),
		OBTAIN_1_TOTEM_OF_UNDYING(new ObtainChallenge(new FuzzyItemStack(TOTEM_OF_UNDYING, 1))),

		// Killing
		KILL_6_SKELETONS(new KillChallenge(EntityType.SKELETON, 6)),
		KILL_6_ZOMBIES(new KillChallenge(EntityType.ZOMBIE, 6)),
		KILL_6_DROWNED(new KillChallenge(EntityType.DROWNED, 6)),
		KILL_2_CREEPERS(new KillChallenge(EntityType.CREEPER, 2)),
		KILL_1_ENDERMAN(new KillChallenge(EntityType.ENDERMAN, 1)),
		KILL_8_PIGLINS(new KillChallenge(EntityType.PIGLIN, 8)),
		KILL_8_BLAZES(new KillChallenge(EntityType.BLAZE, 8)),
		KILL_4_HOGLINS(new KillChallenge(EntityType.HOGLIN, 4)),
		KILL_2_PIGLIN_BRUTES(new KillChallenge(EntityType.PIGLIN_BRUTE, 2)),
		KILL_2_MAGMA_CUBES(new KillChallenge(EntityType.MAGMA_CUBE, 2)),
		KILL_1_GHAST(new KillChallenge(EntityType.GHAST, 1)),

		;

		private final IChallenge challenge;

		public static List<Challenge> shuffle() {
			ArrayList<Challenge> values = new ArrayList<>(Arrays.asList(Challenge.values()));
			Collections.shuffle(values);
			return values;
		}

	}

	public interface IChallenge {}

	public interface ItemChallenge extends IChallenge {}

	public interface EntityChallenge extends IChallenge {}

	@Data
	@Builder
	@AllArgsConstructor
	public static class BreakChallenge implements ItemChallenge {
		private Set<FuzzyItemStack> items;

		public BreakChallenge(FuzzyItemStack... items) {
			this.items = Set.of(items);
		}

	}

	@Data
	@Builder
	@AllArgsConstructor
	public static class CraftChallenge implements ItemChallenge {
		private Set<FuzzyItemStack> items;

		public CraftChallenge(FuzzyItemStack... items) {
			this.items = Set.of(items);
		}

	}

	@Data
	@Builder
	@AllArgsConstructor
	public static class ObtainChallenge implements ItemChallenge {
		private Set<FuzzyItemStack> items;

		public ObtainChallenge(FuzzyItemStack... items) {
			this.items = Set.of(items);
		}

	}

	@Data
	@Builder
	@AllArgsConstructor
	public static class KillChallenge implements EntityChallenge {
		private Set<EntityType> types;
		private int amount;

		public KillChallenge(EntityType type, int amount) {
			this.types = Set.of(type);
			this.amount = amount;
		}

	}

	@Data
	@AllArgsConstructor
	private static class FuzzyItemStack {
		private Set<Material> materials;
		private int amount;

		public FuzzyItemStack(Material material, int amount) {
			this.materials = Set.of(material);
			this.amount = amount;
		}

		public FuzzyItemStack(Tag<Material> tag, int amount) {
			this.materials = tag.getValues();
			this.amount = amount;
		}

		public static Set<FuzzyItemStack> ofEach(Tag<Material> tag, int amount) {
			return new HashSet<>() {{
				for (Material material : tag.getValues())
					add(new FuzzyItemStack(material, amount));
			}};
		}

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

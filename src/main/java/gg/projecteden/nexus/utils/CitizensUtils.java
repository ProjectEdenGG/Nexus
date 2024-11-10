package gg.projecteden.nexus.utils;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.features.mobheads.MobHeadType;
import gg.projecteden.nexus.hooks.Hook;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import lombok.Builder;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Owner;
import net.citizensnpcs.api.trait.trait.Spawned;
import net.citizensnpcs.trait.HologramTrait;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static gg.projecteden.nexus.utils.Distance.distance;
import static gg.projecteden.nexus.utils.PlayerUtils.runCommandAsConsole;
import static gg.projecteden.nexus.utils.RandomUtils.randomElement;

public class CitizensUtils {

	public static NPC getNPC(int id) {
		return Hook.CITIZENS.getNPC(id);
	}

	public static NPC getNPC(Entity entity) {
		return Hook.CITIZENS.getNPC(entity);
	}

	public static boolean isNPC(Entity entity) {
		return Hook.CITIZENS.isNPC(entity);
	}

	public static void updateNameAndSkin(int id, String name) {
		updateNameAndSkin(getNPC(id), name);
	}

	public static void updateNameAndSkin(NPC npc, String name) {
		updateName(npc, name);
		updateSkin(npc, name);
	}

	/**
	 * Sets an NPC to a player's (nick)name and skin
	 * @param npc NPC to update
	 * @param player a server member
	 */
	public static void updateNameAndSkin(NPC npc, HasUniqueId player) {
		updateName(npc, Nickname.of(player));
		updateSkin(npc, Name.of(player));
	}

	/**
	 * Sets an NPC to a player's (nick)name and skin
	 * @param npc NPC to update
	 * @param nerd a server member
	 */
	public static void updateNameAndSkin(NPC npc, Nerd nerd) {
		updateName(npc, nerd.getColoredName());
		updateSkin(npc, nerd.getName());
	}

	public static void updateName(int id, String name) {
		updateName(getNPC(id), name);
	}

	public static void updateName(NPC npc, String name) {
		if (npc == null)
			return;

		Tasks.sync(() -> {
			if (!npc.getName().equals(name))
				npc.setName(name);
		});
	}

	public static void updateSkin(int id, String name) {
		updateSkin(getNPC(id), name);
	}

	public static void updateSkin(NPC npc, String name) {
		updateSkin(npc, name, true);
	}

	public static void updateSkin(NPC npc, String name, boolean useLatest) {
		if (npc == null)
			return;

		final SkinTrait trait = npc.getOrAddTrait(SkinTrait.class);
		trait.setShouldUpdateSkins(useLatest);
		trait.setSkinName(name);
	}

	public static void updateHologram(NPC npc, int line, String text) {
		if (npc == null)
			return;

		Tasks.sync(() -> {
			final HologramTrait hologram = npc.getOrAddTrait(HologramTrait.class);
			hologram.setLine(line, text);
		});
	}

	public static NPC getSelectedNPC(Player player) {
		return Hook.CITIZENS.getSelectedNPC(player);
	}

	/* Doesnt work
	public static void setSelectedNPC(Player player, NPC npc) {
		Nexus.getCitizens().getNPCSelector().select(player, npc);
	}
	*/

	public static NPC spawnNPC(HasUniqueId owner, Location location) {
		String nickname = Nickname.of(owner);
		String name = Name.of(owner);

		NPC npc = Hook.CITIZENS.createNPC(EntityType.PLAYER, nickname);
		npc.spawn(location, SpawnReason.PLUGIN);
		Owner npcOwner = new Owner();
		npcOwner.setOwner(nickname, owner.getUniqueId());
		npc.addTrait(npcOwner);
		updateSkin(npc, name, true);
		return npc;
	}

	public static void respawnNPC(int npcId) {
		if (!shouldSpawn(npcId))
			runCommandAsConsole("npc spawn " + npcId);
	}

	private static boolean shouldSpawn(int npcId) {
		final NPC npc = getNPC(npcId);
		if (npc == null)
			return true;

		return shouldSpawn(npc);
	}

	private static boolean shouldSpawn(NPC npc) {
		final Spawned spawned = npc.getTraitNullable(Spawned.class);
		if (spawned != null && !spawned.shouldSpawn())
			return false;

		return true;
	}

	public static void despawnNPC(int npcId) {
		runCommandAsConsole("npc despawn " + npcId);
	}

	public static Location locationOf(int id) {
		return locationOf(getNPC(id));
	}

	public static Location locationOf(NPC npc) {
		if (npc == null)
			return null;

		if (npc.getEntity() != null)
			return npc.getEntity().getLocation();
		return npc.getStoredLocation();
	}

	/**
	 * Gets a list of NPCs owned by the specified player in a provided world. All parameters are optional.
	 * @deprecated replaced by {@link NPCFinder}
	 */
	@NotNull
	@Deprecated
	public static List<NPC> list(@Nullable OfflinePlayer player, @Nullable World world, @Nullable Boolean spawned) {
		return NPCFinder.builder().owner(player).world(world).spawned(spawned).build().get();
	}

	@Builder
	public static class NPCFinder {
		@Builder.Default
		private final @Nullable Boolean spawned = true;
		private final @Nullable ProtectedRegion region;
		private final @Nullable World world;
		private final @Nullable UUID owner;
		private final @Nullable Rank rankGte;
		private final @Nullable Rank rankLte;

		private final @Nullable Integer radius;
		private final @Nullable Location from;

		private boolean filter(NPC npc) {
			final UUID ownerId = npc.getTrait(Owner.class).getOwnerId();
			if (ownerId != null) {
				if (owner != null && !owner.equals(ownerId))
					return false;

				if (rankGte != null && !Rank.of(ownerId).gte(rankGte))
					return false;

				if (rankLte != null && !Rank.of(ownerId).lte(rankLte))
					return false;
			}

			if (spawned != null && (npc.getStoredLocation() == null || !npc.getTrait(Spawned.class).shouldSpawn()) && spawned)
				return false;

			if (npc.getStoredLocation() == null)
				return false;

			if (world != null && !world.equals(npc.getStoredLocation().getWorld()))
				return false;

			if (region != null && !region.contains(WorldGuardUtils.toBlockVector3(npc.getStoredLocation())))
				return false;

			if (radius != null && from != null) {
				if (!from.getWorld().equals(npc.getStoredLocation().getWorld()))
					return false;
				if (distance(from, npc.getStoredLocation()).gt(radius))
					return false;
			}

			return true;
		}

		public List<NPC> get() {
			return StreamSupport.stream(Hook.CITIZENS.getRegistry().spliterator(), false)
					.filter(this::filter).collect(Collectors.toList());
		}

		public boolean anyMatch() {
			return !get().isEmpty();
		}

		public static class NPCFinderBuilder {
			public @Contract("_ -> this") NPCFinderBuilder owner(@Nullable UUID owner) {
				this.owner = owner;
				return this;
			}

			public @Contract("_ -> this") NPCFinderBuilder owner(@Nullable HasUniqueId owner) {
				if (owner == null)
					return this;
				return owner(owner.getUniqueId());
			}

			/**
			 * Sets the region and, if unset, the world of the builder
			 */
			public @Contract("_ -> this") NPCFinderBuilder region(@Nullable ProtectedRegion region) {
				if (region != null) {
					this.region = region;
					if (world == null)
						world = WorldGuardUtils.getWorld(region);
				}
				return this;
			}

			/**
			 * Sets the region of the builder. {@link #world(World)} must be set first.
			 * @throws IllegalArgumentException method was called before the world was set or the region was not found
			 */
			public @Contract("_ -> this") NPCFinderBuilder region(@Nullable String regionName) throws IllegalArgumentException {
				if (regionName == null)
					return this;
				if (world == null)
					throw new IllegalArgumentException("Call to #region(String) must be done after #world(World)");
				region = new WorldGuardUtils(world).getProtectedRegion(regionName);
				return this;
			}
		}
	}

	public static class NPCRandomizer {
		private static final List<MobHeadType> disabledEntityTypes = List.of(MobHeadType.WITHER, MobHeadType.GHAST,
			MobHeadType.ENDER_DRAGON, MobHeadType.ELDER_GUARDIAN, MobHeadType.IRON_GOLEM, MobHeadType.RAVAGER,
			MobHeadType.HOGLIN, MobHeadType.ZOGLIN, MobHeadType.VEX);

		private static final List<MobHeadType> availableTypes = EnumUtils.valuesExcept(MobHeadType.class, disabledEntityTypes.toArray(Enum<?>[]::new));

		public static void randomize(int npcId, Player clicker) {
			final MobHeadType mob = randomElement(availableTypes);

			final NPC npc = CitizensUtils.getNPC(npcId);
			npc.setBukkitEntityType(mob.getEntityType());
			npc.getEntity().setSilent(true);

			if (mob.getEntityType() == EntityType.PLAYER)
				CitizensUtils.updateSkin(npcId, clicker.getName());
			else if (mob.hasVariants())
				mob.getVariantSetter().accept(npc.getEntity(), mob.getRandomVariant().get());
		}

		public static void randomize(int npcId) {
			final ArrayList<MobHeadType> availableTypes = new ArrayList<>(NPCRandomizer.availableTypes);
			availableTypes.remove(MobHeadType.PLAYER);
			final MobHeadType mob = randomElement(availableTypes);

			final NPC npc = CitizensUtils.getNPC(npcId);
			npc.setBukkitEntityType(mob.getEntityType());
			npc.getEntity().setSilent(true);

			if (mob.hasVariants())
				mob.getVariantSetter().accept(npc.getEntity(), mob.getRandomVariant().get());
		}
	}
}

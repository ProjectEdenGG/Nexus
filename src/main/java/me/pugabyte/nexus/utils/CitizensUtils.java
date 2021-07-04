package me.pugabyte.nexus.utils;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Builder;
import me.lexikiq.HasOfflinePlayer;
import me.lexikiq.HasUniqueId;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nickname.Nickname;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Owner;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

public class CitizensUtils {

	public static NPC getNPC(int id) {
		return CitizensAPI.getNPCRegistry().getById(id);
	}

	public static NPC getNPC(Entity entity) {
		return CitizensAPI.getNPCRegistry().getNPC(entity);
	}

	public static boolean isNPC(Entity entity) {
		return CitizensAPI.getNPCRegistry().isNPC(entity);
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
	public static void updateNameAndSkin(NPC npc, HasOfflinePlayer player) {
		updateName(npc, Nickname.of(player.getOfflinePlayer()));
		updateSkin(npc, Name.of(player.getOfflinePlayer()));
	}

	/**
	 * Sets an NPC to a player's (nick)name and skin
	 * @param npc NPC to update
	 * @param nerd a server member
	 */
	public static void updateNameAndSkin(NPC npc, Nerd nerd) {
		updateName(npc, Nickname.of(nerd));
		updateSkin(npc, nerd.getName());
	}

	public static void updateName(int id, String name) {
		updateName(getNPC(id), name);
	}

	public static void updateName(NPC npc, String name) {
		Tasks.sync(() -> {
			if (!npc.getName().equals(name))
				npc.setName(name);
		});
	}

	public static void updateSkin(int id, String name) {
		updateSkin(getNPC(id), name);
	}

	public static void updateSkin(NPC npc, String name) {
		updateSkin(npc, name, false);
	}

	public static void updateSkin(NPC npc, String name, boolean useLatest) {
		Tasks.sync(() -> {
			npc.data().setPersistent(NPC.PLAYER_SKIN_UUID_METADATA, stripColor(name));
			npc.data().setPersistent(NPC.PLAYER_SKIN_USE_LATEST, useLatest);

			Entity npcEntity = npc.getEntity();
			if (npcEntity instanceof SkinnableEntity skinnableEntity) {
				if (!skinnableEntity.getSkinTracker().getSkin().getSkinName().equals(name))
					skinnableEntity.getSkinTracker().notifySkinChange(npc.data().get(NPC.PLAYER_SKIN_USE_LATEST));
			}
		});
	}

	public static NPC getSelectedNPC(Player player) {
		return Nexus.getCitizens().getNPCSelector().getSelected(player);
	}

	/* Doesnt work
	public static void setSelectedNPC(Player player, NPC npc) {
		Nexus.getCitizens().getNPCSelector().select(player, npc);
	}
	*/

	public static NPC spawnNPC(HasOfflinePlayer owner, Location location) {
		NPC npc = Nexus.getCitizens().getNPCRegistry().createNPC(EntityType.PLAYER, Nickname.of(owner.getOfflinePlayer()));
		npc.spawn(location, SpawnReason.PLUGIN);
		Owner npcOwner = new Owner();
		npcOwner.setOwner(Nickname.of(owner.getOfflinePlayer()), owner.getOfflinePlayer().getUniqueId());
		npc.addTrait(npcOwner);
		updateSkin(npc, Name.of(owner.getOfflinePlayer()), true);
		return npc;
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

		private final @Nullable Integer radius;
		private final @Nullable Location from;

		private boolean filter(NPC npc) {
			if (owner != null && !owner.equals(npc.getTrait(Owner.class).getOwnerId()))
				return false;

			if (spawned != null && (npc.getStoredLocation() == null || !npc.isSpawned()) && spawned)
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
				if (from.distance(npc.getStoredLocation()) > radius)
					return false;
			}

			return true;
		}

		public List<NPC> get() {
			return StreamSupport.stream(Nexus.getCitizens().getNPCRegistry().spliterator(), false)
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
}

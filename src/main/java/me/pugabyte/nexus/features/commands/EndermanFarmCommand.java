package me.pugabyte.nexus.features.commands;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import me.pugabyte.nexus.features.commands.staff.MultiCommandCommand;
import me.pugabyte.nexus.features.regionapi.MovementType;
import me.pugabyte.nexus.features.regionapi.events.player.PlayerEnteringRegionEvent;
import me.pugabyte.nexus.features.warps.Warps;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.WorldEditUtils;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor
@Aliases("endfarm")
public class EndermanFarmCommand extends CustomCommand implements Listener {
	private WorldEditUtils worldEditUtils;
	private WorldGuardUtils worldGuardUtils;

	public EndermanFarmCommand(@NonNull CommandEvent event) {
		super(event);

		if (isPlayerCommandEvent()) {
			if (world().getEnvironment() != Environment.THE_END)
				error("You must be in the end to run this command");

			worldEditUtils = new WorldEditUtils(player());
			worldGuardUtils = new WorldGuardUtils(player());
		}
	}

	@Path
	void explain() {

	}

	private String getRegionId() {
		return getRegionId(uuid());
	}

	private String getRegionId(UUID uuid) {
		return "endermanfarm-" + uuid;
	}

	@NotNull
	private ProtectedRegion getRegion() {
		return getRegion(world(), uuid());
	}

	@NotNull
	private ProtectedRegion getRegion(World world, UUID uuid) {
		try {
			return new WorldGuardUtils(world).getProtectedRegion(getRegionId(uuid));
		} catch (InvalidInputException ex) {
			throw new InvalidInputException("Could not find your region");
		}
	}

	@Path("create [player]")
	@Permission("group.moderator")
	void create(OfflinePlayer player) {
		final Region selection = worldEditUtils.getPlayerSelection(player());
		if (selection == null)
			error("You have not selected the farm");

		final String regionId = getRegionId(player.getUniqueId());

		MultiCommandCommand.run(player(), List.of(
				"rg define " + regionId,
				"rg flag " + regionId + " passthrough allow"
		));
	}

	@SneakyThrows
	@Path("add <player> [owner]")
	void add(OfflinePlayer player, @Arg(value = "self", permission = "group.moderator") OfflinePlayer owner) {
		final UUID uuid = player.getUniqueId();
		final ProtectedRegion region = getRegion(world(), owner.getUniqueId());
		final DefaultDomain members = region.getMembers();
		final String ownerName = isSelf(owner) ? "your" : Nickname.of(owner) + "'s";

		if (members.contains(uuid))
			error(Nickname.of(player) + " already has access to " + ownerName + " enderman farm");

		if (members.size() >= 5)
			error("You can only allow up to 5 players to " + ownerName + " enderman farm");

		members.addPlayer(uuid);
		worldGuardUtils.getManager().save();

		send(PREFIX + "Gave " + nickname(player) + " access to " + ownerName + " enderman farm");
	}

	@SneakyThrows
	@Path("remove <player> [owner]")
	void remove(OfflinePlayer player, @Arg(value = "self", permission = "group.moderator") OfflinePlayer owner) {
		final UUID uuid = player.getUniqueId();
		final ProtectedRegion region = getRegion(world(), owner.getUniqueId());
		final DefaultDomain members = region.getMembers();
		final String ownerName = isSelf(owner) ? "your" : Nickname.of(owner) + "'s";

		if (!members.contains(uuid))
			error(Nickname.of(player) + " does not have access to " + ownerName + " enderman farm");

		members.removePlayer(uuid);
		worldGuardUtils.getManager().save();

		send(PREFIX + "Removed " + nickname(player) + "'s access to " + ownerName + " enderman farm");
	}

	@Path("list [owner]")
	void list(@Arg(value = "self", permission = "group.moderator") OfflinePlayer owner) {
		final ProtectedRegion region = getRegion(world(), owner.getUniqueId());
		final DefaultDomain members = region.getMembers();
		final Set<UUID> uuids = members.getUniqueIds();

		if (uuids.isEmpty())
			error("No one has access to your enderman farm");

		final String names = uuids.stream()
				.map(PlayerUtils::getPlayer)
				.map(OfflinePlayer::getName)
				.collect(Collectors.joining(", "));

		send(PREFIX + "Access list: &7" + names);
	}

	private boolean isEndermanFarmRegion(String region) {
		return region.matches("endermanfarm-" + StringUtils.UUID_REGEX);
	}

	private OfflinePlayer getOwner(Location location) {
		final Set<String> regions = new WorldGuardUtils(location).getRegionNamesAt(location);
		if (!regions.contains("endermanfarm-deny"))
			return null;

		for (String region : regions)
			if (isEndermanFarmRegion(region))
				return PlayerUtils.getPlayer(region.split("-", 2)[1]);

		return null;
	}

	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent event) {
		final EntityType entityType = event.getEntityType();
		final Location location = event.getLocation();
		final World world = location.getWorld();

		if (world.getEnvironment() != Environment.THE_END)
			return;

		if (entityType != EntityType.ENDERMAN)
			return;

		if (getOwner(location) != null)
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerEnteringRegion(PlayerEnteringRegionEvent event) {
		final Player player = event.getPlayer();
		final ProtectedRegion region = event.getRegion();

		if (Rank.of(player).isStaff())
			return;

		if (!isEndermanFarmRegion(region.getId()))
			return;

		final OfflinePlayer owner = getOwner(event.getNewLocation());
		if (owner == null)
			return;

		final ProtectedRegion protectedRegion;
		try {
			protectedRegion = getRegion(event.getNewLocation().getWorld(), owner.getUniqueId());
		} catch (InvalidInputException ex) {
			return;
		}

		if (player.equals(owner))
			return;
		if (protectedRegion.getMembers().contains(player.getUniqueId()))
			return;

		if (event.getMovementType() == MovementType.CONNECT)
			Warps.spawn(player);
		if (event.isCancellable()) {
			event.setCancelled(true);
			PlayerUtils.send(player, "&cYou do not have access to " + Nickname.of(owner) + "'s enderman farm");
		}
	}

}

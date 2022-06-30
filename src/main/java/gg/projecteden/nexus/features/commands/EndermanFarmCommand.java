package gg.projecteden.nexus.features.commands;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.regionapi.MovementType;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteringRegionEvent;
import gg.projecteden.nexus.features.warps.Warps;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
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

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static gg.projecteden.utils.UUIDUtils.UUID_REGEX;

@NoArgsConstructor
@Aliases("endfarm")
public class EndermanFarmCommand extends CustomCommand implements Listener {
	private WorldEditUtils worldedit;
	private WorldGuardUtils worldGuardUtils;

	public EndermanFarmCommand(@NonNull CommandEvent event) {
		super(event);

		if (isPlayerCommandEvent()) {
			worldedit = new WorldEditUtils(player());
			worldGuardUtils = new WorldGuardUtils(player());
		}
	}

	@Path
	void explain() {
		send();
		send("&cThe staff team has decided to regulate enderman farms, in order to restore some balance to the game");
		send();
		send("&3You can still build &eyour own &3enderman farm, but they &emust be enabled by staff&3");
		send();
		send("&3Players can add up to &e5 players &3to their enderman farm, and we ask that they &eonly give access " +
				"to trusted friends&3, not new players or strangers looking for easy exp");
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
	@Permission(Group.SENIOR_STAFF)
	void create(OfflinePlayer player) {
		if (world().getEnvironment() != Environment.THE_END)
			error("You must be in the end to run this command");

		final Region selection = worldedit.getPlayerSelection(player());
		if (selection == null)
			error("You have not selected the farm");

		final String regionId = getRegionId(player.getUniqueId());

		runMultiCommand(
				"rg define " + regionId,
				"rg flag " + regionId + " passthrough allow"
		);
	}

	@SneakyThrows
	@Path("add <player> [owner]")
	void add(OfflinePlayer player, @Arg(value = "self", permission = Group.MODERATOR) OfflinePlayer owner) {
		if (world().getEnvironment() != Environment.THE_END)
			error("You must be in the end to run this command");

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
	void remove(OfflinePlayer player, @Arg(value = "self", permission = Group.MODERATOR) OfflinePlayer owner) {
		if (world().getEnvironment() != Environment.THE_END)
			error("You must be in the end to run this command");

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
	void list(@Arg(value = "self", permission = Group.MODERATOR) OfflinePlayer owner) {
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
		return region.matches("endermanfarm-" + UUID_REGEX);
	}

	private OfflinePlayer getOwner(Location location) {
		final Set<String> regions = new WorldGuardUtils(location).getRegionNamesAt(location);

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

		final Set<String> regions = new WorldGuardUtils(location).getRegionNamesAt(location);
		if (!regions.contains("endermanfarm-deny"))
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
			Warps.survival(player);
		if (event.isCancellable()) {
			event.setCancelled(true);
			PlayerUtils.send(player, "&cYou do not have access to " + Nickname.of(owner) + "'s enderman farm");
		}
	}

}

package me.pugabyte.nexus.utils;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.lexikiq.HasUniqueId;
import me.pugabyte.nexus.Nexus;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

import static me.pugabyte.nexus.utils.PlayerUtils.runCommandAsConsole;

public class LuckPermsUtils {

	private static LuckPerms lp() {
		return Nexus.getLuckPerms();
	}

	@NotNull
	private static GroupManager getGroupManager() {
		return lp().getGroupManager();
	}

	@NotNull
	@SneakyThrows
	public static User getUser(OfflinePlayer player) {
		User user = lp().getUserManager().getUser(player.getUniqueId());
		if (user == null) user = lp().getUserManager().loadUser(player.getUniqueId()).get();
		return user;
	}

	@Nullable
	public static Group getGroup(String group) {
		return getGroupManager().getGroup(group);
	}

	@NotNull
	public static Collection<Group> getGroups(OfflinePlayer player) {
		User user = getUser(player);
		return user.getInheritedGroups(user.getQueryOptions());
	}

	public static boolean hasGroup(OfflinePlayer player, String group) {
		return hasGroup(player, getGroup(group));
	}

	public static boolean hasGroup(OfflinePlayer player, Group group) {
		return getGroups(player).contains(group);
	}

	public static boolean hasPermission(OfflinePlayer player, String permission) {
		return hasPermission(player, permission, (World) null);
	}

	public static boolean hasPermission(OfflinePlayer player, String permission, String world) {
		return hasPermission(player, permission, Bukkit.getWorld(world));
	}

	public static boolean hasPermission(OfflinePlayer player, String permission, World world) {
		return Nexus.getPerms().playerHas(world == null ? null : world.getName(), player, permission);
	}

	public enum PermissionChangeType {
		SET,
		UNSET
	}

	@AllArgsConstructor
	public static class PermissionChange {

		public static PermissionChangeBuilder set() {
			return new PermissionChangeBuilder(PermissionChangeType.SET);
		}

		public static PermissionChangeBuilder unset() {
			return new PermissionChangeBuilder(PermissionChangeType.UNSET);
		}

		@RequiredArgsConstructor
		public static final class PermissionChangeBuilder {
			@NonNull
			private final PermissionChangeType type;
			private UUID uuid;
			private String permission;
			private boolean value = true;
			private World world;

			public PermissionChangeBuilder player(HasUniqueId player) {
				this.uuid = player.getUniqueId();
				return this;
			}

			public PermissionChangeBuilder uuid(String uuid) {
				return uuid(UUID.fromString(uuid));
			}

			public PermissionChangeBuilder uuid(UUID uuid) {
				this.uuid = uuid;
				return this;
			}

			public PermissionChangeBuilder permission(String permission) {
				this.permission = permission;
				return this;
			}

			public PermissionChangeBuilder value(boolean value) {
				this.value = value;
				return this;
			}

			public PermissionChangeBuilder world(String world) {
				this.world = Bukkit.getWorld(world);
				return this;
			}

			public PermissionChangeBuilder world(World world) {
				this.world = world;
				return this;
			}

			public PermissionChangeBuilder world(Location location) {
				this.world = location.getWorld();
				return this;
			}

			public void run() {
				String command = "lp user " + uuid.toString() + " permission " + type + " " + permission;
				if (type == PermissionChangeType.SET)
					command += " " + value;

				if (world != null)
					command += " world=" + world.getName();

				runCommandAsConsole(command);
			}
		}
	}

	private enum GroupChangeType {
		SET,
		ADD,
		REMOVE
	}

	@AllArgsConstructor
	public static class GroupChange {

		public static GroupChangeBuilder set() {
			return new GroupChangeBuilder(GroupChangeType.SET);
		}

		public static GroupChangeBuilder add() {
			return new GroupChangeBuilder(GroupChangeType.ADD);
		}

		public static GroupChangeBuilder remove() {
			return new GroupChangeBuilder(GroupChangeType.REMOVE);
		}

		@RequiredArgsConstructor
		public static final class GroupChangeBuilder {
			@NonNull
			private final GroupChangeType type;
			private UUID uuid;
			private String group;

			public GroupChangeBuilder player(HasUniqueId player) {
				this.uuid = player.getUniqueId();
				return this;
			}

			public GroupChangeBuilder uuid(String uuid) {
				return uuid(UUID.fromString(uuid));
			}

			public GroupChangeBuilder uuid(UUID uuid) {
				this.uuid = uuid;
				return this;
			}

			public GroupChangeBuilder group(String group) {
				this.group = group;
				return this;
			}

			public void run() {
				String command = "lp user " + uuid.toString() + " group " + type + " " + group;

				runCommandAsConsole(command);
			}
		}
	}

}

package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.Utils.QueuedTask;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.lexikiq.HasOfflinePlayer;
import me.lexikiq.HasUniqueId;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.data.NodeMap;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static gg.projecteden.nexus.utils.PlayerUtils.runCommandAsConsole;
import static gg.projecteden.utils.StringUtils.isNullOrEmpty;

public class LuckPermsUtils {

	private static LuckPerms lp() {
		return Nexus.getLuckPerms();
	}

	@NotNull
	private static GroupManager groupManager() {
		return lp().getGroupManager();
	}

	@NotNull
	private static UserManager userManager() {
		return lp().getUserManager();
	}

	@NotNull
	@SneakyThrows
	public static User getUser(HasUniqueId player) {
		User user = lp().getUserManager().getUser(player.getUniqueId());
		if (user == null) user = lp().getUserManager().loadUser(player.getUniqueId()).get();
		return user;
	}

	@Nullable
	public static Group getGroup(String group) {
		return groupManager().getGroup(group);
	}

	@NotNull
	public static Collection<Group> getGroups(HasUniqueId player) {
		User user = getUser(player);
		return user.getInheritedGroups(user.getQueryOptions());
	}

	public static boolean hasGroup(HasUniqueId player, String group) {
		return hasGroup(player, getGroup(group));
	}

	public static boolean hasGroup(HasUniqueId player, Group group) {
		return getGroups(player).contains(group);
	}

	public static boolean hasPermission(HasOfflinePlayer player, String permission) {
		// TODO: briefly cache values (1-2 sec) like worldguard?
		Player _player = player.getOfflinePlayer().getPlayer();
		return hasPermission(player, permission, _player == null ? null : _player.getWorld());
	}

	public static boolean hasPermission(HasOfflinePlayer player, String permission, String world) {
		return hasPermission(player, permission, Bukkit.getWorld(world));
	}

	public static boolean hasPermission(HasOfflinePlayer player, String permission, World world) {
		return Nexus.getPerms().playerHas(world == null ? null : world.getName(), player.getOfflinePlayer(), permission);
	}

	@AllArgsConstructor
	public enum PermissionChangeType {
		SET(NodeMap::add),
		UNSET(NodeMap::remove);

		private final BiConsumer<NodeMap, Node> function;
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
				if (!isNullOrEmpty(world))
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

			@SneakyThrows
			public void run() {
				runAsync().get();
			}

			@NotNull
			public CompletableFuture<Void> runAsync() {
				return userManager().modifyUser(uuid, user -> {
					var node = Node.builder(permission).negated(!value);

					if (world != null)
						node.context(ImmutableContextSet.of("world", world.getName()));

					type.function.accept(user.data(), node.build());
				});
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
			private List<String> groups;

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

			public GroupChangeBuilder group(Rank group) {
				return group(group.name());
			}

			public GroupChangeBuilder group(String group) {
				return groups(group);
			}

			public GroupChangeBuilder groups(Rank... groups) {
				return groups(Arrays.stream(groups).map(Rank::name).toArray(String[]::new));
			}

			public GroupChangeBuilder groups(String... groups) {
				this.groups = Arrays.asList(groups);
				return this;
			}

			public void run() {
				for (String group : groups)
					runCommandAsConsole("lp user " + uuid.toString() + " parent " + type + " " + group);

				Utils.queue(5, new QueuedTask(uuid, "rank cache refresh", () -> Rank.CACHE.refresh(uuid)));
			}
		}
	}

}

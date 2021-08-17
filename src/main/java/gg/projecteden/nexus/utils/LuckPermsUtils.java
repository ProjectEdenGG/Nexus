package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.Utils.QueuedTask;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.lexikiq.HasUniqueId;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.PermissionHolder;
import net.luckperms.api.model.data.NodeMap;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.query.QueryMode;
import net.luckperms.api.query.QueryOptions;
import net.luckperms.api.util.Tristate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static gg.projecteden.nexus.utils.PlayerUtils.runCommandAsConsole;
import static gg.projecteden.utils.StringUtils.isNullOrEmpty;

// TODO: rewrite to respect the CompletableFutures returned by some methods
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
	public static User getUser(@NotNull UUID player) {
		User user = lp().getUserManager().getUser(player);
		if (user == null) user = lp().getUserManager().loadUser(player).get();
		return user;
	}

	@NotNull
	public static User getUser(@NotNull HasUniqueId player) {
		return getUser(player.getUniqueId());
	}

	@Nullable
	public static Group getGroup(@NotNull String group) {
		return groupManager().getGroup(group);
	}

	@NotNull
	public static Collection<Group> getGroups(@NotNull PermissionHolder user) {
		return user.getInheritedGroups(user.getQueryOptions());
	}

	@NotNull
	public static Collection<Group> getGroups(@NotNull UUID player) {
		return getGroups(getUser(player));
	}

	@NotNull
	public static Collection<Group> getGroups(@NotNull HasUniqueId player) {
		return getGroups(player.getUniqueId());
	}

	public static boolean hasGroup(@NotNull UUID player, @NotNull Group group) {
		return getGroups(player).contains(group);
	}

	public static boolean hasGroup(@NotNull HasUniqueId player, @NotNull Group group) {
		return hasGroup(player.getUniqueId(), group);
	}

	public static boolean hasGroup(@NotNull UUID player, @NotNull String group) {
		return hasGroup(player, Objects.requireNonNull(getGroup(group), "Could not find group"));
	}

	public static boolean hasGroup(@NotNull HasUniqueId player, @NotNull String group) {
		return hasGroup(player.getUniqueId(), group);
	}

	public static boolean hasPermission(@NotNull HasUniqueId player, @NotNull String permission) {
		return getPermission(player, permission).asBoolean();
	}

	public static boolean hasPermission(@NotNull HasUniqueId player, @NotNull String permission, @NotNull ContextSet contextOverrides) {
		return getPermission(player, permission, contextOverrides).asBoolean();
	}

	public static boolean hasPermission(@NotNull UUID player, @NotNull String permission) {
		return getPermission(player, permission).asBoolean();
	}

	public static boolean hasPermission(@NotNull UUID player, @NotNull String permission, @NotNull ContextSet contextOverrides) {
		return getPermission(player, permission, contextOverrides).asBoolean();
	}

	public static boolean hasPermission(@NotNull PermissionHolder player, @NotNull String permission) {
		return getPermission(player, permission).asBoolean();
	}

	public static boolean hasPermission(@NotNull PermissionHolder player, @NotNull String permission, @NotNull ContextSet contextOverrides) {
		return getPermission(player, permission, contextOverrides).asBoolean();
	}

	@NotNull
	public static Tristate getPermission(@NotNull PermissionHolder user, @NotNull String permission, @NotNull ContextSet contextOverrides) {
		QueryOptions options = user.getQueryOptions();
		if (!contextOverrides.isEmpty()) {
			ImmutableContextSet.Builder builder = ImmutableContextSet.builder();
			if (options.mode() == QueryMode.CONTEXTUAL)
				builder.addAll(options.context());
			builder.addAll(contextOverrides);
			options = options.toBuilder().context(builder.build()).build();
		}
		return user.getCachedData().getPermissionData(options).checkPermission(permission);
	}

	@NotNull
	public static Tristate getPermission(@NotNull PermissionHolder player, @NotNull String permission) {
		return getPermission(player, permission, ImmutableContextSet.empty());
	}

	@NotNull
	public static Tristate getPermission(@NotNull UUID player, @NotNull String permission, @NotNull ContextSet contextOverrides) {
		return getPermission(getUser(player), permission, contextOverrides);
	}

	@NotNull
	public static Tristate getPermission(@NotNull UUID player, @NotNull String permission) {
		return getPermission(player, permission, ImmutableContextSet.empty());
	}

	@NotNull
	public static Tristate getPermission(@NotNull HasUniqueId player, @NotNull String permission, @NotNull ContextSet contextOverrides) {
		return getPermission(player.getUniqueId(), permission, contextOverrides);
	}

	@NotNull
	public static Tristate getPermission(@NotNull HasUniqueId player, @NotNull String permission) {
		return getPermission(player, permission, ImmutableContextSet.empty());
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

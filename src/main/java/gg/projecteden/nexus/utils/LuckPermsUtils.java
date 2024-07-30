package gg.projecteden.nexus.utils;

import gg.projecteden.api.common.utils.CompletableFutures;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.PermissionHolder;
import net.luckperms.api.model.data.NodeMap;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.matcher.NodeMatcher;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.query.QueryMode;
import net.luckperms.api.query.QueryOptions;
import net.luckperms.api.util.Tristate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;

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
		if (user == null)
			user = lp().getUserManager().loadUser(player).get();
		return user;
	}

	@NotNull
	public static User getUser(@NotNull HasUniqueId player) {
		return getUser(player.getUniqueId());
	}

	@NotNull
	public static Group getGroup(@NotNull Rank rank) {
		return getGroup(rank.name());
	}

	@NotNull
	public static Group getGroup(@NotNull String groupName) {
		final Group group = groupManager().getGroup(groupName);
		if (group == null)
			throw new NullPointerException("Could not find group " + groupName);
		return group;
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
		return hasGroup(player, getGroup(group));
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

	@NotNull
	public static Collection<Node> getPermissions(@NotNull HasUniqueId player) {
		return getPermissions(player.getUniqueId());
	}

	@NotNull
	public static Collection<Node> getPermissions(@NotNull UUID uuid) {
		return getUser(uuid).data().toCollection();
	}

	public static CompletableFuture<List<UUID>> getUsersInGroup(Rank rank) {
		var matcher = NodeMatcher.key(InheritanceNode.builder(getGroup(rank)).build());
		var search = Nexus.getLuckPerms().getUserManager().searchAll(matcher);

		CompletableFuture<List<UUID>> future = new CompletableFuture<>();

		search.thenAccept(map -> future.complete(map.keySet().stream()
			.filter(Dev.KODA::isNot)
			.toList()));

		return future;
	}

	private static final List<ContextCalculator<?>> contextCalculators = new ArrayList<>();

	public static void registerContext(ContextCalculator<?> contextCalculator) {
		contextCalculators.add(contextCalculator);
		Nexus.getLuckPerms().getContextManager().registerCalculator(contextCalculator);
	}

	public static void shutdown() {
		for (ContextCalculator<?> contextCalculator : contextCalculators)
			Nexus.getLuckPerms().getContextManager().unregisterCalculator(contextCalculator);
	}

	@AllArgsConstructor
	public enum PermissionChangeType {
		SET(NodeMap::add),
		UNSET(NodeMap::remove);

		private final BiConsumer<NodeMap, Node> consumer;
	}

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
			private final List<String> permissions = new ArrayList<>();
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

			public PermissionChangeBuilder permissions(String... permission) {
				this.permissions.addAll(Arrays.asList(permission));
				return this;
			}

			public PermissionChangeBuilder permissions(List<String> permissions) {
				this.permissions.addAll(permissions);
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

			@NotNull
			public CompletableFuture<Void> runAsync() {
				return userManager().modifyUser(uuid, user -> {
					for (String permission : permissions) {
						var node = Node.builder(permission).negated(!value);

						if (world != null)
							node.context(ImmutableContextSet.of("world", world.getName()));

						type.consumer.accept(user.data(), node.build());
					}
				});
			}

		}
	}

	@AllArgsConstructor
	private enum GroupChangeType {
		SET(NodeMap::add),
		ADD(NodeMap::add),
		REMOVE(NodeMap::remove),
		;

		private final BiConsumer<NodeMap, Node> consumer;
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

			@SneakyThrows
			public CompletableFuture<Void> runAsync() {
				final Rank oldRank = Rank.of(uuid);
				return modifyGroups().thenRunAsync(() -> {
					Rank.CACHE.refresh(uuid);

					final Rank newRank = Rank.of(uuid);
					if (oldRank == newRank)
						return;

					new PlayerRankChangeEvent(uuid, oldRank, newRank).callEvent();
				});
			}

			private CompletableFuture<Void> modifyGroups() {
				List<CompletableFuture<Void>> futures = new ArrayList<>();
				for (String groupName : groups)
					futures.add(userManager().modifyUser(uuid, user -> {
						final Group group = getGroup(groupName);

						if (type == GroupChangeType.SET)
							user.data().clear(node -> Rank.exists(node.getKey().replace("group.", "")));

						Node node = InheritanceNode.builder(group).build();
						type.consumer.accept(user.data(), node);
					}));

				return CompletableFutures.joinAll(futures);
			}

		}

		@Getter
		public static class PlayerRankChangeEvent extends Event {
			@Getter
			private static final HandlerList handlerList = new HandlerList();
			private final UUID uuid;
			private final Rank oldRank;
			private final Rank newRank;

			public PlayerRankChangeEvent(@NotNull UUID uuid, Rank oldRank, Rank newRank) {
				super(true);
				this.uuid = uuid;
				this.oldRank = oldRank;
				this.newRank = newRank;
			}

			@Override
			public @NotNull HandlerList getHandlers() {
				return handlerList;
			}
		}
	}

}

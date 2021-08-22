package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.LuckPermsUtils;
import gg.projecteden.nexus.utils.LuckPermsUtils.PermissionChange;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.luckperms.api.context.ImmutableContextSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Permission("group.admin")
public class PermHelperCommand extends CustomCommand {
	private static final int MAX = 100;

	PermHelperCommand(CommandEvent event) {
		super(event);
	}

	@Path("(add|remove) <type> <player> <amount>")
	void modify(NumericPermission type, UUID uuid, int amount) {
		if (arg(1).equalsIgnoreCase("remove"))
			amount = -amount;

		int newLimit = add(type, uuid, amount);
		send(PREFIX + "New " + type.name().toLowerCase() + " limit for " + Nerd.of(uuid()).getNickname() + ": " + newLimit);
	}

	public static int add(NumericPermission type, UUID uuid, int amount) {
		int oldLimit = type.getCurrentLimitAndRemoveExisting(uuid);
		int newLimit = Math.min(MAX, oldLimit + amount);

		type.set(uuid, newLimit);
		return newLimit;
	}

	public static void set(NumericPermission type, UUID uuid, int amount) {
		type.getCurrentLimitAndRemoveExisting(uuid);
		type.set(uuid, amount);
	}

	public static void unset(NumericPermission type, UUID uuid, int amount) {
		type.unset(uuid, amount);
	}

	@Getter
	@AllArgsConstructor
	@RequiredArgsConstructor
	public enum NumericPermission {
		NPCS("citizens.npc.limit."),
		PLOTS("plots.plot.", "creative"),
		VAULTS("playervaults.amount."),
		;

		@NonNull
		private final String permission;
		private String world;

		private int getCurrentLimitAndRemoveExisting(UUID uuid) {
			List<Integer> ints = new ArrayList<>();

			for (int amount = 1; amount <= MAX; amount++)
				if (hasPermission(uuid, amount)) {
					unset(uuid, amount);
					ints.add(amount);
				}

			return ints.isEmpty() ? 0 : Math.min(MAX, Collections.max(ints));
		}

		public int getLimit(UUID uuid) {
			for (int i = MAX; i > 0; i--)
				if (hasPermission(uuid, i))
					return i;
			return 0;
		}

		private boolean hasPermission(UUID uuid, int amount) {
			if (StringUtils.isNullOrEmpty(world))
				return LuckPermsUtils.hasPermission(uuid, permission + amount);
			else
				return LuckPermsUtils.hasPermission(uuid, permission + amount, ImmutableContextSet.of("world", world));
		}

		public void set(UUID uuid, int amount) {
			PermissionChange.set().permission(permission + amount).world(world).uuid(uuid).runAsync();
		}

		public void unset(UUID uuid, int amount) {
			PermissionChange.unset().permission(permission + amount).uuid(uuid).world(world).runAsync();
		}
	}

}


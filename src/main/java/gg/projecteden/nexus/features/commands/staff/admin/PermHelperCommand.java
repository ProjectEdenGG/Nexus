package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.extraplots.ExtraPlotUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.LuckPermsUtils;
import gg.projecteden.nexus.utils.LuckPermsUtils.PermissionChange;
import gg.projecteden.nexus.utils.Nullables;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.luckperms.api.context.ImmutableContextSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Permission(Group.ADMIN)
public class PermHelperCommand extends CustomCommand {
	private static final int MAX = 100;

	PermHelperCommand(CommandEvent event) {
		super(event);
	}

	@Path("(add|remove) <type> <player> <amount>")
	@Description("Modify a player's permission limit")
	void modify(NumericPermission type, UUID uuid, int amount) {
		if (arg(1).equalsIgnoreCase("remove"))
			amount = -amount;

		int newLimit = add(type, uuid, amount);
		send(PREFIX + "New " + type.name().toLowerCase() + " limit for " + Nerd.of(uuid).getNickname() + ": " + newLimit);
	}

	@Path("get <type> [player]")
	@Description("View a player's permission limit")
	void get(NumericPermission type, @Arg("self") Nerd player) {
		send(PREFIX + (isSelf(player) ? "You have" : "&e" + player.getNickname() + " &3has") + " &e" + type.getLimit(player.getUuid()) + " " + type.name().toLowerCase());

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
		PLOTS("plots.plot.", "creative") {
			@Override
			public void set(UUID uuid, int amount) {
				super.set(uuid, amount);
				new ExtraPlotUserService().edit(uuid, user -> user.setTotalPlots(amount));
			}
		},
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
			if (Nullables.isNullOrEmpty(world))
				return LuckPermsUtils.hasPermission(uuid, permission + amount);
			else
				return LuckPermsUtils.hasPermission(uuid, permission + amount, ImmutableContextSet.of("world", world));
		}

		public void set(UUID uuid, int amount) {
			PermissionChange.set().permissions(permission + amount).world(world).uuid(uuid).runAsync();
		}

		public void unset(UUID uuid, int amount) {
			PermissionChange.unset().permissions(permission + amount).uuid(uuid).world(world).runAsync();
		}
	}

}


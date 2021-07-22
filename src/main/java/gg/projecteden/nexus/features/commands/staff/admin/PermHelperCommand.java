package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.LuckPermsUtils.PermissionChange;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Permission("group.admin")
public class PermHelperCommand extends CustomCommand {
	private static final int MAX = 100;

	PermHelperCommand(CommandEvent event) {
		super(event);
	}

	@Path("(add|remove) <type> <player> <amount>")
	void modify(NumericPermission type, OfflinePlayer player, int amount) {
		if (arg(1).equalsIgnoreCase("remove"))
			amount = -amount;

		String permission = type.getPermission();

		int oldLimit = type.getLimitForUpdate(player);
		int newLimit = Math.min(MAX, oldLimit + amount);

		PermissionChange.set().permission(permission + newLimit).world(type.getWorld()).player(player).run();
		send(PREFIX + "New " + type.name().toLowerCase() + " limit for " + player.getName() + ": " + newLimit);
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

		private int getLimitForUpdate(OfflinePlayer player) {
			List<Integer> ints = new ArrayList<>();

			for (int i = 1; i <= MAX; i++)
				if (Nexus.getPerms().playerHas(world, player, permission + i)) {
					Nexus.getPerms().playerRemove(world, player, permission + i);
					ints.add(i);
				}

			return ints.isEmpty() ? 0 : Math.min(MAX, Collections.max(ints));
		}

		public int getLimit(OfflinePlayer player) {
			for (int i = MAX; i > 0; i--)
				if (Nexus.getPerms().playerHas(world, player, permission + i))
					return i;
			return 0;
		}
	}

}


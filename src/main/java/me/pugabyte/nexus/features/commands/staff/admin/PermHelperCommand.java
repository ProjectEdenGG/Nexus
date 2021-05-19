package me.pugabyte.nexus.features.commands.staff.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.LuckPermsUtils.PermissionChange;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Permission("permissions.manage")
public class PermHelperCommand extends CustomCommand {
	private static final int MAX = 100;

	PermHelperCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Override
	public void help() {
		send(PREFIX + "Correct usage: /permhelper <npcs|homes|plots|vaults> <add|remove> <player> <amount>");
	}

	@Getter
	@AllArgsConstructor
	@RequiredArgsConstructor
	public enum NumericPermission {
		HOMES("homes.limit."),
		NPCS("citizens.npc.limit."),
		VAULTS("plots.plot."),
		PLOTS("playervaults.amount.", "creative"),
		;

		@NonNull
		private final String permission;
		private String world;

		private int getLimitForUpdate(OfflinePlayer player) {
			List<Integer> ints = new ArrayList<>();

			for (int i = 1; i <= MAX; i++)
				if (Nexus.getPerms().playerHas(world, player, permission + i)) {
					Nexus.getPerms().playerRemove(null, player, permission + i);
					ints.add(i);
				}

			return ints.isEmpty() ? 0 : Math.min(MAX, Collections.max(ints));
		}

		public int getLimit(OfflinePlayer player) {
			List<Integer> ints = new ArrayList<>();

			for (int i = 1; i <= MAX + 1; i++)
				if (Nexus.getPerms().playerHas(world, player, permission + i))
					ints.add(i);

			return ints.isEmpty() ? 0 : Math.min(MAX, Collections.max(ints));
		}
	}

	@Path("<type> (add|remove) <player> <amount>")
	void modify(NumericPermission type, OfflinePlayer player, int amount) {
		if (arg(2).equals("remove"))
			amount = -amount;

		String permission = type.getPermission();

		int oldLimit = type.getLimitForUpdate(player);
		int newLimit = Math.min(MAX, oldLimit + amount);

		PermissionChange.set().permission(permission + newLimit).player(player).run();
		send(PREFIX + "New " + type.name().toLowerCase() + " limit for " + player.getName() + ": " + newLimit);
	}

}


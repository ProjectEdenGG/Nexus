package me.pugabyte.bncore.features.commands.staff;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.event.Listener;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

@Aliases("wgedit")
@NoArgsConstructor
@Permission("group.staff")
public class WorldGuardEditCommand extends CustomCommand implements Listener {
	public static String permission = "worldguard.region.bypass.*";

	public WorldGuardEditCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		BNCore.registerListener(new WorldGuardEditCommand());
	}

	@Path("[enable]")
	void toggle(Boolean enable) {
		if (enable == null)
			if (player().hasPermission(permission))
				off();
			else
				on();
		else
			if (enable)
				on();
			else
				off();

	}

	private void on() {
		PermissionUser user = PermissionsEx.getUser(player());
		user.removePermission(permission);
		send("&eWorldGuard editing &aenabled");
	}

	private void off() {
		PermissionUser user = PermissionsEx.getUser(player());
		user.addPermission(permission);
		send("&eWorldGuard editing &cdisabled");
	}

}

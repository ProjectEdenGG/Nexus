package me.pugabyte.bncore.features.store.perks;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.Rank;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.Utils;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PrefixCommand extends CustomCommand {

	public PrefixCommand(CommandEvent event) {
		super(event);
	}

	SettingService service = new SettingService();
	Setting setting = service.get(player(), "checkmark");
	boolean checkBoolean = setting.getBoolean();
	String check = "&a✔ ";

	@Path("checkmark")
	@Permission("donated")
	void checkmark() {
		PermissionUser user = PermissionsEx.getUser(player());
		if (checkBoolean) {
			Utils.updatePrefix(player(), user.getPrefix().replace(check, ""));
			setting.setBoolean(false);
			send(PREFIX + "Check mark disabled");
		} else {
			Utils.updatePrefix(player(), check + user.getPrefix());
			setting.setBoolean(true);
			send(PREFIX + "Check mark enabled");
		}
		service.save(setting);
	}

	@Path("reset")
	@Permission("set.my.prefix")
	void reset() {
		PermissionUser user = PermissionsEx.getUser(player());
		PermissionGroup[] groups = user.getGroups();
		Rank rank = Rank.valueOf(groups[0].getName().toUpperCase());
		Utils.updatePrefix(player(), ((checkBoolean) ? check : "") +
				rank.getPrefix() +
				(rank.getPrefix().equalsIgnoreCase("") ? "" : " "));
		send(PREFIX + "Reset prefix");
	}

	@Path("<prefix>")
	@Permission("set.my.prefix")
	void prefix(String prefix) {
		Utils.updatePrefix(player(), ((checkBoolean) ? check : "") + "&8&l[&f" + prefix + "&8&l] ");
		send(PREFIX + "Your prefix has been set to &8&l[&f" + prefix + "&8&l]");
	}

}

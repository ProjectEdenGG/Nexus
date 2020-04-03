package me.pugabyte.bncore.features.store.perks;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;

public class PrefixCommand extends CustomCommand {

	public PrefixCommand(CommandEvent event) {
		super(event);
	}

	SettingService service = new SettingService();
	Setting checkmark = service.get(player(), "checkmark");
	Setting prefix = service.get(player(), "prefix");

	@Path("checkmark")
	@Permission("donated")
	void checkmark() {
		checkmark.setBoolean(!checkmark.getBoolean());
		send(PREFIX + "Check mark " + (checkmark.getBoolean() ? "enabled" : "disabled"));
		service.save(checkmark);
	}

	@Path("reset")
	@Permission("set.my.prefix")
	void reset() {
		service.delete(prefix);
		send(PREFIX + "Reset prefix");
	}

	@Path("<prefix>")
	@Permission("set.my.prefix")
	void prefix(String prefix) {
		this.prefix.setValue(prefix);
		service.save(prefix);
		send(PREFIX + "Your prefix has been set to &8&l[&f" + prefix + "&8&l]");
	}

}

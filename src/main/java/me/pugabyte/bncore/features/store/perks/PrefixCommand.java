package me.pugabyte.bncore.features.store.perks;

import me.pugabyte.bncore.features.chat.Emotes;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;

import static me.pugabyte.bncore.utils.StringUtils.stripColor;
import static me.pugabyte.bncore.utils.StringUtils.stripFormat;

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

	@Path("<prefix...>")
	@Permission("set.my.prefix")
	void prefix(String value) {
		if (player().hasPermission("emoticons.use"))
			value = Emotes.process(value);

		if (stripColor(value).length() > 10)
			error("Your prefix cannot be more than 10 characters");

		value = stripFormat(value);

		prefix.setValue(value);
		service.save(prefix);
		send(PREFIX + "Your prefix has been set to &8&l[&f" + value + "&8&l]");
	}

}

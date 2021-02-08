package me.pugabyte.nexus.features.store.perks;

import me.pugabyte.nexus.features.chat.Emotes;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.setting.Setting;
import me.pugabyte.nexus.models.setting.SettingService;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.StringUtils.Gradient;
import me.pugabyte.nexus.utils.StringUtils.Rainbow;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;

import java.util.regex.Matcher;

import static me.pugabyte.nexus.utils.StringUtils.decolorize;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;
import static me.pugabyte.nexus.utils.StringUtils.stripFormat;

public class PrefixCommand extends CustomCommand {
	private final SettingService service = new SettingService();
	private Setting checkmark = null;
	private Setting prefix = null;

	public PrefixCommand(CommandEvent event) {
		super(event);
		if (isPlayer()) {
			checkmark = service.get(player(), "checkmark");
			prefix = service.get(player(), "prefix");
		}
	}

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

	@Path("expire <player>")
	void expire(OfflinePlayer player) {
		console();
		prefix = service.get(player, "prefix");
		service.delete(prefix);
		send(PREFIX + "Reset prefix");
	}

	@Path("<prefix...>")
	@Permission("set.my.prefix")
	void prefix(String input) {
		if (player().hasPermission("emoticons.use"))
			input = Emotes.process(input);

		if (stripColor(input).length() > 10)
			error("Your prefix cannot be more than 10 characters");

		input = stripFormat(input);

		prefix.setValue(input);
		service.save(prefix);
		send(PREFIX + "Your prefix has been set to &8&l[&f" + input + "&8&l]");
	}

	@Path("gradient <color1> <color2> <prefix...>")
	@Permission("set.my.prefix")
	void gradient(ChatColor color1, ChatColor color2, String input) {
		prefix(Gradient.of(color1, color2).apply(input));
	}

	@Path("rainbow <prefix...>")
	@Permission("set.my.prefix")
	void rainbow(String input) {
		prefix(Rainbow.apply(input));
	}

	@Path("copy")
	void copy() {
		String prefix = this.prefix.getValue();

		if (isNullOrEmpty(prefix))
			prefix = new Nerd(player()).getRank().getPrefix();

		if (isNullOrEmpty(prefix))
			error("You do not have a prefix");

		String original = prefix;

		while (true) {
			Matcher matcher = StringUtils.getHexColorizedPattern().matcher(prefix);
			if (!matcher.find()) break;

			String group = matcher.group();
			prefix = prefix.replace(group, group.replaceAll(StringUtils.getColorChar() + "x", "&#").replaceAll(StringUtils.getColorChar(), ""));
		}

		send(json(PREFIX + "Click here to copy your current prefix: &f" + original).copy(decolorize(prefix)).hover("&7Click to copy"));
	}

}

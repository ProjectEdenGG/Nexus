package me.pugabyte.nexus.features.chat.commands;

import lombok.NonNull;
import me.pugabyte.nexus.features.chat.Emotes;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.emote.EmoteService;
import me.pugabyte.nexus.models.emote.EmoteUser;
import me.pugabyte.nexus.utils.JsonBuilder;
import net.md_5.bungee.api.ChatColor;

import java.util.Arrays;

@Aliases("emoticons")
public class EmotesCommand extends CustomCommand {
	public static final String PERMISSION = "emoticons.use";
	private final EmoteService service = new EmoteService();
	private final EmoteUser user;

	public EmotesCommand(@NonNull CommandEvent event) {
		super(event);
		user = service.get(player());
	}

	JsonBuilder format(Emotes emote, String index) {
		JsonBuilder json = json();
		if (emote.getColors().isEmpty()) {
			if (user.isEnabled(emote))
				json
						.next("&a ✔ ")
						.hover("&cClick to disable emote")
						.command("/emotes disable " + emote.name().toLowerCase());
			else
				json
						.next("&c ✖ ")
						.hover("&aClick to enable emote")
						.command("/emotes enable " + emote.name().toLowerCase());
			json.next(" &8| &3" + emote.getKey() + " &7-  " + emote.getEmote());
		} else {
			for (ChatColor color : emote.getColors()) {
				if (!json.isInitialized())
					json.initialize();
				else
					json.newline();

				if (user.isEnabled(emote, color))
					json
							.next("&a ✔ ")
							.hover("&cClick to disable emote")
							.command("/emotes disable " + emote.name().toLowerCase() + " " + color.getName().toLowerCase());
				else
					json
							.next("&c ✖ ")
							.hover("&aClick to enable emote")
							.command("/emotes enable " + emote.name().toLowerCase() + " " + color.getName().toLowerCase());
				json.next(" &8| &3" + emote.getKey() + " &7-  " + color + emote.getEmote());
			}
		}

		return json;
	}

	@Path("[page]")
	void page(@Arg("1") int page) {
		line(3);
		paginate(Arrays.asList(Emotes.values()), this::format, "/emotes", page);
	}

	@Path("toggle")
	void toggle() {
		user.setEnabled(!user.isEnabled());
		service.save(user);
		send(PREFIX + (user.isEnabled() ? "&aEnabled" : "&cDisabled"));
	}

	@TabCompleteIgnore
	@Path("enable <emote> [color]")
	void enable(Emotes emote, ChatColor color) {
		int page = (emote.ordinal() / 10) + 1;
		if (user.enable(emote, color)) {
			service.save(user);
			page(page);
		} else
			error(camelCase(emote) + " is already enabled");
	}

	@TabCompleteIgnore
	@Path("disable <emote> [color]")
	void disable(Emotes emote, ChatColor color) {
		int page = (emote.ordinal() / 10) + 1;
		if (user.disable(emote, color)) {
			service.save(user);
			page(page);
		} else
			error(camelCase(emote) + " is already disabled");
	}

}

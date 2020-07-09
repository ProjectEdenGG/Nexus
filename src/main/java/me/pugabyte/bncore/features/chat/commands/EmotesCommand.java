package me.pugabyte.bncore.features.chat.commands;

import lombok.NonNull;
import me.pugabyte.bncore.features.chat.Emotes;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleteIgnore;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.emote.EmoteService;
import me.pugabyte.bncore.models.emote.EmoteUser;
import me.pugabyte.bncore.utils.JsonBuilder;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.function.Function;

public class EmotesCommand extends CustomCommand {
	private final EmoteService service = new EmoteService();
	private EmoteUser user;

	public EmotesCommand(@NonNull CommandEvent event) {
		super(event);
		user = service.get(player());
	}

	@Path("[page]")
	void run(@Arg("1") int page) {
		Function<Emotes, JsonBuilder> formatter = emote -> {
			JsonBuilder json = json();
			if (emote.getColors().isEmpty())
//				json
//						.next("&c ✖ ")
//						.hover("&aClick to enable emote")
//						.command("/emotes enable " + emote.name().toLowerCase())
//						.next(" &8| &3" + emote.getKey() + " &7-  " + emote.getEmote());
				json.next(" &3" + emote.getKey() + " &7-  " + emote.getEmote());
			else
				for (ChatColor color : emote.getColors()) {
					if (!json.isInitialized())
						json.initialize();
					else
						json.newline();

//					json
//							.next("&c ✖ ")
//							.hover("&aClick to enable emote")
//							.command("/emotes enable " + emote.name().toLowerCase() + " " + color.name().toLowerCase())
//							.next(" &8| &3" + emote.getKey() + " &7-  " + color + emote.getEmote());
					json.next(" &3" + emote.getKey() + " &7-  " + color + emote.getEmote());
				}

			return json;
		};

		paginate(Arrays.asList(Emotes.values()), formatter, "/emotes", page);
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

	}

	@TabCompleteIgnore
	@Path("disable <emote> [color]")
	void disable(Emotes emote, ChatColor color) {

	}

}

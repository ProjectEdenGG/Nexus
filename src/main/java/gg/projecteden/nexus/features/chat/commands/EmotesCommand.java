package gg.projecteden.nexus.features.chat.commands;

import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.features.chat.Emotes;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.emote.EmoteService;
import gg.projecteden.nexus.models.emote.EmoteUser;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.LuckPermsUtils;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Aliases("emoticons")
public class EmotesCommand extends CustomCommand {
	public static final String PERMISSION = "emoticons.use";
	private final EmoteService service = new EmoteService();
	private final EmoteUser user;

	/**
	 * Returns if a user can use the specified emote
	 * @param player player to check
	 * @param emote emote to check
	 * @return if a user can use the specified emote
	 */
	public static boolean hasPermission(HasUniqueId player, Emotes emote) {
		return hasPermission(player) || LuckPermsUtils.hasPermission(player, PERMISSION + "." + emote.name().toLowerCase());
	}

	/**
	 * Returns if a user is able to use all emotes
	 * @param player player to check
	 * @return if a user is able to use all emotes
	 */
	public static boolean hasPermission(HasUniqueId player) {
		return LuckPermsUtils.hasPermission(player, PERMISSION);
	}

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
					json.group().newline();

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
	@Description("View your owned emotes")
	void page(@Arg("1") int page) {
		line(3);

		final List<Emotes> emotes;
		if (hasPermission(player()))
			emotes = Arrays.asList(Emotes.values());
		else
			emotes = Arrays.stream(Emotes.values()).filter(emote -> hasPermission(player(), emote)).collect(Collectors.toList());

		if (emotes.isEmpty())
			error("You do not have access to any emotes");

		new Paginator<Emotes>()
			.values(emotes)
			.formatter(this::format)
			.command("/emotes")
			.page(page)
			.send();
	}

	@Path("toggle")
	@Description("Toggle parsing emotes in your chat messages")
	void toggle() {
		user.setEnabled(!user.isEnabled());
		service.save(user);
		send(PREFIX + (user.isEnabled() ? "&aEnabled" : "&cDisabled"));
	}

	@HideFromWiki
	@HideFromHelp
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

	@HideFromWiki
	@HideFromHelp
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

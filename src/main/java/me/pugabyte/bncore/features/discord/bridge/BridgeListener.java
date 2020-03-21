package me.pugabyte.bncore.features.discord.bridge;

import com.google.common.base.Strings;
import com.vdurmont.emoji.EmojiParser;
import me.pugabyte.bncore.features.chatold.alerts.models.DiscordMessageEvent;
import me.pugabyte.bncore.models.discord.DiscordService;
import me.pugabyte.bncore.models.discord.DiscordUser;
import me.pugabyte.bncore.models.nerds.Nerd;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class BridgeListener extends ListenerAdapter {

	private static String getName(MessageReceivedEvent event) {
		if (event.getMember() == null || event.getMember().getNickname() == null)
			return event.getAuthor().getName();
		else
			return event.getMember().getNickname();
	}

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		Tasks.async(() -> {
			Bridge.Channel channel = Bridge.Channel.get(event.getChannel().getId());
			if (channel == null)
				return;

			if (event.getAuthor().isBot())
//				if (event.getMessage().getContentRaw().contains(">**"))
					return;

			JsonBuilder builder = new JsonBuilder(channel.getColor() + "[D] ");

			DiscordUser user = new DiscordService().getFromUserId(event.getAuthor().getId());

			if (user != null && !Strings.isNullOrEmpty(user.getRoleId()))
				builder.next(new Nerd(user.getUuid()).getChatFormat());
			else
				builder.next(getName(event));

			builder.next(" " + channel.getColor() + "&l>&f");

			String content = EmojiParser.parseToAliases(event.getMessage().getContentDisplay().trim());
			if (content.length() > 0)
				builder.next(" " + colorize(content.replaceAll("&", "&&f")));

			// TODO: censor
			// TODO: emotes

			for (Message.Attachment attachment : event.getMessage().getAttachments())
				builder.group()
						.next(" &f&l[View Attachment]")
						.url(attachment.getUrl());

			Utils.callEvent(new DiscordMessageEvent(content, channel.getPermission()));

			builder.next(" (Java)");

			for (Player player : Bukkit.getOnlinePlayers())
				if (player.hasPermission(channel.getPermission()))
					builder.send(player);
		});
	}

}
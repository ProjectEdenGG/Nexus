package gg.projecteden.nexus.features.chat.bridge;

import com.vdurmont.emoji.EmojiParser;
import gg.projecteden.discord.DiscordId.User;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.chat.ChatManager;
import gg.projecteden.nexus.features.chat.events.DiscordChatEvent;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.models.chat.ChatterService;
import gg.projecteden.nexus.models.chat.PublicChannel;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static gg.projecteden.nexus.utils.StringUtils.colorize;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@NoArgsConstructor
public class DiscordBridgeListener extends ListenerAdapter {

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		Tasks.sync(() -> {
			Optional<PublicChannel> channel = ChatManager.getChannelByDiscordId(event.getChannel().getId());
			if (!channel.isPresent()) return;

			if (event.getAuthor().isBot())
				if (!event.getAuthor().getId().equals(User.UBER.getId()))
					return;

			final Message message = event.getMessage();
			String content = stripColor(message.getContentDisplay().trim());

			try {
				content = EmojiParser.parseToAliases(content);
			} catch (Throwable ignore) {}

			DiscordChatEvent discordChatEvent = new DiscordChatEvent(event.getMember(), channel.get(), content, content, !message.getAttachments().isEmpty(), channel.get().getPermission());
			if (!discordChatEvent.callEvent()) {
				Tasks.async(() -> message.delete().queue());
				return;
			}

			content = discordChatEvent.getMessage();

			DiscordUser user = new DiscordUserService().getFromUserId(event.getAuthor().getId());

			JsonBuilder builder = new JsonBuilder();
			builder.next(" " + channel.get().getDiscordColor() + "&l>&f");

			if (content.length() > 0)
				builder.next(" " + colorize(content.replaceAll("&", "&&f")));

			for (Message.Attachment attachment : message.getAttachments())
				builder.group()
					.next(" &f&l[View Attachment]")
					.url(attachment.getUrl());

			Identity identity = user == null ? Identity.nil() : user.identity();

			Broadcast.ingame().channel(channel.get()).sender(identity).message(viewer -> {
				JsonBuilder json = new JsonBuilder(channel.get().getDiscordColor() + "[D]")
					.hover("&5&lDiscord &fChannel")
					.hover("&fMessages sent in &c#bridge &fon our")
					.hover("&c/discord &fare shown in this channel")
					.group()
					.next(" ")
					.group();

				if (user != null)
					json.next(Nerd.of(user.getUuid()).getChatFormat(viewer == null ? null : new ChatterService().get(viewer)));
				else
					json.next("&f" + Discord.getName(event.getMember(), event.getAuthor()));

				return json.next(builder);
			}).messageType(MessageType.CHAT).send();
		});
	}

}

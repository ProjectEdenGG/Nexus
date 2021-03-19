package me.pugabyte.nexus.features.chat.bridge;

import com.vdurmont.emoji.EmojiParser;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.chat.ChatManager;
import me.pugabyte.nexus.features.chat.events.DiscordChatEvent;
import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.features.discord.DiscordId.User;
import me.pugabyte.nexus.models.chat.PublicChannel;
import me.pugabyte.nexus.models.discord.DiscordService;
import me.pugabyte.nexus.models.discord.DiscordUser;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.NerdService;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.Tasks;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.Strings.isNullOrEmpty;
import static me.pugabyte.nexus.utils.StringUtils.colorize;

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

			String content = event.getMessage().getContentDisplay().trim();

			DiscordChatEvent discordChatEvent = new DiscordChatEvent(event.getMember(), channel.get(), content, content, channel.get().getPermission());
			if (!discordChatEvent.callEvent()) {
				Tasks.async(() -> event.getMessage().delete().queue());
				return;
			}

			content = discordChatEvent.getMessage();

			DiscordUser user = new DiscordService().getFromUserId(event.getAuthor().getId());
			JsonBuilder builder = new JsonBuilder(channel.get().getDiscordColor() + "[D] ");

			if (user != null && !isNullOrEmpty(user.getUuid()))
				builder.next(new NerdService().<Nerd>get(UUID.fromString(user.getUuid())).getChatFormat());
			else
				builder.next("&f" + Discord.getName(event.getMember(), event.getAuthor()));

			builder.next(" " + channel.get().getDiscordColor() + "&l>&f");

			try {
				content = EmojiParser.parseToAliases(content);
			} catch (Throwable ignore) {
			} finally {
				if (content.length() > 0)
					builder.next(" " + colorize(content.replaceAll("&", "&&f")));

				for (Message.Attachment attachment : event.getMessage().getAttachments())
					builder.group()
							.next(" &f&l[View Attachment]")
							.url(attachment.getUrl());

				channel.get().broadcastIngame(builder);
			}
		});
	}

}
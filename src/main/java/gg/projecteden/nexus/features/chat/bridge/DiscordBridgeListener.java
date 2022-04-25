package gg.projecteden.nexus.features.chat.bridge;

import com.vdurmont.emoji.EmojiParser;
import gg.projecteden.nexus.features.chat.Censor;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.chat.ChatManager;
import gg.projecteden.nexus.features.chat.events.DiscordChatEvent;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.models.alerts.AlertsService;
import gg.projecteden.nexus.models.chat.ChatterService;
import gg.projecteden.nexus.models.chat.PublicChannel;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.DiscordId.User;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gg.projecteden.nexus.utils.PlayerUtils.isSelf;
import static gg.projecteden.nexus.utils.StringUtils.colorize;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@NoArgsConstructor
public class DiscordBridgeListener extends ListenerAdapter {

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		Tasks.sync(() -> {
			Optional<PublicChannel> channel = ChatManager.getChannelByDiscordId(event.getChannel().getId());
			if (channel.isEmpty())
				return;

			if (event.getAuthor().isBot())
				if (!event.getAuthor().getId().equals(User.UBER.getId()))
					return;

			final Function<Message, DiscordChatEvent> eventBuilder = message -> {
				final String content = getContent(message);
				final boolean hasAttachments = !message.getAttachments().isEmpty();
				return new DiscordChatEvent(event.getMember(), channel.get(), content, content, hasAttachments, channel.get().getPermission());
			};

			DiscordChatEvent discordChatEvent = eventBuilder.apply(event.getMessage());
			if (!discordChatEvent.callEvent()) {
				Tasks.async(() -> event.getMessage().delete().queue());
				return;
			}

			String content = discordChatEvent.getMessage();
			DiscordUser user = new DiscordUserService().getFromUserId(event.getAuthor().getId());

			JsonBuilder builder = new JsonBuilder();

			if (content.length() > 0)
				builder.next(" " + content).group();

			for (Message.Attachment attachment : event.getMessage().getAttachments())
				builder.next(" &f&l[View Attachment]")
					.url(attachment.getUrl())
					.group();

			Identity identity = user == null ? Identity.nil() : user.identity();

			Broadcast.ingame().channel(channel.get()).sender(identity).message(viewer -> new JsonBuilder()
				.next(channel.get().getDiscordColor() + "[D]")
				.hover("&5&lDiscord &fChannel")
				.hover("&fMessages sent in &c#bridge &fon our")
				.hover("&c/discord &fare shown in this channel")
				.group()
				.next(" ")
				.group()
				.next(getChatFormat(event, user, viewer))
				.group()
				.next(" " + channel.get().getDiscordColor() + "&l>&f")
				.group()
				.next(getReplyContent(event, channel.get(), viewer))
				.group()
				.next(builder)
			).messageType(MessageType.CHAT).send();
		});
	}

	private JsonBuilder getReplyContent(MessageReceivedEvent event, PublicChannel channel, Player viewer) {
		Message message = event.getMessage().getReferencedMessage();
		if (message == null)
			return null;

		final boolean ingame = Bot.RELAY.getId().equals(message.getAuthor().getId());
		final DiscordUserService discordUserService = new DiscordUserService();
		final JsonBuilder json = new JsonBuilder();

		final String content;
		final DiscordUser replyAuthor;

		if (ingame) {
			final String regex = "<@&\\d{18}> \\*\\*>\\*\\* ";
			final Matcher matcher = Pattern.compile(regex).matcher(message.getContentRaw());
			if (matcher.find()) {
				replyAuthor = discordUserService.getFromRoleId(matcher.group().replaceAll("\\D", ""));
				content = getContent(message.getContentRaw().replaceAll(regex, ""));
				json.next(channel.getColor() + "[" + channel.getNickname() + "]");
			} else {
				replyAuthor = null;
				content = getContent(message);
			}
		} else {
			replyAuthor = discordUserService.getFromUserId(message.getAuthor().getId());
			DiscordChatEvent chatEvent = new DiscordChatEvent(replyAuthor.getMember(), channel, getContent(message));
			Censor.process(chatEvent);
			content = chatEvent.getMessage();
			json.next(channel.getDiscordColor() + "[D]");
		}

		json.next(" ")
			.next(getChatFormat(event, replyAuthor, viewer))
			.next(" ")
			.next((ingame ? channel.getColor() : channel.getDiscordColor()) + "&l>&f");

		if (content.length() > 0)
			json.next(" " + content);

		for (Message.Attachment ignore : message.getAttachments())
			json.next(" &f&l[View Attachment]");

		if (replyAuthor != null && viewer != null)
			if (replyAuthor.getUuid().equals(viewer.getUniqueId()))
				if (!isSelf(viewer, discordUserService.getFromUserId(event.getAuthor().getId())))
					new AlertsService().get(viewer).playSound();

		return new JsonBuilder(" &f&l[Reply]").hover(json);
	}

	private JsonBuilder getChatFormat(@NotNull MessageReceivedEvent event, DiscordUser user, Player viewer) {
		if (user != null)
			return Nerd.of(user.getUuid()).getChatFormat(viewer == null ? null : new ChatterService().get(viewer));
		else
			return new JsonBuilder("&f" + Discord.getName(event.getMember(), event.getAuthor()));
	}

	@Contract("null -> null; !null -> !null")
	private String getContent(Message message) {
		return message == null ? null : getContent(message.getContentDisplay());
	}

	@Contract("null -> null; !null -> !null")
	private String getContent(String message) {
		return message == null ? null : colorize(parseAliases(stripColor(message.trim())).replaceAll("&", "&&f"));
	}

	@Contract("null -> null; !null -> !null")
	private String parseAliases(String _content) {
		if (_content != null)
			try {
				return EmojiParser.parseToAliases(_content);
			} catch (Throwable ignore) {
				return _content;
			}

		return null;
	}

}


package gg.projecteden.nexus.features.chat.bridge;

import com.vdurmont.emoji.EmojiParser;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.api.discord.DiscordId.User;
import gg.projecteden.nexus.features.chat.Censor;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.chat.ChatManager;
import gg.projecteden.nexus.features.chat.events.DiscordChatEvent;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.models.alerts.AlertsService;
import gg.projecteden.nexus.models.badge.BadgeUser;
import gg.projecteden.nexus.models.badge.BadgeUser.Badge;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.PublicChannel;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.utils.AdventureUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor
public class DiscordBridgeListener extends ListenerAdapter {

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
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
			event.getMessage().delete().queue();
			return;
		}

		JsonBuilder json = new JsonBuilder();
		String content = discordChatEvent.getMessage();

		if (content.length() > 0) {
			if (event.getMessage().getReferencedMessage() != null)
				json.next(" ");

			json.next(content).group();
		}

		for (Message.Attachment attachment : event.getMessage().getAttachments()) {
			final String plainText = AdventureUtils.asPlainText(json);
			if (!plainText.isEmpty() && !plainText.endsWith(" "))
				json.next(" ");

			json.next("&f&l[View Attachment]")
				.url(attachment.getUrl())
				.group();
		}

		DiscordUser user = new DiscordUserService().getFromUserId(event.getAuthor().getId());
		Identity identity = user == null ? Identity.nil() : user.identity();

		Broadcast.ingame().channel(channel.get()).sender(identity).message(viewer -> new JsonBuilder()
			.next(getChatterFormat(event.getMember(), channel.get(), user, viewer, true))
			.group()
			.next(getReplyContent(event, channel.get(), viewer))
			.group()
			.next(json)
		).messageType(MessageType.CHAT).send();
	}

	private JsonBuilder getReplyContent(MessageReceivedEvent event, PublicChannel channel, Player viewer) {
		Message message = event.getMessage().getReferencedMessage();
		if (message == null)
			return null;

		final boolean ingame = Bot.RELAY.getId().equals(message.getAuthor().getId());
		final DiscordUserService discordUserService = new DiscordUserService();

		String content;
		final DiscordUser replyAuthor;

		if (ingame) {
			final String roleRegex = "<@&\\d{18}> \\*\\*>\\*\\*";
			final Matcher roleMatcher = Pattern.compile(roleRegex).matcher(message.getContentRaw());

			final String plainRegex = "\\*\\*\\w{3,16}\\*\\* \\*\\*>\\*\\*";
			final Matcher plainMatcher = Pattern.compile(plainRegex).matcher(message.getContentRaw());

			if (roleMatcher.find()) {
				replyAuthor = discordUserService.getFromRoleId(roleMatcher.group().replaceAll("\\D", ""));
				content = getContent(message.getContentRaw().replaceAll(roleRegex, "")).trim();
			} else if (plainMatcher.find()) {
				replyAuthor = new DiscordUserService().get(PlayerUtils.getPlayer(plainMatcher.group().replaceAll("\\W", "")));
				content = getContent(message.getContentRaw().replaceAll(plainRegex, "")).trim();
			} else {
				replyAuthor = null;
				content = getContent(message).trim();
			}

			if (!message.getEmbeds().isEmpty()) {
				final String title = message.getEmbeds().iterator().next().getTitle();
				if (!Nullables.isNullOrEmpty(title))
					content = (Nullables.isNullOrEmpty(content) ? "" : content + " ") + "&f&l[" + title + "]";
			}
		} else {
			replyAuthor = discordUserService.getFromUserId(message.getAuthor().getId());
			DiscordChatEvent chatEvent = new DiscordChatEvent(message.getMember(), channel, getContent(message));
			Censor.process(chatEvent);
			content = chatEvent.getMessage();
		}

		final JsonBuilder json = new JsonBuilder(getChatterFormat(message.getMember(), channel, replyAuthor, viewer, !ingame));

		if (content.length() > 0)
			json.next(content);

		for (Message.Attachment ignore : message.getAttachments())
			json.next(" &f&l[View Attachment]");

		if (replyAuthor != null && viewer != null)
			if (replyAuthor.getUuid().equals(viewer.getUniqueId()))
				if (!PlayerUtils.isSelf(viewer, discordUserService.getFromUserId(event.getAuthor().getId())))
					new AlertsService().get(viewer).playSound();

		return new JsonBuilder("&f&l[Reply]").hover(json);
	}

	private JsonBuilder getChatterFormat(Member author, PublicChannel channel, DiscordUser user, Player viewer, boolean isDiscord) {
		final JsonBuilder chatterName;
		if (author != null && author.getUser().isBot()) {
			final BadgeUser badgeUser = new BadgeUser(UUIDUtils.UUID0, Badge.BOT, Set.of(Badge.BOT));
			chatterName = new JsonBuilder(badgeUser.getBadgeJson(Chatter.of(viewer))).next("&5" + Discord.getName(author));
		} else
			chatterName = new JsonBuilder("&7" + Discord.getName(author));

		return channel.getChatterFormat(Chatter.of(user), chatterName, Chatter.of(viewer), isDiscord);
	}

	@Contract("null -> null; !null -> !null")
	private String getContent(Message message) {
		return message == null ? null : getContent(message.getContentDisplay());
	}

	@Contract("null -> null; !null -> !null")
	private String getContent(String message) {
		return message == null ? null : StringUtils.colorize(parseAliases(StringUtils.stripColor(message.trim())).replaceAll("&", "&&f"));
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


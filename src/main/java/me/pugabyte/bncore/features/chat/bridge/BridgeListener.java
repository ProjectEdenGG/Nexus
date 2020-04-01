package me.pugabyte.bncore.features.chat.bridge;

import com.google.common.base.Strings;
import com.vdurmont.emoji.EmojiParser;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.features.chat.ChatManager;
import me.pugabyte.bncore.features.chat.models.PublicChannel;
import me.pugabyte.bncore.features.chat.models.events.DiscordChatEvent;
import me.pugabyte.bncore.features.chat.models.events.PublicChatEvent;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.features.discord.DiscordId;
import me.pugabyte.bncore.features.discord.DiscordId.User;
import me.pugabyte.bncore.framework.exceptions.postconfigured.PlayerNotFoundException;
import me.pugabyte.bncore.models.discord.DiscordService;
import me.pugabyte.bncore.models.discord.DiscordUser;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.events.PermissionEntityEvent;
import ru.tehkode.permissions.events.PermissionEntityEvent.Action;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

@NoArgsConstructor
public class BridgeListener extends ListenerAdapter implements Listener {

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		Tasks.async(() -> {
			Optional<PublicChannel> channel = ChatManager.getChannelByDiscordId(event.getChannel().getId());
			if (!channel.isPresent()) return;

			if (event.getAuthor().isBot())
				if (!event.getAuthor().getId().equals(User.UBER.getId()))
					return;

			JsonBuilder builder = new JsonBuilder(channel.get().getColor() + "[D] ");

			DiscordUser user = new DiscordService().getFromUserId(event.getAuthor().getId());

			if (user != null && !Strings.isNullOrEmpty(user.getRoleId()))
				builder.next(new Nerd(user.getUuid()).getChatFormat());
			else
				builder.next(Discord.getName(event.getMember(), event.getAuthor()));

			builder.next(" " + channel.get().getColor() + "&l>&f");

			String content = EmojiParser.parseToAliases(event.getMessage().getContentDisplay().trim());
			if (content.length() > 0)
				builder.next(" " + colorize(content.replaceAll("&", "&&f")));

			for (Message.Attachment attachment : event.getMessage().getAttachments())
				builder.group()
						.next(" &f&l[View Attachment]")
						.url(attachment.getUrl());

			DiscordChatEvent discordChatEvent = new DiscordChatEvent(event.getMember(), channel.get(), content, channel.get().getPermission());
			Utils.callEvent(discordChatEvent);
			if (discordChatEvent.isCancelled()) {
				event.getMessage().delete().queue();
				return;
			}

			builder.next(" &o(Java)");

			for (Player player : Bukkit.getOnlinePlayers())
				if (player.hasPermission(channel.get().getPermission()))
					builder.send(player);
		});
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onChannelChat(PublicChatEvent event) {
		DiscordId.Channel discordChannel = event.getChannel().getDiscordChannel();
		if (discordChannel == null) return;

		Player player = event.getChatter().getPlayer();
		DiscordUser user = new DiscordService().get(player);
		RoleManager.update(user);

		String message = discordize(event.getMessage()) + " (Java)";
		Discord.send(user.getBridgeName() + message, discordChannel);
	}

	@NotNull
	public String discordize(String message) {
		message = message.replaceAll("\\\\", "\\\\\\\\"); // what the fuck

		if (message.contains("@")) {
			Matcher matcher = Pattern.compile("@[A-Za-z0-9_]").matcher(message);
			while (matcher.find()) {
				String group = matcher.group().replace("@", "");
				try {
					OfflinePlayer player = Utils.getPlayer(group);
					DiscordUser mentioned = new DiscordService().get(player);
					message = message.replace(group, "<@" + mentioned.getUserId() + ">");
				} catch (PlayerNotFoundException ignore) {}
			}
		}

		return message;
	}

	@EventHandler
	public void onRankChange(PermissionEntityEvent event) {
		if (event.getAction() != Action.RANK_CHANGED) return;

		if (event.getEntity() instanceof PermissionUser) {
			OfflinePlayer player = Utils.getPlayer(event.getEntity().getName());
			RoleManager.update(new DiscordService().get(player));
		}
	}

}
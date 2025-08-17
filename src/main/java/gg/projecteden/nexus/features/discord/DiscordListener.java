package gg.projecteden.nexus.features.discord;

import gg.projecteden.api.discord.DiscordId.Role;
import gg.projecteden.api.discord.DiscordId.TextChannel;
import gg.projecteden.api.discord.DiscordId.User;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.models.discord.DiscordConfigService;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@NoArgsConstructor
public class DiscordListener extends ListenerAdapter {

	@Override
	public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
		Tasks.async(() -> {
			String name = Discord.getName(event.getMember());
			if (event.getChannelJoined() != null) {
				String channel = event.getChannelJoined().getName();
				IOUtils.fileAppend("discord", name + " joined " + channel);
			}
			if (event.getChannelLeft() != null) {
				String channel = event.getChannelLeft().getName();
				IOUtils.fileAppend("discord", name + " left " + channel);
			}
		});
	}

	@Override
	public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
		Tasks.async(() -> {
			if (new DiscordConfigService().get0().isLockdown())
				event.getMember().kick().queue();
			else {
				Tasks.waitAsync(5, () -> Discord.applyRoles(event.getUser()));
			}
		});
	}

	private static final List<Role> THREAD_AUTO_JOIN_ROLES = List.of(
		Role.STAFF,
		Role.BUILDERS,
		Role.COMMUNITY_COUNCIL
	);

	@Override
	public void onChannelCreate(@NotNull ChannelCreateEvent event) {
		if (!event.getChannel().getType().isThread())
			return;

		var thread = event.getChannel().asThreadChannel();
		var parent = thread.getParentChannel();
		if (parent.getIdLong() != 1403786507130507395L)
			return;

		var roles = THREAD_AUTO_JOIN_ROLES.stream()
			.map(role -> role.get(Bot.KODA.jda()))
			.toList();

		var mentions = roles.stream()
			.map(IMentionable::getAsMention)
			.collect(Collectors.joining(" "));

		var message = new MessageCreateBuilder()
			.setContent(mentions)
			.setSuppressedNotifications(true);

		thread.sendMessage(message.build()).queue(success -> success.delete().queue());
	}

	@Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		Tasks.async(() -> {
			log(Objects.requireNonNull(event));

			String message = event.getMessage().getContentRaw();

			if (TextChannel.INTRODUCTIONS.getId().equals(event.getChannel().getId())) {
				String name = Discord.getName(event.getMember());
				event.getMessage().createThreadChannel("Hi " + name + "!").queue();
			}

			if (TextChannel.STAFF_CHANGES.getId().equals(event.getChannel().getId())) {
				if (event.getMessage().getMentions().getUsers().size() != 0) {
					String title = null;
					String names = StringUtils.asOxfordList(event.getMessage().getMentions().getUsers().stream().map(Discord::getName).toList(), ", ");

					Predicate<String> contains = text -> message.toLowerCase().contains(text);
					if (contains.test("welcome") && contains.test("back")) {
						title = "Welcome back " + names + "!";
					} else if ((contains.test("step") && contains.test("down")) || contains.test("thank")) {
						title = "Thank you " + names + "!";
					} else if (contains.test("welcome") || contains.test("congratulate")) {
						title = "Congrats " + names + "!";
					}

					if (title == null)
						Nexus.severe("Could not determine thread title for #staff-changes message");
					else
						event.getMessage().createThreadChannel(title).queue();
				}
			}
		});
	}

	private static void log(@NotNull MessageReceivedEvent event) {
		String name = Discord.getName(event.getMember());
		String channel = event.getChannel().getName();
		String message = event.getMessage().getContentRaw();

		if (Arrays.asList(TextChannel.STAFF_BRIDGE.getId(), TextChannel.BRIDGE.getId()).contains(event.getChannel().getId()))
			if (event.getMember().getUser().getId().equals(User.RELAY.getId()))
				return;

		for (Message.Attachment attachment : event.getMessage().getAttachments())
			message += " " + attachment.getUrl();

		IOUtils.fileAppend("discord", "[#" + channel + "] " + name + ": " + message.trim());
	}

}

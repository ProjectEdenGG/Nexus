package me.pugabyte.nexus.features.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.features.discord.DiscordId;
import me.pugabyte.nexus.features.discord.ReactionVoter;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Confirm;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.NerdService;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.models.nickname.Nickname.NicknameHistoryEntry;
import me.pugabyte.nexus.models.nickname.NicknameService;
import me.pugabyte.nexus.models.socialmedia.TwitterData;
import me.pugabyte.nexus.utils.PlayerUtils.Dev;
import me.pugabyte.nexus.utils.Tasks;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Aliases("nick")
@Permission("nickname.use")
public class NicknameCommand extends CustomCommand {
	private final NicknameService service = new NicknameService();

	public NicknameCommand(CommandEvent event) {
		super(event);
	}

	@Async
	@Path("<nickname> [override]")
	void run(@Arg(min = 3, max = 16, regex = "[A-Za-z0-9_]+") String nickname, String cancel) {
		Nickname data = service.get(player());

		Nerd existing = service.getFromNickname(nickname).getNerd();
		if (existing != null)
			error("&e" + nickname + " is not available, " + existing.getName() + " is already using it");

		if (hasPermission("nickname.autoApprove")) {
			data.setNickname(nickname);
			service.save(data);
			send(PREFIX + "Nickname set to &e" + data.getNickname());
		} else {
			// TODO these are all wrong
			Optional<NicknameHistoryEntry> maybePending = data.getNicknameHistory().stream().filter(NicknameHistoryEntry::isPending).findAny();
			Optional<NicknameHistoryEntry> maybeAccepted = data.getNicknameHistory().stream().filter(NicknameHistoryEntry::isAccepted).findAny();

			if ("cancel".equals(cancel)) {
				if (maybePending.isPresent()) {
					maybePending.get().selfCancel();
					maybePending = Optional.empty();
				}
			}

			// if (maybeAccepted.isPresent())

			if (maybePending.isPresent())
				if (maybePending.get().getNickname().equals(nickname))
					error("You have already sent this nickname to staff for approval, please wait");
				else
					error(json("&cYou already have a pending nickname (&e" + maybePending.get().getNickname() + "&c). " +
							"Click here to replace it with &e" + nickname).command("/nickname " + nickname + " cancel"));


			NicknameHistoryEntry entry = new NicknameHistoryEntry(data, nickname);
			send(PREFIX + "Sending nickname &e" + nickname + " &3to staff team for approval...");
			Discord.koda(entry.buildQueueMessage(), success -> {
				entry.setNicknameQueueId(success.getId());
				data.getNicknameHistory().add(entry);
				service.save(data);
				ReactionVoter.addButtons(success);
				send(PREFIX + "Successfully queued nickname for approval");
			}, this::rethrow, DiscordId.TextChannel.TEST);
		}
	}

	@Permission(value = "group.staff", absolute = true)
	@Path("set <player> <nickname>")
	void set(Nickname player, @Arg(min = 3, max = 16, regex = "[A-Za-z0-9_]+") String nickname) {
		player.setNickname(nickname);
		service.save(player);
		send(PREFIX + player.getName() + "'s nickname set to &e" + player.getNickname());
	}

	@Confirm
	@Path("reset [player]")
	void reset(@Arg(value = "self", permission = "group.staff") Nickname player) {
		if (!player.hasNickname())
			error((isSelf(player) ? "You do not" : player.getName() + " doesn't") + " have a nickname");
		player.setNickname(null);
		service.save(player);
		send(PREFIX + (isSelf(player) ? "Nickname" : player.getName() + "'s nickname") + " reset");
	}

	@Permission(value = "group.admin", absolute = true)
	@Path("clearData [player]")
	void clearData(@Arg("self") Nickname player) {
		player.getNicknameHistory().clear();
		player.setNickname(null);
		service.save(player);
	}

	@NoArgsConstructor
	public static class NicknameApprovalListener extends ListenerAdapter {

		@Override
		public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
			Tasks.async(() -> {
				Nerd nerd = Dev.PUGA.getNerd();
				Nickname nickname = new NicknameService().get(nerd);
				for (NicknameHistoryEntry pastNickname : nickname.getNicknameHistory()) { // TODO query for correct player
					if (!event.getMessageId().equals(pastNickname.getNicknameQueueId()))
						continue;

					ReactionVoter.builder()
							.channelId(DiscordId.TextChannel.STAFF_SOCIAL_MEDIA.getId())
							.messageId(pastNickname.getNicknameQueueId())
							.requiredVotes(TwitterData.getRequiredVotes())
							.onDeny(message -> {
								pastNickname.deny();
								new NerdService().save(nerd);
								message.reply("Nickname denied").queue();
							})
							.onAccept(message -> {
								pastNickname.accept();
								new NerdService().save(nerd);
								message.reply("Nickname accepted").queue();
							})
							.onError(error -> {
								pastNickname.cancel();
								new NerdService().save(nerd);
							});
				}
			});
		}

	}

}

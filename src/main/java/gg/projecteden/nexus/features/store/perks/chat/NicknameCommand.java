package gg.projecteden.nexus.features.store.perks.chat;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.discord.DiscordId.TextChannel;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.discord.ReactionVoter;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.CommandCooldownException;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.nickname.Nickname.NicknameHistoryEntry;
import gg.projecteden.nexus.models.nickname.NicknameService;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Optional;

@Aliases("nick")
@Permission(NicknameCommand.PERMISSION)
@WikiConfig(rank = "Store", feature = "Chat")
public class NicknameCommand extends CustomCommand {
	public static final String PERMISSION = "nickname.use";
	private final NicknameService service = new NicknameService();
	private Nickname data;

	public NicknameCommand(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			data = service.get(player());
	}

	@Async
	@Path("<nickname> [override]")
	@Description("Set your nickname")
	void run(@Arg(min = 2, max = 16, regex = "[\\w]+") String nickname, String cancel) {
		player();
		checkExisting(nickname);

		if (hasPermission("nickname.autoApprove")) {
			data.setNickname(nickname);
			service.save(data);
			send(PREFIX + "Nickname set to &e" + data.getNickname());
		} else {
			checkCancel(cancel);
			checkCooldown();
			checkPending(nickname);

			complete(nickname);
		}
	}

	private void checkExisting(String nickname) {
		Nickname existing = service.getFromNickname(nickname);
		if (existing != null)
			error("&e" + nickname + "&c is not available, &e" + existing.getName() + "&c is already using it");
	}

	private void checkCancel(String cancel) {
		if ("cancel".equals(cancel))
			data.getPending().ifPresent(NicknameHistoryEntry::selfCancel);
	}

	private void checkCooldown() {
		data.getNicknameHistory().stream()
				.filter(entry -> entry.isAccepted() && entry.getNicknameQueueId() != null && entry.getResponseTimestamp() != null)
				.forEach(entry -> {
					LocalDateTime expiration = entry.getResponseTimestamp().plusWeeks(2);
					if (expiration.isAfter(LocalDateTime.now()))
						throw new CommandCooldownException(expiration);
				});
	}

	private void checkPending(String nickname) {
		Optional<NicknameHistoryEntry> maybePending = data.getPending();
		maybePending.ifPresent(entry -> {
			if (entry.getNickname().equals(nickname))
				error("You have already sent this nickname to staff for approval, please wait");
			else
				error(json("&cYou already have a pending nickname (&e" + entry.getNickname() + "&c). " +
						"Click here to replace it with &e" + nickname).command("/nickname " + nickname + " cancel"));
		});
	}

	private void complete(String nickname) {
		NicknameHistoryEntry entry = new NicknameHistoryEntry(data, nickname, "pending");
		send(PREFIX + "Sending nickname &e" + nickname + " &3to staff team for approval...");
		Discord.koda(entry.buildQueueMessage(), success -> {
			entry.setNicknameQueueId(success.getId());
			data.getNicknameHistory().add(entry);
			service.save(data);
			ReactionVoter.addButtons(success);
			send(PREFIX + "Successfully queued nickname &e" + nickname + " &3for approval");
		}, this::rethrow, TextChannel.STAFF_NICKNAME_QUEUE);
	}

	@Permission(Group.SENIOR_STAFF)
	@Path("set <player> <nickname>")
	@Description("Set a player's nickname")
	void set(Nickname player, @Arg(min = 2, max = 16, regex = "[\\w]+") String nickname) {
		player.setNickname(nickname);
		service.save(player);
		send(PREFIX + player.getName() + "'s nickname set to &e" + player.getNickname());
	}

	@Confirm
	@Path("reset [player]")
	@Description("Remove a player's nickname")
	void reset(@Arg(value = "self", permission = Group.SENIOR_STAFF) Nickname player) {
		if (!player.hasNickname())
			error((isSelf(player) ? "You do not" : player.getName() + " doesn't") + " have a nickname");
		player.resetNickname();
		service.save(player);
		send(PREFIX + (isSelf(player) ? "Nickname" : player.getName() + "'s nickname") + " reset");
	}

	@Permission(Group.ADMIN)
	@Path("clearData [player]")
	@Description("Clear a player's nickname data")
	void clearData(@Arg("self") Nickname player) {
		player.getNicknameHistory().clear();
		player.setNickname((String) null);
		service.save(player);
		send(PREFIX + "Nickname data cleared for " + player.getNickname());
	}

	@NoArgsConstructor
	public static class NicknameApprovalListener extends ListenerAdapter {

		@Override
		public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
			Tasks.async(() -> {
				if (event.getChannelType() != ChannelType.TEXT)
					return;

				NicknameService service = new NicknameService();
				Nickname data = service.getFromQueueId(event.getMessageId());
				if (data == null)
					return;

				for (NicknameHistoryEntry entry : data.getNicknameHistory()) {
					if (!event.getMessageId().equals(entry.getNicknameQueueId()))
						continue;

					if (!entry.isPending() || entry.isCancelled() || entry.isAccepted())
						continue;

					ReactionVoter.builder()
							.channelId(TextChannel.STAFF_NICKNAME_QUEUE.getId())
							.messageId(entry.getNicknameQueueId())
							.requiredVotes(Nickname.getRequiredVotes())
							.onDeny(message -> {
								entry.deny();
								message.reply("Nickname denied. Add a reason by replying with `/nickname deny <reason>`").queue();
							})
							.onAccept(message -> {
								entry.accept();
								message.reply("Nickname accepted").queue();
							})
							.onError(error -> entry.cancel())
							.onFinally(() -> service.save(service.get(data.getUuid())))
							.run();
				}
			});
		}

	}

}

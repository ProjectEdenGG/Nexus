package gg.projecteden.nexus.features.discord;

import com.vdurmont.emoji.EmojiManager;
import gg.projecteden.nexus.framework.exceptions.NexusException;
import gg.projecteden.utils.DiscordId.Role;
import lombok.Builder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ReactionVoter {
	private final String channelId;
	private final String messageId;
	private final Map<Role, Integer> requiredVotes;
	private final Consumer<Message> onDeny;
	private final Consumer<Message> onAccept;
	private final Consumer<Throwable> onError;
	private final Runnable onFinally;

	@Builder(buildMethodName = "run")
	public ReactionVoter(String channelId, String messageId, Map<Role, Integer> requiredVotes, Consumer<Message> onDeny, Consumer<Message> onAccept, Consumer<Throwable> onError, Runnable onFinally) {
		this.channelId = channelId;
		this.messageId = messageId;
		this.requiredVotes = requiredVotes;
		this.onDeny = onDeny;
		this.onAccept = onAccept;
		this.onError = onError;
		this.onFinally = onFinally;
		run();
	}

	public static void addButtons(Message message) {
		message.addReaction(EmojiManager.getForAlias("white_check_mark").getUnicode()).queue(success ->
				message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue());
	}

	public void run() {
		getChannel().retrieveMessageById(messageId).queue(message -> {
			MessageReaction white_check_mark = null;
			MessageReaction x = null;

			String unicode_white_check_mark = EmojiManager.getForAlias("white_check_mark").getUnicode();
			String unicode_x = EmojiManager.getForAlias("x").getUnicode();

			for (MessageReaction reaction : message.getReactions()) {
				String name = reaction.getReactionEmote().getName();

				if (unicode_x.equals(name))
					x = reaction;
				else if (unicode_white_check_mark.equals(name))
					white_check_mark = reaction;
			}

			if (x == null) {
				message.addReaction(unicode_x).queue();
			} else if (x.getCount() > 1) {
				if (onDeny != null)
					onDeny.accept(message);
				if (onFinally != null)
					onFinally.run();
				return;
			}

			if (white_check_mark == null) {
				message.addReaction(unicode_white_check_mark).queue();
			} else {
				white_check_mark.retrieveUsers().queue(users -> {
					Map<Role, Integer> votesByRole = new HashMap<>();

					// TODO Better logic
					users.forEach(user -> {
						Member member = Discord.getGuild().getMember(user);
						if (member == null)
							throw new NexusException("Member from " + Discord.getName(user) + " not found");
						Role role = Role.of(member.getRoles().get(0));
						if (Role.OWNER.equals(role))
							role = Role.ADMINS;
						if (Role.OPERATORS.equals(role) || Role.BUILDERS.equals(role) || Role.ARCHITECTS.equals(role))
							role = Role.MODERATORS;

						votesByRole.put(role, votesByRole.getOrDefault(role, 0) + 1);
					});

					AtomicBoolean passed = new AtomicBoolean(true);
					requiredVotes.forEach((role, required) -> {
						if (!votesByRole.containsKey(role))
							passed.set(false);
						else if (votesByRole.get(role) < required)
							passed.set(false);
					});

					if (passed.get()) {
						if (onAccept != null)
							onAccept.accept(message);
						if (onFinally != null)
							onFinally.run();
					}
				});
			}
		}, error -> {
			if (onError != null)
				onError.accept(error);
			if (onFinally != null)
				onFinally.run();
		});
	}

	@NotNull
	private TextChannel getChannel() {
		TextChannel textChannel = Discord.getGuild().getTextChannelById(channelId);
		if (textChannel == null)
			throw new NexusException("Channel from id " + channelId + " not found");
		return textChannel;
	}

}

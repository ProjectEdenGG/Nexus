package me.pugabyte.nexus.features.commands;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.framework.exceptions.postconfigured.PlayerNotFoundException;
import me.pugabyte.nexus.models.chat.ChatService;
import me.pugabyte.nexus.models.chat.Chatter;
import me.pugabyte.nexus.models.deathmessages.DeathMessages;
import me.pugabyte.nexus.models.deathmessages.DeathMessages.Behavior;
import me.pugabyte.nexus.models.deathmessages.DeathMessagesService;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.AdventureUtils;
import me.pugabyte.nexus.utils.WorldGroup;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.pugabyte.nexus.features.discord.Discord.discordize;

@NoArgsConstructor
public class DeathMessagesCommand extends CustomCommand implements Listener {
	private final DeathMessagesService service = new DeathMessagesService();

	public DeathMessagesCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("behavior <behavior> [player]")
	void toggle(Behavior behavior, @Arg(value = "self", permission = "group.staff") OfflinePlayer player) {
		final DeathMessages deathMessages = service.get(player);

		deathMessages.setBehavior(behavior);
		service.save(deathMessages);
		send(PREFIX + "Set " + (isSelf(deathMessages) ? "your" : "&e" + player.getName() + "'s") + " &3death message behavior to &e" + camelCase(behavior));
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
		DeathMessagesService service = new DeathMessagesService();
		DeathMessages deathMessages = service.get(event.getEntity());

		Component deathMessageRaw = event.deathMessage();

		TextComponent output = Component.text("☠ ").color(NamedTextColor.RED);
		if (deathMessageRaw == null) {
			return;
		} else if (deathMessageRaw instanceof TextComponent) {
			// i'm still mad that i have to do this
			Component deathMessage = deathMessageRaw;
			TextReplacementConfig replacementConfig1 = TextReplacementConfig.builder()
					.matchLiteral(event.getEntity().getName())
					.replacement(
							Component.text(deathMessages.getNickname()).color(NamedTextColor.YELLOW)
					).build();
			deathMessage = deathMessage.replaceText(replacementConfig1);

			if (event.getEntity().getKiller() != null) {
				Player killer = event.getEntity().getKiller();
				TextReplacementConfig replacementConfig2 = TextReplacementConfig.builder()
						.matchLiteral(killer.getName())
						.replacement(
								Component.text(Nickname.of(killer)).color(NamedTextColor.YELLOW)
						).build();
				deathMessage = deathMessage.replaceText(replacementConfig2);
			}

			output = output.append(deathMessage);
		} else if (!(deathMessageRaw instanceof TranslatableComponent)) {
			Nexus.warn("Death message ("+deathMessageRaw.examinableName()+") is not translatable: " + AdventureUtils.asPlainText(deathMessageRaw));
			output = output.append(deathMessageRaw);
		} else {
			TranslatableComponent deathMessage = (TranslatableComponent) deathMessageRaw;
			List<Component> args = new ArrayList<>();
			deathMessage.args().forEach(component -> {
				String playerName;
				if (component.children().size() > 0 && component.children().get(0) instanceof TextComponent)
					playerName = ((TextComponent) component.children().get(0)).content();
				else if (component instanceof TextComponent && !((TextComponent) component).content().isEmpty()) {
					TextComponent textComponent = (TextComponent) component;
					playerName = textComponent.content();
					component = textComponent.content("");
				} else {
					args.add(component);
					return;
				}

				TextComponent playerComponent = Component.text(playerName).color(NamedTextColor.YELLOW);

				// and set their name to their nickname
				if (playerName.equals(deathMessages.getName()))
					playerComponent = playerComponent.content(deathMessages.getNickname());
				else {
					try {
						playerComponent = playerComponent.content(Nickname.of(playerName));
					} catch (PlayerNotFoundException|InvalidInputException ignored) {}
				}
				args.add(component.children(Collections.singletonList(playerComponent)));
			});
			output = output.append(deathMessage.args(args));
		}

		event.deathMessage(null);

		if (deathMessages.getBehavior() == Behavior.SHOWN) {
			Chat.broadcastIngame(event.getEntity(), output, MessageType.CHAT);

			if (WorldGroup.get(event.getEntity()) == WorldGroup.SURVIVAL)
				Chat.broadcastDiscord(discordize(output));
		} else if (deathMessages.getBehavior() == Behavior.LOCAL) {
			Chatter chatter = new ChatService().get(event.getEntity());
			for (Chatter recipient : StaticChannel.LOCAL.getChannel().getRecipients(chatter))
				recipient.send(event.getEntity(), output, MessageType.CHAT);
		}
	}

}

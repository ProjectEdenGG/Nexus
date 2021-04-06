package me.pugabyte.nexus.features.commands;

import lombok.NoArgsConstructor;
import lombok.NonNull;
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
import me.pugabyte.nexus.utils.WorldGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
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

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		final DeathMessagesService service = new DeathMessagesService();
		DeathMessages deathMessages = service.get(event.getEntity());

		TranslatableComponent deathMessage = (TranslatableComponent) event.deathMessage();
		TextComponent output;
		if (deathMessage == null) {
			// failsafe? :P
			output = Component.text("☠ ").color(NamedTextColor.RED)
					.append(Component.text(deathMessages.getNickname()).color(NamedTextColor.YELLOW))
					.append(Component.text(" spontaneously ceased existence").color(NamedTextColor.RED));
		} else {
			List<Component> args = new ArrayList<>();
			deathMessage.args().forEach(component -> {
				if (!(component instanceof TextComponent) || component.children().size() != 1 || !(component.children().get(0) instanceof TextComponent)) {
					args.add(component);
					return;
				}
				// this (should) have a text component inside with the name of a player so we are going to color it
				TextComponent textComponent = ((TextComponent) component.children().get(0)).color(NamedTextColor.YELLOW);
				// and set their name to their nickname
				if (textComponent.content().equals(deathMessages.getName()))
					textComponent = textComponent.content(deathMessages.getNickname());
				else {
					try {
						textComponent = textComponent.content(Nerd.of(textComponent.content()).getNickname());
					}
					catch (PlayerNotFoundException|InvalidInputException ignored) {}
				}
				args.add(component.children(Collections.singletonList(textComponent)));
			});
			deathMessage = deathMessage.args(args);
			output = Component.text("☠ ").color(NamedTextColor.RED).append(deathMessage);
		}

		event.deathMessage(null);

		if (deathMessages.getBehavior() == Behavior.SHOWN) {
			Chat.broadcastIngame(output);

			if (WorldGroup.get(event.getEntity()) == WorldGroup.SURVIVAL)
				Chat.broadcastDiscord(discordize(output));
		} else if (deathMessages.getBehavior() == Behavior.LOCAL) {
			Chatter chatter = new ChatService().get(event.getEntity());
			for (Chatter recipient : StaticChannel.LOCAL.getChannel().getRecipients(chatter))
				recipient.send(output);
		}
	}

}

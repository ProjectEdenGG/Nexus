package me.pugabyte.nexus.features.commands;

import com.gmail.nossr50.mcMMO;
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
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.AdventureUtils;
import me.pugabyte.nexus.utils.WorldGroup;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@NoArgsConstructor
public class DeathMessagesCommand extends CustomCommand implements Listener {
	private static final Pattern HEART_PATTERN = Pattern.compile("^[\u2764\u25A0]+$");
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
		String deathString = event.getDeathMessage();
		DeathMessagesService service = new DeathMessagesService();
		DeathMessages deathMessages = service.get(event.getEntity());

		Component deathMessageRaw = event.deathMessage();

		TextComponent output = Component.text("☠ ", NamedTextColor.RED);
		if (deathMessageRaw == null) {
			return;
		} else if (deathMessageRaw instanceof TextComponent) {
			// i'm still mad that i have to do this
			Component deathMessage = deathMessageRaw;
			TextReplacementConfig replacementConfig1 = TextReplacementConfig.builder()
					.matchLiteral(event.getEntity().getName())
					.replacement(
							Component.text(deathMessages.getNickname(), NamedTextColor.YELLOW)
					).build();
			deathMessage = deathMessage.replaceText(replacementConfig1);

			if (event.getEntity().getKiller() != null) {
				Player killer = event.getEntity().getKiller();
				TextReplacementConfig replacementConfig2 = TextReplacementConfig.builder()
						.matchLiteral(killer.getName())
						.replacement(
								Component.text(Nickname.of(killer), NamedTextColor.YELLOW)
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
				// get the name of the player (or entity)
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

				Component finalComponent;
				HoverEvent<?> hoverEvent = component.hoverEvent();
				boolean hasEntityHover = hoverEvent != null && hoverEvent.value() instanceof HoverEvent.ShowEntity;

				if (HEART_PATTERN.matcher(playerName).matches() && hasEntityHover) {
					// fix mcMMO hearts
					Component failsafeComponent = Component.text("A Very Scary Mob");
					finalComponent = failsafeComponent; // failsafe

					HoverEvent.ShowEntity hover = (HoverEvent.ShowEntity) hoverEvent.value();
					Entity entity = Bukkit.getEntity(hover.id());

					if (entity != null) {
						// get mcMMO's saved entity name
						if (entity.hasMetadata(mcMMO.customNameKey)) {
							String name = entity.getMetadata(mcMMO.customNameKey).get(0).asString();
							if (!name.isEmpty()) {
								finalComponent = Component.text(name);
								finalComponent = finalComponent.hoverEvent(HoverEvent.showEntity(hover.type(), hover.id(), finalComponent));
							}
						}

						// if that failed or was empty, get a translatable text component instead
						if (finalComponent == failsafeComponent) {
							String key = Bukkit.getUnsafe().getTranslationKey(entity.getType());
							if (key != null)
								finalComponent = Component.translatable(key);
						}
					}
				} else if (hasEntityHover && !((HoverEvent.ShowEntity) hoverEvent.value()).type().value().equalsIgnoreCase("player")) {
					// ignore non-mcMMO, non-player entities
					finalComponent = component;
				} else {
					// finally display player (nick)names + colors
					TextComponent playerComponent = Component.text(playerName, NamedTextColor.YELLOW);

					if (playerName.equals(deathMessages.getName()))
						playerComponent = playerComponent.content(deathMessages.getNickname());
					else {
						try {
							playerComponent = playerComponent.content(Nickname.of(playerName));
						} catch (PlayerNotFoundException|InvalidInputException ignored) {}
					}
					finalComponent = playerComponent;
				}

				args.add(component.children(Collections.singletonList(finalComponent)));
			});
			output = output.append(deathMessage.args(args));
		}

		event.deathMessage(null);

		if (deathMessages.getBehavior() == Behavior.SHOWN) {
			Chat.broadcastIngame(event.getEntity(), output, MessageType.CHAT);

			if (WorldGroup.get(event.getEntity()) == WorldGroup.SURVIVAL)
				Chat.broadcastDiscord("☠ " + deathString); // dumb fix :(
		} else if (deathMessages.getBehavior() == Behavior.LOCAL) {
			Chatter chatter = new ChatService().get(event.getEntity());
			for (Chatter recipient : StaticChannel.LOCAL.getChannel().getRecipients(chatter))
				recipient.send(event.getEntity(), output, MessageType.CHAT);
		}
	}

}

package me.pugabyte.nexus.features.commands;

import com.gmail.nossr50.mcMMO;
import eden.utils.TimeUtils.Time;
import eden.utils.TimeUtils.Timespan;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import me.pugabyte.nexus.features.discord.Discord;
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
import me.pugabyte.nexus.models.mutemenu.MuteMenuUser;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.AdventureUtils;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.WorldGroup;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEvent.ShowEntity;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

	static {
		Tasks.repeatAsync(Time.SECOND.x(10), Time.MINUTE, () -> {
			DeathMessagesService service = new DeathMessagesService();
			for (DeathMessages deathMessages : service.getExpired()) {
				deathMessages.setBehavior(Behavior.SHOWN);
				deathMessages.setExpiration(null);
				service.save(deathMessages);
			}
		});
	}

	@Path("behavior <behavior> [player] [duration...]")
	void toggle(Behavior behavior, @Arg(value = "self", permission = "group.staff") OfflinePlayer player, @Arg(permission = "group.staff") Timespan duration) {
		final DeathMessages deathMessages = service.get(player);

		deathMessages.setBehavior(behavior);
		if (!duration.isNull())
			deathMessages.setExpiration(duration.fromNow());

		service.save(deathMessages);
		send(PREFIX + "Set " + (isSelf(deathMessages) ? "your" : "&e" + player.getName() + "'s") + " &3death message " +
				"behavior to &e" + camelCase(behavior) + (duration.isNull() ? "" : " &3for &e" + duration.format()));
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
		String deathString = event.getDeathMessage();
		Player player = event.getEntity();
		DeathMessages deathMessages = service.get(player);

		Component deathMessageRaw = event.deathMessage();
		if (deathMessageRaw == null)
			return;

		JsonBuilder output = new JsonBuilder("☠ ", NamedTextColor.RED);

		if (deathMessageRaw instanceof TranslatableComponent deathMessage) {
			List<Component> args = new ArrayList<>();
			deathMessage.args().forEach(arg -> args.add(handleArgument(deathMessages, arg)));
			output.next(deathMessage.args(args));
		} else {
			Nexus.warn("Death message ("+deathMessageRaw.examinableName()+") is not translatable: " + AdventureUtils.asPlainText(deathMessageRaw));
			output.next(deathMessageRaw);
		}

		event.deathMessage(null);

		if (deathMessages.getBehavior() == Behavior.SHOWN) {
			Chat.broadcastIngame(player, output, MessageType.CHAT, MuteMenuItem.DEATH_MESSAGES);

			if (WorldGroup.of(player) == WorldGroup.SURVIVAL)
				discord(deathString, player);
		} else if (deathMessages.getBehavior() == Behavior.LOCAL) {
			local(player, output);
		}
	}

	private Component handleArgument(DeathMessages player, Component component) {
		Component originalComponent = component;

		ShowEntity hover = getEntityHover(component);

		String playerName;
		// for some reason, these children differ depending on system. lexi's local test server triggers the
		// first if block, the BN server triggered the 2nd, unsure of which eden fires
		if (!component.children().isEmpty() && component.children().get(0) instanceof TextComponent textComponent)
			playerName = textComponent.content();
		else if (component instanceof TextComponent textComponent && !textComponent.content().isEmpty()) {
			playerName = textComponent.content();
			component = textComponent.content(""); // preserves hover text/click events but clears the content as we manually add it back it
		} else {
			return component;
		}

		Component finalComponent = cleanup(player, originalComponent, hover, playerName);

		return component.children(Collections.singletonList(finalComponent));
	}

	private void discord(String deathString, Player player) {
		// workaround for dumb Adventure bug (ProjectEdenGG/Issues#657)
		if (deathString == null)
			deathString = "☠ " + Nickname.of(player) + " died";
		else {
			deathString = ("☠ " + HEART_PATTERN.matcher(deathString).replaceAll("a mob"))
					.replace(" " + player.getName() + " ", " " + Nickname.of(player) + " ");

			if (player.getKiller() != null)
				deathString = deathString.replace(player.getKiller().getName(), Nickname.of(player.getKiller()));
		}
		Chat.broadcastDiscord(Discord.discordize(deathString));
	}

	private void local(Player player, JsonBuilder output) {
		Chatter chatter = new ChatService().get(player);
		for (Chatter recipient : StaticChannel.LOCAL.getChannel().getRecipients(chatter))
			if (!MuteMenuUser.hasMuted(recipient.getOnlinePlayer(), MuteMenuItem.DEATH_MESSAGES))
				recipient.sendMessage(player, output, MessageType.CHAT);
	}

	private Component cleanup(DeathMessages deathMessages, Component originalComponent, ShowEntity hover, String playerName) {
		Component finalComponent;
		if (HEART_PATTERN.matcher(playerName).matches() && hover != null)
			finalComponent = fixMcMMOHearts(hover);
		else if (hover == null || !hover.type().value().equalsIgnoreCase("player"))
			// ignore non-mcMMO, non-player entities
			finalComponent = originalComponent;
		else
			finalComponent = nickname(deathMessages, playerName);
		return finalComponent;
	}

	@Nullable
	private HoverEvent.ShowEntity getEntityHover(Component component) {
		HoverEvent<?> _hoverEvent = component.hoverEvent();
		ShowEntity hover;
		if (_hoverEvent != null && _hoverEvent.value() instanceof ShowEntity _hover)
			hover = _hover;
		else
			hover = null;
		return hover;
	}

	@NotNull
	private Component fixMcMMOHearts(HoverEvent.ShowEntity hover) {
		Component failsafeComponent = Component.text("A Very Scary Mob");
		Component finalComponent = failsafeComponent;

		Entity entity = Bukkit.getEntity(hover.id());

		if (entity != null) {
			// get mcMMO's saved entity name
			if (entity.hasMetadata(mcMMO.customNameKey)) {
				String name = entity.getMetadata(mcMMO.customNameKey).get(0).asString();
				if (!name.isEmpty())
					finalComponent = new JsonBuilder().content(name).hover(HoverEvent.showEntity(hover.type(), hover.id(), finalComponent)).build();
			}

			// if that failed or was empty, get a translatable text component instead
			if (finalComponent == failsafeComponent) {
				String key = Bukkit.getUnsafe().getTranslationKey(entity.getType());
				if (key != null)
					finalComponent = Component.translatable(key);
			}
		}
		return finalComponent;
	}

	// display player (nick)names + colors
	@NotNull
	private Component nickname(DeathMessages deathMessages, String playerName) {
		JsonBuilder playerComponent = new JsonBuilder(playerName, NamedTextColor.YELLOW);

		if (playerName.equals(deathMessages.getName()))
			playerComponent.content(deathMessages.getNickname());
		else {
			try {
				playerComponent.content(Nickname.of(playerName));
			} catch (PlayerNotFoundException|InvalidInputException ignored) {}
		}
		return playerComponent.build();
	}

}

package gg.projecteden.nexus.features.commands;

import com.gmail.nossr50.util.MetadataConstants;
import com.google.gson.Gson;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.API;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.chat.Chat.StaticChannel;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotFoundException;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.ChatterService;
import gg.projecteden.nexus.models.deathmessages.DeathMessages;
import gg.projecteden.nexus.models.deathmessages.DeathMessages.Behavior;
import gg.projecteden.nexus.models.deathmessages.DeathMessagesService;
import gg.projecteden.nexus.models.mutemenu.MuteMenuUser;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.AdventureUtils;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEvent.ShowEntity;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.io.FileUtils;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

@NoArgsConstructor
public class DeathMessagesCommand extends CustomCommand implements Listener {
	private static final Pattern HEART_PATTERN = Pattern.compile("[\u2764\u25A0]+");
	private final DeathMessagesService service = new DeathMessagesService();

	public DeathMessagesCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		Tasks.repeatAsync(TickTime.SECOND.x(10), TickTime.MINUTE, () -> {
			DeathMessagesService service = new DeathMessagesService();
			for (DeathMessages deathMessages : service.getExpired()) {
				deathMessages.setBehavior(Behavior.SHOWN);
				deathMessages.setExpiration(null);
				service.save(deathMessages);
			}
		});
	}

	@Data
	@Builder
	private static class DeathMessagesConfig {
		private final Map<String, CustomDeathMessage> messages;

		public static CustomDeathMessage of(String key) {
			return config.getMessages().get(key.toLowerCase());
		}
	}

	@Data
	@Builder
	private static class CustomDeathMessage {
		private transient String key;
		private String original;
		private List<String> custom;
		private List<String> suggestions;

		public String random() {
			return RandomUtils.randomElement(custom);
		}

		public int count() {
			return custom.size();
		}

		public boolean hasCustom() {
			return count() > 0;
		}
	}

	private static DeathMessagesConfig config;

	@SneakyThrows
	public static void reloadConfig() {
		config = new Gson().fromJson(FileUtils.readFileToString(getFile()), DeathMessagesConfig.class);
		config.getMessages().forEach((key, custom) -> {
			custom.setKey(key);
			if (custom.getCustom() == null)
				custom.setCustom(new ArrayList<>());
			if (custom.getSuggestions() == null)
				custom.setSuggestions(new ArrayList<>());
		});
	}

	static {
		reloadConfig();
	}

	public static File getFile() {
		return IOUtils.getPluginFile(getFileName());
	}

	@NotNull
	private static String getFileName() {
		return "death-messages.json";
	}

	@SneakyThrows
	private void save() {
		FileUtils.write(getFile(), API.get().getPrettyPrinter().create().toJson(config));
	}

	@Path("reload")
	@Permission(Group.ADMIN)
	@Description("Reload configuration from disk")
	void reload() {
		reloadConfig();
		int total = config.getMessages().values().stream().map(CustomDeathMessage::count).reduce(0, Integer::sum);
		send(PREFIX + "Loaded " + config.getMessages().size() + " keys with " + total + " custom messages");
	}

	@Path("list [page]")
	@Description("View custom death messages")
	void list(@Arg("1") int page) {
		final List<CustomDeathMessage> messages = config.getMessages().values().stream().filter(CustomDeathMessage::hasCustom).toList();
		if (messages.isEmpty())
			error("No custom death messages configured");

		BiFunction<CustomDeathMessage, String, JsonBuilder> formatter = (config, index) -> {
			final JsonBuilder json = json(index + " &e" + config.getKey() + " &3(" + config.count() + ") &7- " + config.getOriginal());
			int shown = 0;
			for (String customMessage : config.getCustom()) {
				if (++shown > 5) {
					json.hover("&7And " + (config.count() - 5) + " more...");
					break;
				}

				json.hover("&7" + customMessage);
			}
			return json.command("/deathmessages messages " + config.getKey());
		};

		new Paginator<CustomDeathMessage>()
			.values(messages)
			.formatter(formatter)
			.command("/deathmessages list")
			.page(page)
			.send();
	}

	@Path("messages <key> [page]")
	@Description("View custom death messages for a specific translation key")
	void list(CustomDeathMessage config, @Arg("1") int page) {
		final List<String> customMessages = config.getCustom();
		if (Nullables.isNullOrEmpty(customMessages))
			error("No custom messages for key &e" + config.getKey());

		send(PREFIX + "Custom messages for key &e" + config.getKey());

		new Paginator<String>()
			.values(customMessages)
			.formatter((message, index) -> json(index + " &7" + message))
			.command("/deathmessages messages " + config.getKey())
			.page(page)
			.send();
	}

	@Path("suggest <key> <message...>")
	@Description("Suggest a new custom death message for a specific translation key")
	void suggest(CustomDeathMessage config, String message) {
		final int expectedVariables = org.apache.commons.lang.StringUtils.countMatches(config.getOriginal(), "%s");
		final int foundVariables = org.apache.commons.lang.StringUtils.countMatches(message, "%s");

		if (expectedVariables != foundVariables)
			error("Your message must contain the same amount of variables (&e%s&c) as the default message " +
					"(Found &e" + foundVariables + "&c, expected &e" + expectedVariables + "&c: &7" + config.getOriginal() + "&c)");

		config.getSuggestions().add(message);
		save();

		send(PREFIX + "Added suggestion for key " + config.getKey());
	}

	@Path("behavior <behavior> [player] [duration...]")
	@Description("Change the broadcast behavior of a your death messages")
	void toggle(Behavior behavior, @Arg(value = "self", permission = Group.STAFF) OfflinePlayer player, @Arg(permission = Group.STAFF) Timespan duration) {
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

		final WorldGroup worldGroup = WorldGroup.of(player);
		JsonBuilder output = new JsonBuilder("&f" + worldGroup.getIcon())
			.hover("&e" + worldGroup)
			.group()
			.next(" &c💀 ")
			.group();

		if (deathMessageRaw instanceof TranslatableComponent deathMessage) {
			output.next(deathMessage.args(deathMessage.args().stream().map(arg -> handleArgument(deathMessages, arg)).toList()));
		} else {
			Nexus.warn("Death message ("+deathMessageRaw.examinableName()+") is not translatable: " + AdventureUtils.asPlainText(deathMessageRaw));
			output.next(deathMessageRaw);
		}

		output.color(NamedTextColor.RED);

		event.deathMessage(null);

		if (deathMessages.getBehavior() == Behavior.SHOWN) {
			Broadcast.ingame().sender(player).message(output).messageType(MessageType.CHAT).muteMenuItem(MuteMenuItem.DEATH_MESSAGES).send();

			if (worldGroup == WorldGroup.SURVIVAL)
				discord(deathString, player);
		} else if (deathMessages.getBehavior() == Behavior.LOCAL) {
			local(player, output);
		}
	}

	private void discord(String deathString, Player player) {
		// workaround for dumb Adventure bug (ProjectEdenGG/Issues#657)
		if (deathString == null)
			deathString = "☠ " + Nickname.of(player) + " died";
		else {
			deathString = StringUtils.stripColor(deathString);
			deathString = ("☠ " + HEART_PATTERN.matcher(deathString).replaceAll("a mob"))
				.replace(" " + player.getName() + " ", " " + Nickname.of(player) + " ");

			if (player.getKiller() != null)
				deathString = deathString.replace(player.getKiller().getName(), Nickname.of(player.getKiller()));
		}
		Broadcast.discord().message(Discord.discordize(deathString)).send();
	}

	private void local(Player player, JsonBuilder output) {
		Chatter chatter = new ChatterService().get(player);
		for (Chatter recipient : StaticChannel.LOCAL.getChannel().getRecipients(chatter))
			if (!MuteMenuUser.hasMuted(recipient.getOnlinePlayer(), MuteMenuItem.DEATH_MESSAGES))
				// TODO - 1.19.2 Chat Validation Kick
				// recipient.sendMessage(player, output, MessageType.CHAT);
				recipient.sendMessage(output, MessageType.CHAT);
	}

	public static Component handleArgument(DeathMessages player, Component component) {
		Component originalComponent = component;

		ShowEntity hover = getEntityHover(component);

		String playerName = null;
		for (Component child : component.children()) {
			if (child instanceof TextComponent childText) {
				final String content = childText.content();
				if (!Nullables.isNullOrEmpty(content)) {
					playerName = content;
					break;
				}
			}
		}

		if (playerName == null) {
			if (component instanceof TextComponent textComponent && !textComponent.content().isEmpty()) {
				playerName = textComponent.content();
				component = textComponent.content(""); // preserves hover text/click events but clears the content as we manually add it back it
			} else {
				return component;
			}
		}

		Component finalComponent = cleanup(player, originalComponent, hover, playerName);

		return component.children(Collections.singletonList(finalComponent));
	}

	private static Component cleanup(DeathMessages deathMessages, Component originalComponent, ShowEntity hover, String playerName) {
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
	private static ShowEntity getEntityHover(Component component) {
		HoverEvent<?> _hoverEvent = component.hoverEvent();
		if (_hoverEvent != null && _hoverEvent.value() instanceof ShowEntity _hover)
			return _hover;
		else
			return null;
	}

	@NotNull
	private static Component fixMcMMOHearts(ShowEntity hover) {
		Component failsafeComponent = Component.text("A Very Scary Mob");
		Component finalComponent = failsafeComponent;

		Entity entity = Bukkit.getEntity(hover.id());

		if (entity != null) {
			// get mcMMO's saved entity name
			if (entity.hasMetadata(MetadataConstants.METADATA_KEY_CUSTOM_NAME)) {
				String name = entity.getMetadata(MetadataConstants.METADATA_KEY_CUSTOM_NAME).get(0).asString();
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
	private static Component nickname(DeathMessages deathMessages, String playerName) {
		final Minigamer minigamer = Minigamer.of(deathMessages);
		final boolean isPlaying = minigamer.isPlaying() && minigamer.getTeam() != null;
		JsonBuilder playerComponent = new JsonBuilder(playerName, isPlaying ? minigamer.getTeam().getColor() : ChatColor.YELLOW.getColor());

		if (playerName.equals(deathMessages.getName()))
			playerComponent.content(deathMessages.getNickname());
		else {
			try {
				playerComponent.content(Nickname.of(playerName));
			} catch (PlayerNotFoundException|InvalidInputException ignored) {}
		}
		return playerComponent.build();
	}

	@ConverterFor(CustomDeathMessage.class)
	CustomDeathMessage convertToCustomDeathMessage(String input) {
		final CustomDeathMessage config = DeathMessagesConfig.of(input);
		if (config == null)
			throw new InvalidInputException("Death message config from key &e" + input + " not found");
		return config;
	}

	@TabCompleterFor(CustomDeathMessage.class)
	List<String> tabCompleteCustomDeathMessage(String filter) {
		return config.getMessages().values().stream()
				.map(CustomDeathMessage::getKey)
				.filter(key -> key.toLowerCase().startsWith(filter.toLowerCase()))
				.toList();
	}

}

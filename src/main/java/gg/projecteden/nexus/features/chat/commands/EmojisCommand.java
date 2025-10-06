package gg.projecteden.nexus.features.chat.commands;

import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.features.chat.events.ChatEvent;
import gg.projecteden.nexus.features.events.store.EventStoreItem;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import gg.projecteden.nexus.features.resourcepack.models.files.FontFile.CustomCharacter;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.emoji.EmojiUser;
import gg.projecteden.nexus.models.emoji.EmojiUser.Emoji;
import gg.projecteden.nexus.models.emoji.EmojiUserService;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.DialogUtils.DialogBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

@NoArgsConstructor
@Aliases("emoji")
public class EmojisCommand extends CustomCommand implements Listener {
	private static final EmojiUserService service = new EmojiUserService();
	private EmojiUser user;

	public EmojisCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@Path("picker")
	@Description("View your owned emojis")
	void picker() {
		final List<Emoji> emojis = Emoji.EMOJIS.stream()
			.filter(emoji -> isAdmin() || user.owns(emoji))
			.toList();

		if (emojis.isEmpty())
			error("You do not own any emojis");

		final JsonBuilder picker = json();

		for (Emoji emoji : emojis) {
			if (!isAdmin() && !user.owns(emoji))
				continue;

			if (picker.isInitialized())
				picker.group().next(" ");
			else
				picker.initialize();

			picker.group().next(emoji.getEmoji()).insert(emoji.getEmoji()).hover("&e" + emoji.getName(), "", "&eShift+Click to insert");
		}

		send(PREFIX);
		send(picker);
	}

	@Path("store")
	@Description("View the emoji store")
	void store() {
		var dialog = new DialogBuilder().title("Emoji Store").multiAction().columns(14);

		var emojis = Emoji.EMOJIS.stream()
			.filter(Emoji::isPurchasable)
			.filter(emoji -> !user.owns(emoji))
			.toList();

		if (emojis.isEmpty())
			error("No emojis available for purchase");

		for (var emoji : emojis) {
			var lore = new JsonBuilder("&e" + emoji.getName())
				.newline().next("")
				.newline().next("&eClick to purchase")
				.newline().next("&3Price: &e" + EventStoreItem.CHAT_EMOJIS.getPrice() + " Event Tokens");

			dialog.button(emoji.getEmoji(), lore, 20, response -> {
				buy(emoji);
				store();
			});
		}

		dialog.open(player());
	}

	@Path("buy <emoji>")
	@Description("Buy an emoji")
	void buy(Emoji emoji) {
		if (user.owns(emoji))
			error("You already own &e" + emoji.getName() + " &f" + emoji.getEmoji());

		new EventUserService().edit(player(), eventUser -> eventUser.charge(EventStoreItem.CHAT_EMOJIS.getPrice()));
		user.give(emoji);
		service.save(user);
		send(PREFIX + "Purchased &e" + emoji.getName() + " &f" + emoji.getEmoji() + "&3, use with &c/emoji picker");
	}

	@Path("reload")
	@Permission(Group.ADMIN)
	@Description("Reload emojis from the resource pack")
	void load() {
		reload();
		send(PREFIX + "Loaded " + Emoji.EMOJIS.size() + " emojis");
	}

	@EventHandler
	public void onChat(ChatEvent event) {
		final Chatter chatter = event.getChatter();
		String message = event.getMessage();

		if (chatter == null) {
			for (Emoji emoji : Emoji.EMOJIS)
				message = message.replaceAll(emoji.getEmoji(), "");
		} else {
			final EmojiUser user = new EmojiUserService().get(chatter);
			if (!Rank.of(chatter).isAdmin())
				for (Emoji emoji : Emoji.EMOJIS) {
					if (!message.contains(emoji.getEmoji()))
						continue;
					if (user.owns(emoji))
						continue;

					message = message.replaceAll(emoji.getEmoji(), "");
				}
		}

		if (!message.equals(event.getMessage()))
			if (chatter != null)
				Tasks.wait(1, () -> PlayerUtils.send(chatter, Chat.PREFIX + "&cYou do not own some of the emojis you used! &3Purchase in &c/emoji store"));

		for (Emoji emoji : Emoji.EMOJIS)
			message = message.replaceAll(emoji.getEmoji(), ChatColor.WHITE + emoji.getEmoji() + event.getChannel().getMessageColor());

		if (!message.equals(event.getMessage()))
			event.setMessage(message);
	}

	@ConverterFor(Emoji.class)
	Emoji convertToEmoji(String value) {
		return Emoji.EMOJIS.stream()
			.filter(emoji -> emoji.getName().equalsIgnoreCase(value))
			.findFirst()
			.orElseThrow(() -> new InvalidInputException("Emoji &e" + value + " &cnot found"));
	}

	@TabCompleterFor(Emoji.class)
	List<String> tabCompleteEmoji(String filter) {
		return Emoji.EMOJIS.stream()
			.map(Emoji::getName)
			.filter(name -> name.toLowerCase().startsWith(filter.toLowerCase()))
			.toList();
	}

	private static final String EMOJI_ROOT = "projecteden/font/emojis/";

	@EventHandler
	public void on(ResourcePackUpdateCompleteEvent event) {
		reload();
	}

	public static void reload() {
		Emoji.EMOJIS.clear();

		for (CustomCharacter character : ResourcePack.getFontFile().getProviders()) {
			if (!character.getType().equals("bitmap") || character.getFile() == null)
				continue;

			if (!character.getFile().contains(EMOJI_ROOT))
				continue;

			final Emoji emoji = new Emoji(character.fileName(), character.getChar(), character.isPurchasable());
			Emoji.EMOJIS.add(emoji);
		}
	}

}

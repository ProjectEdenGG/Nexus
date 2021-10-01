package gg.projecteden.nexus.features.chat.commands;

import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.features.chat.events.ChatEvent;
import gg.projecteden.nexus.features.events.store.EventStoreItem;
import gg.projecteden.nexus.features.menus.BookBuilder.WrittenBookMenu;
import gg.projecteden.nexus.features.resourcepack.FontFile.CustomCharacter;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.emoji.EmojiUser;
import gg.projecteden.nexus.models.emoji.EmojiUser.Emoji;
import gg.projecteden.nexus.models.emoji.EmojiUserService;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

import static gg.projecteden.nexus.models.emoji.EmojiUser.Emoji.EMOJIS;

@NoArgsConstructor
@Aliases("emoji")
@Permission("group.staff")
public class EmojisCommand extends CustomCommand implements Listener {
	private static final EmojiUserService service = new EmojiUserService();
	private EmojiUser user;

	public EmojisCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@Path("picker")
	void picker() {
		send(PREFIX);

		final JsonBuilder picker = json();

		for (Emoji emoji : EMOJIS) {
			if (!isAdmin() && !user.owns(emoji))
				continue;

			if (picker.isInitialized())
				picker.group().next(" ");
			else
				picker.initialize();

			picker.group().next(emoji.getEmoji()).insert(emoji.getEmoji()).hover("&e" + emoji.getName(), "", "&eShift+Click to insert");
		}

		send(picker);
	}

	private static final int PER_PAGE = 9 * 12;

	@Path("store")
	void store() {
		final WrittenBookMenu book = new WrittenBookMenu();

		final List<Emoji> emojis = EMOJIS.stream()
			.filter(Emoji::isPurchasable)
			.filter(emoji -> user.owns(emoji))
			.toList();

		if (emojis.isEmpty())
			error("No emojis available for purchase");

		JsonBuilder picker = json();
		int count = 0;
		for (Emoji emoji : emojis) {
			if (picker.isInitialized())
				picker.group().next(" ");
			else
				picker.initialize();

			picker.group()
				.next(emoji.getEmoji())
				.hover("&e" + emoji.getName(), "", "&eClick to purchase", "&3Price: &e" + EventStoreItem.CHAT_EMOJIS.getPrice() + " Event Tokens")
				.command("/emoji buy " + emoji.getName())
				.color(NamedTextColor.WHITE);

			if (++count >= PER_PAGE) {
				book.addPage(picker);
				picker = json();
				count = 0;
			}
		}

		if (picker.isInitialized())
			book.addPage(picker);

		book.open(player());
	}

	@Path("buy <emoji>")
	void buy(Emoji emoji) {
		if (user.owns(emoji))
			error("You already own &e" + emoji.getName() + " &f" + emoji.getEmoji());

		new EventUserService().edit(player(), eventUser -> eventUser.charge(EventStoreItem.CHAT_EMOJIS.getPrice()));
		user.give(emoji);
		service.save(user);
		send(PREFIX + "Purchased &e" + emoji.getName() + " &f" + emoji.getEmoji() + "&3, use with &c/emoji picker");
	}

	@EventHandler
	public void onChat(ChatEvent event) {
		final Chatter chatter = event.getChatter();
		String message = event.getMessage();

		if (chatter == null) {
			for (Emoji emoji : EMOJIS)
				message = message.replaceAll(emoji.getEmoji(), "");
		} else {
			final EmojiUser user = new EmojiUserService().get(chatter);
			if (!Rank.of(chatter).isAdmin())
				for (Emoji emoji : EMOJIS) {
					if (!message.contains(emoji.getEmoji()))
						continue;
					if (user.owns(emoji))
						continue;

					message = message.replaceAll(emoji.getEmoji(), "");
				}
		}

		if (!message.equals(event.getMessage())) {
			if (chatter != null)
				Tasks.wait(1, () -> PlayerUtils.send(chatter, Chat.PREFIX + "&cYou do not own some of the emojis you used! &3Purchase in &c/emoji store"));

			event.setMessage(message);
		}
	}

	@ConverterFor(Emoji.class)
	Emoji convertToEmoji(String value) {
		return EMOJIS.stream()
			.filter(emoji -> emoji.getName().equalsIgnoreCase(value))
			.findFirst()
			.orElseThrow(() -> new InvalidInputException("Emoji &e" + value + " &cnot found"));
	}

	@TabCompleterFor(Emoji.class)
	List<String> tabCompleteEmoji(String filter) {
		return EMOJIS.stream()
			.map(Emoji::getName)
			.filter(name -> name.toLowerCase().startsWith(filter.toLowerCase()))
			.toList();
	}

	private static final String EMOJI_ROOT = "projecteden/font/emojis/";

	static {
		ResourcePack.getLoader().thenRun(() -> {
			for (CustomCharacter character : ResourcePack.getFontFile().getProviders()) {
				if (!character.getFile().contains(EMOJI_ROOT))
					continue;

				final Emoji emoji = new Emoji(character.fileName(), character.getChar(), character.isPurchasable());
				EMOJIS.add(emoji);
			}
		});
	}

}

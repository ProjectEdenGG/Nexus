package me.pugabyte.nexus.features.commands;

import eden.utils.TimeUtils.Time;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.features.listeners.TemporaryMenuListener;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.HideFromHelp;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.cooldown.CooldownService;
import me.pugabyte.nexus.models.mail.Mailer;
import me.pugabyte.nexus.models.mail.Mailer.Mail;
import me.pugabyte.nexus.models.mail.MailerService;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Utils;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

@NoArgsConstructor
@Aliases("delivery")
@Redirect(from = "/mailbox", to = "/mail box")
public class MailCommand extends CustomCommand implements Listener {
	public static final String PREFIX = StringUtils.getPrefix("Mail");
	private final MailerService service = new MailerService();
	private Mailer from;

	public MailCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			from = service.get(player());
	}

	@Path("send <player> [message...]")
	void send(Mailer to, String message) {
		if (from.hasPending())
			send(PREFIX + "&cYou already have a pending message to " + from.getPending().getNickname());
		else
			from.addPending(new Mail(to.getUuid(), uuid(), worldGroup(), message));

		save(from);
		menu();
	}

	@Path("menu")
	private void menu() {
		Mail mail = from.getPending();
		line(3);
		send(PREFIX + "Sending mail to " + mail.getNickname() + " with " + mail.getContents());
		send(json("&3   ")
				.next("&c&lCancel")
				.command("/mail cancel")
				.hover("&cClick to cancel")
				.group()
				.next("  &3|  ")
				.group()
				.next("&e&l" + (mail.hasMessage() ? "Edit" : "Add") + " Message")
				.suggest("/mail message " + (mail.hasMessage() ? mail.getMessage() : ""))
				.hover("&eClick to " + (mail.hasMessage() ? "edit" : "add a") + " message")
				.group()
				.next("  &3|  ")
				.group()
				.next("&e&l" + (mail.hasItems() ? "Edit" : "Add") + " Items")
				.command("/mail items")
				.hover("&6Click to " + (mail.hasItems() ? "edit" : "add") + " items")
				.group()
				.next("  &3|  ")
				.group()
				.next("&a&lSend")
				.command("/mail confirm")
				.hover("&aClick to send"));
	}

	@TabCompleteIgnore
	@HideFromHelp
	@Path("cancel")
	void cancel() {
		Mail mail = from.getPending();
		mail.cancel();
		save(from);
		send(PREFIX + "Mail to " + mail.getNickname() + " cancelled");
	}

	@TabCompleteIgnore
	@HideFromHelp
	@Path("message [message...]")
	void message(String message) {
		from.getPending().setMessage(message);
		save(from);
		menu();
	}

	@TabCompleteIgnore
	@HideFromHelp
	@Path("items")
	void items() {
		new EditItemsMenu(from.getPending());
	}

	@TabCompleteIgnore
	@HideFromHelp
	@Path("confirm")
	void confirm() {
		Mail mail = from.getPending();
		mail.send();
		save(mail.getOwner());
		save(mail.getFromMailer());
		send(PREFIX + "Your mail is on its way to " + mail.getNickname());
		mail.getOwner().sendNotification();
	}

	@Path("box")
	void box() {
		new MailBoxMenu(from).open(player());
	}

	private void save(Mailer mailer) {
		service.save(mailer);
	}

	@Data
	public static class EditItemsMenu implements TemporaryMenuListener {
		private final String title;
		private final Player player;
		private final Mail mail;

		public EditItemsMenu(Mail mail) {
			this.title = (mail.hasItems() ? "Add" : "Edit") + " Items";
			this.player = mail.getFromMailer().getOnlinePlayer();
			this.mail = mail;

			open(6, mail.getItems());
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			mail.setItems(contents);
			player.chat("/mail menu");
		}
	}

	public static class MailBoxMenu extends MenuUtils implements InventoryProvider {
		private final MailerService service = new MailerService();
		private final Mailer mailer;
		private final WorldGroup worldGroup;

		public MailBoxMenu(Mailer mailer) {
			this.mailer = mailer;
			this.worldGroup = mailer.getWorldGroup();
		}

		@Override
		public void open(Player viewer, int page) {
			SmartInventory.builder()
					.provider(this)
					.size(6, 9)
					.title(colorize("&3Your Deliveries"))
					.build()
					.open(mailer.getOnlinePlayer(), page);
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			addCloseItem(contents);

			ItemStack info = new ItemBuilder(Material.BOOK).name("&3Info")
					.lore("&eOpened mail cannot be closed", "&eAny items left over, will be", "&egiven to you, or dropped")
					.loreize(false)
					.build();

			contents.set(0, 8, ClickableItem.empty(info));

			List<ClickableItem> items = new ArrayList<>();

			List<Mail> mails = mailer.getUnreadMail(worldGroup);
			if (Utils.isNullOrEmpty(mails))
				throw new InvalidInputException("There is no mail in your " + StringUtils.camelCase(worldGroup) + " mailbox");

			for (Mail mail : mails)
				items.add(ClickableItem.from(mail.getDisplayItem().build(), e -> {
					mail.received();
					service.save(mailer);
					new OpenMailMenu(mail);
				}));

			addPagination(player, contents, items);
		}
	}

	@Data
	public static class OpenMailMenu implements TemporaryMenuListener {
		private final String title;
		private final Player player;
		private final Mail mail;
		private final Mailer mailer;

		public OpenMailMenu(Mail mail) {
			this.title = "From " + Nickname.of(mail.getFrom());
			this.player = mail.getOwner().getOnlinePlayer();
			this.mail = mail;
			this.mailer = mail.getOwner();

			open(6, mail.getAllItems());
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			PlayerUtils.giveItems((Player) event.getPlayer(), contents);

			Tasks.wait(1, () -> {
				if (!mailer.getMail(mail.getWorldGroup()).isEmpty())
					new MailBoxMenu(mailer).open(player);
			});
		}
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		WorldGroup worldGroup = WorldGroup.of(player);
		Mailer mailer = service.get(player);

		List<Mail> mails = new ArrayList<>(mailer.getMail(worldGroup));
		if (Utils.isNullOrEmpty(mails))
			return;

		if (!new CooldownService().check(player, "youhavemail", Time.MINUTE.x(5)))
			return;

		mailer.sendNotification();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Mailer user = service.get(event.getPlayer());

		if (!user.getMail().isEmpty())
			Tasks.wait(3, user::sendNotification);
	}

}

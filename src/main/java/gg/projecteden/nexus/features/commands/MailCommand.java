package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.TemporaryMenuListener;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.mail.Mailer;
import gg.projecteden.nexus.models.mail.Mailer.Mail;
import gg.projecteden.nexus.models.mail.MailerService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
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
	@Description("Send a message and/or items to a player")
	void send(Mailer to, String message) {
		if (from.getUuid().equals(to.getUuid()))
			error("You cannot send mail yourself");

		boolean hasPending = from.hasPending();
		if (hasPending)
			send(PREFIX + "&4You already have pending mail to &e" + from.getPending().getNickname());
		else
			from.addPending(new Mail(to.getUuid(), uuid(), worldGroup(), message));

		save(from);
		menu(!hasPending);
	}

	@Path("menu")
	@HideFromHelp
	@HideFromWiki
	@TabCompleteIgnore
	private void menu(boolean emptyLines) {
		Mail mail = from.getPending();
		if (emptyLines)
			line(3);
		send(PREFIX + "Sending mail to &e" + mail.getNickname() + " &3with " + mail.getContents());
		send(json("&3   ")
				.next("&c&lCancel")
				.command("/mail cancel")
				.hover("&cClick to cancel")
				.group()
				.next("  &3|  ")
				.group()
				.next("&e&l" + (mail.hasMessage() ? "Edit" : "Add") + " Message")
				.suggest("/mail message " + (mail.hasMessage() ? new ItemBuilder(mail.getMessage()).getBookPlainContents() : ""))
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

	@Path("cancel")
	@HideFromHelp
	@HideFromWiki
	@TabCompleteIgnore
	void cancel() {
		Mail mail = from.getPending();
		mail.cancel();
		save(from);
		send(PREFIX + "Mail to " + mail.getNickname() + " cancelled");
	}

	@Path("message [message...]")
	@HideFromHelp
	@HideFromWiki
	@TabCompleteIgnore
	void message(String message) {
		from.getPending().setMessage(message);
		save(from);
		menu(true);
	}

	@Path("items")
	@HideFromHelp
	@HideFromWiki
	@TabCompleteIgnore
	void items() {
		new EditItemsMenu(from.getPending());
	}

	@Path("confirm")
	@HideFromHelp
	@HideFromWiki
	@TabCompleteIgnore
	void confirm() {
		Mail mail = from.getPending();
		mail.send();
		save(mail.getOwner());
		save(mail.getFromMailer());
		send(PREFIX + "Your mail is on its way to " + mail.getNickname());
		mail.getOwner().sendNotification();
	}

	@Path("box")
	@Description("View your mail box")
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
			this.title = (mail.hasItems() ? "Add" : "Edit") + " Items to " + mail.getNickname();
			this.player = mail.getFromMailer().getOnlinePlayer();
			this.mail = mail;

			open(6, mail.getItems());
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			mail.setItems(contents);
			new MailerService().save(mail.getFromMailer());
			PlayerUtils.runCommand(player, "mail menu");
		}
	}

	@Title("&3Your Deliveries")
	public static class MailBoxMenu extends InventoryProvider {
		private final Mailer mailer;
		private final WorldGroup worldGroup;

		public MailBoxMenu(Mailer mailer) {
			this.mailer = mailer;
			this.worldGroup = mailer.getWorldGroup();
		}

		@Override
		public void open(Player viewer, int page) {
			if (Nullables.isNullOrEmpty(mailer.getUnreadMail(worldGroup)))
				viewer.sendMessage(JsonBuilder.fromError("Mail", "There is no mail in your " + StringUtils.camelCase(worldGroup) + " mailbox"));
			else
				super.open(viewer, page);
		}

		@Override
		public void init() {
			addCloseItem();

			ItemStack info = new ItemBuilder(Material.BOOK).name("&3Info")
					.lore("&eOpened mail cannot be closed", "&eAny items left over, will be", "&egiven to you, or dropped")
					.loreize(false)
					.build();

			contents.set(0, 8, ClickableItem.empty(info));

			List<ClickableItem> items = new ArrayList<>();

			List<Mail> mails = mailer.getUnreadMail(worldGroup);

			for (Mail mail : mails)
				items.add(ClickableItem.of(mail.getDisplayItem().build(), e -> new OpenMailMenu(mail)));

			paginate(items);

			if (mails.size() > 1)
				contents.set(5, 4, ClickableItem.of(Material.HOPPER, "&eCollect All", e -> {
					mails.forEach(mail -> {
						PlayerUtils.giveItems(viewer, mail.getAllItems());
						mail.received();
					});
					new MailerService().save(mailer);
					close();
					mailer.sendMessage(PREFIX + "Collected " + mails.size() + " mail");
				}));
		}
	}

	@Data
	public static class OpenMailMenu implements TemporaryMenuListener {
		private final Mail mail;
		private final Mailer mailer;
		private final Player player;
		private final String title;

		public OpenMailMenu(Mail mail) {
			this.mail = mail;
			this.mailer = mail.getOwner();
			this.player = mailer.getOnlinePlayer();
			this.title = "From " + mail.getFromName();

			open(6, mail.getAllItems());

			mail.received();
			new MailerService().save(mailer);
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
		if (Nullables.isNullOrEmpty(mails))
			return;

		if (!new CooldownService().check(player, "youhavemail", TickTime.MINUTE.x(5)))
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

package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.MailCommand;
import gg.projecteden.nexus.features.commands.MailCommand.EditItemsMenu;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxFloor;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Interactable;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableFloorThing;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.mail.Mailer;
import gg.projecteden.nexus.models.mail.Mailer.Mail;
import gg.projecteden.nexus.models.mail.MailerService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class Mailbox extends DyeableFloorThing implements Interactable {

	public Mailbox(boolean multiblock, String name, ItemModelType itemModelType, ColorableType colorableType, String hexOverride, HitboxFloor hitbox) {
		super(multiblock, name, itemModelType, colorableType, hexOverride, hitbox);
	}

	static {
		Nexus.registerListener(new MailBoxListener());
	}

	private static class MailBoxListener implements Listener {

		@EventHandler
		public void on(DecorationInteractEvent event) {
			final Player player = event.getPlayer();
			final UUID owner = event.getDecoration().getOwner(player);

			boolean isMailbox = event.getDecoration().is(DecorationType.MAILBOX);
			boolean isPostbox = event.getDecoration().is(DecorationType.POSTBOX);

			if (!isMailbox && !isPostbox)
				return;

			if (isPostbox) {
				PlayerUtils.runCommand(player, "mail box");
				return;
			}

			if (WorldGroup.of(player) == WorldGroup.EVENTS)
				return;

			if (owner == null) {
				PlayerUtils.send(player, MailCommand.PREFIX + "Could not determine owner of mailbox");
				return;
			}

			if (player.getUniqueId().equals(owner)) {
				PlayerUtils.runCommand(player, "mail box");
				return;
			}

			event.setCancelled(true);
			final MailerService service = new MailerService();
			Mailer from = service.get(player);
			Mailer to = service.get(owner);

			if (from.hasPending()) {
				if (!from.getPending().getUuid().equals(to.getUuid())) {
					from.sendMessage(MailCommand.PREFIX + "&cYou already have pending mail to " + from.getPending().getNickname());
					return;
				}
			} else
				from.addPending(new Mail(to.getUuid(), from.getUuid(), WorldGroup.of(player), null));

			service.save(from);

			new EditItemsMenu(from.getPending());
		}
	}
}

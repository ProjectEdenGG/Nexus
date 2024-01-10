package gg.projecteden.nexus.features.justice.misc;

import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.punishments.Punishment;
import gg.projecteden.nexus.models.punishments.PunishmentType;
import gg.projecteden.nexus.models.punishments.Punishments;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@NoArgsConstructor
public abstract class _PunishmentCommand extends _JusticeCommand {

	public _PunishmentCommand(@NonNull CommandEvent event) {
		super(event);
	}

	protected void punish(List<Punishments> players) {
		punish(players, null);
	}

	protected void punish(List<Punishments> players, String input) {
		punish(players, input, false);
	}

	protected void punish(List<Punishments> players, String input, boolean now) {
		List<Punishments> toPunish = new ArrayList<>(players);
		AtomicReference<Runnable> loop = new AtomicReference<>();
		loop.set(() -> {
			if (toPunish.isEmpty())
				return;

			try {
				Punishments player = toPunish.remove(0);

				if (player.getRank().isStaff())
					if (!isAdmin())
						throw new InvalidInputException("You cannot " + getType().name().toLowerCase() + " staff members");

				Runnable punish = () -> player.add(Punishment.ofType(getType())
						.punisher(uuid())
						.input(input)
						.now(now));

				Optional<Punishment> cooldown = player.getCooldown(uuid());
				if (cooldown.isPresent()) {
					final Punishment punishment = cooldown.get();
					ConfirmationMenu.builder()
							.title(camelCase(getType()) + " " + player.getNickname())
							.confirmLore(List.of(
								"&c" + player.getNickname() + " was recently",
								"&c" + punishment.getType().getColoredName().toLowerCase() + " by " + Nickname.of(punishment.getPunisher())
							))
							.onConfirm($ -> punish.run())
							.onFinally(e -> loop.get().run())
							.open(player());
				} else {
					punish.run();
					loop.get().run();
				}
			} catch (Exception ex) {
				event.handleException(ex);
			}
		});

		loop.get().run();
	}

	protected void deactivate(List<Punishments> players) {
		for (Punishments player : players) {
			try {
				Optional<Punishment> punishment = player.getMostRecentActive(getType());
				if (punishment.isPresent())
					punishment.get().deactivate(uuid());
				else
					error(player.getNickname() + " is not " + getType().getPastTense());
			} catch (Exception ex) {
				event.handleException(ex);
			}
		}
	}

	abstract protected PunishmentType getType();

}

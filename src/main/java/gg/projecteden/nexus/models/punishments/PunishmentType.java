package gg.projecteden.nexus.models.punishments;

import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.FormatType;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.justice.Justice;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.framework.interfaces.Colored;
import gg.projecteden.nexus.framework.interfaces.IsColoredAndNamed;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.freeze.FreezeService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Function;

@Getter
@AllArgsConstructor
public enum PunishmentType implements IsColoredAndNamed {
	BAN("banned", ChatColor.DARK_RED, true, true, false, true) {
		@Override
		public void action(Punishment punishment) {
			kick(punishment);

			checkEstablishedPlayer(punishment);
		}

		private void checkEstablishedPlayer(Punishment punishment) {
			Tasks.waitAsync(10, () -> {
				if (!Justice.isNewPlayer(punishment)) {
					Nerd punisher = Nerd.of(punishment.getPunisher());
					DiscordUser discordUser = new DiscordUserService().get(punisher);
					if (!Nullables.isNullOrEmpty(discordUser.getUserId()))
						Discord.staffLog("<@" + discordUser.getUserId() + "> Please include any additional information about the " +
								"ban here, such as screenshots, chat logs, and any other information that will help us understand the ban.");

					new JsonBuilder()
							.line()
							.next("&cYou have banned an established player (1+ hour)")
							.line()
							.next("&3If applicable, please remember to add any additional information about this ban in #staff-log")
							.line()
							.send(punisher);
				}
			});
		}

		@Override
		public String getDisconnectMessage(Punishment punishment) {
			String nl = System.lineSeparator();
			String message = "&4You are " + (punishment.getSeconds() == 0 ? "permanently " : "") + "banned from this server"
					+ nl + nl
				+ "&3Banned on &e" + TimeUtils.shortDateTimeFormat(punishment.getTimestamp()) + " &3by &e" + Nickname.of(punishment.getPunisher());

			if (!Nullables.isNullOrEmpty(punishment.getReason()))
					message += nl + "&3Reason: &c" + punishment.getReason();

			if (punishment.getExpiration() != null)
				message += nl + "&3Expires in: &3" + Timespan.of(punishment.getExpiration()).format(FormatType.LONG);

			message += nl + nl + "&3Appeal at &c" + EdenSocialMediaSite.WEBSITE.getUrl() + "/appeal";

			return message;
		}
	},
	ALT_BAN("alt-banned", ChatColor.DARK_RED, true, true, false, true) {
		@Override
		public void action(Punishment punishment) {
			BAN.action(punishment);
			for (UUID alt : Punishments.of(punishment).getAlts())
				kick(Bukkit.getPlayer(alt), punishment);
		}

		@Override
		public String getDisconnectMessage(Punishment punishment) {
			return BAN.getDisconnectMessage(punishment);
		}
	},
	KICK("kicked", ChatColor.YELLOW, false, false, true, true) {
		@Override
		public void action(Punishment punishment) {
			kick(punishment);
		}

		@Override
		public String getDisconnectMessage(Punishment punishment) {
			String nl = System.lineSeparator();
			String message = "&3Kicked by &e" + Nickname.of(punishment.getPunisher());
			if (!Nullables.isNullOrEmpty(punishment.getReason()))
				message += nl + "&3Reason: &c" + punishment.getReason();

			message += nl + nl + "&3You may connect to the server again";
			return message;
		}
	},
	MUTE("muted", ChatColor.GOLD, true, true, false, false) {
		@Override
		public void action(Punishment punishment) {
			punishment.sendMessage("&cYou have been muted by &e" + Nickname.of(punishment.getPunisher()) + punishment.getTimeAndReason());
		}

		@Override
		public void onExpire(Punishment punishment) {
			punishment.sendMessage("&cYour mute has &eexpired");
		}
	},
	WARN("warned", ChatColor.RED, false, false, false, false) {
		@Override
		public void action(Punishment punishment) {
			Punishments.of(punishment).tryShowWarns();
		}
	},
	FREEZE("froze", ChatColor.AQUA, false, true, true, true) {
		@Override
		public void action(Punishment punishment) {
			new FreezeService().get(punishment).freeze();
			punishment.sendMessage("&cYou have been frozen! This likely means you are breaking a rule; please pay attention to staff in chat");
		}

		@Override
		public void onExpire(Punishment punishment) {
			Tasks.sync(() -> {
				new FreezeService().get(punishment).unfreeze();
				punishment.sendMessage("&cYou have been unfrozen");
			});
		}
	},
	WATCHLIST("watchlisted", ChatColor.LIGHT_PURPLE, false, true, true, true) {
		@Override
		public void action(Punishment punishment) {}
	};

	private final String pastTense;
	private final ChatColor chatColor;
	@Accessors(fluent = true)
	private final boolean hasTimespan;
	private final boolean onlyOneActive;
	private final boolean automaticallyReceived;
	private final boolean receivedIfAfk;

	public abstract void action(Punishment punishment);

	public void onExpire(Punishment punishment) {
	}

	public String getDisconnectMessage(Punishment punishment) {
		throw new UnsupportedOperationException("Punishment type " + StringUtils.camelCase(this) + " does not have a disconnect message");
	}

	void kick(Punishment punishment) {
		if (punishment.isOnline()) {
			kick(punishment.getPlayer(), punishment);
			punishment.received();
		}
	}

	void kick(Player player, Punishment punishment) {
		Runnable task = () -> {
			if (player != null && player.isOnline()) {
				player.leaveVehicle();
				player.kick(punishment.getDisconnectMessage());
			}
		};

		if (!Bukkit.isPrimaryThread())
			Tasks.sync(task);
		else
			task.run();
	}

	public JsonBuilder getHistoryDisplay(Punishment punishment) {
		int seconds = punishment.getSeconds();
		Function<UUID, String> staff = uuid -> "&f&#dddddd" + Nickname.of(uuid);

		JsonBuilder json = new JsonBuilder("- " + getColoredName() + " &fby " + staff.apply(punishment.getPunisher()) + " ")
				.group()
				.next("&f" + punishment.getTimeSince())
			.hover("&e" + TimeUtils.shortDateTimeFormat(punishment.getTimestamp()))
				.group()
				.next(hasTimespan && punishment.isActive() ? " &c[Active]" : "")
				.group()
				.next(" &e| &c")
				.group()
				.next(StringUtils.X)
				.command("/history delete " + punishment.getName() + " " + punishment.getId())
				.hover("&cClick to delete");

		if (punishment.hasReason())
			json.newline().next("&7   Reason &f" + punishment.getReason());

		if (hasTimespan) {
			json.newline().next("&7   Duration &f" + (seconds > 0 ? Timespan.ofSeconds(seconds).format() : "forever"));

			if (seconds > 0 && punishment.isActive())
				json.newline().next("&7   Time left &f" + punishment.getTimeLeft());
		}

		if (!automaticallyReceived && punishment.isActive())
			json.newline().next("&7   Received &f" + (punishment.hasBeenReceived() ? Timespan.of(punishment.getReceived()).format() + " ago" : "false"));

		if (punishment.hasBeenRemoved()) {
			json.newline().next("&7   Removed by " + staff.apply(punishment.getRemover()) + " ")
					.group()
					.next("&f" + punishment.getTimeSinceRemoved())
				.hover("&e" + TimeUtils.shortDateTimeFormat(punishment.getRemoved()))
					.group();
		}
		if (punishment.hasBeenReplaced()) {
			Punishment replacedBy = Punishments.of(punishment.getUuid()).getById(punishment.getReplacedBy());
			if (replacedBy == null)
				json.newline().next("&7   Replaced by &cnull");
			else
				json.newline().next("&7   Replaced by " + staff.apply(replacedBy.getPunisher()) + " ")
						.group()
						.next("&f" + replacedBy.getTimeSince())
					.hover("&e" + TimeUtils.shortDateTimeFormat(punishment.getTimestamp()))
						.group();
		}

		return json;
	}

	@Override
	public @NotNull Colored colored() {
		return Colored.of(chatColor);
	}

	@Override
	public @NotNull String getName() {
		return StringUtils.camelCase(pastTense);
	}

}

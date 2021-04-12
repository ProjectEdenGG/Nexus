package me.pugabyte.nexus.features.listeners;

import com.google.common.base.Strings;
import litebans.api.Entry;
import litebans.api.Events;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.features.commands.staff.DelayedBanCommand;
import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.framework.exceptions.postconfigured.PlayerNotFoundException;
import me.pugabyte.nexus.models.delayedban.DelayedBan;
import me.pugabyte.nexus.models.delayedban.DelayedBanService;
import me.pugabyte.nexus.models.discord.DiscordUser;
import me.pugabyte.nexus.models.discord.DiscordUserService;
import me.pugabyte.nexus.models.hours.Hours;
import me.pugabyte.nexus.models.hours.HoursService;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

public class LiteBans implements Listener {

	@EventHandler
	public void onLiteBansBan(BanEvent event) {
		try {
			Entry entry = event.getEntry();
			OfflinePlayer player = PlayerUtils.getPlayer(entry.getUuid());
			OfflinePlayer executor = PlayerUtils.getPlayer(entry.getExecutorUUID());

			OfflinePlayer pug = PlayerUtils.getPlayer("Pugabyte");
			if (player.equals(pug) && !executor.equals(pug) && executor.isOnline()) {
				PlayerUtils.runCommandAsConsole("unban Pugabyte");
				PlayerUtils.runCommandAsConsole("ban " + executor.getName() + " No.");
			}

			if (player.getUniqueId().version() != 4) {
				if (executor.isOnline() && executor.getPlayer() != null)
					PlayerUtils.send(executor.getPlayer(), "&4&lUnknown player, check your spelling");

			} else {

				try {
					if (!player.isOnline() && !entry.isPermanent() && player.hasPlayedBefore()) {
						DelayedBanService delayedBanService = new DelayedBanService();
						DelayedBan delayedBan = delayedBanService.get(player.getUniqueId());

						delayedBan.setUuid_staff(executor.getUniqueId());
						delayedBan.setReason(entry.getReason());
						delayedBan.setDuration(entry.getDurationString());
						delayedBanService.save(delayedBan);

						PlayerUtils.runCommandAsConsole("unban " + player.getName());

						Tasks.wait(10, () -> {
							PlayerUtils.runCommandAsConsole("prunehistory " + player.getName() + " 2minutes");

							Tasks.wait(10, () -> {
								String message = "&e" + player.getName() + " &3will be banned upon login for &e" + entry.getReason() + " &3for &e" + entry.getDurationString();
								Chat.broadcastIngame(DelayedBanCommand.PREFIX + message, StaticChannel.STAFF);
								Discord.log("**[DelayedBan]** " + stripColor(message));
							});
						});
					}
				} catch (Exception e) {
					Nexus.log(e.getMessage());
				}

				Tasks.waitAsync(10, () -> {
					Nerd nerd = Nerd.of(player);
					Hours hours = new HoursService().get(nerd);
					if (hours.getTotal() >= Time.HOUR.get() / 20) {
						DiscordUser discordUser = new DiscordUserService().get(executor);
						if (!Strings.isNullOrEmpty(discordUser.getUserId()))
							Discord.staffLog("<@" + discordUser.getUserId() + "> Please include any additional information about the " +
									"ban here, such as screenshots, chat log, and any other information that will help us understand the ban.");

						if (executor.isOnline())
							new JsonBuilder()
									.line()
									.next("&cYou have banned an established player")
									.line()
									.next("&3If applicable, please remember to add any additional information about this ban in #staff-log")
									.line()
									.send(executor.getPlayer());
					}
				});
			}
		} catch (PlayerNotFoundException ignore) {}

	}

	@EventHandler
	public void onLiteBansBroadcast(BroadcastEvent event) {
		if (Arrays.asList("broadcast", "banned_join", "mute").contains(event.getType())) {
			String message = event.getMessage();
			if (!message.contains("Server restarting.")) {
				Tasks.wait(10, () -> {
					// Cancel message if there is a delayed ban queued
					try {
						if (event.getType().equalsIgnoreCase("broadcast")
								&& (message.contains("tempbanned") || message.contains("unbanned"))) {

							String[] messageSplit = message.split(" ");
							OfflinePlayer offlinePlayer = PlayerUtils.getPlayer(stripColor(messageSplit[2]));

							DelayedBanService delayedBanService = new DelayedBanService();
							if (delayedBanService.hasQueuedBan(offlinePlayer))
								return;
						}
					} catch (Exception e) {
						Nexus.log(e.getMessage());
					}
					//

					Discord.log("**[LiteBans]** " + message);
				});

			}
		}
	}

	private static final Events.Listener entryAdded;
	private static final Events.Listener broadcastSent;

	static {
		entryAdded = new Events.Listener() {
			@Override
			public void entryAdded(Entry entry) {
				Plugin liteBans = Bukkit.getPluginManager().getPlugin("LiteBans");
				switch (entry.getType()) {
					case "ban":
						Bukkit.getScheduler().runTask(liteBans, () -> new BanEvent(entry).callEvent());
						break;
					case "kick":
						Bukkit.getScheduler().runTask(liteBans, () -> new KickEvent(entry).callEvent());
						break;
					case "mute":
						Bukkit.getScheduler().runTask(liteBans, () -> new MuteEvent(entry).callEvent());
						break;
					case "warn":
						Bukkit.getScheduler().runTask(liteBans, () -> new WarnEvent(entry).callEvent());
						break;
				}
			}
		};

		broadcastSent = new Events.Listener() {
			@Override
			public void broadcastSent(String message, String type) {
				Plugin liteBans = Bukkit.getPluginManager().getPlugin("LiteBans");
				Bukkit.getScheduler().runTask(liteBans, () -> new BroadcastEvent(message, type).callEvent());
			}
		};

		Events.get().register(entryAdded);
		Events.get().register(broadcastSent);
	}

	public static void shutdown() {
		Events.get().unregister(entryAdded);
		Events.get().unregister(broadcastSent);
	}

	public static class EntryEvent extends Event {
		private static final HandlerList handlers = new HandlerList();
		private final Entry entry;

		public EntryEvent(final Entry entry) {
			this.entry = entry;
		}

		public Entry getEntry() {
			return entry;
		}

		@Override
		public HandlerList getHandlers() {
			return handlers;
		}

		public static HandlerList getHandlerList() {
			return handlers;
		}
	}

	public static class BanEvent extends EntryEvent {
		public BanEvent(final Entry entry) {
			super(entry);
		}
	}

	public static class KickEvent extends EntryEvent {
		public KickEvent(final Entry entry) {
			super(entry);
		}
	}

	public static class MuteEvent extends EntryEvent {
		public MuteEvent(final Entry entry) {
			super(entry);
		}
	}

	public static class WarnEvent extends EntryEvent {
		public WarnEvent(final Entry entry) {
			super(entry);
		}
	}

	public static class BroadcastEvent extends Event {
		private static final HandlerList handlers = new HandlerList();
		private final String message;
		private final String type;

		public BroadcastEvent(final String message, final String type) {
			this.message = message;
			this.type = type;
		}

		public static HandlerList getHandlerList() {
			return handlers;
		}

		public String getMessage() {
			return message;
		}

		public String getType() {
			return type;
		}

		@Override
		public HandlerList getHandlers() {
			return handlers;
		}
	}

}

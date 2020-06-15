package me.pugabyte.bncore.features.listeners;

import com.google.common.base.Strings;
import litebans.api.Entry;
import litebans.api.Events;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.framework.exceptions.postconfigured.PlayerNotFoundException;
import me.pugabyte.bncore.models.discord.DiscordService;
import me.pugabyte.bncore.models.discord.DiscordUser;
import me.pugabyte.bncore.models.hours.Hours;
import me.pugabyte.bncore.models.hours.HoursService;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class LiteBans implements Listener {

	@EventHandler
	public void onLiteBansBan(BanEvent event) {
		try {
			OfflinePlayer player = Utils.getPlayer(event.getEntry().getUuid());
			OfflinePlayer executor = Utils.getPlayer(event.getEntry().getExecutorUUID());

			OfflinePlayer pug = Utils.getPlayer("Pugabyte");
			if (player.equals(pug) && !executor.equals(pug) && executor.isOnline()) {
				Utils.runCommandAsConsole("unban Pugabyte");
				Utils.runCommandAsConsole("ban " + executor.getName() + " No.");
			}

			if (player.getUniqueId().version() != 4) {
				if (executor.isOnline())
					executor.getPlayer().sendMessage(colorize("&4&lUnknown player, check your spelling"));
			} else {
				Tasks.waitAsync(10, () -> {
					Nerd nerd = new Nerd(player);
					Hours hours = new HoursService().get(nerd);
					if (hours.getTotal() >= Time.HOUR.get() / 20) {
						DiscordUser discordUser = new DiscordService().get(executor);
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
		if (Arrays.asList("broadcast", "banned_join", "mute").contains(event.getType()))
			if (!event.getMessage().contains("Server restarting."))
				Discord.log("**[LiteBans]** " + event.getMessage());
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
						Bukkit.getScheduler().runTask(liteBans, () -> Utils.callEvent(new BanEvent(entry)));
						break;
					case "kick":
						Bukkit.getScheduler().runTask(liteBans, () -> Utils.callEvent(new KickEvent(entry)));
						break;
					case "mute":
						Bukkit.getScheduler().runTask(liteBans, () -> Utils.callEvent(new MuteEvent(entry)));
						break;
					case "warn":
						Bukkit.getScheduler().runTask(liteBans, () -> Utils.callEvent(new WarnEvent(entry)));
						break;
				}
			}
		};

		broadcastSent = new Events.Listener() {
			@Override
			public void broadcastSent(String message, String type) {
				Plugin liteBans = Bukkit.getPluginManager().getPlugin("LiteBans");
				Bukkit.getScheduler().runTask(liteBans, () -> Utils.callEvent(new BroadcastEvent(message, type)));
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
		private Entry entry;

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
		private String message;
		private String type;

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

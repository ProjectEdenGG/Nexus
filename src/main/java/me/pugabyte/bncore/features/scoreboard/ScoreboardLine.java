package me.pugabyte.bncore.features.scoreboard;

import com.gmail.nossr50.util.player.UserManager;
import lombok.Getter;
import lombok.SneakyThrows;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.commands.PushCommand;
import me.pugabyte.bncore.models.afk.AFKPlayer;
import me.pugabyte.bncore.models.chat.Channel;
import me.pugabyte.bncore.models.chat.ChatService;
import me.pugabyte.bncore.models.chat.Chatter;
import me.pugabyte.bncore.models.chat.PrivateChannel;
import me.pugabyte.bncore.models.chat.PublicChannel;
import me.pugabyte.bncore.models.hours.Hours;
import me.pugabyte.bncore.models.hours.HoursService;
import me.pugabyte.bncore.models.scoreboard.ScoreboardUser;
import me.pugabyte.bncore.models.ticket.TicketService;
import me.pugabyte.bncore.models.vote.VoteService;
import me.pugabyte.bncore.models.vote.Voter;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.pugabyte.bncore.utils.StringUtils.camelCase;
import static me.pugabyte.bncore.utils.StringUtils.left;
import static me.pugabyte.bncore.utils.StringUtils.timespanDiff;
import static me.pugabyte.bncore.utils.StringUtils.timespanFormat;
import static me.pugabyte.bncore.utils.Utils.isVanished;

public enum ScoreboardLine {
	ONLINE {
		@Override
		public String render(Player player) {
			long count = Bukkit.getOnlinePlayers().stream().filter(_player -> Utils.canSee(player, _player)).count();
			return "&3Online nerds: &e" + count;
		}
	},

	@Permission("group.moderator")
	TICKETS {
		@Override
		public String render(Player player) {
			TicketService service = new TicketService();
			int open = service.getAllOpen().size();
			int all = service.getAll().size();
			return "&3Tickets: &" + (open == 0 ? "e" : "c") + open + " &3/ " + all;
		}
	},

	TPS {
		@Override
		public String render(Player player) {
			double tps1m = Bukkit.getTPS()[0];
			return "&3TPS: &" + (tps1m > 19 ? "e" : tps1m > 16 ? "6" : "c") + new DecimalFormat("0.00").format(tps1m);
		}
	},

	@Permission("group.moderator")
	RAM {
		@Override
		public String render(Player player) {
			long total = Runtime.getRuntime().totalMemory();
			long used = total - Runtime.getRuntime().freeMemory();
			double gb = Math.pow(1024, 3);
			return "&3RAM: &e" + new DecimalFormat("0.00").format(used / gb)
					+ "&3/" + new DecimalFormat("#.##").format(total / gb) + "gb";
		}
	},

	PING {
		@Override
		public String render(Player player) {
			return "&3Ping: &e" + player.spigot().getPing() + "ms";
		}
	},

	CHANNEL {
		@Override
		public String render(Player player) {
			String line = "&3Channel: &e";
			Chatter chatter = new ChatService().get(player);
			if (chatter == null)
				return line + "&eNone";
			Channel activeChannel = chatter.getActiveChannel();
			if (activeChannel == null)
				return line + "&eNone";
			if (activeChannel instanceof PrivateChannel)
				return line + "&bDM / " + String.join(",", ((PrivateChannel) activeChannel).getOthersNames(chatter));
			if (activeChannel instanceof PublicChannel) {
				PublicChannel channel = (PublicChannel) activeChannel;
				return line + channel.getColor() + channel.getName();
			}
			return line + "Unknown";
		}
	},

	@Permission("pv.use")
	VANISHED {
		@Override
		public String render(Player player) {
			return "&3Vanished: &e" + isVanished(player);
		}
	},

	PUSHING {
		@Override
		public String render(Player player) {
			return "&3Pushing: &e" + player.hasPermission(PushCommand.getPerm());
		}
	},

	@Permission("essentials.gamemode")
	GAMEMODE {
		@Override
		public String render(Player player) {
			return "&3Mode: &e" + camelCase(player.getGameMode().name());
		}
	},

	WORLD {
		@Override
		public String render(Player player) {
			String world = player.getWorld().getName();
			if (Arrays.asList("world", "world_nether", "world_the_end").contains(world))
				world = world.replace("world", "legacy");
			return "&3World: &e" + world;
		}
	},

	MCMMO {
		@Override
		public String render(Player player) {
			return "&3McMMO Level: &e" + UserManager.getPlayer(player).getPowerLevel();
		}
	},

	BALANCE {
		@Override
		public String render(Player player) {
			double balance = BNCore.getEcon().getBalance(player);

			String formatted = new DecimalFormat("###,###,###.00").format(balance);

			if (balance > 1000000)
				formatted = new DecimalFormat("###,###,###.###").format(balance / 1000000) + "m";
			else if (balance > 100000)
				formatted = new DecimalFormat("###,###").format(balance);

			if (formatted.endsWith(".00"))
				formatted = left(formatted, formatted.length() - 3);

			return "&3Balance: &e$" + formatted;
		}
	},

	VOTE_POINTS {
		@Override
		public String render(Player player) {
			return "&3Vote Points: &e" + ((Voter) new VoteService().get(player)).getPoints();
		}
	},

	@Interval(2)
	COMPASS {
		private static final String compass = "[S] ---- SW ---- [W] ---- NW ---- [N] ---- NE ---- [E] ---- SE ---- ";
		private static final int extra = 8;
		@Override
		public String render(Player player) {
			float yaw = Location.normalizeYaw(player.getLocation().getYaw());
			if (yaw < 0) yaw = 360 + yaw;

			int center = (int) (Math.round(yaw / (360D / compass.length())) + 1);

			String instance;
			if (center - extra < 0) {
				center += compass.length();
				instance = (compass + compass).substring(center - extra, center + extra);
			} else if (center + extra > compass.length())
				instance = (compass + compass).substring(center - extra, center + extra);
			else
				instance = compass.substring(center - extra, center + extra);

			instance = instance.replaceAll("\\[", "&2[&f");
			instance = instance.replaceAll("]", "&2]&f");
			return instance;
		}
	},

	@Interval(3)
	COORDINATES {
		@Override
		public String render(Player player) {
			Location location = player.getLocation();
			return "&e" + (int) location.getX() + " " + (int) location.getY() + " " + (int) location.getZ();
		}
	},

	@Interval(20)
	HOURS {
		@Override
		public String render(Player player) {
			Hours hours = new HoursService().get(player);
			return "&3Hours: &e" + timespanFormat(hours.getTotal(), "None");
		}
	},

	HELP {
		@Override
		public String render(Player player) {
			return "&c/scoreboard";
		}
	},

	@Interval(20)
	@NotOptional
	AFK {
		@Override
		public String render(Player player) {
			AFKPlayer afkPlayer = me.pugabyte.bncore.features.afk.AFK.get(player);
			if (afkPlayer.isAfk())
				return "&3AFK for: &e" + timespanDiff(afkPlayer.getTime());
			return null;
		}
	};

	public abstract String render(Player player);

	@SneakyThrows
	public <T extends Annotation> T getAnnotation(Class<? extends Annotation> clazz) {
		return (T) getClass().getField(name()).getAnnotation(clazz);
	}

	public Permission getPermission() {
		return getAnnotation(Permission.class);
	}

	public boolean isOptional() {
		return getAnnotation(NotOptional.class) == null;
	}

	public int getInterval() {
		Interval annotation = getAnnotation(Interval.class);
		return annotation == null ? ScoreboardUser.UPDATE_INTERVAL : annotation.value();
	}

	public boolean hasPermission(Player player) {
		Permission annotation = getPermission();
		return annotation == null || player.hasPermission(annotation.value());
	}

	public static Map<ScoreboardLine, Boolean> getDefaultLines(Player player) {
		return new HashMap<ScoreboardLine, Boolean>() {{
			if (ScoreboardLine.ONLINE.hasPermission(player))		put(ScoreboardLine.ONLINE, true);
			if (ScoreboardLine.TICKETS.hasPermission(player))		put(ScoreboardLine.TICKETS, true);
			if (ScoreboardLine.TPS.hasPermission(player))			put(ScoreboardLine.TPS, true);
			if (ScoreboardLine.PING.hasPermission(player))			put(ScoreboardLine.PING, true);
			if (ScoreboardLine.CHANNEL.hasPermission(player))		put(ScoreboardLine.CHANNEL, true);
			if (ScoreboardLine.VANISHED.hasPermission(player))		put(ScoreboardLine.VANISHED, true);
			if (ScoreboardLine.MCMMO.hasPermission(player))			put(ScoreboardLine.MCMMO, !player.hasPermission("group.staff"));
			if (ScoreboardLine.BALANCE.hasPermission(player))		put(ScoreboardLine.BALANCE, !player.hasPermission("group.staff"));
			if (ScoreboardLine.VOTE_POINTS.hasPermission(player))	put(ScoreboardLine.VOTE_POINTS, !player.hasPermission("group.staff"));
			if (ScoreboardLine.GAMEMODE.hasPermission(player))		put(ScoreboardLine.GAMEMODE, true);
			if (ScoreboardLine.WORLD.hasPermission(player))			put(ScoreboardLine.WORLD, true);
			if (ScoreboardLine.COMPASS.hasPermission(player))		put(ScoreboardLine.COMPASS, true);
			if (ScoreboardLine.COORDINATES.hasPermission(player))	put(ScoreboardLine.COORDINATES, true);
			if (ScoreboardLine.HOURS.hasPermission(player))			put(ScoreboardLine.HOURS, true);
			if (ScoreboardLine.HELP.hasPermission(player))			put(ScoreboardLine.HELP, !player.hasPermission("group.staff"));
		}};
	}

	@Target({ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	protected @interface Permission {
		String value();
	}

	@Target({ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	protected @interface NotOptional {
	}

	@Target({ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	protected @interface Interval {
		int value();
	}

	@Getter
	private static final List<String> headerFrames = Arrays.asList(
			"&e< &3Bear Nation &e>",
			"&e< &3Bear Nation &e>",
			"&e< &3Bear Nation &e>",
			"&e< &bBear Nation &e>",
			"&e< &3Bear Nation &e>",
			"&e< &bBear Nation &e>",
			"&e< &3Bear Nation &e>",
			"&e< &3Bear Nation &e>",
			"&e< &3Bear Nation &e>",
			"&e< &bB&3ear Nation&3 &e>",
			"&e< &3B&be&3ar Nation &e>",
			"&e< &3Be&ba&3r Nation &e>",
			"&e< &3Bea&br&3 Nation &e>",
			"&e< &3Bear&b N&3ation &e>",
			"&e< &3Bear N&ba&3tion &e>",
			"&e< &3Bear Na&bt&3ion &e>",
			"&e< &3Bear Nat&bi&3on &e>",
			"&e< &3Bear Nati&bo&3n &e>",
			"&e< &3Bear Natio&bn&3 &e>",
			"&e< &3Bear Nati&bo&3n &e>",
			"&e< &3Bear Nat&bi&3on &e>",
			"&e< &3Bear Na&bt&3ion &e>",
			"&e< &3Bear N&ba&3tion &e>",
			"&e< &3Bear&b N&3ation &e>",
			"&e< &3Bea&br&3 Nation &e>",
			"&e< &3Be&ba&3r Nation &e>",
			"&e< &3B&be&3ar Nation &e>",
			"&e< &bB&3ear Nation&3 &e>",
			"&e< &3B&be&3ar Nation &e>",
			"&e< &3Be&ba&3r Nation &e>",
			"&e< &3Bea&br&3 Nation &e>",
			"&e< &3Bear&b N&3ation &e>",
			"&e< &3Bear N&ba&3tion &e>",
			"&e< &3Bear Na&bt&3ion &e>",
			"&e< &3Bear Nat&bi&3on &e>",
			"&e< &3Bear Nati&bo&3n &e>",
			"&e< &3Bear Natio&bn&3 &e>",
			"&e< &3Bear Nati&bo&3n &e>",
			"&e< &3Bear Nat&bi&3on &e>",
			"&e< &3Bear Na&bt&3ion &e>",
			"&e< &3Bear N&ba&3tion &e>",
			"&e< &3Bear&b N&3ation &e>",
			"&e< &3Bea&br&3 Nation &e>",
			"&e< &3Be&ba&3r Nation &e>",
			"&e< &3B&be&3ar Nation &e>"
	);
}

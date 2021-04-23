package me.pugabyte.nexus.features.scoreboard;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.player.UserManager;
import eden.models.hours.Hours;
import eden.models.hours.HoursService;
import lombok.Getter;
import lombok.SneakyThrows;
import me.pugabyte.nexus.features.commands.PushCommand;
import me.pugabyte.nexus.models.afk.AFKPlayer;
import me.pugabyte.nexus.models.banker.BankerService;
import me.pugabyte.nexus.models.chat.Channel;
import me.pugabyte.nexus.models.chat.ChatService;
import me.pugabyte.nexus.models.chat.Chatter;
import me.pugabyte.nexus.models.chat.PrivateChannel;
import me.pugabyte.nexus.models.chat.PublicChannel;
import me.pugabyte.nexus.models.scoreboard.ScoreboardUser;
import me.pugabyte.nexus.models.shop.Shop.ShopGroup;
import me.pugabyte.nexus.models.ticket.TicketService;
import me.pugabyte.nexus.models.vote.Voter;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.TimeUtils.Timespan;
import me.pugabyte.nexus.utils.TimeUtils.Timespan.TimespanBuilder;
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

import static me.pugabyte.nexus.utils.PlayerUtils.isStaffGroup;
import static me.pugabyte.nexus.utils.PlayerUtils.isVanished;
import static me.pugabyte.nexus.utils.StringUtils.camelCase;
import static me.pugabyte.nexus.utils.StringUtils.left;

public enum ScoreboardLine {
	ONLINE {
		@Override
		public String render(Player player) {
			long count = Bukkit.getOnlinePlayers().stream().filter(_player -> PlayerUtils.canSee(player, _player)).count();
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
			return "&3TPS: &" + (tps1m >= 19 ? "e" : tps1m >= 16 ? "6" : "c") + new DecimalFormat("0.00").format(tps1m);
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
				return line + "&b" + String.join(",", ((PrivateChannel) activeChannel).getOthersNames(chatter));
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
			return "&3World: &e" + StringUtils.getWorldDisplayName(player.getWorld());
		}
	},

	BIOME {
		@Override
		public String render(Player player) {
			Location location = player.getLocation();
			return "&3Biome: &e" + camelCase(location.getWorld().getBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ()).name());
		}
	},

	MCMMO {
		@Override
		public String render(Player player) {
			McMMOPlayer mcmmo = UserManager.getPlayer(player);
			return "&3McMMO Level: &e" + (mcmmo == null ? "0" : mcmmo.getPowerLevel());
		}
	},

	BALANCE {
		@Override
		public String render(Player player) {
			ShopGroup shopGroup = ShopGroup.get(player);
			if (shopGroup == null) shopGroup = ShopGroup.SURVIVAL;
			double balance = new BankerService().getBalance(player, shopGroup);

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
			return "&3Vote Points: &e" + new Voter(player).getPoints();
		}
	},

	@Interval(2)
	COMPASS {
		@Override
		public String render(Player player) {
			return StringUtils.compass(player, 8);
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
			Hours hours = new HoursService().get(player.getUniqueId());
			return "&3Hours: &e" + TimespanBuilder.of(hours.getTotal()).noneDisplay(true).format();
		}
	},

	HELP {
		@Override
		public String render(Player player) {
			return "&c/sb help";
		}
	},

	@Interval(20)
	@Required
	AFK {
		@Override
		public String render(Player player) {
			AFKPlayer afkPlayer = me.pugabyte.nexus.features.afk.AFK.get(player);
			if (afkPlayer.isAfk())
				return "&3AFK for: &e" + Timespan.of(afkPlayer.getTime()).format();
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
		return getAnnotation(Required.class) == null;
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
			if (ScoreboardLine.ONLINE.hasPermission(player)) put(ScoreboardLine.ONLINE, true);
			if (ScoreboardLine.TICKETS.hasPermission(player)) put(ScoreboardLine.TICKETS, true);
			if (ScoreboardLine.TPS.hasPermission(player)) put(ScoreboardLine.TPS, true);
			if (ScoreboardLine.PING.hasPermission(player)) put(ScoreboardLine.PING, true);
			if (ScoreboardLine.CHANNEL.hasPermission(player)) put(ScoreboardLine.CHANNEL, true);
			if (ScoreboardLine.VANISHED.hasPermission(player)) put(ScoreboardLine.VANISHED, true);
			if (ScoreboardLine.MCMMO.hasPermission(player)) put(ScoreboardLine.MCMMO, !isStaffGroup(player));
			if (ScoreboardLine.BALANCE.hasPermission(player)) put(ScoreboardLine.BALANCE, !isStaffGroup(player));
			if (ScoreboardLine.VOTE_POINTS.hasPermission(player)) put(ScoreboardLine.VOTE_POINTS, !isStaffGroup(player));
			if (ScoreboardLine.GAMEMODE.hasPermission(player)) put(ScoreboardLine.GAMEMODE, true);
			if (ScoreboardLine.WORLD.hasPermission(player)) put(ScoreboardLine.WORLD, true);
			if (ScoreboardLine.BIOME.hasPermission(player)) put(ScoreboardLine.BIOME, false);
			if (ScoreboardLine.COMPASS.hasPermission(player)) put(ScoreboardLine.COMPASS, true);
			if (ScoreboardLine.COORDINATES.hasPermission(player)) put(ScoreboardLine.COORDINATES, true);
			if (ScoreboardLine.HOURS.hasPermission(player)) put(ScoreboardLine.HOURS, true);
			if (ScoreboardLine.HELP.hasPermission(player)) put(ScoreboardLine.HELP, !isStaffGroup(player));
			if (ScoreboardLine.AFK.hasPermission(player)) put(ScoreboardLine.AFK, true);
		}};
	}

	@Target({ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	protected @interface Permission {
		String value();
	}

	@Target({ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	protected @interface Required {
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

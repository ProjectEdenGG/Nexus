package gg.projecteden.nexus.features.scoreboard;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.player.UserManager;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.TimespanBuilder;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.nameplates.Nameplates;
import gg.projecteden.nexus.features.votes.party.VoteParty;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.models.afk.AFKUser;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.chat.Channel;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.ChatterService;
import gg.projecteden.nexus.models.chat.PrivateChannel;
import gg.projecteden.nexus.models.chat.PublicChannel;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.geoip.GeoIP;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import gg.projecteden.nexus.models.hours.Hours;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.scoreboard.ScoreboardUser;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.models.ticket.Tickets;
import gg.projecteden.nexus.models.ticket.TicketsService;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.Getter;
import lombok.SneakyThrows;
import me.lucko.spark.api.statistic.StatisticWindow.MillisPerTick;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static gg.projecteden.nexus.features.vanish.Vanish.isVanished;
import static gg.projecteden.nexus.utils.StringUtils.camelCase;
import static gg.projecteden.nexus.utils.StringUtils.left;
import static java.time.format.DateTimeFormatter.ofPattern;

public enum ScoreboardLine {
	ONLINE {
		@Override
		public String render(Player player) {
			return "&3Online Nerds: &e" + OnlinePlayers.where().viewer(player).get().size();
		}
	},

	@Permission(Group.MODERATOR)
	TICKETS {
		@Override
		public String render(Player player) {
			final TicketsService service = new TicketsService();
			final Tickets tickets = service.get0();
			int open = tickets.getAllOpen().size();
			int all = tickets.getAll().size();
			return "&3Tickets: &" + (open == 0 ? "e" : "c") + open + " &3/ " + all;
		}
	},

	TPS {
		@Override
		public String render(Player player) {
			double tps1m = Bukkit.getTPS()[0];
			return "&3TPS: &" + (tps1m >= 19 ? "a" : tps1m >= 16 ? "6" : "c") + new DecimalFormat("0.00").format(tps1m);
		}
	},

	MSPT {
		@Override
		public String render(Player player) {
			final var mspt = Nexus.getSpark().mspt();
			if (mspt == null)
				return "&3MSPT: &cnull";

			final var recent = mspt.poll(MillisPerTick.MINUTES_1);
			Function<Double, String> formatter = value -> {
				if (value < 40)
					return "&a" + Math.round(value);
				else if (value > 50)
					return "&c" + Math.round(value);
				else
					return "&6" + Math.round(value);
			};
			return "&3MSPT: %s&7/%s&7/%s&7/%s".formatted(
				formatter.apply(recent.min()),
				formatter.apply(recent.mean()),
				formatter.apply(recent.percentile95th()),
				formatter.apply(recent.max())
			);
		}
	},

	@Permission(Group.MODERATOR)
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
			Function<Integer, String> formatter = value -> {
				if (value < 200)
					return "&a" + Math.round(value);
				else if (value > 500)
					return "&c" + Math.round(value);
				else
					return "&6" + Math.round(value);
			};
			return "&3Ping: &e" + formatter.apply(player.getPing()) + "ms";
		}
	},

	CHANNEL {
		@Override
		public String render(Player player) {
			String line = "&3Channel: &e";
			Chatter chatter = new ChatterService().get(player);
			if (chatter == null)
				return line + "&eNone";
			Channel activeChannel = chatter.getActiveChannel();
			if (activeChannel == null)
				return line + "&eNone";
			if (activeChannel instanceof PrivateChannel)
				return line + "&b" + String.join(",", ((PrivateChannel) activeChannel).getOthersNames(chatter));
			if (activeChannel instanceof PublicChannel channel) {
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
			return "&3Pushing: &e" + Nameplates.get().getPushService().get(player).isEnabled();
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
			return "&3World: &e" + StringUtils.getWorldDisplayName(player.getLocation(), player.getWorld());
		}
	},

	BIOME {
		@Override
		public String render(Player player) {
			Location location = player.getLocation();
			return "&3Biome: &e" + camelCase(location.getWorld().getBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ()).name());
		}
	},

	LIGHT_LEVEL {
		@Override
		public String render(Player player) {
			return "&3Light Level: &e" + player.getLocation().getBlock().getLightLevel();
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
			ShopGroup shopGroup = ShopGroup.of(player);
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
			return "&3Vote Points: &e" + new VoterService().get(player).getPoints();
		}
	},

	EVENT_TOKENS {
		@Override
		public String render(Player player) {
			return "&3Event Tokens: &e" + new EventUserService().get(player).getTokens();
		}
	},

	@Interval(2)
	COMPASS {
		@Override
		public String render(Player player) {
			return StringUtils.compass(player, 8);
		}
	},

	@Interval(2)
	FACING {
		@Override
		public String render(Player player) {
			return "&3Facing: &e" + camelCase(player.getFacing()) + " (" + LocationUtils.getShortFacingDirection(player) + ")";
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
			return "&3Hours: &e" + TimespanBuilder.ofSeconds(hours.getTotal()).noneDisplay(true).format();
		}
	},

	@Interval(20)
	VOTE_PARTY {
		@Override
		public String render(Player player) {
			if (VoteParty.isCompleted())
				return "&3Vote Party: &eCompleted";
			return "&3Vote Party: &e%d&3/&e%d".formatted(VoteParty.getAmount(), VoteParty.getCurrentTarget());
		}

		@Override
		public boolean hasPermission(Player player) {
			return VoteParty.isFeatureEnabled(player);
		}
	},

	HELP {
		@Override
		public String render(Player player) {
			return "&c/sb help";
		}
	},

	@Interval(5)
	WORLD_TIME {
		@Override
		public String render(Player player) {
			GeoIP geoIP = new GeoIPService().get(player);
			int time = (int) player.getWorld().getTime();
			boolean is24HourFormat = geoIP.getTimeFormat() == GeoIP.TimeFormat.TWENTY_FOUR;
			return "&3World Time: &e" + Utils.minecraftTimeToHumanTime(time, is24HourFormat);
		}
	},

	@Permission(Group.ADMIN)
	SERVER_TIME {
		@Override
		public String render(Player player) {
			final GeoIP geoip = new GeoIPService().get(player);
			final LocalDateTime now = LocalDateTime.now();
			return "&3Server Time: &e" + now.format(ofPattern("MMM d ")) + geoip.getTimeFormat().formatShort(now);
		}
	},

	LOCAL_TIME {
		@Override
		public String render(Player player) {
			final GeoIP geoip = new GeoIPService().get(player);
			final ZonedDateTime now = geoip.getCurrentTime();
			return "&7" + now.format(ofPattern("MMM d ")) + geoip.getTimeFormat().formatShort(now);
		}
	},

	@Interval(20)
	@Required
	AFK {
		@Override
		public String render(Player player) {
			AFKUser afkUser = gg.projecteden.nexus.features.afk.AFK.get(player);
			if (afkUser.isAfk())
				return "&3AFK for: &e" + Timespan.of(afkUser.getTime()).format();
			return null;
		}
	},
	;

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
		final Rank rank = Rank.of(player);
		final boolean isStaff = rank.isStaff();
		return new HashMap<>() {{
			if (ScoreboardLine.ONLINE.hasPermission(player)) put(ScoreboardLine.ONLINE, true);
			if (ScoreboardLine.TICKETS.hasPermission(player)) put(ScoreboardLine.TICKETS, true);
			if (ScoreboardLine.TPS.hasPermission(player)) put(ScoreboardLine.TPS, true);
			if (ScoreboardLine.PING.hasPermission(player)) put(ScoreboardLine.PING, true);
			if (ScoreboardLine.CHANNEL.hasPermission(player)) put(ScoreboardLine.CHANNEL, true);
			if (ScoreboardLine.VANISHED.hasPermission(player)) put(ScoreboardLine.VANISHED, true);
			if (ScoreboardLine.MCMMO.hasPermission(player)) put(ScoreboardLine.MCMMO, !isStaff);
			if (ScoreboardLine.BALANCE.hasPermission(player)) put(ScoreboardLine.BALANCE, !isStaff);
			if (ScoreboardLine.VOTE_POINTS.hasPermission(player)) put(ScoreboardLine.VOTE_POINTS, !isStaff);
			if (ScoreboardLine.EVENT_TOKENS.hasPermission(player)) put(ScoreboardLine.EVENT_TOKENS, false);
			if (ScoreboardLine.GAMEMODE.hasPermission(player)) put(ScoreboardLine.GAMEMODE, true);
			if (ScoreboardLine.WORLD.hasPermission(player)) put(ScoreboardLine.WORLD, true);
			if (ScoreboardLine.BIOME.hasPermission(player)) put(ScoreboardLine.BIOME, false);
			if (ScoreboardLine.LIGHT_LEVEL.hasPermission(player)) put(ScoreboardLine.LIGHT_LEVEL, false);
			if (ScoreboardLine.COMPASS.hasPermission(player)) put(ScoreboardLine.COMPASS, true);
			if (ScoreboardLine.COORDINATES.hasPermission(player)) put(ScoreboardLine.COORDINATES, true);
			if (ScoreboardLine.HOURS.hasPermission(player)) put(ScoreboardLine.HOURS, true);
			if (ScoreboardLine.VOTE_PARTY.hasPermission(player)) put(ScoreboardLine.VOTE_PARTY, true);
			if (ScoreboardLine.HELP.hasPermission(player)) put(ScoreboardLine.HELP, !isStaff);
			if (ScoreboardLine.LOCAL_TIME.hasPermission(player)) put(ScoreboardLine.LOCAL_TIME, false);
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
			"&a⚘ &3Project Eden &a⚘",
			"&a⚘ &3Project Eden &a⚘",
			"&a⚘ &3Project Eden &a⚘",
			"&a⚘ &bProject Eden &a⚘",
			"&a⚘ &3Project Eden &a⚘",
			"&a⚘ &bProject Eden &a⚘",
			"&a⚘ &3Project Eden &a⚘",
			"&a⚘ &3Project Eden &a⚘",
			"&a⚘ &3Project Eden &a⚘",

			"&a⚘ &3&bP&3roject Eden &a⚘",
			"&a⚘ &3P&br&3oject Eden &a⚘",
			"&a⚘ &3Pr&bo&3ject Eden &a⚘",
			"&a⚘ &3Pro&bj&3ect Eden &a⚘",
			"&a⚘ &3Proj&be&3ct Eden &a⚘",
			"&a⚘ &3Proje&bc&3t Eden &a⚘",
			"&a⚘ &3Projec&bt&3 Eden &a⚘",
			"&a⚘ &3Project&b &3Eden &a⚘",
			"&a⚘ &3Project &bE&3den &a⚘",
			"&a⚘ &3Project E&bd&3en &a⚘",
			"&a⚘ &3Project Ed&be&3n &a⚘",
			"&a⚘ &3Project Ede&bn&3 &a⚘",
			"&a⚘ &3Project Ed&be&3n &a⚘",
			"&a⚘ &3Project E&bd&3en &a⚘",
			"&a⚘ &3Project &bE&3den &a⚘",
			"&a⚘ &3Project&b &3Eden &a⚘",
			"&a⚘ &3Projec&bt&3 Eden &a⚘",
			"&a⚘ &3Proje&bc&3t Eden &a⚘",
			"&a⚘ &3Proj&be&3ct Eden &a⚘",
			"&a⚘ &3Pro&bj&3ect Eden &a⚘",
			"&a⚘ &3Pr&bo&3ject Eden &a⚘",
			"&a⚘ &3P&br&3oject Eden &a⚘",

			"&a⚘ &3&bP&3roject Eden &a⚘",
			"&a⚘ &3P&br&3oject Eden &a⚘",
			"&a⚘ &3Pr&bo&3ject Eden &a⚘",
			"&a⚘ &3Pro&bj&3ect Eden &a⚘",
			"&a⚘ &3Proj&be&3ct Eden &a⚘",
			"&a⚘ &3Proje&bc&3t Eden &a⚘",
			"&a⚘ &3Projec&bt&3 Eden &a⚘",
			"&a⚘ &3Project&b &3Eden &a⚘",
			"&a⚘ &3Project &bE&3den &a⚘",
			"&a⚘ &3Project E&bd&3en &a⚘",
			"&a⚘ &3Project Ed&be&3n &a⚘",
			"&a⚘ &3Project Ede&bn&3 &a⚘",
			"&a⚘ &3Project Ed&be&3n &a⚘",
			"&a⚘ &3Project E&bd&3en &a⚘",
			"&a⚘ &3Project &bE&3den &a⚘",
			"&a⚘ &3Project&b &3Eden &a⚘",
			"&a⚘ &3Projec&bt&3 Eden &a⚘",
			"&a⚘ &3Proje&bc&3t Eden &a⚘",
			"&a⚘ &3Proj&be&3ct Eden &a⚘",
			"&a⚘ &3Pro&bj&3ect Eden &a⚘",
			"&a⚘ &3Pr&bo&3ject Eden &a⚘",
			"&a⚘ &3P&br&3oject Eden &a⚘",
			"&a⚘ &3&bP&3roject Eden &a⚘"
	);

}

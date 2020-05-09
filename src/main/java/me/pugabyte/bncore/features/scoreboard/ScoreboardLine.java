package me.pugabyte.bncore.features.scoreboard;

import com.gmail.nossr50.util.player.UserManager;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.commands.PushCommand;
import me.pugabyte.bncore.models.chat.Channel;
import me.pugabyte.bncore.models.chat.ChatService;
import me.pugabyte.bncore.models.chat.Chatter;
import me.pugabyte.bncore.models.chat.PrivateChannel;
import me.pugabyte.bncore.models.chat.PublicChannel;
import me.pugabyte.bncore.models.hours.Hours;
import me.pugabyte.bncore.models.hours.HoursService;
import me.pugabyte.bncore.models.ticket.TicketService;
import me.pugabyte.bncore.models.vote.VoteService;
import me.pugabyte.bncore.models.vote.Voter;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.DecimalFormat;

import static me.pugabyte.bncore.utils.StringUtils.left;
import static me.pugabyte.bncore.utils.StringUtils.timespanFormat;
import static me.pugabyte.bncore.utils.Utils.isVanished;

public enum ScoreboardLine {
	ONLINE {
		@Override
		String render(Player player) {
			long count = Bukkit.getOnlinePlayers().stream().filter(_player -> Utils.canSee(player, _player)).count();
			return "&3Online: &e" + count + " &3nerds";
		}
	},

	@Permission("group.moderator")
	TICKETS {
		@Override
		String render(Player player) {
			TicketService service = new TicketService();
			int open = service.getAllOpen().size();
			int all = service.getAll().size();
			return "&3Tickets: &" + (open == 0 ? "e" : "c") + open + " &3/ " + all;
		}
	},

	TPS {
		@Override
		String render(Player player) {
			double tps1m = Bukkit.getTPS()[0];
			return "&3TPS: &" + (tps1m > 19 ? "e" : "c") + new DecimalFormat("0.00").format(tps1m);
		}
	},

	@Permission("group.moderator")
	RAM {
		@Override
		String render(Player player) {
			long total = Runtime.getRuntime().totalMemory();
			long used = total - Runtime.getRuntime().freeMemory();
			double gb = Math.pow(1024, 3);
			return "&3RAM: &e" + new DecimalFormat("#.##").format(used / gb)
					+ "&3/" + new DecimalFormat("#.##").format(total / gb) + "gb";
		}
	},

	PING {
		@Override
		String render(Player player) {
			return "&3Ping: &e" + player.spigot().getPing() + "ms";
		}
	},

	CHANNEL {
		@Override
		String render(Player player) {
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

	@Permission("group.moderator")
	VANISHED {
		@Override
		String render(Player player) {
			return "&3Vanished: &e" + isVanished(player);
		}
	},

	MCMMO {
		@Override
		String render(Player player) {
			return "&3McMMO Level: &e" + UserManager.getPlayer(player).getPowerLevel();
		}
	},

	BALANCE {
		@Override
		String render(Player player) {
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

	VOTEPOINTS {
		@Override
		String render(Player player) {
			return "&3Vote Points: &e" + ((Voter) new VoteService().get(player)).getPoints();
		}
	},

	PUSHING {
		@Override
		String render(Player player) {
			return "&3Pushing: &e" + player.hasPermission(PushCommand.getPerm());
		}
	},

	GAMEMODE {
		@Override
		String render(Player player) {
			return "&3Mode: &e" + player.getGameMode().name().toLowerCase();
		}
	},

	WORLD {
		@Override
		String render(Player player) {
			return "&3World: &e" + player.getWorld().getName();
		}
	},

	COMPASS {
		@Override
		String render(Player player) {
			return "null";
		}
	},

	COORDINATES {
		@Override
		String render(Player player) {
			Location location = player.getLocation();
			return "&e" + (int) location.getX() + " " + (int) location.getY() + " " + (int) location.getZ();
		}
	},

	HOURS {
		@Override
		String render(Player player) {
			Hours hours = new HoursService().get(player);
			return "&3Hours: &e" + timespanFormat(hours.getTotal(), "None");
		}
	};

	abstract String render(Player player);

	@Target({ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	protected @interface Permission {
		String value();
	}
}

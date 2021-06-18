package me.pugabyte.nexus.features.achievements.listeners;

import me.pugabyte.nexus.features.achievements.events.social.DiscordLinkEvent;
import me.pugabyte.nexus.features.achievements.events.social.poof.PoofToEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import me.pugabyte.nexus.models.achievement.Achievement;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class SocialListener implements Listener {

	static {
		Tasks.repeat(300, 300, SocialListener::check);
	}

	@EventHandler
	public void onPoofTo(PoofToEvent event) {
		Player player = event.getInitiator();

		Achievement.FOUND_A_FRIEND.check(player);
		Achievement.MR_PERIOD_POPULAR.check(player, event.getAcceptor().getUniqueId().toString());
	}

	@EventHandler
	public void onDiscordLink(DiscordLinkEvent event) {
		Achievement.ONE_OF_US.check(event.getPlayer());
	}

	@EventHandler
	public void onMinigameJoin(MatchJoinEvent event) {
		Player player = event.getMinigamer().getPlayer();
		LocalDateTime now = LocalDateTime.now();

		if (now.getDayOfWeek() == DayOfWeek.SATURDAY)
			if (now.getHour() == 16 || now.getHour() == 17) {
				Achievement.NOOB.check(player);
				Achievement.PRO_GAMER.check(player, now.getYear() + "-" + now.getMonth().getValue() + "-" + now.getDayOfMonth());
			}
	}

	// TODO Rely on event?
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String[] args = event.getMessage().toLowerCase().split(" ");

		if (args.length > 1)
			if (args[0].equals("/ch") || args[0].equals("/channel") || args[0].equals("/chat"))
				if (args[1].matches("g|global|l|local|m|minigames"))
					Achievement.CHANNEL_ALIKE.check(player);
	}

	private static void check() {
		for (Player player : PlayerUtils.getOnlinePlayers()) {
			checkJoinDate(player);
			checkHours(player);
			checkRank(player);
			checkSession(player);
		}
	}

	private static void checkJoinDate(Player player) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(player.getFirstPlayed()), ZoneId.systemDefault());
		LocalDateTime yearAgo = now.minusYears(1);
		if (date.isBefore(yearAgo))
			Achievement.ONE_YEAR_STRONG.check(player);
	}

	private static void checkHours(Player player) {
		Achievement.CREATE_THE_TALE_OF_TIME.check(player);
	}

	private static void checkRank(Player player) {
		if (Rank.of(player) != Rank.GUEST)
			Achievement.MEMBER_OF_SOCIETY.check(player);
	}

	private static void checkSession(Player player) {
		Nerd nerd = Nerd.of(player);
		long login = nerd.getLastJoin().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		long now = System.currentTimeMillis();

		if ((now - login) >= (60 * 60 * 12 * 1000))
			// Odd behavior occurs when new players leave too quickly
			if ((now - login) <= (60 * 60 * 13 * 1000))
				Achievement.SLEEPLESS_NIGHT.check(player);
	}

}

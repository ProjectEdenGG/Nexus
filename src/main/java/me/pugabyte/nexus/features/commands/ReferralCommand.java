package me.pugabyte.nexus.features.commands;

import com.google.common.base.Strings;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.menus.BookBuilder.WrittenBookMenu;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.HideFromHelp;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.cooldown.CooldownService;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.referral.Referral;
import me.pugabyte.nexus.models.referral.Referral.Origin;
import me.pugabyte.nexus.models.referral.ReferralService;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Time;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.Utils.sortByValueReverse;

@NoArgsConstructor
public class ReferralCommand extends CustomCommand implements Listener {
	private final ReferralService service = new ReferralService();
	private Referral referral;

	public ReferralCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayer())
			referral = service.get(player());
	}

	@Path
	void run() {
		JsonBuilder json = json();
		for (Referral.Origin origin : Referral.Origin.values())
			json.next("&3" + origin.getDisplay())
					.hover("&e" + origin.getLink())
					.command("/referral choose " + origin.name().toLowerCase())
					.group()
					.newline();

		new WrittenBookMenu().addPage(json).open(player());
	}

	@HideFromHelp
	@TabCompleteIgnore
	@Path("choose [origin]")
	void choose(Origin origin) {
		referral.setOrigin(origin);
		service.save(referral);
		if (origin == Origin.OTHER) {

// In case this ever works in the future
//			new EditableBookMenu()
//					.addPage(json("Tell use more:").newline())
//					.onSign(meta -> {
//						referral.setExtra(meta.getPage(0));
//						service.save(referral);
//					})
//					.open(player());

			Nexus.getSignMenuFactory().blank()
					.prefix(PREFIX)
					.response(lines -> {
						List<String> list = new ArrayList<>(Arrays.asList(lines));
						list.removeIf(Strings::isNullOrEmpty);
						referral.setExtra(String.join(" ", list));
						service.save(referral);
						send(PREFIX + "Thank you for your feedback!");
					})
					.open(player());
		} else
			send(PREFIX + "Thank you for your feedback!");
	}

	@Path("debug [player]")
	void debug(@Arg("self") Referral referral) {
		send(toPrettyString(referral));
	}

	@Async
	@Path("extraInputs")
	void others() {
		List<Referral> referrals = service.<Referral>getAll().stream()
				.filter(_referral -> !isNullOrEmpty(_referral.getExtra()))
				.collect(Collectors.toList());

		if (referrals.isEmpty())
			error("No referrals with extra content found");

		send(PREFIX + "Extra input: ");
		for (Referral _referral : referrals)
			send(" &e" + _referral.getName() + " &7" + _referral.getExtra());
	}

	@Async
	@Path("stats")
	void stats() {
		List<Referral> referrals = service.getAll();
		if (referrals.isEmpty())
			error("No referral stats available");

		Map<Origin, Integer> manuals = new HashMap<>();
		Map<String, Integer> ips = new HashMap<>();
		for (Referral referral : referrals) {
			if (referral.getOrigin() != null)
				manuals.put(referral.getOrigin(), manuals.getOrDefault(referral.getOrigin(), 0) + 1);
			if (referral.getIp() != null)
				ips.put(referral.getIp(), ips.getOrDefault(referral.getIp(), 0) + 1);
		}

		line();
		send(PREFIX + "Stats:");
		send(" &3Manual input:");
		sortByValueReverse(manuals).forEach((origin, count) -> send("&7  " + count + " - &e" + origin.getDisplay()));
		line();
		send(" &3IPs:");
		sortByValueReverse(ips).forEach((ip, count) -> send("&7  " + count + " - &e" + ip));
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Tasks.waitAsync(Time.MINUTE, () -> {
			Referral referral = new ReferralService().get(event.getPlayer());
			if (referral.getOrigin() == null) {
				Nerd nerd = Nerd.of(event.getPlayer());
				if (nerd.getFirstJoin().isAfter(LocalDateTime.now().minusHours(2))) {
					Tasks.sync(() -> {
						if (!event.getPlayer().isOnline())
							return;

						if (new CooldownService().check(event.getPlayer(), "referralAsk", Time.MINUTE.x(5))) {
							send(event.getPlayer(), json().newline()
									.next("&e&lHey there! &3Could you quickly tell us where you found this server? &eClick here!")
									.command("/referral")
									.newline());
						}
					});
				}
			}
		});
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		ReferralService service = new ReferralService();
		Referral referral = service.get(event.getPlayer());

		String hostname = event.getHostname();
		if (hostname.contains(":"))
			hostname = hostname.split(":")[0];
		if (hostname.endsWith("."))
			hostname = hostname.substring(0, hostname.length() - 1);
		if (hostname.equalsIgnoreCase("server.bnn.gg"))
			hostname = "bnn.gg";

		referral.setIp(hostname);
		service.save(referral);
	}

}

package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Location;

import java.text.DecimalFormat;

public class CalcSpeedCommand extends CustomCommand {
	private Location[] locations = new Location[4];

	public CalcSpeedCommand(CommandEvent event) {
		super(event);
	}

	@Path("[startDelay]")
	@Description("Calculate your movement speed")
	void speed(@Arg("3") int startDelay) {
		line();
		Tasks.Countdown.builder()
				.duration(startDelay * 20L)
				.onSecond(i -> send("&3Starting in &e" + i + "&3..."))
				.onComplete(() -> {
					send("&eCalculating...");
					Tasks.Countdown.builder()
							.duration(15)
							.doZero(true)
							.onTick(i -> {
								if (i % 5 == 0)
									locations[(int) (i / 5)] = location();
							})
							.onComplete(this::calculate)
							.start();
				})
				.start();
	}

	double dist1, dist2, dist3, bps, bph, kph, mph;

	void calculate() {
		dist1 = Distance.distance(locations[0], locations[1]).getRealDistance();
		dist2 = Distance.distance(locations[1], locations[2]).getRealDistance();
		dist3 = Distance.distance(locations[2], locations[3]).getRealDistance();

		bps = ((dist1 + dist2 + dist3) / 3) * 4;
		bph = bps * 3600;
		kph = bps * 3.6;
		mph = kph * .621371;

		DecimalFormat nf = new DecimalFormat("#.00");

		send("&3" +
				"BPS: &e" + nf.format(bps) + "&3 | " +
				"BPH: &e" + nf.format(bph) + "&3 | " +
				"KPH: &e" + nf.format(kph) + "&3 | " +
				"MPH: &e" + nf.format(mph));
	}

}

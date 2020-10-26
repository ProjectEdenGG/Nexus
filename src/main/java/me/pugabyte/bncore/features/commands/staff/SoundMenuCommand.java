package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.features.menus.BookBuilder;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.JsonBuilder;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Permission("group.staff")
public class SoundMenuCommand extends CustomCommand {

	private static final List<UUID> playToOthers = new ArrayList<>();
	private static final Map<UUID, Double> pitchMap = new HashMap<>();
	private static final Map<UUID, Integer> pageMap = new HashMap<>();

	public SoundMenuCommand(CommandEvent event) {
		super(event);
	}

	@Path("[integer]")
	public void bookMenu(Integer page) {
		BookBuilder.WrittenBookMenu builder = new BookBuilder.WrittenBookMenu();
		JsonBuilder json = new JsonBuilder();

		if (page == null)
			page = pageMap.getOrDefault(player().getUniqueId(), 1);
		else {
			if (page < 1)
				page = 88;
			else if (page > 88)
				page = 1;
			pageMap.put(player().getUniqueId(), page);
		}

		int index = 3;
		int soundIndex = ((page - 1) * 11) + 1;

		String playTo = playToOthers.contains(player().getUniqueId()) ? "[Others]" : "[You]";

		json.next("&3Play To: &6&l" + playTo)
				.hover("&eClick to toggle")
				.command("/soundmenu togglePlayTo")
				.group().newline();
		json.next("&3Pitch: ").group()
				.next(getPitchJson(player(), 0.1))
				.hover("&eChange pitch to .1")
				.command("/soundmenu changePitch 0.1")
				.group().next(" ").group()
				.next(getPitchJson(player(), 1.0))
				.hover("&eChange pitch to 1")
				.command("/soundmenu changePitch 1.0")
				.group().next(" ").group()
				.next(getPitchJson(player(), 2.0))
				.hover("&eChange pitch to 2")
				.command("/soundmenu changePitch 2.0")
				.group().newline();

		int sounds = 0;
		for (Sound value : Sound.values()) {
			String sound = value.name().toLowerCase();
			if (sound.contains("music"))
				continue;

			if (++sounds < soundIndex)
				continue;

			String soundDots = sound.replaceAll("_", ".");
			String soundCommas = soundDots.replace(".", "ͺ");
			if (soundCommas.length() > 20)
				soundCommas = soundCommas.substring(0, 19);

			json.next(soundCommas)
					.hover("&eClick to play " + soundDots)
					.command("/soundmenu play " + sound)
					.group().newline();

			if (++index > 13) {
				json.next("&0 &0 &0 &0 &3<--- ")
						.hover("&eBack")
						.command("/soundmenu " + (page - 1))
						.group()
						.next("&0 &0 &9&l" + page + "&0 &0 ")
						.group()
						.next(" &3--->")
						.hover("&eNext")
						.command("/soundmenu " + (page + 1))
						.group().newline();
				break;
			}
		}

		if (index <= 13) {
			json.newline()
					.next("&0 &0 &0 &0 &3<--- ")
					.hover("&eBack")
					.command("/soundmenu " + (page - 1))
					.group()
					.next("&0 &0 &9&l" + page + "&0 &0 ")
					.group()
					.next(" &3--->")
					.hover("&eNext")
					.command("/soundmenu " + (page + 1))
					.group();
		}

		builder.addPage(json).open(player());
	}

	@Path("play <sound>")
	public void playSound(String string) {
		Location loc = player().getLocation();
		float pitch = (float) getPitchDouble(player());
		Sound sound = getSound(string);

		if (sound == null) {
			error("Couldn't find sound: " + string);
			return; // I know this is unnecessary
		}

		if (playToOthers.contains(player().getUniqueId()))
			loc.getWorld().playSound(loc, sound, 1F, pitch);
		else
			player().playSound(loc, sound, 1F, pitch);

		bookMenu(pageMap.get(player().getUniqueId()));
	}

	@Path("togglePlayTo")
	public void togglePlayTo() {
		if (playToOthers.contains(player().getUniqueId()))
			playToOthers.remove(player().getUniqueId());
		else
			playToOthers.add(player().getUniqueId());

		bookMenu(pageMap.get(player().getUniqueId()));
	}

	@Path("changePitch <number>")
	public void changePitch(Double number) {
		pitchMap.put(player().getUniqueId(), number);
		bookMenu(pageMap.get(player().getUniqueId()));
	}

	private String getPitchJson(Player player, double number) {
		double pitch = pitchMap.getOrDefault(player.getUniqueId(), 1.0);

		String numberFormat;
		if (number == 0.1)
			numberFormat = ".1";
		else if (number == 2.0)
			numberFormat = "2";
		else
			numberFormat = "1";

		if (pitch == number) {
			return "&2&l[" + numberFormat + "&2&l]";
		}
		return "&c&l[" + numberFormat + "&c&l]";
	}

	private double getPitchDouble(Player player) {
		return pitchMap.getOrDefault(player.getUniqueId(), 1.0);
	}

	private Sound getSound(String value) {
		for (Sound sound : Sound.values()) {
			if (sound.name().equalsIgnoreCase(value))
				return sound;
		}
		return null;
	}
}

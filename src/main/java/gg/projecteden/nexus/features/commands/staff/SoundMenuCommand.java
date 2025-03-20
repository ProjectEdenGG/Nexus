package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.features.menus.BookBuilder;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.JsonBuilder;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"removal", "UnstableApiUsage"})
@Permission(Group.STAFF)
public class SoundMenuCommand extends CustomCommand {

	private static final List<UUID> playToOthers = new ArrayList<>();
	private static final Map<UUID, Double> pitchMap = new HashMap<>();
	private static final Map<UUID, Integer> pageMap = new HashMap<>();

	public SoundMenuCommand(CommandEvent event) {
		super(event);
	}

	private int getMaxPages() {
		int soundCount = 0;
		for (Sound value : Sound.values()) {
			String sound = value.name().toLowerCase();
			if (sound.contains("music"))
				continue;

			soundCount++;
		}

		return (int) Math.ceil(soundCount / 11.0);
	}

	@Path("[integer]")
	@Description("Open the sound menu")
	public void bookMenu(Integer page) {
		BookBuilder.WrittenBookMenu builder = new BookBuilder.WrittenBookMenu();
		JsonBuilder json = new JsonBuilder();

		if (page == null)
			page = pageMap.getOrDefault(uuid(), 1);
		else {
			if (page < 1)
				page = getMaxPages();
			else if (page > getMaxPages())
				page = 1;
			pageMap.put(uuid(), page);
		}

		int index = 3;
		int soundIndex = ((page - 1) * 11) + 1;

		String playTo = playToOthers.contains(uuid()) ? "[Others]" : "[You]";

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
	@Description("Play a sound")
	public void playSound(String string) {
		Location loc = location();
		float pitch = (float) getPitchDouble(player());
		Sound sound = getSound(string);

		if (sound == null)
			error("Couldn't find sound: " + string);

		if (playToOthers.contains(uuid()))
			loc.getWorld().playSound(loc, sound, 1F, pitch);
		else
			player().playSound(loc, sound, 1F, pitch);

		bookMenu(pageMap.get(uuid()));
	}

	@Path("togglePlayTo")
	@Description("Toggle playing the sounds to other players")
	public void togglePlayTo() {
		if (playToOthers.contains(uuid()))
			playToOthers.remove(uuid());
		else
			playToOthers.add(uuid());

		bookMenu(pageMap.get(uuid()));
	}

	@Path("changePitch <number>")
	@Description("Update the pitch at which sounds play at")
	public void changePitch(Double number) {
		pitchMap.put(uuid(), number);
		bookMenu(pageMap.get(uuid()));
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

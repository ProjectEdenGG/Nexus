package me.pugabyte.bncore.features.holidays.halloween20.models;

import me.pugabyte.bncore.models.halloween20.Halloween20Service;
import me.pugabyte.bncore.models.halloween20.Halloween20User;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static me.pugabyte.bncore.utils.StringUtils.camelCase;

public enum QuestNPC {

	JEFFERY(3066) {
		@Override
		public List<String> getScript(Player player) {
			Halloween20Service service = new Halloween20Service();
			Halloween20User user = service.get(player);
			switch (user.getLostPumpkinsStage()) {
				case NOT_STARTED:
					user.setLostPumpkinsStage(QuestStage.LostPumpkins.STARTED);
					service.save(user);
					return Arrays.asList(
							"Oh no.. What am I going to tell everyone. I can’t tell them that I have misplaced their pumpkins!",
							"I leave for 5 minutes and somebody stole them! Who would do such a thing?",
							"Do you think you could look for them for me? I could give you a nice reward if you find them."
					);
				case STARTED:
					return Arrays.asList(
							"I see you've found " + user.getFoundPumpkins().size() + " of my pumpkins.",
							"If you can help me find all 8, I'll give you a reward"
					);
				case FOUND_ALL:
					user.setLostPumpkinsStage(QuestStage.LostPumpkins.COMPLETE);
					service.save(user);
					return Arrays.asList(
							"You found all my pumpkins! Thank you so much.",
							"For all your work, I'll give you this.",
							"I heard it works at spawn on some weird box. See what it gives you!"
					);
				case COMPLETE:
					return Arrays.asList(

					);
			}
			return new ArrayList<>();
		}
	};

	int npcId;

	QuestNPC(int id) {
		npcId = id;
	}

	public static QuestNPC getByID(int id) {
		for (QuestNPC value : QuestNPC.values())
			if (value.npcId == id) return value;
		return null;
	}

	public void sendScript(Player player) {
		if (this.getScript(player) == null) return;
		AtomicReference<String> npcName = new AtomicReference<>("");

		AtomicInteger wait = new AtomicInteger(0);
		this.getScript(player).forEach(line -> {
			npcName.set(camelCase(name().replaceAll("_", " ")));
			npcName.set(npcName.get().replaceAll("[0-9]+", ""));
			if (line.toLowerCase().matches("^wait \\d+$"))
				wait.getAndAdd(Integer.parseInt(line.toLowerCase().replace("wait ", "")));
			else {
				line = line.replaceAll("<player>", player.getName());
				if (line.contains("<self>")) {
					npcName.set("&b&lYOU&f");
					line = line.replaceAll("<self> ", "");
				}
				String message = "&3" + npcName.get() + " &7> &f" + line;
				Tasks.wait(wait.get(), () -> {
					Utils.send(player, message);
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1F, 1F);
				});
			}
		});
	}

	public abstract List<String> getScript(Player player);

}

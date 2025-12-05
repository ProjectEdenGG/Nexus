package gg.projecteden.nexus.features.events.y2025.pugmas25.features;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25QuestProgress;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Waystone;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25NPC;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25Quest;
import gg.projecteden.nexus.models.pugmas25.Pugmas25ConfigService;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.models.quests.Quester;
import gg.projecteden.nexus.models.quests.QuesterService;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class Pugmas25Particles {
	private final Pugmas25UserService userService = new Pugmas25UserService();
	private final QuesterService questerService = new QuesterService();

	private final Location TREE_RECORD_PLAYER = Pugmas25.get().location(-683.5, 119.5, -3116.5);

	private final ParticleBuilder DEFAULT_PARTICLES = new ParticleBuilder(Particle.HAPPY_VILLAGER)
		.offset(0.25, 0.25, 0.25)
		.count(10);

	public Pugmas25Particles() {
		Tasks.repeat(0, TickTime.SECOND.x(2), () -> {
			treeRecordPlayer();

			Pugmas25.get().getOnlinePlayers().forEach(player -> {
				realQuests(player);
				fakeQuests(player);
				nutcrackers(player);
				mayor(player);
			});
		});

		Tasks.repeat(0, 1, () -> {
			Pugmas25.get().getOnlinePlayers().forEach(player -> {
				waystones(player);
			});
		});
	}

	private void treeRecordPlayer() {
		new ParticleBuilder(Particle.NOTE)
			.location(TREE_RECORD_PLAYER)
			.offset(0.2, 0.2, 0.2)
			.count(RandomUtils.randomInt(2, 5))
			.spawn();
	}

	private void mayor(Player player) {
		var mayor = Pugmas25NPC.MAYOR;
		NPC npc = CitizensUtils.getNPC(mayor.getNpcId());
		if (npc == null)
			return;

		DEFAULT_PARTICLES.clone()
			.receivers(player)
			.location(npc.getStoredLocation().toCenterLocation())
			.spawn();
	}

	private void nutcrackers(Player player) {
		var nutcrackers = new Pugmas25ConfigService().get0().getNutCrackerLocations();
		var user = userService.get(player);
		var nutCrackersLeft = new HashSet<>(nutcrackers);

		nutCrackersLeft.removeAll(user.getFoundNutCrackers());

		for (Location nutCracker : nutCrackersLeft)
			DEFAULT_PARTICLES.clone()
				.receivers(player)
				.location(nutCracker.toCenterLocation())
				.spawn();
	}

	private void fakeQuests(Player player) {
		for (Pugmas25QuestProgress questProgress : Pugmas25QuestProgress.values()) {
			Pugmas25NPC pugmas25NPC = questProgress.getNpc();
			if (pugmas25NPC == null)
				continue;

			NPC npc = CitizensUtils.getNPC(pugmas25NPC.getNpcId());
			if (npc == null)
				continue;

			DEFAULT_PARTICLES.clone()
				.receivers(player)
				.location(npc.getStoredLocation().toCenterLocation())
				.spawn();
		}
	}

	private void realQuests(Player player) {
		Quester quester = questerService.get(player);
		for (Pugmas25Quest pugmas25Quest : Pugmas25Quest.values()) {
			var quest = quester.getQuest(pugmas25Quest);
			if (quest == null || quest.isComplete())
				continue;

			var interactable = quest.getCurrentTaskStep().getInteractable();
			if (!(interactable instanceof Pugmas25NPC pugmas25NPC))
				continue;

			NPC npc = CitizensUtils.getNPC(pugmas25NPC.getNpcId());
			if (npc == null)
				continue;

			DEFAULT_PARTICLES.clone()
				.receivers(player)
				.location(npc.getStoredLocation().toCenterLocation())
				.spawn();
		}
	}

	private void waystones(Player player) {
		for (Pugmas25Waystone waystone : Pugmas25Waystone.values()) {
			Location location = waystone.getFrameLoc().clone().add(0, 1, 0).toCenterLocation();
			new ParticleBuilder(Particle.ENCHANT)
				.receivers(player)
				.count(2)
				.location(location)
				.spawn();
		}
	}
}

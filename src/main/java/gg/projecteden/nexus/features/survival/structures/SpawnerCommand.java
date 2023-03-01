package gg.projecteden.nexus.features.survival.structures;

import gg.projecteden.nexus.features.survival.structures.models.Spawner;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry.Wrapper;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;

import java.util.List;

@Permission(Group.ADMIN)
public class SpawnerCommand extends CustomCommand {

	public SpawnerCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("info")
	void spawner() {
		Block block = getTargetBlockRequired();

		SpawnerBlockEntity spawnerBlock = Spawner.getNMSSpawner(block);
		if (spawnerBlock == null)
			error("Target block is not a spawner");

		BaseSpawner baseSpawner = spawnerBlock.getSpawner();

		SimpleWeightedRandomList<SpawnData> potentialsWeighted = baseSpawner.spawnPotentials;
		List<Wrapper<SpawnData>> potentials = potentialsWeighted.unwrap();
		send(potentials.size() + "");
		for (Wrapper<SpawnData> spawnDataWrapper : potentials) {
			send("- " + spawnDataWrapper.getData());
		}

		SpawnData spawnData = baseSpawner.nextSpawnData;
		send(spawnData.toString());
	}

	@Path("test <entityType>")
	void set(EntityType entityType) {
		Block block = getTargetBlockRequired();
		SpawnerBlockEntity spawnerBlock = Spawner.getNMSSpawner(block);
		if (spawnerBlock == null)
			error("Target block is not a spawner");

		Spawner.createSpawnData(block, entityType);
	}
}

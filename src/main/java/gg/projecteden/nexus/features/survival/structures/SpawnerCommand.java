package gg.projecteden.nexus.features.survival.structures;

import gg.projecteden.nexus.features.survival.structures.models.Spawner;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.NMSUtils;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry.Wrapper;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.SpawnData.CustomSpawnRules;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.Optional;

@HideFromWiki // TODO
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

	@Path("lightLevels [min] [max]")
	void lightLevels(
		@Arg(value = "0", min = 0, max = 15) int min,
		@Arg(value = "15", min = 0, max = 15) int max
	) {
		if (min > max)
			error("Min cannot be greater than max");

		Block block = getTargetBlockRequired();
		BaseSpawner spawner = Spawner.getBaseSpawner(block);

		BlockPos pos = NMSUtils.toNMS(block.getLocation());
		ServerLevel world = NMSUtils.toNMS(block.getLocation().getWorld());

		InclusiveRange<Integer> allLightLevels = new InclusiveRange<>(min, max);
		SpawnData spawnData = new SpawnData(spawner.nextSpawnData.getEntityToSpawn(), Optional.of(new CustomSpawnRules(allLightLevels, allLightLevels)));

		spawner.setNextSpawnData(world, pos, spawnData);
	}
}

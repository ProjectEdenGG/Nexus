package gg.projecteden.nexus.features.minigames.models.mechanics.custom.monstermaze;

import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftMob;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class MonsterMazePathfinder implements com.destroystokyo.paper.entity.Pathfinder {
    private final net.minecraft.world.entity.Mob entity;

    public MonsterMazePathfinder(Mob entity) {
        this.entity = ((CraftMob) entity).getHandle();
    }

    @Override
    public @NotNull Mob getEntity() {
        return (Mob) entity.getBukkitEntity();
    }

    @Override
    public void stopPathfinding() {
        entity.getNavigation().stop();
    }

    @Override
    public boolean hasPath() {
        return entity.getNavigation().getPath() != null;
    }

    @Nullable
    @Override
    public PathResult getCurrentPath() {
        Path path = entity.getNavigation().getPath();
        return path != null ? new MonsterMazePathResult(path) : null;
    }

    @Nullable
    @Override
    public PathResult findPath(@NotNull Location loc) {
        Validate.notNull(loc, "Location can not be null");
        Path path = entity.getNavigation().createPath(loc.getX(), loc.getY(), loc.getZ(), 0);
        return path != null ? new MonsterMazePathResult(path) : null;
    }

    @Nullable
    @Override
    public PathResult findPath(@NotNull LivingEntity target) {
        Validate.notNull(target, "Target can not be null");
        Path path = entity.getNavigation().createPath(((CraftLivingEntity) target).getHandle(), 0);
        return path != null ? new MonsterMazePathResult(path) : null;
    }

    @Override
    public boolean moveTo(@Nonnull PathResult path, double speed) {
        Validate.notNull(path, "PathResult can not be null");
        Path pathEntity = ((MonsterMazePathResult) path).path;
        return entity.getNavigation().moveTo(pathEntity, speed);
    }

    @Override
    public boolean canOpenDoors() {
        return entity.getNavigation().pathFinder.nodeEvaluator.canOpenDoors();
    }

    @Override
    public void setCanOpenDoors(boolean canOpenDoors) {
        entity.getNavigation().pathFinder.nodeEvaluator.setCanOpenDoors(canOpenDoors);
    }

    @Override
    public boolean canPassDoors() {
        return entity.getNavigation().pathFinder.nodeEvaluator.canPassDoors();
    }

    @Override
    public void setCanPassDoors(boolean canPassDoors) {
        entity.getNavigation().pathFinder.nodeEvaluator.setCanPassDoors(canPassDoors);
    }

    @Override
    public boolean canFloat() {
        return entity.getNavigation().pathFinder.nodeEvaluator.canFloat();
    }

    @Override
    public void setCanFloat(boolean canFloat) {
        entity.getNavigation().pathFinder.nodeEvaluator.setCanFloat(canFloat);
    }

    public class MonsterMazePathResult implements com.destroystokyo.paper.entity.PaperPathfinder.PathResult {
        private final Path path;

        MonsterMazePathResult(Path path) {
            this.path = path;
        }

        @Nullable
        @Override
        public Location getFinalPoint() {
            Node point = path.getEndNode();
            return point != null ? toLoc(point) : null;
        }

		@Override
		public boolean canReachFinalPoint() {
			return true;
		}

		@Override
        public @NotNull List<Location> getPoints() {
            return new ArrayList<>() {{
				for (Node point : path.nodes)
					add(toLoc(point));
			}};
        }

        @Override
        public int getNextPointIndex() {
            return path.getNextNodeIndex();
        }

        @Nullable
        @Override
        public Location getNextPoint() {
            if (path.isDone())
                return null;

            return toLoc(path.nodes.get(path.getNextNodeIndex()));
        }
    }

    private Location toLoc(Node point) {
        return new Location(entity.level().getWorld(), point.x, point.y, point.z).toCenterLocation();
    }

}

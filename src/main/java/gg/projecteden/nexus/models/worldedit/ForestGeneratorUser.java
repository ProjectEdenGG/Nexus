package gg.projecteden.nexus.models.worldedit;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.worldedit.ForestGeneratorConfig.TreeList;
import gg.projecteden.nexus.models.worldedit.ForestGeneratorConfig.TreeList.Tree;
import lombok.*;
import org.bukkit.Location;

import java.util.UUID;

@Data
@Entity(value = "forest_generator_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class ForestGeneratorUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private TreeList treeList;
	private boolean selecting;
	private Location viewOrigin;

	public void add(Tree tree) {
		getTreeList().getTrees().add(tree);
	}

	public TreeList getTreeList() {
		if (!Nullables.isNullOrEmpty(treeList.getId())) {
			final TreeList original = new ForestGeneratorConfigService().get0().getTreeList(treeList.getId());
			if (original != null)
				treeList = original;
		}

		return treeList;
	}

}

package gg.projecteden.nexus.features.resourcepack.decoration.events;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.utils.Nullables;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class DecorationDestroyEvent extends DecorationEvent {
	List<ItemStack> drops = new ArrayList<>();
	boolean ignoreLocked = false;

	public DecorationDestroyEvent(Player player, Decoration decoration) {
		super(player, decoration);
		ItemStack item = decoration.getItemDrop(player);
		if (Nullables.isNotNullOrAir(item))
			this.drops = new ArrayList<>(List.of(item));
	}

}

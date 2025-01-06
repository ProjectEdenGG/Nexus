package gg.projecteden.nexus.features.minigames.menus.custom;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.mechanics.UncivilEngineers;
import gg.projecteden.nexus.features.minigames.menus.ArenaMenu;
import gg.projecteden.nexus.features.minigames.menus.MechanicsMenu;
import gg.projecteden.nexus.features.minigames.menus.annotations.CustomMechanicSettings;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.arenas.UncivilEngineersArena;
import gg.projecteden.nexus.features.minigames.models.arenas.UncivilEngineersArena.MobPoint;
import gg.projecteden.nexus.features.mobheads.MobHeadType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@CustomMechanicSettings(UncivilEngineers.class)
public class UncivilEngineersMenu extends ICustomMechanicMenu {
	private final UncivilEngineersArena arena;

	public UncivilEngineersMenu(Arena arena) {
		this.arena = ArenaManager.convert(arena, UncivilEngineersArena.class);
	}

	@Override
	public void init() {
		addBackItem(e -> new ArenaMenu(arena).open(viewer));

		contents.set(1, 0, ClickableItem.of(new ItemBuilder(Material.ZOMBIE_SPAWN_EGG).name("&eMob Points"), e -> new MobPointsMenu().open(viewer)));
	}

	@Title("Mob Points")
	public class MobPointsMenu extends ICustomMechanicMenu {

		@Override
		public void init() {
			addBackItem(e -> MechanicsMenu.openCustomSettingsMenu(viewer, arena));

			contents.set(0, 4, ClickableItem.of(Material.EMERALD_BLOCK, "&aAdd Mob Point", e -> new AddMobPointMenu().open(viewer)));

			List<ClickableItem> items = new ArrayList<>();

			for (MobPoint mobPoint : arena.getMobPoints()) {
				final MobHeadType mobHeadType = MobHeadType.of(mobPoint.getType());
				ItemStack skull;
				if (mobHeadType == null)
					skull = new ItemStack(Material.BARRIER);
				else {
					skull = mobHeadType.getBaseSkull();
					if (Nullables.isNullOrAir(skull))
						skull = new ItemStack(Material.BARRIER);
				}

				final ItemBuilder item = new ItemBuilder(skull)
					.name("&3" + StringUtils.camelCase(mobPoint.getType()))
					.lore(MenuUtils.getLocationLore(mobPoint.getLocation()))
					.lore("", "&7Click to remove");

				items.add(ClickableItem.of(item, e -> {
					arena.getMobPoints().remove(mobPoint);
					arena.write();
					new MobPointsMenu().open(viewer);
				}));
			}

			paginate(items);
		}

	}

	@Title("Add Mob Point")
	public class AddMobPointMenu extends InventoryProvider {

		@Override
		public void init() {
			addBackItem(e -> MechanicsMenu.openCustomSettingsMenu(viewer, arena));

			List<ClickableItem> items = new ArrayList<>();

			for (EntityType type : EntityType.values()) {
				if (type.getEntityClass() == null)
					continue;
				if (!LivingEntity.class.isAssignableFrom(type.getEntityClass()))
					continue;

				final MobHeadType mobHeadType = MobHeadType.of(type);
				if (mobHeadType == null)
					continue;
				final ItemStack skull = mobHeadType.getNamedSkull();
				if (Nullables.isNullOrAir(skull))
					continue;

				final ItemStack item = new ItemBuilder(skull).name("&e" + StringUtils.camelCase(type)).build();

				items.add(ClickableItem.of(item, e -> {
					arena.getMobPoints().add(new MobPoint(viewer.getLocation(), type));
					arena.write();
					new MobPointsMenu().open(viewer);
				}));
			}

			paginate(items);
		}

	}

}

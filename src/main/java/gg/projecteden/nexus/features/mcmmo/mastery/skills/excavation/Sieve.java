package gg.projecteden.nexus.features.mcmmo.mastery.skills.excavation;

import gg.projecteden.nexus.features.crates.gemcrafter.GemCommand;
import gg.projecteden.nexus.features.equipment.skins.ArmorSkin;
import gg.projecteden.nexus.features.equipment.skins.ToolSkin;
import gg.projecteden.nexus.features.equipment.stattrack.StatTrack;
import gg.projecteden.nexus.features.mcmmo.mastery.models.ProcessingMachine;
import gg.projecteden.nexus.features.recipes.functionals.Proportionator;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.features.workbenches.dyestation.DyeStation;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sieve extends ProcessingMachine {

	public Sieve() {
		super("Sieve", ItemModelType.SIEVE);
	}

	@Override
	public String getProcessingIconBaseModel() {
		return "ui/gui/mcmmo/machines/sieve/";
	}

	@Override
	public Map<List<Material>, Map<ItemStack, Integer>> getConversions() {
		return Map.of(
			List.of(Material.SAND, Material.RED_SAND, Material.SOUL_SAND), new HashMap<>() {{
				put(new ItemStack(Material.GOLD_NUGGET), 200);
				put(new ItemStack(Material.IRON_NUGGET), 200);
				put(new ItemStack(Material.COPPER_NUGGET), 200);
				put(new ItemStack(Material.QUARTZ), 100);
				put(new ItemStack(Material.ENDER_PEARL), 25);
				put(new ItemStack(Material.DIAMOND), 20);
				put(DyeStation.getMagicMineral().build(), 10);
				put(GemCommand.makeGem(Enchantment.EFFICIENCY, 1), 4);
				put(new ItemStack(Material.NETHERITE_SCRAP), 4);
				put(ArmorSkin.SCULK.getTemplate(), 1);
				put(ItemStack.empty(), 6876);
				}},
			List.of(Material.DIRT, Material.PODZOL, Material.GRASS_BLOCK, Material.MYCELIUM), new HashMap<>() {{
				put(new ItemStack(Material.GOLD_NUGGET), 200);
				put(new ItemStack(Material.QUARTZ), 200);
				put(new ItemStack(Material.IRON_NUGGET), 200);
				put(new ItemStack(Material.SLIME_BALL), 40);
				put(new ItemStack(Material.AMETHYST_SHARD), 30);
				put(new ItemStack(Material.ENDER_PEARL), 25);
				put(new ItemStack(Material.DIAMOND), 20);
				put(DyeStation.getMagicStain().build(), 10);
				put(GemCommand.makeGem(Enchantment.EFFICIENCY), 4);
				put(new ItemStack(Material.NETHERITE_SCRAP), 4);
				put(ToolSkin.SCULK.getTemplate(), 1);
				put(ItemStack.empty(), 6606);
			}},
				List.of(Material.GRAVEL, Material.CLAY), new HashMap<>() {{
				put(new ItemStack(Material.GOLD_NUGGET), 200);
				put(new ItemStack(Material.QUARTZ), 200);
				put(new ItemStack(Material.COPPER_NUGGET), 200);
				put(new ItemStack(Material.RAW_IRON), 40);
				put(new ItemStack(Material.PRISMARINE_SHARD), 35);
				put(new ItemStack(Material.AMETHYST_SHARD), 30);
				put(new ItemStack(Material.EMERALD), 20);
				put(DyeStation.getMagicDye().build(), 10);
				put(Proportionator.FUEL.build(), 8);
				put(GemCommand.makeGem(Enchantment.EFFICIENCY), 4);
				put(new ItemStack(Material.NETHERITE_SCRAP), 4);
				put(StatTrack.getTemplate(), 1);
				put(ItemStack.empty(), 6768);
			}}
		);
	}
}

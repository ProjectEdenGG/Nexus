package gg.projecteden.nexus.features.mcmmo.mastery.skills.mining;

import gg.projecteden.nexus.features.crates.gemcrafter.GemCommand;
import gg.projecteden.nexus.features.equipment.skins.ArmorSkin;
import gg.projecteden.nexus.features.equipment.skins.ToolSkin;
import gg.projecteden.nexus.features.equipment.stattrack.StatTrack;
import gg.projecteden.nexus.features.mcmmo.mastery.models.ProcessingMachine;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.features.workbenches.dyestation.DyeStation;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RockCrusher extends ProcessingMachine {

	public RockCrusher() {
		super("Rock Crusher", ItemModelType.ROCK_CRUSHER);
	}

	@Override
	public String getProcessingIconBaseModel() {
		return "ui/gui/mcmmo/machines/rock_crusher/";
	}

	@Override
	public Map<List<Material>, Map<ItemStack, Integer>> getConversions() {
		return Map.of(
			List.of(Material.COBBLESTONE, Material.STONE, Material.COBBLED_DEEPSLATE, Material.DEEPSLATE, Material.ANDESITE, Material.DIORITE, Material.CALCITE, Material.GRANITE, Material.TUFF),
			new HashMap<>() {{
				put(new ItemStack(Material.COAL), 390);
				put(new ItemStack(Material.QUARTZ), 300);
				put(new ItemStack(Material.RAW_IRON), 300);
				put(new ItemStack(Material.RAW_GOLD), 300);
				put(new ItemStack(Material.RAW_COPPER), 300);
				put(new ItemStack(Material.LAPIS_LAZULI), 135);
				put(new ItemStack(Material.REDSTONE), 120);
				put(new ItemStack(Material.AMETHYST_SHARD), 105);
				put(new ItemStack(Material.EMERALD), 90);
				put(new ItemStack(Material.DIAMOND), 60);
				put(new ItemStack(Material.EXPERIENCE_BOTTLE), 36);
				put(new ItemStack(Material.NETHERITE_SCRAP), 12);
				put(GemCommand.makeGem(Enchantment.EFFICIENCY), 12);
				put(DyeStation.getMagicStain().build(), 7);
				put(DyeStation.getMagicDye().build(), 7);
				put(DyeStation.getMagicMineral().build(), 7);
				put(ArmorSkin.AMETHYST.getTemplate(), 1);
				put(ToolSkin.AMETHYST.getTemplate(), 1);
				put(StatTrack.getTemplate(), 1);
				put(ItemStack.empty(), 19926);
			}}
		);
	}
}

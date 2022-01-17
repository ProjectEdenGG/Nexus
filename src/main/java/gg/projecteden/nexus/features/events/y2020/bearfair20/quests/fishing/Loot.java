package gg.projecteden.nexus.features.events.y2020.bearfair20.quests.fishing;

import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.BFQuests.itemLore;

public class Loot {
	// Default fish
	public static ItemStack cod = new ItemBuilder(Material.COD).lore(itemLore).build();
	public static ItemStack salmon = new ItemBuilder(Material.SALMON).lore(itemLore).build();
	public static ItemStack tropicalFish = new ItemBuilder(Material.TROPICAL_FISH).lore(itemLore).build();
	public static ItemStack pufferfish = new ItemBuilder(Material.PUFFERFISH).lore(itemLore).build();
	// Generic Fish
	public static ItemStack bullhead = new ItemBuilder(Material.COOKED_SALMON).name("Bullhead").lore(itemLore).build();
	public static ItemStack sturgeon = new ItemBuilder(Material.COD).name("Sturgeon").lore(itemLore).build();
	public static ItemStack woodskip = new ItemBuilder(Material.COD).name("Woodskip").lore(itemLore).build();
	public static ItemStack voidSalmon = new ItemBuilder(Material.SALMON).name("Void Salmon").lore(itemLore).build();
	public static ItemStack redSnapper = new ItemBuilder(Material.SALMON).name("Red Snapper").lore(itemLore).build();
	public static ItemStack redMullet = new ItemBuilder(Material.SALMON).name("Red Mullet").lore(itemLore).build();
	// Island Fish
	public static ItemStack tigerTrout = new ItemBuilder(Material.COOKED_SALMON).name("Tiger Trout").lore(itemLore).glow().build();
	public static ItemStack glacierfish = new ItemBuilder(Material.COD).name("Glacierfish").lore(itemLore).glow().build();
	public static ItemStack crimsonfish = new ItemBuilder(Material.SALMON).name("Crimsonfish").lore(itemLore).glow().build();
	public static ItemStack flathead = new ItemBuilder(Material.COOKED_SALMON).name("Flathead").lore(itemLore).glow().build();
	public static ItemStack midnightCarp = new ItemBuilder(Material.TROPICAL_FISH).name("Midnight Carp").glow().lore(itemLore).build();
	public static ItemStack sunfish = new ItemBuilder(Material.TROPICAL_FISH).name("Sunfish").glow().lore(itemLore).build();
	public static ItemStack seaCucumber = new ItemBuilder(Material.SEA_PICKLE).name("Sea Cucumber").lore(itemLore).build();
	// Treasure
	public static ItemStack brainCoral = new ItemBuilder(Material.BRAIN_CORAL).lore(itemLore).build();
	public static ItemStack hornCoral = new ItemBuilder(Material.HORN_CORAL).lore(itemLore).build();
	public static ItemStack tubeCoral = new ItemBuilder(Material.TUBE_CORAL).lore(itemLore).build();
	public static ItemStack fireCoral = new ItemBuilder(Material.FIRE_CORAL).lore(itemLore).build();
	public static ItemStack bubbleCoral = new ItemBuilder(Material.BUBBLE_CORAL).lore(itemLore).build();
	public static ItemStack scales = new ItemBuilder(Material.PHANTOM_MEMBRANE).name("Scales").lore(itemLore).build();
	public static ItemStack heartOfTheSea = new ItemBuilder(Material.HEART_OF_THE_SEA).lore(itemLore).build();
	public static ItemStack nautilusShell = new ItemBuilder(Material.NAUTILUS_SHELL).lore(itemLore).build();
	// Trash
	public static ItemStack oldBoots = new ItemBuilder(Material.LEATHER_BOOTS).name("Old Boots").lore(itemLore).build();
	public static ItemStack rustySpoon = new ItemBuilder(Material.WOODEN_SHOVEL).name("Rusty Spoon").lore(itemLore).build();
	public static ItemStack brokenCD = new ItemBuilder(Material.MUSIC_DISC_11).name("Broken CD").lore(itemLore).build();
	public static ItemStack lostBook = new ItemBuilder(Material.BOOK).name("Lost Book").lore(itemLore).build();
	public static ItemStack soggyNewsPaper = new ItemBuilder(Material.PAPER).name("Soggy Newspaper").lore(itemLore).build();
	public static ItemStack driftwood = new ItemBuilder(Material.STICK).name("Driftwood").lore(itemLore).build();
	public static ItemStack seaweed = new ItemBuilder(Material.KELP).name("Seaweed").lore(itemLore).build();

	// Lists
	public static List<ItemStack> defaultFish = Arrays.asList(cod, salmon, tropicalFish, pufferfish);
	public static List<ItemStack> islandFish = Arrays.asList(tigerTrout, glacierfish, crimsonfish, flathead, midnightCarp, sunfish, seaCucumber);
	public static List<ItemStack> genericFish = Arrays.asList(bullhead, sturgeon, woodskip, voidSalmon, redSnapper, redMullet);
	//
	public static List<ItemStack> fish = new ArrayList<>();
	public static List<ItemStack> loot = new ArrayList<>();
	public static List<ItemStack> treasure = Arrays.asList(brainCoral, hornCoral, tubeCoral, fireCoral, bubbleCoral, scales, heartOfTheSea, nautilusShell);
	public static List<ItemStack> trash = Arrays.asList(oldBoots, rustySpoon, brokenCD, lostBook, soggyNewsPaper, driftwood, seaweed);
	//
	public static List<ItemStack> coral = Arrays.asList(brainCoral, hornCoral, tubeCoral, fireCoral, bubbleCoral);

	public Loot() {
		fish.addAll(defaultFish);
		fish.addAll(islandFish);
		fish.addAll(genericFish);

		loot.addAll(fish);
		loot.addAll(treasure);
		loot.addAll(trash);
	}
}

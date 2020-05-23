package me.pugabyte.bncore.features.holidays.bearfair20.models;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Ingredient {
	@NonNull
	private ItemStack itemStack;
	@NonNull
	private char character;

	public Ingredient(String character, ItemStack ingredient) {
		this.character = character.charAt(0);
		this.itemStack = ingredient;
	}

	public Ingredient(char character, ItemStack ingredient) {
		this.character = character;
		this.itemStack = ingredient;
	}

	public Ingredient(char character, Material material) {
		this.character = character;
		this.itemStack = new ItemStack(material);
	}

	public Ingredient(String character, Material material) {
		this.character = character.charAt(0);
		this.itemStack = new ItemStack(material);
	}

	public Ingredient ingredient(ItemStack ingredient) {
		this.itemStack = ingredient;
		return this;
	}

	public Ingredient character(char character) {
		this.character = character;
		return this;
	}

	public Ingredient character(String character) {
		this.character = character.charAt(0);
		return this;
	}

	public char getCharacter() {
		return character;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}
}

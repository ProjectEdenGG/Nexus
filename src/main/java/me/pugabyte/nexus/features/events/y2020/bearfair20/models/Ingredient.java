package me.pugabyte.nexus.features.events.y2020.bearfair20.models;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Ingredient {
	@NonNull
	private ItemStack itemStack;
	private char character;

	public Ingredient(String character, @NotNull ItemStack ingredient) {
		this.character = character.charAt(0);
		this.itemStack = ingredient;
	}

	public Ingredient(char character, @NotNull ItemStack ingredient) {
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

	@NotNull
	public ItemStack getItemStack() {
		return itemStack;
	}
}

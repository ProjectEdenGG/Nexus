package gg.projecteden.nexus.features.menus.itemeditor;

public enum ItemEditMenu {
	MAIN(3),
	EDIT_NAME(1),
	EDIT_LORE(1),
	VANILLA_OR_CUSTOM(1),
	VANILLA_ADD_REMOVE(1),
	VANILLA_ADD(4),
	VANILLA_REMOVE(4),
	CUSTOM_ADD_REMOVE(1),
	CUSTOM_ADD(4),
	CUSTOM_REMOVE(4);

	private int size = 0;

	ItemEditMenu(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}

}

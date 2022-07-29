package fr.itrooz.datafinder.datasample;

import fr.test.Player;

public class Item {

	public Player p = null;
	private ItemType type;
	private int amount;

	public Item(ItemType type, int amount) {
		this.type = type;
		this.amount = amount;
	}

	public ItemType getType() {
		return type;
	}

	public void setType(ItemType type) {
		this.type = type;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
}

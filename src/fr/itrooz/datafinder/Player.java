package fr.itrooz.datafinder;

import fr.test.data.Item;

public class Player {
	private Item firstHand;

	public Player(Item firstHand) {
		this.firstHand = firstHand;
	}

	public Item getFirstHand() {
		return firstHand;
	}
}

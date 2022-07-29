package fr.itrooz.datafinder;

import fr.test.data.Item;
import fr.test.data.ItemType;

public class Main {
	public static void main(String[] args) {

		Item firstHand = new Item(ItemType.DIRT, 2);

		Player player = new Player(firstHand);

		firstHand.p = player;

		String s = new DataFinder().setCheckFields(false).setCheckGetters(true).find(player, ItemType.DIRT);
		System.out.println(s);
	}
}

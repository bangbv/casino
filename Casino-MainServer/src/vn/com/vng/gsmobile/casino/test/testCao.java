package vn.com.vng.gsmobile.casino.test;

import java.util.Arrays;
import vn.com.vng.gsmobile.casino.entries.bala.CaoCard;
import vn.com.vng.gsmobile.casino.entries.bala.CaoHand;
import vn.com.vng.gsmobile.casino.flatbuffers.CardID;

public class testCao {
	public static void main(String[] agrs){
	
		CaoHand c1 = new CaoHand(Arrays.asList(
				new CaoCard(CardID.Card_10_B),
				new CaoCard(CardID.Card_2_A),
				new CaoCard(CardID.Card_2_B)
			));
		CaoHand c2 = new CaoHand(Arrays.asList(
				new CaoCard(CardID.Card_K_A),
				new CaoCard(CardID.Card_J_D),
				new CaoCard(CardID.Card_Q_A)
			));
		System.out.println(c1);
		System.out.println(c2);
		System.out.println(c1.compareTo(c2));
	}
}

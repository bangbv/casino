package vn.com.vng.gsmobile.casino.entries;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import vn.com.vng.gsmobile.casino.flatbuffers.CardID;

public class Deck {
	protected List<Card> cards = Arrays.asList(
			new Card(CardID.Card_3_A), new Card(CardID.Card_3_B), new Card(CardID.Card_3_C), new Card(CardID.Card_3_D),
			new Card(CardID.Card_4_A), new Card(CardID.Card_4_B), new Card(CardID.Card_4_C), new Card(CardID.Card_4_D),
			new Card(CardID.Card_5_A), new Card(CardID.Card_5_B), new Card(CardID.Card_5_C), new Card(CardID.Card_5_D),
			new Card(CardID.Card_6_A), new Card(CardID.Card_6_B), new Card(CardID.Card_6_C), new Card(CardID.Card_6_D),
			new Card(CardID.Card_7_A), new Card(CardID.Card_7_B), new Card(CardID.Card_7_C), new Card(CardID.Card_7_D),
			new Card(CardID.Card_8_A), new Card(CardID.Card_8_B), new Card(CardID.Card_8_C), new Card(CardID.Card_8_D),
			new Card(CardID.Card_9_A), new Card(CardID.Card_9_B), new Card(CardID.Card_9_C), new Card(CardID.Card_9_D),
			new Card(CardID.Card_10_A), new Card(CardID.Card_10_B), new Card(CardID.Card_10_C), new Card(CardID.Card_10_D),
			new Card(CardID.Card_J_A), new Card(CardID.Card_J_B), new Card(CardID.Card_J_C), new Card(CardID.Card_J_D),
			new Card(CardID.Card_Q_A), new Card(CardID.Card_Q_B), new Card(CardID.Card_Q_C), new Card(CardID.Card_Q_D),
			new Card(CardID.Card_K_A), new Card(CardID.Card_K_B), new Card(CardID.Card_K_C), new Card(CardID.Card_K_D),
			new Card(CardID.Card_A_A), new Card(CardID.Card_A_B), new Card(CardID.Card_A_C), new Card(CardID.Card_A_D),
			new Card(CardID.Card_2_A), new Card(CardID.Card_2_B), new Card(CardID.Card_2_C), new Card(CardID.Card_2_D)
	);
	public List<Card> dealing(Byte...bytes){
		boolean isFake = false;
		if(bytes.length>0){
			List<Card> fake = FakeCards.get(bytes[0]);
			if(fake!=null){
				cards = fake;
				isFake = true;
			}
		}
		if(!isFake){
			//trộn bài
			for(int i=0; i < 200; i++)
				Collections.shuffle(cards);
		}
		return cards;
	}
	public List<Card> getCards() {
		return cards;
	}
}

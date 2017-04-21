package vn.com.vng.gsmobile.casino.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import vn.com.vng.gsmobile.casino.entries.Card;
import vn.com.vng.gsmobile.casino.entries.tlmn.TLMNHand;
import vn.com.vng.gsmobile.casino.entries.tlmn.TLMNCard;
import vn.com.vng.gsmobile.casino.flatbuffers.CardID;

public class testTLMN {
	public static void main(String[] agrs){
		int len = 3;
		int player_idx = 2;
		
		TLMNHand c1 = new TLMNHand(Arrays.asList(
				new TLMNCard(CardID.Card_3_C),
				new TLMNCard(CardID.Card_3_B),
				new TLMNCard(CardID.Card_4_D),
				new TLMNCard(CardID.Card_4_C),
				new TLMNCard(CardID.Card_5_D),
				new TLMNCard(CardID.Card_5_B),
				new TLMNCard(CardID.Card_6_C),
				new TLMNCard(CardID.Card_6_D),
				new TLMNCard(CardID.Card_5_C),
				new TLMNCard(CardID.Card_5_A),
				new TLMNCard(CardID.Card_6_A),
				new TLMNCard(CardID.Card_6_B)
//				new TLMNCard(CardID.Card_J_D)
			), true, true, len - (len+0-player_idx)%len);
		TLMNHand c2 = new TLMNHand(Arrays.asList(
				new TLMNCard(CardID.Card_3_A),
				new TLMNCard(CardID.Card_3_D),
				new TLMNCard(CardID.Card_4_C),
				new TLMNCard(CardID.Card_4_B),
				new TLMNCard(CardID.Card_5_A),
				new TLMNCard(CardID.Card_5_C),
//				new TLMNCard(CardID.Card_3_C),
				new TLMNCard(CardID.Card_4_D),
				new TLMNCard(CardID.Card_4_A),
				new TLMNCard(CardID.Card_5_B),
				new TLMNCard(CardID.Card_6_A),
				new TLMNCard(CardID.Card_5_D),
				new TLMNCard(CardID.Card_6_B)
			), true, true, len - (len+1-player_idx)%len);
		System.out.println(c1);
		System.out.println(c2);
		System.out.println(c1.compareTo(c2));
		
		
		System.out.println(c1.getFinishPenanceScore(false));
		System.out.println(c2.getFinishPenanceScore(false));
//		
//		List<TLMNCard> l = new ArrayList<>();
//		l.add(new TLMNCard(CardID.Card_2_C));
//		l.add(new TLMNCard(CardID.Card_4_C));
//		l.add(new TLMNCard(CardID.Card_6_A));
//		l.add(new TLMNCard(CardID.Card_5_A));
//		Collections.sort(l);
//		System.out.println(l);
//		Collections.sort(l, new Comparator<TLMNCard>() {
//
//			@Override
//			public int compare(TLMNCard o1, TLMNCard o2) {
//				// TODO Auto-generated method stub
//				return o2.compareTo(o1);
//			}
//		});
//		System.out.println(l);
	}
}

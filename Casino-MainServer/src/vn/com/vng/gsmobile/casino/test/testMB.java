package vn.com.vng.gsmobile.casino.test;

import java.util.ArrayList;
import java.util.List;
import vn.com.vng.gsmobile.casino.entries.maubinh.MBHand;
import vn.com.vng.gsmobile.casino.entries.maubinh.MBCard;
import vn.com.vng.gsmobile.casino.flatbuffers.CardID;

public class testMB {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<MBCard> l = new ArrayList<MBCard>() {{
		    add(new MBCard(CardID.Card_10_B));
		    add(new MBCard(CardID.Card_5_B));
		    add(new MBCard(CardID.Card_8_B));
		    add(new MBCard(CardID.Card_9_A));
		    add(new MBCard(CardID.Card_8_A));
		    
		    add(new MBCard(CardID.Card_2_A));
		    add(new MBCard(CardID.Card_K_A));
		    add(new MBCard(CardID.Card_A_A));
		    add(new MBCard(CardID.Card_7_A));
		    add(new MBCard(CardID.Card_7_B));
		    
		    add(new MBCard(CardID.Card_J_C));
		    add(new MBCard(CardID.Card_10_C));
		    add(new MBCard(CardID.Card_5_A));
		}};
		MBHand m = new MBHand(l, (byte)1);
		System.out.println(m);
		List<MBCard> l2 = new ArrayList<MBCard>() {{
		    add(new MBCard(CardID.Card_9_A));
		    add(new MBCard(CardID.Card_10_A));
		    add(new MBCard(CardID.Card_J_A));
		    add(new MBCard(CardID.Card_Q_A));
		    add(new MBCard(CardID.Card_K_A));
		    
		    add(new MBCard(CardID.Card_2_A));
		    add(new MBCard(CardID.Card_A_A));
		    add(new MBCard(CardID.Card_4_A));
		    add(new MBCard(CardID.Card_3_A));
		    add(new MBCard(CardID.Card_5_A));
		    
		    add(new MBCard(CardID.Card_6_A));
		    add(new MBCard(CardID.Card_7_A));
		    add(new MBCard(CardID.Card_8_A));
		}};
		MBHand m2 = new MBHand(l2, (byte)0);
		System.out.println(m2);
		
		System.out.println(m.compareTo(m2));
	}
}

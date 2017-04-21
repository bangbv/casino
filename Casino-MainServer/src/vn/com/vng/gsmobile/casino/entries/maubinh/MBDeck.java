package vn.com.vng.gsmobile.casino.entries.maubinh;

import java.util.List;
import vn.com.vng.gsmobile.casino.entries.Card;
import vn.com.vng.gsmobile.casino.entries.Deck;
import vn.com.vng.gsmobile.casino.flatbuffers.GameType;

public class MBDeck extends Deck {
	public List<Card> dealing(){
		return super.dealing(GameType.MAUBINH);
	}
}

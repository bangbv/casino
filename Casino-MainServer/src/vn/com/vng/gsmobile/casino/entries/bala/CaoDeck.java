package vn.com.vng.gsmobile.casino.entries.bala;

import java.util.List;
import vn.com.vng.gsmobile.casino.entries.Card;
import vn.com.vng.gsmobile.casino.entries.Deck;
import vn.com.vng.gsmobile.casino.flatbuffers.GameType;

public class CaoDeck extends Deck {
	public List<Card> dealing(){
		return super.dealing(GameType.BALA);
	}
}

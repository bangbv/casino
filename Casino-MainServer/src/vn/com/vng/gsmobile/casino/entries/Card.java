package vn.com.vng.gsmobile.casino.entries;

import vn.com.vng.gsmobile.casino.flatbuffers.CardClass;
import vn.com.vng.gsmobile.casino.flatbuffers.CardID;
import vn.com.vng.gsmobile.casino.flatbuffers.CardValue;

public class Card {
	public byte Index = -1;
	public byte Id = CardID.Card_None;
	public byte Value = CardValue.Card_None;
	public byte Class = CardClass.Card_None;
	public byte Color = CardColor.NONE;
	public Card(int c){
		if(c != CardID.Card_None){
			this.Id = (byte)c;
			this.Value = (byte) ((c - CardID.Card_3_A)/4);
			this.Class = (byte) ((c-1)%4);
			this.Color = (byte) (this.Class/2);
		}
	}
	public Card(int c, int idx){
		if(c != CardID.Card_None){
			this.Id = (byte)c;
			this.Value = (byte) ((c - CardID.Card_3_A)/4);
			this.Class = (byte) ((c-1)%4);
			this.Color = (byte) (this.Class/2);
			this.Index = (byte) idx;
		}
	}
	@Override
	public String toString(){
		return CardID.name(Id);
	}
	
	public static byte getCardValue(byte cardId){
		return (byte) ((cardId - CardID.Card_3_A)/4);
	}
	public static byte getCardColor(byte cardId){
		return (byte) ((byte) ((cardId-1)%4)/2);
	}
	public static byte getCardClass(byte cardId){
		return (byte) ((cardId-1)%4);
	}
}
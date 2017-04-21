package vn.com.vng.gsmobile.casino.entries.maubinh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.com.vng.gsmobile.casino.entries.Card;
import vn.com.vng.gsmobile.casino.flatbuffers.MauBinhType;
import vn.com.vng.gsmobile.casino.flatbuffers.PlayerMBCardInfo;

public class MBHand implements Comparable<MBHand>  {
	public static final String CARDS = "Cards";
	public static final String HAND_TYPE = "HandType";
	public static final String CHI = "Chi";
	public static final String SCORE = "Score";
	public static final String COLOR_SCORE = "ColorScore";
	public static final String CLASS_SCORE = "ClassScore";
	public static final String HOST = "Host";
	private List<MBCard> oCards = null;
	private byte host = 0;
	private String score = "";
	private Integer colorScore = 0;
	private Integer classScore = 0;
	private List<MBChi> chi = null;
	private byte handType = MauBinhType.None;
	
	private int playerIdx = -1;
	@Override
	public int compareTo(MBHand o) {
		// TODO Auto-generated method stub
		return this.score.compareTo(o.score);
	}
	
	@Override
	public String toString(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(CARDS, oCards);
		map.put(CHI, chi);
		map.put(HAND_TYPE, MauBinhType.name(handType));
		map.put(HOST, host);
		map.put(SCORE, score);
		return map.toString();
	}
	
	public void setPlayerIdx(int playerIdx){
		this.playerIdx = playerIdx;
	}
	public int getPlayerIdx(){
		return this.playerIdx;
	}
	public List<MBCard> getCards(){
		return this.oCards;
	}
	public MBChi getChi(byte chiNo) {
		if(chiNo==HandType.NONE||chiNo==HandType.TOTAL)
			return null;
		return chi.get(chiNo-1);
	}	
	public byte getType() {
		return handType;
	}
	public byte isHost() {
		return host;
	}
	public boolean isBinhLung(){
		return handType == MauBinhType.BinhLung;
	}
	public boolean isMauBinh(){
		return MBRule.mauBinhListOrderDesc.contains(handType);
	}
	public boolean isMauBinh3Sanh(){
		return handType == MauBinhType.BaSanh;
	}
	public boolean isMauBinh3Thung(){
		return handType == MauBinhType.BaThung;
	}
	public boolean isBinhThuong(){
		return handType == MauBinhType.Normal;
	}
	public void release() {
		// TODO Auto-generated method stub
		oCards = null;
	}
	public MBHand(PlayerMBCardInfo oData){
		this.oCards = new ArrayList<>();
		for(int j = 0; j < oData.cardsLength(); j++)
			this.oCards.add(new MBCard(oData.cards(j)));
		this.chi = Arrays.asList(
				new MBChi(new ArrayList<>(oCards.subList(0, 5)), host, HandType.CHI1),
				new MBChi(new ArrayList<>(oCards.subList(5, 10)), host, HandType.CHI2),
				new MBChi(new ArrayList<>(oCards.subList(10, 13)), host, HandType.CHI3)
		);
		this.buildScore();
	}
	public MBHand(PlayerMBCardInfo oData, byte host){
		this.host = host;
		this.oCards = new ArrayList<>();
		for(int j = 0; j < oData.cardsLength(); j++)
			this.oCards.add(new MBCard(oData.cards(j)));
		this.chi = Arrays.asList(
				new MBChi(new ArrayList<>(oCards.subList(0, 5)), host, HandType.CHI1),
				new MBChi(new ArrayList<>(oCards.subList(5, 10)), host, HandType.CHI2),
				new MBChi(new ArrayList<>(oCards.subList(10, 13)), host, HandType.CHI3)
		);
		this.buildScore();
	}
	public MBHand(List<MBCard> oData, byte host){
		this.host = host;
		this.oCards = oData;
		this.chi = Arrays.asList(
				new MBChi(new ArrayList<>(oCards.subList(0, 5)), host, HandType.CHI1),
				new MBChi(new ArrayList<>(oCards.subList(5, 10)), host, HandType.CHI2),
				new MBChi(new ArrayList<>(oCards.subList(10, 13)), host, HandType.CHI3)
		);
		this.buildScore();
	}
	
	public MBHand(byte[] oData, byte host, byte hand){
		this.host = host;
		this.oCards = new ArrayList<>();
		for(byte c : oData)
			this.oCards.add(new MBCard(c));
		this.chi = Arrays.asList(
				new MBChi(new ArrayList<>(oCards.subList(0, 5)), host, HandType.CHI1),
				new MBChi(new ArrayList<>(oCards.subList(5, 10)), host, HandType.CHI2),
				new MBChi(new ArrayList<>(oCards.subList(10, 13)), host, HandType.CHI3)
		);
		this.buildScore();
	}
	
	@SuppressWarnings("unchecked")
	private void buildScore(){
		//1. Sort theo thứ tự giảm dần của từng lá bài
		Collections.sort(this.oCards, new Comparator<MBCard>() {

			@Override
			public int compare(MBCard o1, MBCard o2) {
				// TODO Auto-generated method stub
				return o2.compareTo(o1);
			}
		});
	
		//2. Kiểm tra loại binh
		String keyClass = "";
		for(Card c : oCards){
			this.colorScore += c.Color;
			keyClass += String.format(MBRule.PATTERN_CARD_CLASS, c.Class);
		}
		this.classScore = MBRule.getClassScore(keyClass);
		//2.1 Kiểm tra Mậụ Binh
		this.handType = MauBinhType.None;
		List<MBCard> boList = new ArrayList<>();
		List<MBCard> racList = new ArrayList<>(this.oCards);
		List<?> lSanh = detectSanh();
		List<?> lSam = detectSam();
		List<?> lDoi = detectDoi();
		for(byte b : MBRule.mauBinhListOrderDesc){
			if(handType!=MauBinhType.None)
				break;
			switch(b){
			case MauBinhType.RongCuon:
				if((int) lSanh.get(0)==13 && this.classScore > 0)
					handType = MauBinhType.RongCuon;
				break;
			case MauBinhType.SanhRong:
				if((int) lSanh.get(0)==13)
					handType = MauBinhType.SanhRong;
				break;
			case MauBinhType.DongHoa1:
				if(this.colorScore%13==0)
					handType = MauBinhType.DongHoa1;
				break;
			case MauBinhType.DongHoa2:
				if(this.colorScore==1 || this.colorScore==12)
					handType = MauBinhType.DongHoa2;
				break;
			case MauBinhType.BonSamCo:
				if((int)lSam.get(0)==4){
					handType = MauBinhType.BonSamCo;
					boList.addAll((List<MBCard>)lSam.get(1));
				}
				break;
			case MauBinhType.NamDoiMotSam:
				if((int)lSam.get(0)==1 && (int)lDoi.get(0)==6){
					handType = MauBinhType.NamDoiMotSam;
					List<MBCard> s = (List<MBCard>) lSam.get(1);
					List<MBCard> d = (List<MBCard>) lDoi.get(1);
					d.removeAll(s);
					boList.addAll(s);
					boList.addAll(d);
				}
				break;
			case MauBinhType.LucPheBon:
				if((int)lSam.get(0)==0 && (int)lDoi.get(0)==6){
					handType = MauBinhType.LucPheBon;
					boList.addAll((List<MBCard>)lDoi.get(1));
				}
				break;
			case MauBinhType.BaThung:
				if(getChi(HandType.CHI1).isThung() 
				&& getChi(HandType.CHI2).isThung() 
				&& getChi(HandType.CHI3).isThung())
					handType = MauBinhType.BaThung;
				break;
			case MauBinhType.BaSanh:
				if(getChi(HandType.CHI1).isSanh() 
				&& getChi(HandType.CHI2).isSanh() 
				&& getChi(HandType.CHI3).isSanh())
					handType = MauBinhType.BaSanh;				
				break;				
			}
		}
		//3.2 Kiểm tra Binh Lủng - Binh thường
		if(handType==MauBinhType.None){
			if(getChi(HandType.CHI1).compareTo(getChi(HandType.CHI2))>=0 
			&& getChi(HandType.CHI2).compareTo(getChi(HandType.CHI3))>=0)
				handType = MauBinhType.Normal;
			else
				handType = MauBinhType.BinhLung;
		}
		//4. Tính điểm
		int mainScore = MBRule.getMainScore(this.handType);
		String bonusScore = "";
		if(isMauBinh()){
			List<Object> valueList = new ArrayList<>();
			if(handType==MauBinhType.BaSanh || handType==MauBinhType.BaThung)
				valueList.addAll(Arrays.asList(getChi(HandType.CHI1).getScore(), getChi(HandType.CHI2).getScore(), getChi(HandType.CHI3).getScore()));
			else{
				for(MBCard c : boList)
					valueList.add(c.Value);
				racList.removeAll(boList);
				for(MBCard c : racList)
					valueList.add(c.Value);
			}
			bonusScore = MBRule.getBonusScore(this.handType, valueList.toArray()) + String.format(MBRule.PATTERN_CARD, host);
		}
		this.score = String.format(MBRule.PATTERN_MAIN_SCORE, mainScore) + bonusScore;
		//5. Sort lại theo điểm binh (bộ -> mậu thầu)
		this.oCards.clear();
		this.oCards.addAll(boList);
		this.oCards.addAll(racList);
	}

	private List<?> detectDoi(){
		int cnt = 0;
		List<MBCard> l = new ArrayList<>();
		for(int i = 0;  i < oCards.size()-1;){
			if(oCards.get(i).Value == oCards.get(i+1).Value){
				cnt++;
				l.add(oCards.get(i));
				l.add(oCards.get(i+1));
				i += 2;
			}
			else
				i++;
		}
		return Arrays.asList(cnt, l);
	}
	private List<?> detectSam(){
		int cnt = 0;
		List<MBCard> l = new ArrayList<>();
		for(int i = 0;  i < oCards.size()-2;){
			if(oCards.get(i).Value == oCards.get(i+1).Value && oCards.get(i).Value == oCards.get(i+2).Value){
				cnt++;
				l.add(oCards.get(i));
				l.add(oCards.get(i+1));
				l.add(oCards.get(i+2));
				i += 3;
			}
			else
				i++;
		}
		return Arrays.asList(cnt, l);
	}
	private List<?> detectSanh(){
		int cnt = 0;
		List<MBCard> l = new ArrayList<>();
		List<Object> k = new ArrayList<>();
		for(MBCard c : this.oCards)
			k.add(c.Value);
		cnt = MBRule.getSanhType(k.toArray());
		if(cnt>0)
			l.addAll(this.oCards);
		return Arrays.asList(cnt, l);
	}
}

package vn.com.vng.gsmobile.casino.entries.tlmn;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import vn.com.vng.gsmobile.casino.flatbuffers.CardClass;
import vn.com.vng.gsmobile.casino.flatbuffers.CardID;
import vn.com.vng.gsmobile.casino.flatbuffers.CardValue;

public class TLMNDLRule {
	public static final String SPLIT_CHAR = ",";
	public static final Map<String, Integer> valueScore = MapUtils.putAll(
		new HashMap<String, Integer>(),  
		new Object[][]{
			{CardValue.Card_3, 1},
			{CardValue.Card_4, 2},
			{CardValue.Card_5, 3},
			{CardValue.Card_6, 4},
			{CardValue.Card_7, 5},
			{CardValue.Card_8, 6},
			{CardValue.Card_9, 7},
			{CardValue.Card_10, 8},
			{CardValue.Card_J, 9},
			{CardValue.Card_Q, 10},
			{CardValue.Card_K, 11},
			{CardValue.Card_A, 12},
			{CardValue.Card_2, 13},
	});
	
	public static final Map<String, Integer> classScore = MapUtils.putAll(
		new HashMap<String, Integer>(),  
		new Object[][]{
			{CardClass.Card_A, 1},
			{CardClass.Card_B, 2},
			{CardClass.Card_C, 3},
			{CardClass.Card_D, 4},
	});
	public static final int HEO1_SCORE = 9;
	public static final int HEO2_SCORE = 8;
	public static final int HEO3_SCORE = 6;
	public static final int HANG_SPLIT_SCORE = 10;
	public static final int BA_DOI_THONG_SCORE = 11;
	public static final int TU_QUY_SCORE = 12;
	public static final int BON_DOI_THONG_SCORE = 13;
	private static final Map<String, Integer> hangScore = MapUtils.putAll(
		new HashMap<String, Integer>(),  
		new Object[][]{
			//{String.format("%d,%d,%d", CardValue.Card_2, CardValue.Card_2, CardValue.Card_2), HEO3_SCORE},
			{String.format("%d,%d", CardValue.Card_2, CardValue.Card_2), HEO2_SCORE},
			{String.format("%d", CardValue.Card_2), HEO1_SCORE},
			
			{String.format("%d,%d,%d,%d,%d,%d", CardValue.Card_3, CardValue.Card_3, CardValue.Card_4, CardValue.Card_4, CardValue.Card_5, CardValue.Card_5), BA_DOI_THONG_SCORE},
			{String.format("%d,%d,%d,%d,%d,%d", CardValue.Card_4, CardValue.Card_4, CardValue.Card_5, CardValue.Card_5, CardValue.Card_6, CardValue.Card_6), BA_DOI_THONG_SCORE},
			{String.format("%d,%d,%d,%d,%d,%d", CardValue.Card_5, CardValue.Card_5, CardValue.Card_6, CardValue.Card_6, CardValue.Card_7, CardValue.Card_7), BA_DOI_THONG_SCORE},
			{String.format("%d,%d,%d,%d,%d,%d", CardValue.Card_6, CardValue.Card_6, CardValue.Card_7, CardValue.Card_7, CardValue.Card_8, CardValue.Card_8), BA_DOI_THONG_SCORE},
			{String.format("%d,%d,%d,%d,%d,%d", CardValue.Card_7, CardValue.Card_7, CardValue.Card_8, CardValue.Card_8, CardValue.Card_9, CardValue.Card_9), BA_DOI_THONG_SCORE},
			{String.format("%d,%d,%d,%d,%d,%d", CardValue.Card_8, CardValue.Card_8, CardValue.Card_9, CardValue.Card_9, CardValue.Card_10, CardValue.Card_10), BA_DOI_THONG_SCORE},
			{String.format("%d,%d,%d,%d,%d,%d", CardValue.Card_9, CardValue.Card_9, CardValue.Card_10, CardValue.Card_10, CardValue.Card_J, CardValue.Card_J), BA_DOI_THONG_SCORE},
			{String.format("%d,%d,%d,%d,%d,%d", CardValue.Card_10, CardValue.Card_10, CardValue.Card_J, CardValue.Card_J, CardValue.Card_Q, CardValue.Card_Q), BA_DOI_THONG_SCORE},
			{String.format("%d,%d,%d,%d,%d,%d", CardValue.Card_J, CardValue.Card_J, CardValue.Card_Q, CardValue.Card_Q, CardValue.Card_K, CardValue.Card_K), BA_DOI_THONG_SCORE},
			{String.format("%d,%d,%d,%d,%d,%d", CardValue.Card_Q, CardValue.Card_Q, CardValue.Card_K, CardValue.Card_K, CardValue.Card_A, CardValue.Card_A), BA_DOI_THONG_SCORE},
			
			{String.format("%d,%d,%d,%d", CardValue.Card_3, CardValue.Card_3, CardValue.Card_3, CardValue.Card_3), TU_QUY_SCORE},
			{String.format("%d,%d,%d,%d", CardValue.Card_4, CardValue.Card_4, CardValue.Card_4, CardValue.Card_4), TU_QUY_SCORE},
			{String.format("%d,%d,%d,%d", CardValue.Card_5, CardValue.Card_5, CardValue.Card_5, CardValue.Card_5), TU_QUY_SCORE},
			{String.format("%d,%d,%d,%d", CardValue.Card_6, CardValue.Card_6, CardValue.Card_6, CardValue.Card_6), TU_QUY_SCORE},
			{String.format("%d,%d,%d,%d", CardValue.Card_7, CardValue.Card_7, CardValue.Card_7, CardValue.Card_7), TU_QUY_SCORE},
			{String.format("%d,%d,%d,%d", CardValue.Card_8, CardValue.Card_8, CardValue.Card_8, CardValue.Card_8), TU_QUY_SCORE},
			{String.format("%d,%d,%d,%d", CardValue.Card_9, CardValue.Card_9, CardValue.Card_9, CardValue.Card_9), TU_QUY_SCORE},
			{String.format("%d,%d,%d,%d", CardValue.Card_10, CardValue.Card_10, CardValue.Card_10, CardValue.Card_10), TU_QUY_SCORE},
			{String.format("%d,%d,%d,%d", CardValue.Card_J, CardValue.Card_J, CardValue.Card_J, CardValue.Card_J), TU_QUY_SCORE},
			{String.format("%d,%d,%d,%d", CardValue.Card_Q, CardValue.Card_Q, CardValue.Card_Q, CardValue.Card_Q), TU_QUY_SCORE},
			{String.format("%d,%d,%d,%d", CardValue.Card_K, CardValue.Card_K, CardValue.Card_K, CardValue.Card_K), TU_QUY_SCORE},
			{String.format("%d,%d,%d,%d", CardValue.Card_A, CardValue.Card_A, CardValue.Card_A, CardValue.Card_A), TU_QUY_SCORE},
			{String.format("%d,%d,%d,%d", CardValue.Card_2, CardValue.Card_2, CardValue.Card_2, CardValue.Card_2), TU_QUY_SCORE},
			
			{String.format("%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_3, CardValue.Card_3, CardValue.Card_4, CardValue.Card_4, CardValue.Card_5, CardValue.Card_5, CardValue.Card_6, CardValue.Card_6), BON_DOI_THONG_SCORE},
			{String.format("%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_4, CardValue.Card_4, CardValue.Card_5, CardValue.Card_5, CardValue.Card_6, CardValue.Card_6, CardValue.Card_7, CardValue.Card_7), BON_DOI_THONG_SCORE},
			{String.format("%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_5, CardValue.Card_5, CardValue.Card_6, CardValue.Card_6, CardValue.Card_7, CardValue.Card_7, CardValue.Card_8, CardValue.Card_8), BON_DOI_THONG_SCORE},
			{String.format("%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_6, CardValue.Card_6, CardValue.Card_7, CardValue.Card_7, CardValue.Card_8, CardValue.Card_8, CardValue.Card_9, CardValue.Card_9), BON_DOI_THONG_SCORE},
			{String.format("%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_7, CardValue.Card_7, CardValue.Card_8, CardValue.Card_8, CardValue.Card_9, CardValue.Card_9, CardValue.Card_10, CardValue.Card_10), BON_DOI_THONG_SCORE},
			{String.format("%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_8, CardValue.Card_8, CardValue.Card_9, CardValue.Card_9, CardValue.Card_10, CardValue.Card_10, CardValue.Card_J, CardValue.Card_J), BON_DOI_THONG_SCORE},
			{String.format("%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_9, CardValue.Card_9, CardValue.Card_10, CardValue.Card_10, CardValue.Card_J, CardValue.Card_J, CardValue.Card_Q, CardValue.Card_Q), BON_DOI_THONG_SCORE},
			{String.format("%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_10, CardValue.Card_10, CardValue.Card_J, CardValue.Card_J, CardValue.Card_Q, CardValue.Card_Q, CardValue.Card_K, CardValue.Card_K), BON_DOI_THONG_SCORE},
			{String.format("%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_J, CardValue.Card_J, CardValue.Card_Q, CardValue.Card_Q, CardValue.Card_K, CardValue.Card_K, CardValue.Card_A, CardValue.Card_A), BON_DOI_THONG_SCORE},
	});
	public static Integer getHangScore(String key){
		Integer score = hangScore.get(key);
		return score==null?0:score;
	}
	
	private static final Map<String, Integer> trangScore = MapUtils.putAll(
		new HashMap<String, Integer>(),  
		new Object[][]{
			{TrangType.SAU_DOI, 1},
			{TrangType.TU_QUY_2, 2},
			{TrangType.NAM_DOI_THONG, 3},
			{TrangType.DONG_HOA, 4},
			{TrangType.HAI_TU_QUY, 5},
			{TrangType.BON_SAM_CO, 6},
			{TrangType.SAU_DOI_THONG, 7},
			{TrangType.SANH_RONG, 8},
			{TrangType.SANH_RONG_DONG_HOA, 9},		
			{TrangType.BA_DOI_THONG_3BICH, 0},	
			{TrangType.TU_QUY_3, 0},	
			{TrangType.BON_DOI_THONG_3BICH, 0},	
	});
	
	private static final Map<String, Integer> trang1Score = MapUtils.putAll(
		new HashMap<String, Integer>(),  
		new Object[][]{
			{TrangType.SAU_DOI, 0},
			{TrangType.TU_QUY_2, 0},
			{TrangType.NAM_DOI_THONG, 0},
			{TrangType.DONG_HOA, 0},
			{TrangType.HAI_TU_QUY, 0},
			{TrangType.BON_SAM_CO, 0},
			{TrangType.SAU_DOI_THONG, 0},
			{TrangType.SANH_RONG, 0},
			{TrangType.SANH_RONG_DONG_HOA, 0},		
			{TrangType.BA_DOI_THONG_3BICH, 1},	
			{TrangType.TU_QUY_3, 1},	
			{TrangType.BON_DOI_THONG_3BICH, 1},	
	});
	public static Integer getTrangScore(Object key, boolean isFirst){
		Integer score = isFirst?trang1Score.get(key):trangScore.get(key);
		return score==null?0:score;
	}
	
	public static String TUQUY2 = 
			CardValue.Card_2+TLMNDLRule.SPLIT_CHAR+
			CardValue.Card_2+TLMNDLRule.SPLIT_CHAR+
			CardValue.Card_2+TLMNDLRule.SPLIT_CHAR+
			CardValue.Card_2;
	public static String TUQUY3 = 
			CardValue.Card_3+TLMNDLRule.SPLIT_CHAR+
			CardValue.Card_3+TLMNDLRule.SPLIT_CHAR+
			CardValue.Card_3+TLMNDLRule.SPLIT_CHAR+
			CardValue.Card_3;	
	
	public static final int NORMAL_PENANCE_SCORE = 1;
	public static final int CONG_PENANCE_SCORE = 2;
	public static final int TRANG_PENANCE_SCORE = 26;
	public static final int HEO_DEN_PENANCE_SCORE = 3;
	public static final int HEO_DO_PENANCE_SCORE = 6;
	public static final int BA_DOI_THONG_PENANCE_SCORE = 12;
	public static final int BON_DOI_THONG_PENANCE_SCORE = 16;
	public static final int HAI_BA_DOI_THONG_PENANCE_SCORE = 24;
	public static final int TU_QUY_PENANCE_SCORE = 12;
	public static final int HAI_TU_QUY_PENANCE_SCORE = 24;
	public static final int BA_TU_QUY_PENANCE_SCORE = 36;
	
	private static final Map<String, Integer> penanceScore = MapUtils.putAll(
			new HashMap<String, Integer>(),  
			new Object[][]{
				{CardValue.Card_3, NORMAL_PENANCE_SCORE},
				{CardValue.Card_4, NORMAL_PENANCE_SCORE},
				{CardValue.Card_5, NORMAL_PENANCE_SCORE},
				{CardValue.Card_6, NORMAL_PENANCE_SCORE},
				{CardValue.Card_7, NORMAL_PENANCE_SCORE},
				{CardValue.Card_8, NORMAL_PENANCE_SCORE},
				{CardValue.Card_9, NORMAL_PENANCE_SCORE},
				{CardValue.Card_10, NORMAL_PENANCE_SCORE},
				{CardValue.Card_J, NORMAL_PENANCE_SCORE},
				{CardValue.Card_Q, NORMAL_PENANCE_SCORE},
				{CardValue.Card_K, NORMAL_PENANCE_SCORE},
				{CardValue.Card_A, NORMAL_PENANCE_SCORE},
				
				{CardID.name(CardID.Card_2_A), HEO_DEN_PENANCE_SCORE},
				{CardID.name(CardID.Card_2_B), HEO_DEN_PENANCE_SCORE},
				{CardID.name(CardID.Card_2_C), HEO_DO_PENANCE_SCORE},
				{CardID.name(CardID.Card_2_D), HEO_DO_PENANCE_SCORE},
				
				{PenanceType.name(PenanceType.BA_DOI_THONG), BA_DOI_THONG_PENANCE_SCORE},
				{PenanceType.name(PenanceType.BON_DOI_THONG), BON_DOI_THONG_PENANCE_SCORE},
				{PenanceType.name(PenanceType.HAI_BA_DOI_THONG), HAI_BA_DOI_THONG_PENANCE_SCORE},
				{PenanceType.name(PenanceType.MOT_TU_QUY), TU_QUY_PENANCE_SCORE},
				{PenanceType.name(PenanceType.HAI_TU_QUY), HAI_TU_QUY_PENANCE_SCORE},
				{PenanceType.name(PenanceType.BA_TU_QUY), BA_TU_QUY_PENANCE_SCORE},

				{String.format("%d,%d,%d,%d,%d,%d", CardValue.Card_3, CardValue.Card_3, CardValue.Card_4, CardValue.Card_4, CardValue.Card_5, CardValue.Card_5), BA_DOI_THONG_PENANCE_SCORE},
				{String.format("%d,%d,%d,%d,%d,%d", CardValue.Card_4, CardValue.Card_4, CardValue.Card_5, CardValue.Card_5, CardValue.Card_6, CardValue.Card_6), BA_DOI_THONG_PENANCE_SCORE},
				{String.format("%d,%d,%d,%d,%d,%d", CardValue.Card_5, CardValue.Card_5, CardValue.Card_6, CardValue.Card_6, CardValue.Card_7, CardValue.Card_7), BA_DOI_THONG_PENANCE_SCORE},
				{String.format("%d,%d,%d,%d,%d,%d", CardValue.Card_6, CardValue.Card_6, CardValue.Card_7, CardValue.Card_7, CardValue.Card_8, CardValue.Card_8), BA_DOI_THONG_PENANCE_SCORE},
				{String.format("%d,%d,%d,%d,%d,%d", CardValue.Card_7, CardValue.Card_7, CardValue.Card_8, CardValue.Card_8, CardValue.Card_9, CardValue.Card_9), BA_DOI_THONG_PENANCE_SCORE},
				{String.format("%d,%d,%d,%d,%d,%d", CardValue.Card_8, CardValue.Card_8, CardValue.Card_9, CardValue.Card_9, CardValue.Card_10, CardValue.Card_10), BA_DOI_THONG_PENANCE_SCORE},
				{String.format("%d,%d,%d,%d,%d,%d", CardValue.Card_9, CardValue.Card_9, CardValue.Card_10, CardValue.Card_10, CardValue.Card_J, CardValue.Card_J), BA_DOI_THONG_PENANCE_SCORE},
				{String.format("%d,%d,%d,%d,%d,%d", CardValue.Card_10, CardValue.Card_10, CardValue.Card_J, CardValue.Card_J, CardValue.Card_Q, CardValue.Card_Q), BA_DOI_THONG_PENANCE_SCORE},
				{String.format("%d,%d,%d,%d,%d,%d", CardValue.Card_J, CardValue.Card_J, CardValue.Card_Q, CardValue.Card_Q, CardValue.Card_K, CardValue.Card_K), BA_DOI_THONG_PENANCE_SCORE},
				{String.format("%d,%d,%d,%d,%d,%d", CardValue.Card_Q, CardValue.Card_Q, CardValue.Card_K, CardValue.Card_K, CardValue.Card_A, CardValue.Card_A), BA_DOI_THONG_PENANCE_SCORE},
				
				{String.format("%d,%d,%d,%d", CardValue.Card_3, CardValue.Card_3, CardValue.Card_3, CardValue.Card_3), TU_QUY_PENANCE_SCORE},
				{String.format("%d,%d,%d,%d", CardValue.Card_4, CardValue.Card_4, CardValue.Card_4, CardValue.Card_4), TU_QUY_PENANCE_SCORE},
				{String.format("%d,%d,%d,%d", CardValue.Card_5, CardValue.Card_5, CardValue.Card_5, CardValue.Card_5), TU_QUY_PENANCE_SCORE},
				{String.format("%d,%d,%d,%d", CardValue.Card_6, CardValue.Card_6, CardValue.Card_6, CardValue.Card_6), TU_QUY_PENANCE_SCORE},
				{String.format("%d,%d,%d,%d", CardValue.Card_7, CardValue.Card_7, CardValue.Card_7, CardValue.Card_7), TU_QUY_PENANCE_SCORE},
				{String.format("%d,%d,%d,%d", CardValue.Card_8, CardValue.Card_8, CardValue.Card_8, CardValue.Card_8), TU_QUY_PENANCE_SCORE},
				{String.format("%d,%d,%d,%d", CardValue.Card_9, CardValue.Card_9, CardValue.Card_9, CardValue.Card_9), TU_QUY_PENANCE_SCORE},
				{String.format("%d,%d,%d,%d", CardValue.Card_10, CardValue.Card_10, CardValue.Card_10, CardValue.Card_10), TU_QUY_PENANCE_SCORE},
				{String.format("%d,%d,%d,%d", CardValue.Card_J, CardValue.Card_J, CardValue.Card_J, CardValue.Card_J), TU_QUY_PENANCE_SCORE},
				{String.format("%d,%d,%d,%d", CardValue.Card_Q, CardValue.Card_Q, CardValue.Card_Q, CardValue.Card_Q), TU_QUY_PENANCE_SCORE},
				{String.format("%d,%d,%d,%d", CardValue.Card_K, CardValue.Card_K, CardValue.Card_K, CardValue.Card_K), TU_QUY_PENANCE_SCORE},
				{String.format("%d,%d,%d,%d", CardValue.Card_A, CardValue.Card_A, CardValue.Card_A, CardValue.Card_A), TU_QUY_PENANCE_SCORE},
				
				{String.format("%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_3, CardValue.Card_3, CardValue.Card_4, CardValue.Card_4, CardValue.Card_5, CardValue.Card_5, CardValue.Card_6, CardValue.Card_6), BON_DOI_THONG_PENANCE_SCORE},
				{String.format("%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_4, CardValue.Card_4, CardValue.Card_5, CardValue.Card_5, CardValue.Card_6, CardValue.Card_6, CardValue.Card_7, CardValue.Card_7), BON_DOI_THONG_PENANCE_SCORE},
				{String.format("%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_5, CardValue.Card_5, CardValue.Card_6, CardValue.Card_6, CardValue.Card_7, CardValue.Card_7, CardValue.Card_8, CardValue.Card_8), BON_DOI_THONG_PENANCE_SCORE},
				{String.format("%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_6, CardValue.Card_6, CardValue.Card_7, CardValue.Card_7, CardValue.Card_8, CardValue.Card_8, CardValue.Card_9, CardValue.Card_9), BON_DOI_THONG_PENANCE_SCORE},
				{String.format("%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_7, CardValue.Card_7, CardValue.Card_8, CardValue.Card_8, CardValue.Card_9, CardValue.Card_9, CardValue.Card_10, CardValue.Card_10), BON_DOI_THONG_PENANCE_SCORE},
				{String.format("%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_8, CardValue.Card_8, CardValue.Card_9, CardValue.Card_9, CardValue.Card_10, CardValue.Card_10, CardValue.Card_J, CardValue.Card_J), BON_DOI_THONG_PENANCE_SCORE},
				{String.format("%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_9, CardValue.Card_9, CardValue.Card_10, CardValue.Card_10, CardValue.Card_J, CardValue.Card_J, CardValue.Card_Q, CardValue.Card_Q), BON_DOI_THONG_PENANCE_SCORE},
				{String.format("%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_10, CardValue.Card_10, CardValue.Card_J, CardValue.Card_J, CardValue.Card_Q, CardValue.Card_Q, CardValue.Card_K, CardValue.Card_K), BON_DOI_THONG_PENANCE_SCORE},
				{String.format("%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_J, CardValue.Card_J, CardValue.Card_Q, CardValue.Card_Q, CardValue.Card_K, CardValue.Card_K, CardValue.Card_A, CardValue.Card_A), BON_DOI_THONG_PENANCE_SCORE},
		});
	public static Integer getPenanceScore(Object key){
		Integer score = penanceScore.get(key);
		return score == null?0:score;
	}
	
	private static final Map<String, Byte> doithongKind = MapUtils.putAll(
			new HashMap<String, Byte>(),  
			new Object[][]{
				{String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_3, CardValue.Card_3, CardValue.Card_4, CardValue.Card_4, CardValue.Card_5, CardValue.Card_5, CardValue.Card_6, CardValue.Card_6, CardValue.Card_7, CardValue.Card_7), TLMNCardKind.NAM_DOI_THONG},
				{String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_4, CardValue.Card_4, CardValue.Card_5, CardValue.Card_5, CardValue.Card_6, CardValue.Card_6, CardValue.Card_7, CardValue.Card_7, CardValue.Card_8, CardValue.Card_8), TLMNCardKind.NAM_DOI_THONG},
				{String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_5, CardValue.Card_5, CardValue.Card_6, CardValue.Card_6, CardValue.Card_7, CardValue.Card_7, CardValue.Card_8, CardValue.Card_8, CardValue.Card_9, CardValue.Card_9), TLMNCardKind.NAM_DOI_THONG},
				{String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_6, CardValue.Card_6, CardValue.Card_7, CardValue.Card_7, CardValue.Card_8, CardValue.Card_8, CardValue.Card_9, CardValue.Card_9, CardValue.Card_10, CardValue.Card_10), TLMNCardKind.NAM_DOI_THONG},
				{String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_7, CardValue.Card_7, CardValue.Card_8, CardValue.Card_8, CardValue.Card_9, CardValue.Card_9, CardValue.Card_10, CardValue.Card_10, CardValue.Card_J, CardValue.Card_J), TLMNCardKind.NAM_DOI_THONG},
				{String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_8, CardValue.Card_8, CardValue.Card_9, CardValue.Card_9, CardValue.Card_10, CardValue.Card_10, CardValue.Card_J, CardValue.Card_J, CardValue.Card_Q, CardValue.Card_Q), TLMNCardKind.NAM_DOI_THONG},
				{String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_9, CardValue.Card_9, CardValue.Card_10, CardValue.Card_10, CardValue.Card_J, CardValue.Card_J, CardValue.Card_Q, CardValue.Card_Q, CardValue.Card_K, CardValue.Card_K), TLMNCardKind.NAM_DOI_THONG},
				{String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_10, CardValue.Card_10, CardValue.Card_J, CardValue.Card_J, CardValue.Card_Q, CardValue.Card_Q, CardValue.Card_K, CardValue.Card_K, CardValue.Card_A, CardValue.Card_A), TLMNCardKind.NAM_DOI_THONG},

				{String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_3, CardValue.Card_3, CardValue.Card_4, CardValue.Card_4, CardValue.Card_5, CardValue.Card_5, CardValue.Card_6, CardValue.Card_6, CardValue.Card_7, CardValue.Card_7, CardValue.Card_8, CardValue.Card_8), TLMNCardKind.SAU_DOI_THONG},
				{String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_4, CardValue.Card_4, CardValue.Card_5, CardValue.Card_5, CardValue.Card_6, CardValue.Card_6, CardValue.Card_7, CardValue.Card_7, CardValue.Card_8, CardValue.Card_8, CardValue.Card_9, CardValue.Card_9), TLMNCardKind.SAU_DOI_THONG},
				{String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_5, CardValue.Card_5, CardValue.Card_6, CardValue.Card_6, CardValue.Card_7, CardValue.Card_7, CardValue.Card_8, CardValue.Card_8, CardValue.Card_9, CardValue.Card_9, CardValue.Card_10, CardValue.Card_10), TLMNCardKind.SAU_DOI_THONG},
				{String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_6, CardValue.Card_6, CardValue.Card_7, CardValue.Card_7, CardValue.Card_8, CardValue.Card_8, CardValue.Card_9, CardValue.Card_9, CardValue.Card_10, CardValue.Card_10, CardValue.Card_J, CardValue.Card_J), TLMNCardKind.SAU_DOI_THONG},
				{String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_7, CardValue.Card_7, CardValue.Card_8, CardValue.Card_8, CardValue.Card_9, CardValue.Card_9, CardValue.Card_10, CardValue.Card_10, CardValue.Card_J, CardValue.Card_J, CardValue.Card_Q, CardValue.Card_Q), TLMNCardKind.SAU_DOI_THONG},
				{String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_8, CardValue.Card_8, CardValue.Card_9, CardValue.Card_9, CardValue.Card_10, CardValue.Card_10, CardValue.Card_J, CardValue.Card_J, CardValue.Card_Q, CardValue.Card_Q, CardValue.Card_K, CardValue.Card_K), TLMNCardKind.SAU_DOI_THONG},
				{String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d", CardValue.Card_9, CardValue.Card_9, CardValue.Card_10, CardValue.Card_10, CardValue.Card_J, CardValue.Card_J, CardValue.Card_Q, CardValue.Card_Q, CardValue.Card_K, CardValue.Card_K, CardValue.Card_A, CardValue.Card_A), TLMNCardKind.SAU_DOI_THONG},
		});
	public static Byte getDoiThongKind(Object key){
		Byte kind = doithongKind.get(key);
		return kind == null?TLMNCardKind.NONE:kind;
	}
}

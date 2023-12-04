package com.example.model.enums;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum AuthorityEnum {
	
//	ALL("ALL", "全部業管機關", "ALL"), TAIPEI_CPT("Taipei", "臺北市運資平臺", "TPE-CPT"),
	TAIPEI("Taipei", "臺北市", "TPE", "客運業者"), NEWTAIPEI("NewTaipei", "新北市", "NWT", "客運業者"), TAOYUAN("Taoyuan", "桃園市", "TAO", "客運業者"), TAICHUNG("Taichung", "臺中市", "TXG", "客運業者"), TAINAN("Tainan", "臺南市", "TNN", "客運業者"), TEST("TEST", "高雄市", "KHH", "客運業者"), KEELUNG("Keelung", "基隆市", "KEE", "客運業者"), HSINCHU("Hsinchu", "新竹市", "HSZ", "客運業者"),
	HSINCHUCOUNTY("HsinchuCounty", "新竹縣", "HSQ", "客運業者"), MIAOLICOUNTY("MiaoliCounty", "苗栗縣", "MIA", "客運業者"), CHANGHUACOUNTY("ChanghuaCounty", "彰化縣", "CHA", "客運業者"), NANTOUCOUNTY("NantouCounty", "南投縣", "NAN", "客運業者"), YUNLINCOUNTY("YunlinCounty", "雲林縣", "YUN", "客運業者"), CHIAYICOUNTY("ChiayiCounty", "嘉義縣", "CYQ", "客運業者"), CHIAYI("Chiayi", "嘉義市", "CYI", "客運業者"),
	PINGTUNGCOUNTY("PingtungCounty", "屏東縣", "PIF", "客運業者"), YILANCOUNTY("YilanCounty", "宜蘭縣", "ILA", "客運業者"), HUALIENCOUNTY("HualienCounty", "花蓮縣", "HUA", "客運業者"), TAITUNGCOUNTY("TaitungCounty", "臺東縣", "TTT", "客運業者"), KINMENCOUNTY("KinmenCounty", "金門縣", "KIN", "客運業者"), PENGHUCOUNTY("PenghuCounty", "澎湖縣", "PEN", "客運業者"), LIENCHIANGCOUNTY("LienchiangCounty", "連江縣", "LIE", "客運業者"),
	INTERCITY("InterCity", "公路總局", "THB", "客運業者"), THBVO151("THB-VO15-1", "臺北區監理所", "THB-VO15-1", "客運業者"), THBVO181("THB-VO18-1", "新竹區監理所", "THB-VO18-1", "客運業者"), THBVO111("THB-VO11-1", "臺中區監理所", "THB-VO11-1", "客運業者"), THBVO241("THB-VO24-1", "嘉義區監理所", "THB-VO24-1", "客運業者"), THBVO142("THB-VO14-2", "高雄區監理所", "THB-VO14-2", "客運業者"),
	THBVO101("THB-VO10-1", "臺北市區監理所", "THB-VO10-1", "客運業者"), THBVO141("THB-VO14-1", "高雄市區監理所", "THB-VO14-1", "客運業者"),
	TRTC("TRTC", "台北捷運公司", "TRTC", "捷運業者"), TYMC("TYMC", "桃園大眾捷運股份有限公司", "TYMC", "捷運業者"), KRTC("KRTC", "高雄捷運", "KRTC", "捷運業者"), TRA("TRA", "臺灣鐵路管理局", "TRA", "鐵道業者"), THSRC("THSRC", "台灣高鐵", "THSRC", "鐵道業者");
	
	private String authorityNameEn;
	
	private String authorityNameZh;

	private String authorityCode;
	
	private String operatorType;

	public static AuthorityEnum fromCode(String authorityCode) {
		return Stream.of(AuthorityEnum.values())
				.filter(a -> a.authorityCode.equals(authorityCode))
				.findAny()
				.orElseThrow(IllegalArgumentException::new);
	}
	
	public static AuthorityEnum fromNameZh(String authorityNameZh) {
		return Stream.of(AuthorityEnum.values())
				.filter(a -> a.authorityNameZh.equals(authorityNameZh))
				.findAny()
				.orElseThrow(IllegalArgumentException::new);
	}
	
	public static AuthorityEnum fromNameEn(String authorityNameEn) {
		return Stream.of(AuthorityEnum.values())
				.filter(a -> a.authorityNameEn.equals(authorityNameEn))
				.findAny()
				.orElseThrow(IllegalArgumentException::new);
	}
	
	public static List<AuthorityEnum> fromByOperatorType(String operatorType) {
		return Stream.of(AuthorityEnum.values())
			.filter(a -> a.operatorType.equals(operatorType))
			.collect(Collectors.toList());
	}
	
	public static List<AuthorityEnum> fromByOperatorTypeExclude(String operatorType) {
		return Stream.of(AuthorityEnum.values())
			.filter(a -> !a.operatorType.equals(operatorType))
			.collect(Collectors.toList());
	}
}

package com.example.model.dto.source.paging;

import java.io.Serializable;
import java.util.Date;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class LetServiceGetRedLightWithPaging  implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String irSID;
	private String plateNumber;
	private Date sTime;
	private Date eTime;
	private String beenVerified;
	private String techLawType_sid;
	private String techLawSid;
	private String techLawName;
	private String proofImageUID;
	private String proofVideoUID;
	private String updateUserSID;
	private String verifiedUserSID;
	private String dataMonth;
	@AssignFrom(name = "updateTime")
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date srcUpdateTime;
	private Date updateTime;
	@AssignFrom(name = "updateTime")
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date infoTime;
	@AssignFrom(name = "updateTime")
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date infoDate;

}

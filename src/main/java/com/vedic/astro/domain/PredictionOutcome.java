package com.vedic.astro.domain;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import com.vedic.astro.enums.MemberType;

@Document(collection="prediction_outcomes")
@TypeAlias("prediction_outcome")
public class PredictionOutcome {

	@Id
	private String id = null;
	private String code = null;
	private String name = null;
	private String templateCode = null;
	private MemberType memberType = null;
	
	private Map<String, PredictionObservation> predictionObservations = 
			new HashMap<String, PredictionObservation>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTemplateCode() {
		return templateCode;
	}
	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}
	public Map<String, PredictionObservation> getPredictionObservations() {
		return predictionObservations;
	}
	public void setPredictionObservations(Map<String, PredictionObservation> predictionObservations) {
		this.predictionObservations = predictionObservations;
	}
	public MemberType getMemberType() {
		return memberType;
	}
	public void setMemberType(MemberType memberType) {
		this.memberType = memberType;
	}
	@Override
	public String toString() {
		return "PredictionOutcome [id=" + id + ", code=" + code + ", name=" + name + ", templateCode=" + templateCode
				+ ", memberType=" + memberType + ", predictionObservations=" + predictionObservations + "]";
	}
}

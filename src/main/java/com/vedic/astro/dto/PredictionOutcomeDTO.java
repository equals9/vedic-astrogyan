package com.vedic.astro.dto;

import java.util.ArrayList;
import java.util.List;

public class PredictionOutcomeDTO {
	
	private String code = null;
	private String name = null;
	private String templateCode = null;
	
	private List<PredictionObservationDTO> observations = 
			new ArrayList<PredictionObservationDTO>();
	
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
	public List<PredictionObservationDTO> getObservations() {
		return observations;
	}
	public void setObservations(List<PredictionObservationDTO> observations) {
		this.observations = observations;
	}
	@Override
	public String toString() {
		return "PredictionOutcomeDTO [code=" + code + ", name=" + name + ", templateCode=" + templateCode
				+ ", observations=" + observations + "]";
	}
}
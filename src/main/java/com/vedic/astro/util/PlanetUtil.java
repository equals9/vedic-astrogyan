package com.vedic.astro.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.vedic.astro.domain.PlanetDetails;
import com.vedic.astro.enums.DecimalPlace;
import com.vedic.astro.enums.Gender;
import com.vedic.astro.enums.House;
import com.vedic.astro.enums.Planet;
import com.vedic.astro.enums.PlanetAge;
import com.vedic.astro.vo.ChartHouse;
import com.vedic.astro.vo.HousePlanetInput;

@Component("planetUtil")
public class PlanetUtil {

	@Autowired
	@Qualifier("relationshipUtil")
	private RelationshipUtil relationshipUtil;

	@Value("${score.zodiac.compatibility.weightage}")
	protected float zodiacWeight;

	@Value("${score.house.compatibility.weightage}")
	protected float houseWeight;

	@Value("${score.owner.compatibility.weightage}")
	protected float ownerWeight;

	@Value("${score.inhabitants.compatibility.weightage}")
	protected float inhabitantsWeight;

	private PlanetUtil() {
	}

	public PlanetDetails getPlanetDetails(Planet planet) {
		return BaseEntityRefData.createPlanetRefData().getData()
				.get(planet.name());
	}

	public List<Integer> getAspects(Planet planet) {
		List<Integer> aspectCount = new ArrayList<Integer>();
		PlanetDetails planetDetails = getPlanetDetails(planet);
		if(planetDetails!=null){
			aspectCount =getPlanetDetails(planet).getAspectCount(); 
		}
		return aspectCount;
	}

	public List<House> getHousesForKaraka(Planet planet) {
		List<House> karakaHouses = null;
		PlanetDetails planetDetails = getPlanetDetails(planet); 
		if(planetDetails != null){
			karakaHouses = planetDetails.getKarakaOf();
		}
		return karakaHouses;
	}

	public PlanetAge findAge(Double degrees) {

		PlanetAge age = null;

		if (degrees.doubleValue() > 0.0 && degrees.doubleValue() <= 1.0) {
			age = PlanetAge.Infant;
		} else if (degrees.doubleValue() > 1.0 && degrees.doubleValue() <= 5.0) {
			age = PlanetAge.Teen;
		} else if (degrees.doubleValue() > 5.0 && degrees.doubleValue() <= 17.0) {
			age = PlanetAge.Young;
		} else if (degrees.doubleValue() > 17.0
				&& degrees.doubleValue() <= 25.0) {
			age = PlanetAge.Mature;
		} else if (degrees.doubleValue() > 25.0 && degrees.doubleValue() < 30.0) {
			age = PlanetAge.Old;
		}

		return age;
	}

	public Map<Planet, PlanetAge> evaluatePlanetsAge(
			List<ChartHouse> houseInputList) {
		Map<Planet, PlanetAge> planetStrengthMap = new HashMap<Planet, PlanetAge>();

		for (ChartHouse chartHouse : houseInputList) {
			for (HousePlanetInput housePlanetInput : chartHouse.getPlanets()) {
				planetStrengthMap.put(housePlanetInput.getPlanet(),
						findAge(housePlanetInput.getDegrees()));
			}
		}
		return planetStrengthMap;
	}

	public Double evaluatePlanetsAgeMultiplierFactor(Planet planet,
			PlanetAge age) {
		double factor = age.getFactor();
		Map<PlanetAge, List<Planet>> map = BaseEntityRefData
				.createPlanetAgeToPlanetMatrix();

		List<Planet> planets = map.get(age);

		if (planets != null) {

			if (!planets.isEmpty() && planets.contains(planet)) {
				factor = 1.25;
			}

		}

		return factor;
	}

	/*
	 * public PlanetStrengthOutput evaluateOwnStrength(PlanetOwnStrengthInput
	 * input) {
	 * 
	 * PlanetStrengthOutput output = new PlanetStrengthOutput(); Double
	 * consolidatedScore = 0.0;
	 * 
	 * House house = input.getHouse(); Planet planet = input.getPlanet(); Zodiac
	 * zodiac = input.getZodiac(); Planet ownerPlanet = input.getOwnerOfHouse();
	 * List<Planet> otherPlanets = input.getOtherInhabitants();
	 * 
	 * Map<PlanetStrengthCriteria, PlanetStrengthResult> strengthMap = new
	 * HashMap<PlanetStrengthCriteria, PlanetStrengthResult>();
	 * 
	 * EntityRelationshipValue housePlanetRelationshipValue = relationshipUtil
	 * .evaluate(house, planet); PlanetStrengthResult planetHouseResult = new
	 * PlanetStrengthResult(
	 * housePlanetRelationshipValue.getHouseImpact().getScore(),
	 * housePlanetRelationshipValue.getHouseImpact().name());
	 * 
	 * strengthMap.put(PlanetStrengthCriteria.HOUSE_COMPATIBILITY,
	 * planetHouseResult);
	 * 
	 * EntityRelationshipValue zodiacPlanetRelationshipValue = relationshipUtil
	 * .evaluate(zodiac, planet); PlanetStrengthResult planetZodiacResult = new
	 * PlanetStrengthResult(
	 * zodiacPlanetRelationshipValue.getZodiacImpact().getScore(),
	 * zodiacPlanetRelationshipValue.getZodiacImpact().name());
	 * 
	 * strengthMap.put(PlanetStrengthCriteria.ZODIAC_COMPATIBILITY,
	 * planetZodiacResult);
	 * 
	 * EntityRelationshipValue ownerPlanetRelationshipValue = relationshipUtil
	 * .evaluate(ownerPlanet, planet);
	 * 
	 * PlanetStrengthResult planetOwnerResult = new PlanetStrengthResult(
	 * ownerPlanetRelationshipValue.getPlanetImpact().getScore(),
	 * ownerPlanetRelationshipValue.getPlanetImpact().name());
	 * 
	 * strengthMap.put(PlanetStrengthCriteria.OWNER_COMPATIBILITY,
	 * planetOwnerResult);
	 * 
	 * Double degreeScoreValue = evaluatePlanetsAgeMultiplierFactor(planet,
	 * input.getPlanetAge()); PlanetStrengthResult planetAgeResult = new
	 * PlanetStrengthResult( degreeScoreValue, input.getPlanetAge().name());
	 * 
	 * strengthMap.put(PlanetStrengthCriteria.AGE_FACTOR, planetAgeResult);
	 * 
	 * Double inhabitantsScore = 0.0; int inhabitantCount = 0; Double
	 * inhabitantsAvgScore = 0.0;
	 * 
	 * if ((otherPlanets != null) && !otherPlanets.isEmpty()) {
	 * 
	 * StringBuilder descBuilder = new StringBuilder(); for (Planet otherPlanet
	 * : otherPlanets) { inhabitantCount++; EntityRelationshipValue
	 * planetImpactValue = relationshipUtil .evaluate(otherPlanet, planet);
	 * 
	 * inhabitantsScore = inhabitantsScore +
	 * planetImpactValue.getPlanetImpact().getScore();
	 * descBuilder.append("planet=").append(otherPlanet) .append("relation=")
	 * .append(planetImpactValue.getPlanetImpact().name()); }
	 * 
	 * inhabitantsAvgScore = inhabitantsScore / inhabitantCount;
	 * 
	 * PlanetStrengthResult planetPlanetResult = new PlanetStrengthResult(
	 * inhabitantsAvgScore, descBuilder.toString());
	 * 
	 * strengthMap.put(PlanetStrengthCriteria.OTHERS_COMPATIBILITY,
	 * planetPlanetResult);
	 * 
	 * }
	 * 
	 * if ((otherPlanets != null) && !otherPlanets.isEmpty()) {
	 * consolidatedScore = (housePlanetRelationshipValue.getHouseImpact()
	 * .getScore() houseWeight + zodiacPlanetRelationshipValue.getZodiacImpact()
	 * .getScore() zodiacWeight +
	 * ownerPlanetRelationshipValue.getPlanetImpact().getScore() ownerWeight +
	 * inhabitantsAvgScore * inhabitantsWeight) / (houseWeight + zodiacWeight +
	 * ownerWeight + inhabitantsWeight); } else { consolidatedScore =
	 * (housePlanetRelationshipValue.getHouseImpact() .getScore() houseWeight +
	 * zodiacPlanetRelationshipValue.getZodiacImpact() .getScore() *
	 * zodiacWeight + ownerPlanetRelationshipValue .getPlanetImpact().getScore()
	 * * ownerWeight) / (houseWeight + zodiacWeight + ownerWeight); }
	 * 
	 * output.setOverallScore(consolidatedScore * degreeScoreValue);
	 * output.setStrengthMap(strengthMap);
	 * output.setPlanetStrengthType(PlanetStrengthType.Individual);
	 * 
	 * return output; }
	 */
	public Double calcExaltationBasedStrength(Double degrees, Planet planet) {

		Double score = -1.0;

		Double debilitationPoint = this.getPlanetDetails(planet)
				.getDebilitationPoint();
		if (debilitationPoint > 0.0) {
			score = (Double) Math.abs(degrees - debilitationPoint) / 3;
		}
		return MathUtil.round(score, 2);
	}

	public Double calcDreshkonBala(Double degrees, Planet planet) {
		Double score = 0.0;
		PlanetDetails planetDetails = getPlanetDetails(planet);

		if (planetDetails.getGender() != null) {
			if (planetDetails.getGender().equals(Gender.Male) && (degrees > 0)
					&& (degrees <= 10)) {
				score = 15.0;
			} else if (planetDetails.getGender().equals(Gender.Hermaphrodite)
					&& (degrees > 10) && (degrees <= 20)) {
				score = 15.0;
			} else if (planetDetails.getGender().equals(Gender.Female)
					&& (degrees > 20) && (degrees <= 30)) {
				score = 15.0;
			}
		}

		return score;
	}

	public Double getEpochCorrection(Planet planet, int years) {
		Double correction = 0.00;

		if (planet.equals(Planet.JUP)) {
			correction = -1 * MathUtil.round((3.33 + 0.0067 * years), 2);
		} else if (planet.equals(Planet.SAT)) {
			correction = MathUtil.round((5.0 + 0.001 * years), 2);
		} else if (planet.equals(Planet.MER)) {
			correction = MathUtil.round((6.67 - 0.00133 * years), 2);
		} else if (planet.equals(Planet.VEN)) {
			correction = -1 * MathUtil.round((5.0 + 0.001 * years), 2);
		}
		return correction;
	}

	public Double getMeanLatitude(Planet planet, String dob) {

		int days = DateUtil.daysBetween(
				DateUtil.toDate("01/01/1900", "MM/dd/yyyy"),
				DateUtil.toDate(dob, "MM/dd/yyyy"));

		// System.out.println("days = " + days);

		int tenThousandsValue = MathUtil.place(days, DecimalPlace.TenThousands);
		int thousandsValue = MathUtil.place(days, DecimalPlace.Thousands);
		int hundredsValue = MathUtil.place(days, DecimalPlace.Hundreds);
		int tensValue = MathUtil.place(days, DecimalPlace.Tens);
		int unitValue = MathUtil.place(days, DecimalPlace.Unit);

		Map<Planet, Map<Integer, Double>> planetMotionMap = BaseEntityRefData
				.getDailyMotionDegreesData();

		Double tenThousandsValueDeg = planetMotionMap.get(planet).get(
				tenThousandsValue);
		Double thousandsValueDeg = planetMotionMap.get(planet).get(
				thousandsValue);
		Double hundredsValueDeg = planetMotionMap.get(planet)
				.get(hundredsValue);
		Double tensValueDeg = planetMotionMap.get(planet).get(tensValue);
		Double unitsValueDeg = 0.0;
		if (unitValue > 0) {
			unitsValueDeg = planetMotionMap.get(planet).get(unitValue);
		}

		Double totalDegree = tenThousandsValueDeg
				+ thousandsValueDeg
				+ hundredsValueDeg
				+ tensValueDeg
				+ unitsValueDeg
				+ getPlanetDetails(planet).getEpochDegree()
				+ getEpochCorrection(planet,
						DateUtil.yearsBetween("01/01/1900", dob));

		/*
		 * System.out.println("tenThousandsValueDeg = " + tenThousandsValueDeg);
		 * System.out.println("thousandsValueDeg = " + thousandsValueDeg);
		 * System.out.println("hundredsValueDeg = " + hundredsValueDeg);
		 * System.out.println("tensValueDeg = " + tensValueDeg);
		 * System.out.println("unitsValueDeg = " + unitsValueDeg);
		 * System.out.println("epoch deg = " +
		 * getPlanetDetails(planet).getEpochDegree());
		 * System.out.println("years = " + DateUtil.yearsBetween("01/01/1900",
		 * dob)); System.out.println("correction = " +
		 * getEpochCorrection(planet, DateUtil.yearsBetween("01/01/1900",
		 * dob))); System.out.println("totalDegree = " + totalDegree);
		 */
		int counter = 0;

		while (totalDegree > counter * 360) {
			counter++;
		}

		Double meanLongitude = MathUtil.round(
				(totalDegree - 360 * (counter - 1)), 2);
		// System.out.println("counter =" + counter);

		return meanLongitude;
	}

	public Double getAspectStrength(Double degrees, Planet aspectingPlanet) {
		
		System.out.println("degrees diff = " + degrees);
		System.out.println("aspectingPlanet = " + aspectingPlanet);
		
		Double aspectValue = 0.00;
		Double specialAspect = 0.00;

		if (degrees > 30.0 && degrees <= 60.0) {
			aspectValue = (degrees - 30.0) / 2;
		} else if (degrees > 60.0 && degrees <= 90.0) {
			aspectValue = (degrees - 60.0) + 15.0;
		} else if (degrees > 90.0 && degrees <= 120.0) {
			aspectValue = (120 - degrees) / 2 + 30.0;
		} else if (degrees > 120.0 && degrees <= 150.0) {
			aspectValue = 150 - degrees;
		} else if (degrees > 150.0 && degrees <= 180.0) {
			aspectValue = (degrees - 150)*2;
		} else if (degrees > 180.0 && degrees <= 300.0) {
			aspectValue = (300 - degrees) / 2;
		}

		if (aspectingPlanet.equals(Planet.MAR)) {
			if ((degrees > 90.0 && degrees <= 120.0)
					|| (degrees > 210.0 && degrees <= 240.0)) {
				specialAspect = 15.0;
			}
		} else if (aspectingPlanet.equals(Planet.JUP)) {
			if ((degrees > 120.0 && degrees <= 150.0)
					|| (degrees > 240.0 && degrees <= 270.0)) {
				specialAspect = 30.0;
			}
		} else if (aspectingPlanet.equals(Planet.SAT)) {
			if ((degrees > 60.0 && degrees <= 90.0)
					|| (degrees > 270.0 && degrees <= 300.0)) {
				specialAspect = 45.0;
			}
		}

		return MathUtil.round(aspectValue + specialAspect, 2);
	}
	
	public Double calcDeclination(Double differenceFromEquinocticalPoint){
		
		Double declinationInMinutes = 0.00;
		
		if(differenceFromEquinocticalPoint> 0.0 && differenceFromEquinocticalPoint<=15.00){
			declinationInMinutes = (differenceFromEquinocticalPoint/15)*362;
		}
		else if(differenceFromEquinocticalPoint> 15.0 && differenceFromEquinocticalPoint<=30.00){
			declinationInMinutes = 362 + ((differenceFromEquinocticalPoint-15)/15)*341;
		}
		else if(differenceFromEquinocticalPoint> 30.0 && differenceFromEquinocticalPoint<=45.00){
			declinationInMinutes = 703 + ((differenceFromEquinocticalPoint-30)/15)*299;
		}
		else if(differenceFromEquinocticalPoint> 45.0 && differenceFromEquinocticalPoint<=60.00){
			declinationInMinutes = 1002 + ((differenceFromEquinocticalPoint-45)/15)*236;
		}
		else if(differenceFromEquinocticalPoint> 60.0 && differenceFromEquinocticalPoint<=75.00){
			declinationInMinutes = 1238 + ((differenceFromEquinocticalPoint-60)/15)*150;
		}
		else if(differenceFromEquinocticalPoint> 75.0 && differenceFromEquinocticalPoint<=90.00){
			declinationInMinutes = 1388 + ((differenceFromEquinocticalPoint-75)/15)*52;
		}
		
		Double declinationInDeg = MathUtil.round(declinationInMinutes/60, 2);
		
		return declinationInDeg;

	}
	
	public List<Planet> getPlanetsForConsideration(boolean includeAsc) {

		List<Planet> planets = new ArrayList<Planet>();

		planets.add(Planet.JUP);
		planets.add(Planet.SAT);
		planets.add(Planet.SUN);
		planets.add(Planet.MON);
		planets.add(Planet.VEN);
		planets.add(Planet.MAR);
		planets.add(Planet.MER);
		
		if(includeAsc){
			planets.add(Planet.ASC);
		}

		return planets;
	}
}
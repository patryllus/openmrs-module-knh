/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.knh.fragment.controller.appointmentRescheduling;

import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.wrapper.PatientWrapper;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

/**
 * serves counseling fragment
 */
public class AppointmentsHistoryFragmentController {
	
	ConceptService conceptService = Context.getConceptService();
	
	SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy");
	
	public void controller(FragmentModel model, @FragmentParam("patient") Patient patient) {
		
		PatientWrapper patientWrapper = new PatientWrapper(patient);
		Form hivGreenCardForm = MetadataUtils.existing(Form.class, HivMetadata._Form.HIV_GREEN_CARD);
		List<Encounter> hivGreenCardEncounters = patientWrapper.allEncounters(hivGreenCardForm);
		TreeMap<String, List<SimpleObject>> orderedEpisodes = getSessionDetails(hivGreenCardEncounters);
		model.put("episodes", orderedEpisodes);
	}
	
	/**
	 * Extract TCA information from encounters and order them based on Date
	 * 
	 * @param encounters
	 * @return
	 */
	TreeMap<String, List<SimpleObject>> getSessionDetails(List<Encounter> encounters) {
		
		Integer sessionConcept = 1639;
		Integer dateOfFirstSessionConcept = 164891;
		TreeMap<String, List<SimpleObject>> orderedEpisodes = new TreeMap<String, List<SimpleObject>>();
		
		for (Encounter e : encounters) {
			Double sessionNum = null;
			Date episodeStartDate = null;
			String episodeStartDateString = null;
			for (Obs obs : e.getObs()) {
				if (obs.getConcept().getConceptId().equals(sessionConcept)) {
					sessionNum = obs.getValueNumeric();
				} else if (obs.getConcept().getConceptId().equals(dateOfFirstSessionConcept)) {
					episodeStartDate = obs.getValueDatetime();
				}
			}
			
			episodeStartDateString = episodeStartDate != null ? DATE_FORMAT.format(episodeStartDate) : DATE_FORMAT.format(e
			        .getEncounterDatetime());
			
			SimpleObject encData = SimpleObject.create("episodeStartDate", episodeStartDateString, "sessionNum",
			    sessionNum != null ? sessionNum.intValue() : "", "encDate", e.getEncounterDatetime(), "encounter",
			    Arrays.asList(e), "form", e.getForm());
			
			if (orderedEpisodes.containsKey(episodeStartDateString)) {
				orderedEpisodes.get(episodeStartDateString).add(encData);
			} else {
				List<SimpleObject> sessionList = new ArrayList<SimpleObject>();
				sessionList.add(encData);
				orderedEpisodes.put(episodeStartDateString, sessionList);
			}
		}
		
		return orderedEpisodes;
	}
	
}

package org.haxwell.dtim.techprofile.controllers;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.haxwell.dtim.techprofile.entities.TechProfile;
import org.haxwell.dtim.techprofile.entities.TechProfileLineItem;
import org.haxwell.dtim.techprofile.entities.TechProfileTopic;
import org.haxwell.dtim.techprofile.services.TechProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

@RestController
public class TechProfileAPIController {

	@Autowired
	TechProfileService techProfileService;
	
	TechProfileAPIController() {
		
	}
	
	@RequestMapping(value = { "/api/techprofile/{id}" }, method=RequestMethod.GET)
	public TechProfile get(@PathVariable Long id) {
		return techProfileService.get(id);
	}

	@RequestMapping(value = { "/api/techprofile/topics/new" }, method=RequestMethod.POST)
	public TechProfileTopic newTopic(HttpServletRequest request) {
		String name = request.getParameter("topicName");
		
		return techProfileService.addTopic(name);
	}

	public static final String L0DESCRIPTION = "l0Description";
	public static final String L1DESCRIPTION = "l1Description";
	public static final String L2DESCRIPTION = "l2Description";
	public static final String L3DESCRIPTION = "l3Description";
	
	@RequestMapping(value = { "/api/techprofile/topics/{id}/lineitem/new" }, method=RequestMethod.POST)
	public TechProfileLineItem newLineItem(HttpServletRequest request, @PathVariable Long id) {
		String name = request.getParameter("lineItemName");
		String l0desc = request.getParameter(L0DESCRIPTION);
		String l1desc = request.getParameter(L1DESCRIPTION);
		String l2desc = request.getParameter(L2DESCRIPTION);
		String l3desc = request.getParameter(L3DESCRIPTION);
		
		return techProfileService.addLineItem(id, name, l0desc, l1desc, l2desc, l3desc);
	}
	
	@RequestMapping(value = { "/api/techprofile/topic/{topicId}" }, method=RequestMethod.POST)
	public TechProfileTopic updateTopic(HttpServletRequest request, @PathVariable Long topicId) {
		TechProfileTopic rtn = null;
		
		try {
			String str = request.getReader().lines().collect(Collectors.joining());
			net.minidev.json.parser.JSONParser parser = new JSONParser();
			
			JSONObject obj = (JSONObject)parser.parse(str);
			
			String newName = ((JSONObject)obj.get("topic")).get("name").toString();
			
			rtn = techProfileService.updateTopic(
					topicId,
					newName);
			
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rtn;
	}
	
	@RequestMapping(value = { "/api/techprofile/lineitem/{lineItemId}" }, method=RequestMethod.POST)
	public TechProfileLineItem updateLineItem(HttpServletRequest request, @PathVariable Long lineItemId) {
		TechProfileLineItem rtn = null;
		
		try {
			String str = request.getReader().lines().collect(Collectors.joining());
			net.minidev.json.parser.JSONParser parser = new JSONParser();
			
			JSONObject obj = (JSONObject)((JSONObject)parser.parse(str)).get("lineItem");
			
			rtn = techProfileService.updateLineItem(
					Long.parseLong(obj.getAsString("id")),
					obj.getAsString("name"),
					obj.getAsString(L0DESCRIPTION),
					obj.getAsString(L1DESCRIPTION),
					obj.getAsString(L2DESCRIPTION),
					obj.getAsString(L3DESCRIPTION));
			
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rtn;
	}
	
	@RequestMapping(value = { "/api/techprofile/sequences" }, method=RequestMethod.POST)
	public boolean updateLineItemSequences(HttpServletRequest request) {
		try {
			String str = request.getReader().lines().collect(Collectors.joining());
			
			net.minidev.json.parser.JSONParser parser = new JSONParser();
			
			JSONObject obj = (JSONObject)parser.parse(str);
			JSONArray arr = (JSONArray)obj.get("arr");
			
			long numOfTopics = arr.size();
			
			for (long x=0; x < numOfTopics; x++) {
				JSONArray topicArr = (JSONArray)arr.get((int)x);
				long numOfLineItems = topicArr.size();
				
				for (long y=0; y < numOfLineItems; y++) {
					JSONArray liArr = (JSONArray)topicArr.get((int)y);
					String str2 = liArr.toString();
					str2 = str2.substring(1, str2.length() - 1);
					List<String> list = Arrays.asList(str2.split("\\s*,\\s*"));

					long[] larr = new long[5];

					larr[0] = Long.parseLong(list.get(0));
					larr[1] = Long.parseLong(list.get(1));
					larr[2] = Long.parseLong(list.get(2));
					larr[3] = Long.parseLong(list.get(3));
					larr[4] = Long.parseLong(list.get(4));

					techProfileService.updateSequencesRelatedToATopicAndItsLineItems(larr);
				}
			}
			
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}
	
	
}

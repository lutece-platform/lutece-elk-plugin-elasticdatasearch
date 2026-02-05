/*
 * Copyright (c) 2002-2025, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
 	
 
package fr.paris.lutece.plugins.elasticdatasearch.web;

import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.AbstractPaginator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.paris.lutece.plugins.elasticdatasearch.business.Fieldsearch;
import fr.paris.lutece.plugins.elasticdatasearch.business.FieldsearchHome;
import fr.paris.lutece.plugins.elasticdatasearch.business.FormResponseElastic;
import fr.paris.lutece.plugins.elasticdatasearch.business.Person;
import fr.paris.lutece.plugins.libraryelastic.business.search.SearchRequest;
import fr.paris.lutece.plugins.libraryelastic.util.Elastic;
import fr.paris.lutece.plugins.libraryelastic.util.ElasticClientException;
import fr.paris.lutece.plugins.unittree.business.unit.Unit;
import fr.paris.lutece.plugins.unittree.service.unit.IUnitService;
import fr.paris.lutece.plugins.unittree.service.unit.IUnitUserService;
import fr.paris.lutece.plugins.unittree.service.unit.UnitService;

/**
 * This class provides the user interface to manage Fieldsearch features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageFieldsearchs.jsp", controllerPath = "jsp/admin/plugins/elasticdatasearch/", right = "ELASTICDATASEARCH_MANAGEMENT" )
public class FieldsearchJspBean extends AbstractJspBean <Integer, Fieldsearch>
{

	// Rights
	public static final String RIGHT_MANAGEELASTICDATASEARCH = "ELASTICDATASEARCH_MANAGEMENT";
		
    // Templates
    private static final String TEMPLATE_MANAGE_FIELDSEARCHS = "/admin/plugins/elasticdatasearch/manage_fieldsearchs.html";
    private static final String TEMPLATE_CREATE_FIELDSEARCH = "/admin/plugins/elasticdatasearch/create_fieldsearch.html";
    private static final String TEMPLATE_MODIFY_FIELDSEARCH = "/admin/plugins/elasticdatasearch/modify_fieldsearch.html";

    // Parameters
    private static final String PARAMETER_ID_FIELDSEARCH = "id";
    private static final String PARAMETER_AGENT_NAME = "agentName";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_FIELDSEARCHS = "elasticdatasearch.manage_fieldsearchs.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_FIELDSEARCH = "elasticdatasearch.modify_fieldsearch.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_FIELDSEARCH = "elasticdatasearch.create_fieldsearch.pageTitle";
    private static final String PROPERTY_SEARCH_SIZE = "elasticdatasearch.search.size";
    private static final String PROPERTY_SEARCH_INDEX_NAME = "elasticdatasearch.search.index";
    
    private static final String PROPERTY_INDEX_URL = "elasticdatasearch.index.url";
    private static final String PROPERTY_LIST_SEARCHPARAM = "elasticdatasearch.searchFields";

    // Markers
    private static final String MARK_FIELDSEARCH_LIST = "fieldsearch_list";
    private static final String MARK_FIELDSEARCH = "fieldsearch";

    private static final String JSP_MANAGE_FIELDSEARCHS = "jsp/admin/plugins/elasticdatasearch/ManageFieldsearchs.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_FIELDSEARCH = "elasticdatasearch.message.confirmRemoveFieldsearch";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "elasticdatasearch.model.entity.fieldsearch.attribute.";

    // Views
    private static final String VIEW_MANAGE_FIELDSEARCHS = "manageFieldsearchs";
    private static final String VIEW_CREATE_FIELDSEARCH = "createFieldsearch";
    private static final String VIEW_MODIFY_FIELDSEARCH = "modifyFieldsearch";

    // Actions
    private static final String ACTION_CREATE_FIELDSEARCH = "createFieldsearch";
    private static final String ACTION_MODIFY_FIELDSEARCH = "modifyFieldsearch";
    private static final String ACTION_REMOVE_FIELDSEARCH = "removeFieldsearch";
    private static final String ACTION_CONFIRM_REMOVE_FIELDSEARCH = "confirmRemoveFieldsearch";

    // Infos
    private static final String INFO_FIELDSEARCH_CREATED = "elasticdatasearch.info.fieldsearch.created";
    private static final String INFO_FIELDSEARCH_UPDATED = "elasticdatasearch.info.fieldsearch.updated";
    private static final String INFO_FIELDSEARCH_REMOVED = "elasticdatasearch.info.fieldsearch.removed";
    
    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";
    
    // Session variable to store working values
    private Fieldsearch _fieldsearch;
    private List<Integer> _listIdFieldsearchs;
    private HashMap<String,String> _mapFilterCriteria = new HashMap<>();
    private String _optionOrderBy;
    private String _strOrderByASCorDESC;
    private String _strRequestElastic;
    
    private static final String BEAN_UNIT_USER_SERVICE = "unittree.unitUserService";
    IUnitUserService _unitUserService = SpringContextService.getBean( BEAN_UNIT_USER_SERVICE );
 	IUnitService _unitService = SpringContextService.getBean( UnitService.BEAN_UNIT_SERVICE );
    
    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_FIELDSEARCHS, defaultView = true )
    public String getManageFieldsearchs( HttpServletRequest request )
    {
        _fieldsearch = null;
        String strOrderByRequest = "";
        String strSearchDate = "";
        String strSearchParam = "";
        String strSearchCloseRequest = "";
        //Liste des noms d'agents
        List<String> lstAgentNames = new ArrayList<String>();
        List<String> lstAgentNamesAndSOI  = new ArrayList<String>();
        
        // new search only if in pagination mode
        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX) == null )
        {
        	// if sorting request : new search with the existing filter criteria, ordered 
        	// example of order by parameter : orderby=name
        	if ( StringUtils.isNotBlank( (String)request.getParameter(PARAMETER_SEARCH_ORDER_BY) ) )
        	{
        		
        		String strOrderByColumn =  (String)request.getParameter(PARAMETER_SEARCH_ORDER_BY);
        		_optionOrderBy = strOrderByColumn;
        		String strSortMode = getSortMode(); 
        		_strOrderByASCorDESC = strSortMode;
        		if ( !strOrderByColumn.equals("timestamp") )
        			strOrderByRequest =  ", \"sort\": [ { \"" + strOrderByColumn + ".keyword\": { \"order\":\"" + strSortMode.trim() + "\" } } ]";
        		else
        			strOrderByRequest =  ", \"sort\": [ { \"" + strOrderByColumn + "\": { \"order\":\"" + strSortMode.trim() + "\" } } ]";
        		_listIdFieldsearchs = FieldsearchHome.getIdFieldsearchsList( _mapFilterCriteria, strOrderByColumn, strSortMode );
               	
	       	}
	       	else
	       	{
	       		// reload the filter criteria and search
	       		_mapFilterCriteria = (HashMap<String, String>) getFilterCriteriaFromRequest( request );
	       		_listIdFieldsearchs = FieldsearchHome.getIdFieldsearchsList( _mapFilterCriteria, null ,null);
	       	}
        	
        	//set CurrentPageIndex of Paginator to null in aim of displays the first page of results
        	resetCurrentPageIndexOfPaginator();
        }
        
        else if ( StringUtils.isNotBlank( _optionOrderBy ) )
    	{
    		
    		String strOrderByColumn =  _optionOrderBy;
    		String strSortMode = _strOrderByASCorDESC;
    		if ( !strOrderByColumn.equals("timestamp") )
    			strOrderByRequest =  ", \"sort\": [ { \"" + strOrderByColumn + ".keyword\": { \"order\":\"" + strSortMode.trim() + "\" } } ]";
    		else
    			strOrderByRequest =  ", \"sort\": [ { \"" + strOrderByColumn + "\": { \"order\":\"" + strSortMode.trim() + "\" } } ]";
    		_listIdFieldsearchs = FieldsearchHome.getIdFieldsearchsList( _mapFilterCriteria, strOrderByColumn, strSortMode );
           	
       	}

        //_mapFilterCriteria = (HashMap<String, String>) getFilterCriteriaFromRequest( request );
        
        
        
        Elastic elastic = new Elastic( AppPropertiesService.getProperty( PROPERTY_INDEX_URL ) );
        SearchRequest searchRequest =  new SearchRequest();
        String strResponseJson = "";
        String strPropertyFieldSearch = AppPropertiesService.getProperty( PROPERTY_LIST_SEARCHPARAM );
        List<String> lstSearchField = Arrays.asList( strPropertyFieldSearch.split( "," ) );
        List<String> lstSearchFieldParameter = new ArrayList<String>();
        
        String strDateSince = request.getParameter("filter_timestamp");
        //String strTypeDemande = request.getParameter("formName");
        
        for ( String strParamSearch : lstSearchField )
        {
        	if ( request.getParameter( "filter_" + strParamSearch ) != null && !request.getParameter( "filter_" + strParamSearch ).isEmpty( ) && !strParamSearch.equals("timestamp") )
        	{
        		lstSearchFieldParameter.add( strParamSearch );
        	}
        	/*
        	else if ( _mapFilterCriteria.get( strParamSearch ) != null )
        	{
        		lstSearchFieldParameter.add( strParamSearch );
        	}
        	*/
        }
        
        String searchRequestMultiParameter = "{\n"
		+ "  \"query\": {\n"
		+ "    \"bool\": {\n" ;
		
		//filtre sur date
		if ( strDateSince != null && !strDateSince.isEmpty() )
        {
        	//searchRequestMultiParameter = searchRequestMultiParameter
        	//		+ "   \"filter\" : [ {  \"range\": { \"timestamp\": { \"gt\": \"" + strDateSince + "\" } } } ] , ";
            strSearchDate = "   \"filter\" : [ {  \"range\": { \"timestamp\": { \"gt\": \"" + strDateSince + "\" } } } ] , ";
        }
		
		
		
		
		
		//searchRequestMultiParameter = searchRequestMultiParameter + "      \"should\": [\n";
		//searchRequestMultiParameter = searchRequestMultiParameter + "      \"must\": [\n";
		if ( !lstSearchFieldParameter.isEmpty() )
		{
			strSearchParam = "      \"must\": [\n";
	        for (int i = 0; i < lstSearchFieldParameter.size(); i++) 
	        {
	        	boolean isLast = (i == lstSearchFieldParameter.size() - 1);
	        	if ( lstSearchFieldParameter.get(i).equals( "formName" )  )
	        	{
	        		String unescapedUrlParam = StringEscapeUtils.unescapeHtml4( request.getParameter( "filter_" + lstSearchFieldParameter.get(i) ) );
	        		strSearchParam = strSearchParam
	        		+ "  {\n"
	        		+ "          \"query_string\": {\n"
	        		+ "      \"query\": \"" + unescapedUrlParam +"\",\n"
	        		+ "            \"fields\": [\"" + lstSearchFieldParameter.get(i) + "\"],\n"
	        		+ "            \"default_operator\": \"AND\"\n"
	        		+ "          }\n";
	        	}
	        	else
	        	{
	        		strSearchParam = strSearchParam
		    		+ "        {\n"
		    		+ "          \"query_string\": {\n"
		    		+ "      \"query\": \"" + request.getParameter( "filter_" + lstSearchFieldParameter.get(i) ) +"\",\n"
		    		+ "            \"fields\": [\"userResponses.*." + lstSearchFieldParameter.get(i) + "\"]\n"
		    		+ "          }\n";
	        	}
	    		if ( !isLast )
	    			strSearchParam = strSearchParam	+ "        },\n";
	
	        }
	        strSearchParam = strSearchParam
			+ "        }\n"
			+ "      ]\n";
		
	        //searchRequestMultiParameter = searchRequestMultiParameter + "      \"minimum_should_match\": 1\n"
	        //searchRequestMultiParameter = searchRequestMultiParameter
	        strSearchParam = strSearchParam 
        		+ "    }\n"
        		+ "  }";
		}
				int nSizeSearch = AppPropertiesService.getPropertyInt( PROPERTY_SEARCH_SIZE, 100);
        		strSearchCloseRequest = strSearchCloseRequest
        		+ ",\"size\": " + nSizeSearch + "\n"
        		+ "}"; 
        
        
        try {
        	//if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX) == null && !StringUtils.isNotBlank( (String)request.getParameter(PARAMETER_SEARCH_ORDER_BY) ) && request.getParameter( PARAMETER_AGENT_NAME ) == null )
        	if ( !strSearchDate.isEmpty() || !strSearchParam.isEmpty() )
        	{
        		searchRequestMultiParameter = searchRequestMultiParameter + strSearchDate + strSearchParam + strOrderByRequest + strSearchCloseRequest;
        		
        		_strRequestElastic = strSearchDate + strSearchParam;
        	}
        	else {
        		//searchRequestMultiParameter = _strRequestElastic;
        		searchRequestMultiParameter = searchRequestMultiParameter + _strRequestElastic + strOrderByRequest + strSearchCloseRequest;
        	}
 
        	
        	strResponseJson = elastic.search(AppPropertiesService.getProperty( PROPERTY_SEARCH_INDEX_NAME, "formsdatasource" ) , searchRequestMultiParameter);
		} catch (ElasticClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        ObjectMapper mapper = new ObjectMapper();
        String strPrettyJson = "";
        List<Person> lstPersons = new ArrayList<>();
        List<FormResponseElastic> lstFormResponseElastic = new ArrayList<>();
        
        //List<HashMap<String,String>> lstMapAttributes = new ArrayList<>();
        //List<HashMap<String,String>> lstMapMetaDataAttributes = new ArrayList<>();
        
        try {
        	//strPrettyJson = mapper.writerWithDefaultPrettyPrinter()
			//                          .writeValueAsString(strResponseJson);
        	
        	JsonNode root = mapper.readTree(strResponseJson);

            

            // Si ton JSON est un tableau de résultats
            //for (JsonNode node : root) {
            for (JsonNode node : root.path("hits").path("hits")) {
                JsonNode userResponses = node.path("_source").path("userResponses");
                HashMap<String,String> mapAttributes = new HashMap<>();

                /*
                String nom = userResponses.path("*.Nom").asText();
                String prenom = userResponses.path("*.Prénom").asText();
                String matricule = userResponses.path("*.Matricule").asText();
				*/
                String nom = findBySuffix(userResponses, ".Nom");
                String prenom = findBySuffix(userResponses, ".Prénom");
                String matricule = findBySuffix(userResponses, ".Matricule");
                
                addMapAttributes(userResponses, mapAttributes);
                //lstMapAttributes.add(mapAttributes);
                
                lstPersons.add(new Person(nom, prenom, matricule));
                
                
                
                
                //MetaData
                JsonNode source = node.path("_source");
                HashMap<String,String> mapMetaDataAttributes = new HashMap<>();
                
                FormResponseElastic formElastic = new FormResponseElastic();
                formElastic.setFormName( findBySuffix( source, "formName" ) );
                formElastic.setStatus( findBySuffix( source, "workflowState" ) );
                formElastic.setDateForm( toLocalDate( findBySuffix( source, "timestamp" ) ) );
                formElastic.setMapAttributes( mapAttributes );
                
                //Verifier si user abilité
                String strUnitLabel = mapAttributes.get("Code UO");
                String strUnitCodeUGD = findBySuffix(userResponses, ".CodeUGD");
                
                Unit unitByCode = _unitService.getUnitByCode(strUnitLabel, true);
                
                AdminUser currentUser = AdminUserService.getAdminUser( request );
                List<Unit> unitsByIdUser = _unitService.getUnitsByIdUser( Integer.valueOf( currentUser.getUserId() ) , false);
                //_unitService.isUnitInList(unitByCode, unitsByIdUser);
                
                
                
                boolean isUnitValid = false;
                /*
                for ( Unit unitUser : unitsByIdUser )
                {
                	if ( unitByCode != null && unitUser.getCode().equals( unitByCode.getCode() ) )
	            	{
	            		isUnitValid = true;
	            	}
                	for ( Unit subUnit : _unitService.getAllSubUnits(unitUser, false) )
                	{
                	
		            	if ( unitByCode != null && ( unitUser.getCode().equals( unitByCode.getCode() ) || subUnit.getCode().equals( unitByCode.getCode() )  ) )
		            	//if ( unitByCode != null && unitUser.getCode().equals( unitByCode.getCode() ) )
		            	{
		            		isUnitValid = true;
		            	}
		            	
                	}
                }
                */
                if ( unitByCode != null )
                {
	                if ( _unitService.isUnitInList(unitByCode, unitsByIdUser) )
	                {
	            		isUnitValid = true;
	            	}
	                else {
	                	for ( Unit unitUser : unitsByIdUser )
	                	{
	                	
			            	if ( _unitService.isUnitInList(unitByCode, _unitService.getAllSubUnits(unitUser, false) ) )
			            	{
			            		isUnitValid = true;
			            	}
			            	
	                	}
	                }
                }
                if ( strUnitLabel == null || strUnitLabel.isEmpty() )
                	isUnitValid = true;
                
                if ( isUnitValid )
                {
                	lstFormResponseElastic.add( formElastic );
                	if ( !lstAgentNames.contains( mapAttributes.get("Nom") ) )
                    	lstAgentNames.add( mapAttributes.get("Nom") );
                	if ( !lstAgentNamesAndSOI.contains( mapAttributes.get("Nom") + " " + mapAttributes.get("Prénom") + " , SOI : " + mapAttributes.get("Matricule") ) )
                		lstAgentNamesAndSOI.add( mapAttributes.get("Nom") + " " + mapAttributes.get("Prénom") + " , SOI : " + mapAttributes.get("Matricule") );
                }
                
                addMapAttributes(source, mapMetaDataAttributes);
                mapMetaDataAttributes.put("attributesJson", userResponses.toString());
                //mapMetaDataAttributes.put("jjj",mapper.writeValueAsString(userResponses));
                //lstMapMetaDataAttributes.add(mapMetaDataAttributes);
            }
            
            for (Person p : lstPersons) {
                System.out.println(p);
            }
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       	
        
       	//Map<String, Object> model = getPaginatedListModel( request, MARK_FIELDSEARCH_LIST, _listIdFieldsearchs, JSP_MANAGE_FIELDSEARCHS );
        //lstFormResponseElastic.sort(FormResponseElastic.sortByAttributeKey("Nom"));
        if ( request.getParameter( PARAMETER_AGENT_NAME ) != null )
        	lstFormResponseElastic = FormResponseElastic.filterByAttribute( lstFormResponseElastic, "Nom", request.getParameter( PARAMETER_AGENT_NAME ) );
        Map<String, Object> model = getPaginatedListModelWithoutId( request, MARK_FIELDSEARCH_LIST, lstFormResponseElastic, JSP_MANAGE_FIELDSEARCHS );
             
        addSearchParameters(model,_mapFilterCriteria); //allow the persistence of search values in inputs search bar inputs
        
        ReferenceList lstTypeDemande = getTypeDemande(elastic);
        
        model.put( "lstSearchField", lstSearchField);
        model.put( "lstFormElastic", lstFormResponseElastic);
        model.put( "lstAgentsName", lstAgentNames );
        model.put( "lstAgentsNameAndMatricule", lstAgentNamesAndSOI );
        
        model.put( "lstTypeDemande" , lstTypeDemande);
        
        return getPage( PROPERTY_PAGE_TITLE_MANAGE_FIELDSEARCHS, TEMPLATE_MANAGE_FIELDSEARCHS, model );

    }
    
    private static String findBySuffix(JsonNode node, String suffix) {
    	Iterator<String> fieldNames = node.fieldNames();
        while (fieldNames.hasNext()) {
            String key = fieldNames.next();
            if (key.endsWith(suffix)) {
                JsonNode value = node.get(key);
                if (!value.isMissingNode() && !value.isNull()) {
                    return value.asText();
                }
            }
        }
        return null;
    }
    
    private static void addMapAttributes(JsonNode node, HashMap<String,String> mapattributes) {
    	Iterator<String> fieldNames = node.fieldNames();
        while (fieldNames.hasNext()) {
            String key = fieldNames.next();
            JsonNode value = node.get(key);
            if (!value.isMissingNode() && !value.isNull()) {
            	if ( key.contains(".") )
            		mapattributes.put(key.split("\\.")[1].replace("'", "\'"), value.asText());
            	else if ( key.contains("timestamp") ) {
					LocalDate localDate = toLocalDate( value.asText() );
					mapattributes.put(key, localDate.toString() );
				} 
            	else
            		mapattributes.put(key, value.asText() );
            }
        }
    }
    
    private static LocalDate toLocalDate(String timestampStr) {
        if ( timestampStr == null || timestampStr.isEmpty( ) ) {
            return null;
        }
        try {
            long millis = Long.parseLong(timestampStr);
            return Instant.ofEpochMilli(millis)
                          .atZone(ZoneId.systemDefault())
                          .toLocalDate();
        } catch (NumberFormatException e) {
            return null; // si ce n'est pas un timestamp
        }
    }
    
    private static ReferenceList getTypeDemande( Elastic elastic )
    {
    	ReferenceList lstTypeDemande = new ReferenceList();
    	lstTypeDemande.addItem("*", "Toutes");
    	String strResponseJson;
    	String strRequestTypeDemande = "{\n"
    			+ "  \"size\": 0,\n"
    			+ "  \"aggs\": {\n"
    			+ "    \"distinct_form_names\": {\n"
    			+ "      \"terms\": {\n"
    			+ "        \"field\": \"formName.keyword\",\n"
    			+ "        \"size\": 1000\n"
    			+ "      }\n"
    			+ "    }\n"
    			+ "  }\n"
    			+ "}";
    	
    	try {
        	strResponseJson = elastic.search("formsdatasource", strRequestTypeDemande);
        	
        	ObjectMapper mapper = new ObjectMapper();
        	JsonNode root = mapper.readTree(strResponseJson);

            

            // Si ton JSON est un tableau de résultats
            //for (JsonNode node : root) {
            for (JsonNode node : root.path("aggregations").path("distinct_form_names").path("buckets") ) {
            	
            	JsonNode value = node.get("key");
            	lstTypeDemande.addItem( value.asText( ), value.asText() );
            }
        	
        	
		} catch (ElasticClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	

    	lstTypeDemande.sort( Comparator.comparing( ReferenceItem::getName, String.CASE_INSENSITIVE_ORDER ) );

    	
    	//Collections.sort( listMessages );
    	return lstTypeDemande;
    }

	/**
     * Get Items from Ids list
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
	@Override
	List<Fieldsearch> getItemsFromIds( List<Integer> listIds ) 
	{
		List<Fieldsearch> listFieldsearch = FieldsearchHome.getFieldsearchsListByIds( listIds );
		
		// keep original order
        return listFieldsearch.stream()
                 .sorted(Comparator.comparingInt( notif -> listIds.indexOf( notif.getId())))
                 .collect(Collectors.toList());
	}
	
	@Override
	int getPluginDefaultNumberOfItemPerPage( ) {
		return AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 50 );
	}
    
    /**
    * reset the _listIdFieldsearchs list
    */
    public void resetListId( )
    {
    	_listIdFieldsearchs = new ArrayList<>( );
    }

    /**
     * Returns the form to create a fieldsearch
     *
     * @param request The Http request
     * @return the html code of the fieldsearch form
     */
    @View( VIEW_CREATE_FIELDSEARCH )
    public String getCreateFieldsearch( HttpServletRequest request )
    {
        _fieldsearch = ( _fieldsearch != null ) ? _fieldsearch : new Fieldsearch(  );

        Map<String, Object> model = getModel(  );
        model.put( MARK_FIELDSEARCH, _fieldsearch );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_FIELDSEARCH ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_FIELDSEARCH, TEMPLATE_CREATE_FIELDSEARCH, model );
    }

    /**
     * Process the data capture form of a new fieldsearch
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_FIELDSEARCH )
    public String doCreateFieldsearch( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _fieldsearch, request, getLocale( ) );
        

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_FIELDSEARCH ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _fieldsearch, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_FIELDSEARCH );
        }

        FieldsearchHome.create( _fieldsearch );
        addInfo( INFO_FIELDSEARCH_CREATED, getLocale(  ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_FIELDSEARCHS );
    }

    /**
     * Manages the removal form of a fieldsearch whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_FIELDSEARCH )
    public String getConfirmRemoveFieldsearch( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_FIELDSEARCH ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_FIELDSEARCH ) );
        url.addParameter( PARAMETER_ID_FIELDSEARCH, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_FIELDSEARCH, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a fieldsearch
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage fieldsearchs
     */
    @Action( ACTION_REMOVE_FIELDSEARCH )
    public String doRemoveFieldsearch( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_FIELDSEARCH ) );
        
        
        FieldsearchHome.remove( nId );
        addInfo( INFO_FIELDSEARCH_REMOVED, getLocale(  ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_FIELDSEARCHS );
    }

    /**
     * Returns the form to update info about a fieldsearch
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_FIELDSEARCH )
    public String getModifyFieldsearch( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_FIELDSEARCH ) );

        if ( _fieldsearch == null || ( _fieldsearch.getId(  ) != nId ) )
        {
            Optional<Fieldsearch> optFieldsearch = FieldsearchHome.findByPrimaryKey( nId );
            _fieldsearch = optFieldsearch.orElseThrow( ( ) -> new AppException(ERROR_RESOURCE_NOT_FOUND ) );
        }


        Map<String, Object> model = getModel(  );
        model.put( MARK_FIELDSEARCH, _fieldsearch );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_FIELDSEARCH ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_FIELDSEARCH, TEMPLATE_MODIFY_FIELDSEARCH, model );
    }

    /**
     * Process the change form of a fieldsearch
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_FIELDSEARCH )
    public String doModifyFieldsearch( HttpServletRequest request ) throws AccessDeniedException
    {   
        populate( _fieldsearch, request, getLocale( ) );
		
		
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_FIELDSEARCH ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _fieldsearch, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_FIELDSEARCH, PARAMETER_ID_FIELDSEARCH, _fieldsearch.getId( ) );
        }

        FieldsearchHome.update( _fieldsearch );
        addInfo( INFO_FIELDSEARCH_UPDATED, getLocale(  ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_FIELDSEARCHS );
    }
}

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


 package fr.paris.lutece.plugins.elasticdatasearch.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;


import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class provides instances management methods (create, find, ...) for Fieldsearch objects
 */
public final class FieldsearchHome
{
    // Static variable pointed at the DAO instance
    private static IFieldsearchDAO _dao = SpringContextService.getBean( "elasticdatasearch.fieldsearchDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "elasticdatasearch" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private FieldsearchHome(  )
    {
    }

    /**
     * Create an instance of the fieldsearch class
     * @param fieldsearch The instance of the Fieldsearch which contains the informations to store
     * @return The  instance of fieldsearch which has been created with its primary key.
     */
    public static Fieldsearch create( Fieldsearch fieldsearch )
    {
        _dao.insert( fieldsearch, _plugin );

        return fieldsearch;
    }

    /**
     * Update of the fieldsearch which is specified in parameter
     * @param fieldsearch The instance of the Fieldsearch which contains the data to store
     * @return The instance of the  fieldsearch which has been updated
     */
    public static Fieldsearch update( Fieldsearch fieldsearch )
    {
        _dao.store( fieldsearch, _plugin );

        return fieldsearch;
    }

    /**
     * Remove the fieldsearch whose identifier is specified in parameter
     * @param nKey The fieldsearch Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a fieldsearch whose identifier is specified in parameter
     * @param nKey The fieldsearch primary key
     * @return an instance of Fieldsearch
     */
    public static Optional<Fieldsearch> findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the fieldsearch objects and returns them as a list
     * @return the list which contains the data of all the fieldsearch objects
     */
    public static List<Fieldsearch> getFieldsearchsList( )
    {
        return _dao.selectFieldsearchsList( _plugin );
    }
    
        /**
     * Load the id of all the fieldsearch objects and returns them as a list
     * @param mapFilterCriteria contains search bar names/values inputs 
     * @param strColumnToOrder contains the column name to use for orderBy statement in case of sorting request (must be null)
     * @param strSortMode contains the sortMode in case of sorting request : ASC or DESC (must be null)
     * @return the list which contains the id of all the project objects
     */
    public static List<Integer> getIdFieldsearchsList( Map <String,String> mapFilterCriteria, String strColumnToOrder, String strSortMode )
    {
        return _dao.selectIdFieldsearchsList( _plugin,mapFilterCriteria,strColumnToOrder,strSortMode );
    }
    
    /**
     * Load the data of all the fieldsearch objects and returns them as a referenceList
     * @return the referenceList which contains the data of all the fieldsearch objects
     */
    public static ReferenceList getFieldsearchsReferenceList( )
    {
        return _dao.selectFieldsearchsReferenceList( _plugin );
    }
    
	
    /**
     * Load the data of all the avant objects and returns them as a list
     * @param listIds liste of ids
     * @return the list which contains the data of all the avant objects
     */
    public static List<Fieldsearch> getFieldsearchsListByIds( List<Integer> listIds )
    {
        return _dao.selectFieldsearchsListByIds( _plugin, listIds );
    }

}


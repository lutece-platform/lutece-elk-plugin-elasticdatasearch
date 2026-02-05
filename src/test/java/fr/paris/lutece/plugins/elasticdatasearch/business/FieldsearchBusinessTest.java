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
 *"
 * License 1.0
 */

package fr.paris.lutece.plugins.elasticdatasearch.business;

import fr.paris.lutece.test.LuteceTestCase;

import java.util.Optional;


/**
 * This is the business class test for the object Fieldsearch
 */
public class FieldsearchBusinessTest extends LuteceTestCase
{
    private static final String NAME1 = "Name1";
    private static final String NAME2 = "Name2";
	private static final boolean MANDATORY1 = true;
    private static final boolean MANDATORY2 = false;

	/**
	* test Fieldsearch
	*/
    public void testBusiness(  )
    {
        // Initialize an object
        Fieldsearch fieldsearch = new Fieldsearch();
        fieldsearch.setName( NAME1 );
        fieldsearch.setMandatory( MANDATORY1 );

        // Create test
        FieldsearchHome.create( fieldsearch );
        Optional<Fieldsearch> optFieldsearchStored = FieldsearchHome.findByPrimaryKey( fieldsearch.getId( ) );
        Fieldsearch fieldsearchStored = optFieldsearchStored.orElse( new Fieldsearch ( ) );
        assertEquals( fieldsearchStored.getName( ) , fieldsearch.getName( ) );
        assertEquals( fieldsearchStored.getMandatory( ) , fieldsearch.getMandatory( ) );

        // Update test
        fieldsearch.setName( NAME2 );
        fieldsearch.setMandatory( MANDATORY2 );
        FieldsearchHome.update( fieldsearch );
        optFieldsearchStored = FieldsearchHome.findByPrimaryKey( fieldsearch.getId( ) );
        fieldsearchStored = optFieldsearchStored.orElse( new Fieldsearch ( ) );
        
        assertEquals( fieldsearchStored.getName( ) , fieldsearch.getName( ) );
        assertEquals( fieldsearchStored.getMandatory( ) , fieldsearch.getMandatory( ) );

        // List test
        FieldsearchHome.getFieldsearchsList( );

        // Delete test
        FieldsearchHome.remove( fieldsearch.getId( ) );
        optFieldsearchStored = FieldsearchHome.findByPrimaryKey( fieldsearch.getId( ) );
        fieldsearchStored = optFieldsearchStored.orElse( null );
        assertNull( fieldsearchStored );
        
    }
    
    
     

}
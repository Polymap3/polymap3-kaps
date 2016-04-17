/*
 * polymap.org Copyright 2013 Polymap GmbH. All rights reserved.
 * 
 * This is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package org.polymap.kaps.ui.filter;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.opengis.filter.Filter;
import org.opengis.filter.Id;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.qi4j.api.query.Query;

import org.eclipse.swt.widgets.Display;

import org.eclipse.jface.dialogs.MessageDialog;

import org.polymap.core.model.Entity;
import org.polymap.core.project.ILayer;
import org.polymap.core.runtime.Polymap;

import org.polymap.rhei.data.entityfeature.AbstractEntityFilter;
import org.polymap.rhei.filter.FilterEditor;
import org.polymap.rhei.filter.IFilterEditorSite;

import org.polymap.kaps.model.KapsRepository;

/**
 * @author <a href="http://www.polymap.de">Steffen Stundzig</a>
 */
public abstract class KapsEntityFilter<T extends Entity>
        extends AbstractEntityFilter {

    private static Log           log = LogFactory.getLog( KapsEntityFilter.class );

    private Calendar             cal = new GregorianCalendar();

    private final int            maxResults;

    private final KapsRepository repository;


    public KapsEntityFilter( String id, ILayer layer, String label, Set<String> keywords, int maxResults,
            Class<T> entityClass ) {
        super( id, layer, label, keywords, Integer.MAX_VALUE, entityClass );
        this.maxResults = maxResults;
        this.repository = KapsRepository.instance();
    }


    protected KapsRepository repository() {
        return repository;
    }


    protected Date dayStart( Date date ) {
        if (date != null) {
            cal.setTime( date );
            cal.set( Calendar.HOUR_OF_DAY, 0 );
            cal.set( Calendar.MINUTE, 0 );
            cal.set( Calendar.SECOND, 0 );
            cal.set( Calendar.MILLISECOND, 0 );
            date = cal.getTime();
        }
        return date;
    }


    protected Date dayEnd( Date date ) {
        if (date != null) {
            cal.setTime( date );
            cal.set( Calendar.HOUR_OF_DAY, 23 );
            cal.set( Calendar.MINUTE, 59 );
            cal.set( Calendar.SECOND, 59 );
            cal.set( Calendar.MILLISECOND, 999 );
            date = cal.getTime();
        }
        return date;
    }

    private Display activeDisplay = null;


    protected final Display sessionDisplay() {
        return activeDisplay;
    }


    @Override
    public Filter createFilter( final IFilterEditorSite site ) {

        activeDisplay = Polymap.getSessionDisplay();

        ExecutorService exec = Executors.newSingleThreadExecutor();
        final Callable<Filter> call = new Callable<Filter>() {

            @Override
            public Filter call() {
                return superCreateFilter( site );
            }
        };
        final Future<Filter> future = exec.submit( call );
        try {
            return future.get( 10000, TimeUnit.MILLISECONDS );
        }
        catch (TimeoutException timeout) {
            showError( "Anfrage dauert zu lange", "Die Suchanfrage dauerte zu lange.\n"
                    + "Schränken Sie die Suche bitte weiter ein, um alle Treffer anzuzeigen." );
            log.error( "TIMEOUT KapsEntityFilter.createFilter();" + this.getClass().getName() + ":'"
                    + ((FilterEditor)site).getFieldValues().toString() + "'" );
            Thread.currentThread().dumpStack();
            return null;
        }
        catch (InterruptedException e) {
            throw new RuntimeException( e );
        }
        catch (ExecutionException e) {
            throw new RuntimeException( e );
        }
    }


    protected void showError( final String title, final String message ) {
        activeDisplay.asyncExec( new Runnable() {

            @Override
            public void run() {
                MessageDialog.openInformation( activeDisplay.getActiveShell(), title, message );
            }
        } );
    }


    private Filter superCreateFilter( final IFilterEditorSite site ) {
        return super.createFilter( site );
    }


    @Override
    protected Id buildFidFilter( Query<? extends Entity> entities, int maxResults ) {
        if (entities == null) {
            return ff.id( Collections.EMPTY_SET );
        }
        else {
            return super.buildFidFilter( entities, maxResults );
        }
    }


    @Override
    protected final Query<T> createQuery( IFilterEditorSite site ) {
        Query<T> query = createFilterQuery( site, repository() );
        if (query != null) {
            long count = query.count();
            if (count > maxResults) {
                showError( "Zu große Ergebnisliste",
                        "Die Suchanfrage ergab " + count + " Treffer.\n" + "Die Maximalanzahl für die Suche beträgt "
                                + maxResults + ".\n"
                                + "Schränken Sie die Suche weiter ein, um alle Treffer anzuzeigen." );
                return null;
            }
        }
        return query;
    }


    protected abstract Query<T> createFilterQuery( final IFilterEditorSite site, final KapsRepository repository );
}
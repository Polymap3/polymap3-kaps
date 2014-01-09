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

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.qi4j.api.query.Query;
import org.qi4j.api.query.QueryExpressions;
import org.qi4j.api.query.grammar.BooleanExpression;

import com.google.common.collect.Sets;

import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.jface.dialogs.MessageDialog;

import org.polymap.core.project.ILayer;
import org.polymap.core.runtime.Polymap;
import org.polymap.core.workbench.PolymapWorkbench;

import org.polymap.rhei.field.BetweenFormField;
import org.polymap.rhei.field.BetweenValidator;
import org.polymap.rhei.field.DateTimeFormField;
import org.polymap.rhei.field.PicklistFormField;
import org.polymap.rhei.field.SelectlistFormField;
import org.polymap.rhei.filter.IFilterEditorSite;

import org.polymap.kaps.model.KapsRepository;
import org.polymap.kaps.model.data.FlurstueckComposite;
import org.polymap.kaps.model.data.GemarkungComposite;
import org.polymap.kaps.model.data.GemeindeComposite;
import org.polymap.kaps.model.data.NutzungComposite;
import org.polymap.kaps.model.data.VertragComposite;
import org.polymap.kaps.model.data.VertragsdatenBaulandComposite;
import org.polymap.kaps.ui.NotNullValidator;

/**
 * 
 * @author <a href="http://www.polymap.de">Steffen Stundzig</a>
 */
public class VertragsdatenBaulandBRLFilter
        extends KapsEntityFilter {

    private static Log log = LogFactory.getLog( VertragsdatenBaulandBRLFilter.class );


    public VertragsdatenBaulandBRLFilter( ILayer layer ) {
        super( VertragsdatenBaulandBRLFilter.class.getName(), layer, "nach Nutzung, Datum, Gemeinde...", null, 10000, VertragsdatenBaulandComposite.class );
    }


    @Override
    public boolean hasControl() {
        return true;
    }


    @Override
    public Composite createControl( Composite parent, IFilterEditorSite site ) {
        Composite result = site.createStandardLayout( parent );

        site.addStandardLayout( site.newFormField( result, "datum", Date.class, new BetweenFormField(
                new DateTimeFormField(), new DateTimeFormField() ), new BetweenValidator( new NotNullValidator() ),
                "Vertragsdatum" ) );

        site.addStandardLayout( site.newFormField( result, "gemeinde", GemeindeComposite.class, new PicklistFormField(
                KapsRepository.instance().entitiesWithNames( GemeindeComposite.class ) ), null, "Gemeinde" ) );

        SelectlistFormField field = new SelectlistFormField( KapsRepository.instance().entitiesWithNames(
                NutzungComposite.class ) );
        field.setIsMultiple( true );
        Composite formField = site.newFormField( result, "nutzung", NutzungComposite.class, field,
                new NotNullValidator(), "Nutzung" );
        site.addStandardLayout( formField );
        ((FormData)formField.getLayoutData()).height = 200;
        ((FormData)formField.getLayoutData()).width = 100;

        return result;
    }


    @Override
    protected Query<? extends VertragsdatenBaulandComposite> createQuery( IFilterEditorSite site ) {

        List<NutzungComposite> nutzungen = (List<NutzungComposite>)site.getFieldValue( "nutzung" );
        GemeindeComposite gemeinde = (GemeindeComposite)site.getFieldValue( "gemeinde" );

        Object[] vertragsDatum = (Object[])site.getFieldValue( "datum" );
        BooleanExpression vertragsDatumExpr = null;
        if (vertragsDatum != null) {
            VertragComposite dateTemplate = QueryExpressions.templateFor( VertragComposite.class );
            BooleanExpression ge = vertragsDatum[0] != null ? QueryExpressions.ge( dateTemplate.vertragsDatum(),
                    dayStart( (Date)vertragsDatum[0] ) ) : null;

            BooleanExpression le = vertragsDatum[1] != null ? QueryExpressions.le( dateTemplate.vertragsDatum(),
                    dayEnd( (Date)vertragsDatum[1] ) ) : null;

            if (ge != null) {
                vertragsDatumExpr = ge;
            }
            if (le != null) {
                vertragsDatumExpr = vertragsDatumExpr == null ? le : QueryExpressions.and( ge, le );
            }
        }

        BooleanExpression fExpr = null;
        VertragsdatenBaulandComposite template = QueryExpressions.templateFor( VertragsdatenBaulandComposite.class );

        // if (nutzungen != null || gemeinde != null) {
        FlurstueckComposite flurTemplate = QueryExpressions.templateFor( FlurstueckComposite.class );

        // nach Vertragsdatum vorsortieren
        Set<VertragComposite> vertraegeNachDatum = null;
        if (vertragsDatumExpr != null) {
            vertraegeNachDatum = Sets.newHashSet();
            Query<VertragComposite> vertraege = KapsRepository.instance().findEntities( VertragComposite.class,
                    vertragsDatumExpr, 0, -1 );
            for (VertragComposite vertrag : vertraege) {
                vertraegeNachDatum.add( vertrag );
            }
        }

        // gemeinde
        BooleanExpression gExpr = null;
        if (gemeinde != null) {
            GemarkungComposite gemarkungTemplate = QueryExpressions.templateFor( GemarkungComposite.class );
            Query<GemarkungComposite> gemarkungen = KapsRepository.instance().findEntities( GemarkungComposite.class,
                    QueryExpressions.eq( gemarkungTemplate.gemeinde(), gemeinde ), 0, -1 );
            for (GemarkungComposite gemarkung : gemarkungen) {
                BooleanExpression newExpr = QueryExpressions.eq( flurTemplate.gemarkung(), gemarkung );
                if (gExpr == null) {
                    gExpr = newExpr;
                }
                else {
                    gExpr = QueryExpressions.or( gExpr, newExpr );
                }
            }
            // gemeinde gewählt, aber keine gemeinden gefunden
            if (gExpr == null) {
                gExpr = QueryExpressions.eq( flurTemplate.identity(), "unknown" );
            }
        }

        // nutzungen
        BooleanExpression nExpr = null;
        if (nutzungen != null) {
            for (NutzungComposite nutzung : nutzungen) {
                BooleanExpression newExpr = QueryExpressions.eq( flurTemplate.nutzung(), nutzung );
                if (nExpr == null) {
                    nExpr = newExpr;
                }
                else {
                    nExpr = QueryExpressions.or( nExpr, newExpr );
                }
            }
        }
        if (nExpr != null) {
            if (gExpr != null) {
                nExpr = QueryExpressions.and( nExpr, gExpr );
            }
        }
        else {
            nExpr = gExpr;
        }

        Query<FlurstueckComposite> flurstuecke = KapsRepository.instance().findEntities( FlurstueckComposite.class,
                nExpr, 0, -1 );
        
        Set<VertragComposite> vertraegeNachDatumUndFlurstueck = Sets.newHashSet();
        for (FlurstueckComposite fc : flurstuecke) {
            // mehrere Flurstücke können einem Vertrag angehören
            VertragComposite vertrag = fc.vertrag().get();
            if (vertrag != null) {
                if (vertraegeNachDatum == null || vertraegeNachDatum.contains( vertrag ) ) {
                    vertraegeNachDatumUndFlurstueck.add( vertrag );
                }
            }
        }
        
        if (vertraegeNachDatumUndFlurstueck.size() > 5000) {
            Polymap.getSessionDisplay().asyncExec( new Runnable() {

                public void run() {
                    MessageDialog.openError( PolymapWorkbench.getShellToParentOn(), "Zu viele Ergebnisse",
                            "Es wurden über 5000 Ergebnisse gefunden. Bitte schränken Sie die Suche weiter ein." );
                }
            } );
            return KapsRepository.instance().findEntities( VertragsdatenBaulandComposite.class,
                    QueryExpressions.eq( template.identity(), "unknown" ), 0, -1 );
        }
        for (VertragComposite vertrag : vertraegeNachDatumUndFlurstueck) {
            BooleanExpression newExpr = QueryExpressions.eq( template.vertrag(), vertrag );
            if (fExpr == null) {
                fExpr = newExpr;
            }
            else {
                fExpr = QueryExpressions.or( fExpr, newExpr );
            }
        }
        // wenn keine gefunden, ungültige Query erzeugen, damit auch keine
        // Verträge gefunden werden
        if (fExpr == null) {
            fExpr = QueryExpressions.eq( template.identity(), "unknown" );
        }
        // }

        return KapsRepository.instance().findEntities( VertragsdatenBaulandComposite.class, fExpr, 0, getMaxResults() );
    }
}

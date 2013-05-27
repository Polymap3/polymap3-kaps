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
package org.polymap.kaps.ui.form;

import java.text.NumberFormat;

import org.geotools.data.FeatureStore;
import org.opengis.feature.Feature;

import org.polymap.rhei.form.IFormEditorPage;
import org.polymap.rhei.form.IFormEditorPageSite;

import org.polymap.kaps.model.data.FlurstuecksdatenBaulandComposite;
import org.polymap.kaps.model.data.VertragComposite;
import org.polymap.kaps.ui.KapsDefaultFormEditorPage;

/**
 * @author <a href="http://www.polymap.de">Steffen Stundzig</a>
 */
public abstract class FlurstuecksdatenBaulandFormEditorPage
        extends KapsDefaultFormEditorPage
        implements IFormEditorPage {

    protected FlurstuecksdatenBaulandComposite vb;

    public FlurstuecksdatenBaulandFormEditorPage( String id, String title, Feature feature,
            FeatureStore featureStore ) {
        super( id, title, feature, featureStore );

        vb = repository.findEntity( FlurstuecksdatenBaulandComposite.class, feature
                .getIdentifier().getID() );
    }


    @Override
    public void createFormContent( IFormEditorPageSite site ) {
        super.createFormContent( site );

        VertragComposite kaufvertrag = vb.kaufvertrag().get();
        String nummer = EingangsNummerFormatter.format( kaufvertrag.eingangsNr().get());
        site.setEditorTitle( formattedTitle( "Flurstücksdaten Bauland", nummer, null ) );
        site.setFormTitle( formattedTitle( "erweiterte Flurstücksdaten - Bauland - für Vertrag", nummer,
                getTitle() ) );

    }

    protected NumberFormat getFormatter(int fractionDigits) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits( fractionDigits );
        nf.setMinimumFractionDigits( fractionDigits );
        nf.setMinimumIntegerDigits( 1 );
        return nf;
    }
}
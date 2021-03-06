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

import org.geotools.data.FeatureStore;
import org.opengis.feature.Feature;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.qi4j.api.entity.association.Association;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.core.runtime.IProgressMonitor;

import org.polymap.core.project.ui.util.SimpleFormData;
import org.polymap.core.runtime.event.EventManager;

import org.polymap.rhei.data.entityfeature.AssociationAdapter;
import org.polymap.rhei.field.IFormFieldLabel;
import org.polymap.rhei.field.IFormFieldListener;
import org.polymap.rhei.form.FormEditor;
import org.polymap.rhei.form.IFormEditorPage2;
import org.polymap.rhei.form.IFormEditorPageSite;

import org.polymap.kaps.model.data.EtageComposite;
import org.polymap.kaps.ui.FieldCalculation;
import org.polymap.kaps.ui.FieldListener;
import org.polymap.kaps.ui.FieldMultiplication;
import org.polymap.kaps.ui.FieldSummation;

/**
 * @author <a href="http://www.polymap.de">Steffen Stundzig</a>
 */
public class ErtragswertverfahrenErtraegeFormEditorPage
        extends ErtragswertverfahrenFormEditorPage
        implements IFormEditorPage2 {

    private static Log          log = LogFactory.getLog( ErtragswertverfahrenErtraegeFormEditorPage.class );

    @SuppressWarnings("unused")
    private FieldMultiplication line1multiplicator;

    @SuppressWarnings("unused")
    private FieldMultiplication line2multiplicator;

    @SuppressWarnings("unused")
    private FieldMultiplication line3multiplicator;

    @SuppressWarnings("unused")
    private FieldMultiplication line4multiplicator;

    @SuppressWarnings("unused")
    private FieldMultiplication line5multiplicator;

    @SuppressWarnings("unused")
    private FieldMultiplication line6multiplicator;

    @SuppressWarnings("unused")
    private FieldMultiplication line7multiplicator;

    @SuppressWarnings("unused")
    private FieldMultiplication line8multiplicator;

    @SuppressWarnings("unused")
    private FieldMultiplication line9multiplicator;

    @SuppressWarnings("unused")
    private FieldMultiplication line10multiplicator;

    @SuppressWarnings("unused")
    private FieldMultiplication line11multiplicator;

    @SuppressWarnings("unused")
    private FieldMultiplication line12multiplicator;

    @SuppressWarnings("unused")
    private FieldMultiplication line13multiplicator;

    @SuppressWarnings("unused")
    private FieldMultiplication line14multiplicator;

    @SuppressWarnings("unused")
    private FieldMultiplication line15multiplicator;

    @SuppressWarnings("unused")
    private FieldSummation      rohertragMonatSummation;

    @SuppressWarnings("unused")
    private FieldCalculation    rohertragJahrCalculation;

    @SuppressWarnings("unused")
    private FieldSummation      rohertragBruttoJahrCalculation;

    @SuppressWarnings("unused")
    private FieldCalculation    rohertragMonatCalculation;

    @SuppressWarnings("unused")
    private FieldListener       fieldListener;


    // private Double jahresBetriebskosten;

    // private InterEditorListener fieldListener;

    // private IFormFieldListener gemeindeListener;

    public ErtragswertverfahrenErtraegeFormEditorPage( FormEditor formEditor, Feature feature, FeatureStore featureStore ) {
        super( ErtragswertverfahrenErtraegeFormEditorPage.class.getName(), "Erträge", feature, featureStore );

        EventManager.instance().subscribe(
                fieldListener = new FieldListener( vb.jahresBetriebskosten(), vb.bruttoRohertragProJahr() ),
                new FieldListener.EventFilter( formEditor ) );
    }


    @Override
    public void afterDoLoad( IProgressMonitor monitor )
            throws Exception {
        fieldListener.flush( pageSite );
    }


    @Override
    public void dispose() {
        super.dispose();
        EventManager.instance().unsubscribe( fieldListener );
    }

    private boolean        initialized = false;

    private FieldSummation jahresBetriebskostenListener;


    // private IFormFieldListener anteiligeKostenListener;

    @SuppressWarnings("unchecked")
    @Override
    public void createFormContent( final IFormEditorPageSite site ) {
        super.createFormContent( site );

        Control newLine, lastLine = null;
        Composite parent = pageSite.getPageBody();

        // Section section = newSection( parent, "Bodenwertaufteilung" );
        // Composite client = (Composite)section.getClient();
        Composite client = parent;

        newLine = createLabel( client, "Etage", one().top( null ), SWT.CENTER );
        createLabel( client, "Fläche in m²", two().top( null ), SWT.CENTER );
        createLabel( client, "Wohnfläche?", three().top( null ), SWT.CENTER );
        createLabel( client, "Nettomiete/Monat in €/m²", four().top( null ), SWT.CENTER );
        createLabel( client, "Gesamtmiete/Monat in €", five().top( null ), SWT.CENTER );

        lastLine = newLine;
        newLine = createEtageField( vb.etageZeile1(), one().top( lastLine ), client );
        createFlaecheField( vb.flaecheZeile1(), two().top( lastLine ), client, true );
        createBooleanField( vb.wohnflaecheZeile1(), three().top( lastLine ), client );
        createPreisField( vb.mieteQmZeile1(), four().top( lastLine ), client, true );
        createPreisField( vb.miete1(), five().top( lastLine ), client, false );
        site.addFieldListener( line1multiplicator = new FieldMultiplication( site, 2, vb.flaecheZeile1(), vb
                .mieteQmZeile1(), vb.miete1() ) );

        lastLine = newLine;
        newLine = createEtageField( vb.etageZeile2(), one().top( lastLine ), client );
        createFlaecheField( vb.flaecheZeile2(), two().top( lastLine ), client, true );
        createBooleanField( vb.wohnflaecheZeile2(), three().top( lastLine ), client );
        createPreisField( vb.mieteQmZeile2(), four().top( lastLine ), client, true );
        createPreisField( vb.miete2(), five().top( lastLine ), client, false );
        site.addFieldListener( line2multiplicator = new FieldMultiplication( site, 2, vb.flaecheZeile2(), vb
                .mieteQmZeile2(), vb.miete2() ) );

        lastLine = newLine;
        newLine = createEtageField( vb.etageZeile3(), one().top( lastLine ), client );
        createFlaecheField( vb.flaecheZeile3(), two().top( lastLine ), client, true );
        createBooleanField( vb.wohnflaecheZeile3(), three().top( lastLine ), client );
        createPreisField( vb.mieteQmZeile3(), four().top( lastLine ), client, true );
        createPreisField( vb.miete3(), five().top( lastLine ), client, false );
        site.addFieldListener( line3multiplicator = new FieldMultiplication( site, 2, vb.flaecheZeile3(), vb
                .mieteQmZeile3(), vb.miete3() ) );

        lastLine = newLine;
        newLine = createEtageField( vb.etageZeile4(), one().top( lastLine ), client );
        createFlaecheField( vb.flaecheZeile4(), two().top( lastLine ), client, true );
        createBooleanField( vb.wohnflaecheZeile4(), three().top( lastLine ), client );
        createPreisField( vb.mieteQmZeile4(), four().top( lastLine ), client, true );
        createPreisField( vb.miete4(), five().top( lastLine ), client, false );
        site.addFieldListener( line4multiplicator = new FieldMultiplication( site, 2, vb.flaecheZeile4(), vb
                .mieteQmZeile4(), vb.miete4() ) );

        lastLine = newLine;
        newLine = createEtageField( vb.etageZeile5(), one().top( lastLine ), client );
        createFlaecheField( vb.flaecheZeile5(), two().top( lastLine ), client, true );
        createBooleanField( vb.wohnflaecheZeile5(), three().top( lastLine ), client );
        createPreisField( vb.mieteQmZeile5(), four().top( lastLine ), client, true );
        createPreisField( vb.miete5(), five().top( lastLine ), client, false );
        site.addFieldListener( line5multiplicator = new FieldMultiplication( site, 2, vb.flaecheZeile5(), vb
                .mieteQmZeile5(), vb.miete5() ) );

        lastLine = newLine;
        newLine = createEtageField( vb.etageZeile6(), one().top( lastLine ), client );
        createFlaecheField( vb.flaecheZeile6(), two().top( lastLine ), client, true );
        createBooleanField( vb.wohnflaecheZeile6(), three().top( lastLine ), client );
        createPreisField( vb.mieteQmZeile6(), four().top( lastLine ), client, true );
        createPreisField( vb.miete6(), five().top( lastLine ), client, false );
        site.addFieldListener( line6multiplicator = new FieldMultiplication( site, 2, vb.flaecheZeile6(), vb
                .mieteQmZeile6(), vb.miete6() ) );

        lastLine = newLine;
        newLine = createEtageField( vb.etageZeile7(), one().top( lastLine ), client );
        createFlaecheField( vb.flaecheZeile7(), two().top( lastLine ), client, true );
        createBooleanField( vb.wohnflaecheZeile7(), three().top( lastLine ), client );
        createPreisField( vb.mieteQmZeile7(), four().top( lastLine ), client, true );
        createPreisField( vb.miete7(), five().top( lastLine ), client, false );
        site.addFieldListener( line7multiplicator = new FieldMultiplication( site, 2, vb.flaecheZeile7(), vb
                .mieteQmZeile7(), vb.miete7() ) );

        lastLine = newLine;
        newLine = createEtageField( vb.etageZeile8(), one().top( lastLine ), client );
        createFlaecheField( vb.flaecheZeile8(), two().top( lastLine ), client, true );
        createBooleanField( vb.wohnflaecheZeile8(), three().top( lastLine ), client );
        createPreisField( vb.mieteQmZeile8(), four().top( lastLine ), client, true );
        createPreisField( vb.miete8(), five().top( lastLine ), client, false );
        site.addFieldListener( line8multiplicator = new FieldMultiplication( site, 2, vb.flaecheZeile8(), vb
                .mieteQmZeile8(), vb.miete8() ) );

        lastLine = newLine;
        newLine = createEtageField( vb.etageZeile9(), one().top( lastLine ), client );
        createFlaecheField( vb.flaecheZeile9(), two().top( lastLine ), client, true );
        createBooleanField( vb.wohnflaecheZeile9(), three().top( lastLine ), client );
        createPreisField( vb.mieteQmZeile9(), four().top( lastLine ), client, true );
        createPreisField( vb.miete9(), five().top( lastLine ), client, false );
        site.addFieldListener( line9multiplicator = new FieldMultiplication( site, 2, vb.flaecheZeile9(), vb
                .mieteQmZeile9(), vb.miete9() ) );

        lastLine = newLine;
        newLine = createEtageField( vb.etageZeile10(), one().top( lastLine ), client );
        createFlaecheField( vb.flaecheZeile10(), two().top( lastLine ), client, true );
        createBooleanField( vb.wohnflaecheZeile10(), three().top( lastLine ), client );
        createPreisField( vb.mieteQmZeile10(), four().top( lastLine ), client, true );
        createPreisField( vb.miete10(), five().top( lastLine ), client, false );
        site.addFieldListener( line10multiplicator = new FieldMultiplication( site, 2, vb.flaecheZeile10(), vb
                .mieteQmZeile10(), vb.miete10() ) );

        lastLine = newLine;
        newLine = createEtageField( vb.etageZeile11(), one().top( lastLine ), client );
        createFlaecheField( vb.flaecheZeile11(), two().top( lastLine ), client, true );
        createBooleanField( vb.wohnflaecheZeile11(), three().top( lastLine ), client );
        createPreisField( vb.mieteQmZeile11(), four().top( lastLine ), client, true );
        createPreisField( vb.miete11(), five().top( lastLine ), client, false );
        site.addFieldListener( line11multiplicator = new FieldMultiplication( site, 2, vb.flaecheZeile11(), vb
                .mieteQmZeile11(), vb.miete11() ) );

        lastLine = newLine;
        newLine = createEtageField( vb.etageZeile12(), one().top( lastLine ), client );
        createFlaecheField( vb.flaecheZeile12(), two().top( lastLine ), client, true );
        createBooleanField( vb.wohnflaecheZeile12(), three().top( lastLine ), client );
        createPreisField( vb.mieteQmZeile12(), four().top( lastLine ), client, true );
        createPreisField( vb.miete12(), five().top( lastLine ), client, false );
        site.addFieldListener( line12multiplicator = new FieldMultiplication( site, 2, vb.flaecheZeile12(), vb
                .mieteQmZeile12(), vb.miete12() ) );

        lastLine = newLine;
        newLine = createEtageField( vb.etageZeile13(), one().top( lastLine ), client );
        createFlaecheField( vb.flaecheZeile13(), two().top( lastLine ), client, true );
        createBooleanField( vb.wohnflaecheZeile13(), three().top( lastLine ), client );
        createPreisField( vb.mieteQmZeile13(), four().top( lastLine ), client, true );
        createPreisField( vb.miete13(), five().top( lastLine ), client, false );
        site.addFieldListener( line13multiplicator = new FieldMultiplication( site, 2, vb.flaecheZeile13(), vb
                .mieteQmZeile13(), vb.miete13() ) );

        lastLine = newLine;
        newLine = createEtageField( vb.etageZeile14(), one().top( lastLine ), client );
        createFlaecheField( vb.flaecheZeile14(), two().top( lastLine ), client, true );
        createBooleanField( vb.wohnflaecheZeile14(), three().top( lastLine ), client );
        createPreisField( vb.mieteQmZeile14(), four().top( lastLine ), client, true );
        createPreisField( vb.miete14(), five().top( lastLine ), client, false );
        site.addFieldListener( line14multiplicator = new FieldMultiplication( site, 2, vb.flaecheZeile14(), vb
                .mieteQmZeile14(), vb.miete14() ) );

        lastLine = newLine;
        newLine = createEtageField( vb.etageZeile15(), one().top( lastLine ), client );
        createFlaecheField( vb.flaecheZeile15(), two().top( lastLine ), client, true );
        createBooleanField( vb.wohnflaecheZeile15(), three().top( lastLine ), client );
        createPreisField( vb.mieteQmZeile15(), four().top( lastLine ), client, true );
        createPreisField( vb.miete15(), five().top( lastLine ), client, false );
        site.addFieldListener( line15multiplicator = new FieldMultiplication( site, 2, vb.flaecheZeile15(), vb
                .mieteQmZeile15(), vb.miete15() ) );
        
        lastLine = newLine;
        newLine = createTextField( vb.ertraegeZeile16(), one().top( lastLine ), client );
        createPreisField( vb.miete16(), five().top( lastLine ), client, true );

        lastLine = newLine;
        newLine = createTextField( vb.ertraegeZeile17(), one().top( lastLine ), client );
        createPreisField( vb.miete17(), five().top( lastLine ), client, true );

        lastLine = newLine;
        newLine = createTextField( vb.ertraegeZeile18(), one().top( lastLine ), client );
        createPreisField( vb.miete18(), five().top( lastLine ), client, true );

        lastLine = newLine;
        newLine = createLabel( client, "monatlicher Rohertrag (netto) in €", one().right( 83 ).top( lastLine, 30 ),
                SWT.RIGHT );
        createPreisField( vb.nettoRohertragProMonat(), five().top( lastLine, 30 ), client, false );
        site.addFieldListener( rohertragMonatSummation = new FieldSummation( site, 2, vb.nettoRohertragProMonat(), vb
                .miete1(), vb.miete2(), vb.miete3(), vb.miete4(), vb.miete5(), vb.miete6(), vb.miete7(), vb.miete8(),
                vb.miete9(), vb.miete10(), vb.miete11(), vb.miete12(), vb.miete13(), vb.miete14(), vb.miete15(), vb
                        .miete16(), vb.miete17(), vb.miete18() ) );

        lastLine = newLine;
        newLine = createLabel( client, "jährlicher Rohertrag (netto) in €", one().right( 83 ).top( lastLine ),
                SWT.RIGHT );
        createPreisField( vb.nettoRohertragProJahr(), five().top( lastLine ), client, false );
        site.addFieldListener( rohertragJahrCalculation = new FieldCalculation( site, 2, vb.nettoRohertragProJahr(), vb
                .nettoRohertragProMonat() ) {

            @Override
            protected Double calculate( ValueProvider values ) {
                Double v = values.get( vb.nettoRohertragProMonat() );
                return v != null ? v * 12 : null;
            }
        } );

        lastLine = newLine;
        newLine = createLabel( client, "jährliche Betriebskosten in €", one().right( 83 ).top( lastLine ), SWT.RIGHT );
        createPreisField( vb.jahresBetriebskostenE(), five().top( lastLine ), client, false );
        site.addFieldListener( jahresBetriebskostenListener = new FieldSummation( site, 2, vb.jahresBetriebskostenE(),
                vb.jahresBetriebskosten() ) );

        lastLine = newLine;
        newLine = createLabel( client, "jährlicher Rohertrag (brutto) in €", one().right( 83 ).top( lastLine ),
                SWT.RIGHT );
        createPreisField( vb.bruttoRohertragProJahr(), five().top( lastLine ), client, false );
        site.addFieldListener( rohertragBruttoJahrCalculation = new FieldSummation( site, 2, vb
                .bruttoRohertragProJahr(), vb.nettoRohertragProJahr(), vb.jahresBetriebskostenE() ) );

        lastLine = newLine;
        newLine = createLabel( client, "monatlicher Rohertrag (brutto) in €", one().right( 83 ).top( lastLine ),
                SWT.RIGHT );
        createPreisField( vb.bruttoRohertragProMonat(), five().top( lastLine ), client, false );
        site.addFieldListener( rohertragMonatCalculation = new FieldCalculation( site, 2, vb.bruttoRohertragProMonat(),
                vb.bruttoRohertragProJahr() ) {

            @Override
            protected Double calculate( ValueProvider values ) {
                Double v = values.get( vb.bruttoRohertragProJahr() );
                return v != null ? v / 12 : null;
            }
        } );

        initialized = true;
    }


    private Composite createEtageField( Association<EtageComposite> property, SimpleFormData data, Composite client ) {
        return newFormField( IFormFieldLabel.NO_LABEL ).setEnabled( true )
                .setProperty( new AssociationAdapter<EtageComposite>( property ) )
                .setField( namedAssocationsPicklist( EtageComposite.class ) ).setLayoutData( data.create() )
                .setParent( client ).create();
    }


    @Override
    protected SimpleFormData one() {
        return new SimpleFormData( SPACING ).left( 0 ).right( 45 );
    }


    @Override
    protected SimpleFormData two() {
        return new SimpleFormData( SPACING ).left( 45 ).right( 60 );
    }


    @Override
    protected SimpleFormData three() {
        return new SimpleFormData( SPACING ).left( 60 ).right( 68 );
    }


    @Override
    protected SimpleFormData four() {
        return new SimpleFormData( SPACING ).left( 68 ).right( 83 );
    }


    @Override
    protected SimpleFormData five() {
        return new SimpleFormData( SPACING ).left( 83 ).right( 100 );
    }
}

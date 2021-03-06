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

import org.eclipse.swt.widgets.Composite;

import org.polymap.core.project.ui.util.SimpleFormData;

import org.polymap.rhei.data.entityfeature.AssociationAdapter;
import org.polymap.rhei.data.entityfeature.PropertyAdapter;
import org.polymap.rhei.field.CheckboxFormField;
import org.polymap.rhei.field.DateTimeFormField;
import org.polymap.rhei.field.IFormFieldListener;
import org.polymap.rhei.field.StringFormField;
import org.polymap.rhei.field.TextFormField;
import org.polymap.rhei.form.IFormEditorPageSite;

import org.polymap.kaps.model.data.KaeuferKreisComposite;
import org.polymap.kaps.model.data.VertragsArtComposite;
import org.polymap.kaps.ui.FieldCalculation;
import org.polymap.kaps.ui.MyNumberValidator;
import org.polymap.kaps.ui.NotNullMyNumberValidator;
import org.polymap.kaps.ui.NotNullValidator;

/**
 * @author <a href="http://www.polymap.de">Steffen Stundzig</a>
 */
public class Kaufvertrag1FormEditorPage
        extends KaufvertragFormEditorPage {

    private IFormFieldListener vollpreisRefresher;


    public Kaufvertrag1FormEditorPage( Feature feature, FeatureStore featureStore ) {
        super( Kaufvertrag1FormEditorPage.class.getName(), "Vertragsdaten", feature, featureStore );
    }


    @Override
    protected void updateEingangsNummer( Integer nummer ) {
        super.updateEingangsNummer( nummer );
        pageSite.setFieldValue( kaufvertrag.eingangsNr().qualifiedName().name(),
                EingangsNummerFormatter.format( nummer ) );
    }


    @Override
    public void createFormContent( final IFormEditorPageSite site ) {
        super.createFormContent( site );

        Composite parent = site.getPageBody();

        // readonly
        Composite eingangsNr = site.newFormField( parent, new PropertyAdapter( kaufvertrag.eingangsNr() ),
                new StringFormField(), new EingangsNummerFormatter(), "Eingangsnummer" );
        eingangsNr.setEnabled( false );
        eingangsNr.setLayoutData( left().create() );

        // Datums
        Composite line2 = site.newFormField( parent, new PropertyAdapter( kaufvertrag.vertragsDatum() ),
                new DateTimeFormField(), new NotNullValidator(),
                // new DateMustBeforeValidator( kd,
                // "Das Vertragsdatum muss vor dem Eingangsdatum liegen." ),
                "Vertragsdatum" );
        line2.setLayoutData( left().top( eingangsNr ).create() );

        site.newFormField( parent, new PropertyAdapter( kaufvertrag.eingangsDatum() ), new DateTimeFormField(),
                new NotNullValidator(),
                // new DateMustAfterValidator( vd,
                // "Das Vertragsdatum muss vor dem Eingangsdatum liegen." ),
                "Eingangsdatum" ).setLayoutData( right().top( eingangsNr ).create() );

        // kreise
        Composite line3 = site.newFormField( parent,
                new AssociationAdapter<KaeuferKreisComposite>( kaufvertrag.verkaeuferKreis() ),
                namedAssocationsPicklist( KaeuferKreisComposite.class ), new NotNullValidator(), "Verkäuferkreis" );
        line3.setLayoutData( left().top( line2 ).create() );

        site.newFormField( parent, new AssociationAdapter<KaeuferKreisComposite>( kaufvertrag.kaeuferKreis() ),
                namedAssocationsPicklist( KaeuferKreisComposite.class ), new NotNullValidator(), "Käuferkreis" )
                .setLayoutData( right().top( line2 ).create() );

        // alle Vertragsarten in PickList
        Composite line4 = site.newFormField( parent,
                new AssociationAdapter<VertragsArtComposite>( kaufvertrag.vertragsArt() ),
                namedAssocationsPicklist( VertragsArtComposite.class ), new NotNullValidator(), "Vertragsart" );
        line4.setLayoutData( left().top( line3 ).create() );

        // Kaufpreis Nenner/Zähler
        final Composite line5 = site.newFormField( parent, new PropertyAdapter( kaufvertrag.kaufpreis() ),
                new StringFormField( StringFormField.Style.ALIGN_RIGHT ), new NotNullMyNumberValidator( Double.class,
                        2 ), "Kaufpreis (€)" );
        line5.setLayoutData( left().right( 30 ).top( line4 ).create() );

        site.newFormField( parent, new PropertyAdapter( kaufvertrag.kaufpreisAnteilZaehler() ),
                new StringFormField( StringFormField.Style.ALIGN_RIGHT ),
                new NotNullMyNumberValidator( Double.class,  2 ), "Anteil Zähler" )
                .setLayoutData( new SimpleFormData( SPACING ).left( 60 ).right( 80 ).top( line4 ).create() );

        site.newFormField( parent, new PropertyAdapter( kaufvertrag.kaufpreisAnteilNenner() ),
                new StringFormField( StringFormField.Style.ALIGN_RIGHT ),
                new NotNullMyNumberValidator( Double.class,  2 ), "/Nenner" )
                .setLayoutData( new SimpleFormData( SPACING ).left( 80 ).right( RIGHT ).top( line4 ).create() );

        final Composite line6 = site.newFormField( parent, new PropertyAdapter( kaufvertrag.vollpreis() ),
                new StringFormField( StringFormField.Style.ALIGN_RIGHT ),
                new MyNumberValidator( Double.class,  2 ), "Vollpreis (€)" );
        line6.setEnabled( false );
        line6.setLayoutData( left().right( 30 ).top( line5 ).create() );

        // Bemerkungen
        final Composite line7 = site.newFormField( parent, new PropertyAdapter( kaufvertrag.bemerkungen() ),
                new TextFormField(), null, "Bemerkungen" );
        line7.setLayoutData( left().right( RIGHT ).top( line6 ).height( 100 ).create() );

        // Eignungen
        final Composite line8 = site.newFormField( parent, new PropertyAdapter( kaufvertrag.fuerAuswertungGeeignet() ),
                new CheckboxFormField(), null, "für Auswertung" );
        line8.setToolTipText( "Ist dieser Vertrag zur Auswertung geeignet?" );
        line8.setLayoutData( left().top( line7 ).create() );

        newFormField( "für GEWOS" ).setProperty( new PropertyAdapter( kaufvertrag.fuerGewosGeeignet() ) )
                .setField( new CheckboxFormField() ).setLayoutData( right().top( line7 ).create() )
                .setToolTipText( "Ist dieser Vertrag für GEWOS geeignet?" ).create();

        // Splittung
        final Composite line9 = site.newFormField( parent, new PropertyAdapter( kaufvertrag.gesplittet() ),
                new CheckboxFormField(), null, "gesplittet" );
        line9.setToolTipText( "Handelt es sich bei diesem Vertrag um einen geplitteten Vertrag?" );
        line9.setLayoutData( left().top( line8 ).create() );
        site.newFormField( parent, new PropertyAdapter( kaufvertrag.gesplittetEingangsnr() ), new StringFormField(),
                null, "zugeordneter Vertrag" ).setLayoutData( right().top( line8 ).bottom( 100 ).create() );

        // Listener
        site.addFieldListener( vollpreisRefresher = new FieldCalculation( site, 2, kaufvertrag.vollpreis(), kaufvertrag
                .kaufpreis(), kaufvertrag.kaufpreisAnteilNenner(), kaufvertrag.kaufpreisAnteilZaehler() ) {

            @Override
            protected Double calculate( ValueProvider values ) {
                Double kp = values.get( kaufvertrag.kaufpreis() );
                Double n = values.get( kaufvertrag.kaufpreisAnteilNenner() );
                Double z = values.get( kaufvertrag.kaufpreisAnteilZaehler() );

                if (kp != null && n != null && z != null && z != 0) {
                    return kp * n / z;
                }
                return null;
            }

        } );
    }
}
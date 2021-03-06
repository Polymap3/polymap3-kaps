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

import java.util.Date;

import org.geotools.data.FeatureStore;
import org.opengis.feature.Feature;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.core.runtime.IProgressMonitor;

import org.polymap.core.project.ui.util.SimpleFormData;
import org.polymap.core.runtime.event.EventManager;

import org.polymap.rhei.data.entityfeature.PropertyAdapter;
import org.polymap.rhei.field.CheckboxFormField;
import org.polymap.rhei.field.DateTimeFormField;
import org.polymap.rhei.field.IFormFieldLabel;
import org.polymap.rhei.field.IFormFieldListener;
import org.polymap.rhei.form.FormEditor;
import org.polymap.rhei.form.IFormEditorPageSite;

import org.polymap.kaps.MathUtil;
import org.polymap.kaps.model.data.VertragComposite;
import org.polymap.kaps.ui.FieldCalculation;
import org.polymap.kaps.ui.FieldCalculationWithTrigger;
import org.polymap.kaps.ui.FieldListener;
import org.polymap.kaps.ui.FieldSummation;
import org.polymap.kaps.ui.NumberFormatter;

/**
 * @author <a href="http://www.polymap.de">Steffen Stundzig</a>
 */
public class WohnungLiegenschaftzinsFormEditorPage
        extends WohnungFormEditorPage {

    private static Log                  log   = LogFactory.getLog( WohnungLiegenschaftzinsFormEditorPage.class );

    private static final int            ONE   = 0;

    private static final int            TWO   = 23;

    private static final int            THREE = 43;

    private static final int            FOUR  = 71;

    private static final int            FIVE  = 80;

    private static final int            SIX   = 100;

    @SuppressWarnings("unused")
    private FieldCalculation            bebabschlag;

    @SuppressWarnings("unused")
    private FieldCalculation            berPreis;

    @SuppressWarnings("unused")
    private FieldListener               fieldListener;

    @SuppressWarnings("unused")
    private FieldCalculation            jahresRohertrag;

    @SuppressWarnings("unused")
    private FieldCalculation            bewirtschaftungskosten;

    @SuppressWarnings("unused")
    private FieldCalculation            jahresReinertrag;

    @SuppressWarnings("unused")
    private FieldCalculation            bodenwertAnteilDerWohnung;

    @SuppressWarnings("unused")
    private FieldCalculation            gebaeudewertAnteilDerWohnung;

    @SuppressWarnings("unused")
    private FieldSummation              sachwertDerWohnung;

    @SuppressWarnings("unused")
    private FieldCalculationWithTrigger jahresReinErtragZuKaufpreis;

    @SuppressWarnings("unused")
    private FieldCalculationWithTrigger gebaeudewertAnteilZuKaufpreis;

    @SuppressWarnings("unused")
    private FieldCalculationWithTrigger liegenschaftsZins;


    public WohnungLiegenschaftzinsFormEditorPage( final FormEditor editor, Feature feature, FeatureStore featureStore ) {
        super( WohnungLiegenschaftzinsFormEditorPage.class.getName(), "Liegenschaftszins", feature, featureStore );
        EventManager.instance().subscribe(
                fieldListener = new FieldListener( wohnung.wohnflaeche(), wohnung.bereinigterVollpreis(),
                        wohnung.kaufpreis(), wohnung.bereinigtesBaujahr(), wohnung.baujahr(), wohnung.gesamtNutzungsDauer(),
                        wohnung.bodenpreis(), wohnung.bodenrichtwert() ) {

                    @Override
                    protected void onChangedValue( IFormEditorPageSite site, String fieldName, Object newValue ) {
                        if (fieldName.equals( wohnung.bodenpreis().qualifiedName().name() )) {
                            site.setFieldValue( fieldName,
                                    newValue != null ? NumberFormatter.getFormatter( 2 ).format( newValue ) : null );
                        }
                        else if (fieldName.equals( wohnung.bodenrichtwert().qualifiedName().name() )) {
                            site.setFieldValue( fieldName,
                                    newValue != null ? NumberFormatter.getFormatter( 2 ).format( newValue ) : null );
                        }
                        else {
                            super.onChangedValue( site, fieldName, newValue );
                        }
                    }
                }, new FieldListener.EventFilter( editor ) );
    }


    @Override
    public void dispose() {
        EventManager.instance().unsubscribe( fieldListener );
    }


    @Override
    public void afterDoLoad( IProgressMonitor monitor )
            throws Exception {
        fieldListener.flush( pageSite );
        // recalcualte imported wohnungen
        pageSite.fireEvent( this, wohnung.monatlicherRohertrag().qualifiedName().name(),
                IFormFieldListener.VALUE_CHANGE, wohnung.monatlicherRohertrag().get() );
    }


    protected SimpleFormData one() {
        return new SimpleFormData( SPACING ).left( ONE ).right( TWO );
    }


    protected SimpleFormData two() {
        return new SimpleFormData( SPACING ).left( TWO ).right( THREE );
    }


    protected SimpleFormData twothree() {
        return new SimpleFormData( SPACING ).left( TWO ).right( FOUR );
    }


    protected SimpleFormData three() {
        return new SimpleFormData( SPACING ).left( THREE ).right( FOUR );
    }


    protected SimpleFormData four() {
        return new SimpleFormData( SPACING ).left( FOUR ).right( FIVE );
    }


    protected SimpleFormData five() {
        return new SimpleFormData( SPACING ).left( FIVE ).right( SIX );
    }


    @SuppressWarnings("unchecked")
    @Override
    public void createFormContent( final IFormEditorPageSite site ) {
        super.createFormContent( site );

        Control newLine, lastLine = null;
        Composite parent = pageSite.getPageBody();

        newLine = createLabel( parent, "Richtwert in €/m²", one().top( lastLine ), SWT.RIGHT );
        createPreisField( wohnung.bodenrichtwert(), two().top( lastLine ), parent, false );
        createLabel( parent, "Bodenpreis in €/m²", three().top( lastLine ), SWT.RIGHT );
        createPreisField( wohnung.bodenpreis(), five().top( lastLine ), parent, true );

        lastLine = newLine;
        newLine = createLabel( parent, "Mietfestsetzung seit", one().top( lastLine ), SWT.RIGHT );
        newFormField( IFormFieldLabel.NO_LABEL ).setProperty( new PropertyAdapter( wohnung.mietfestsetzungSeit() ) )
                .setField( new DateTimeFormField() ).setLayoutData( two().top( lastLine ).create() ).create();
        createLabel( parent, "Bebauungsabschlag in %", three().top( lastLine ), SWT.RIGHT );
        createFlaecheField( wohnung.bebauungsabschlagInProzent(), four().top( lastLine ), parent, true );
        createPreisField( wohnung.bebauungsabschlag(), five().top( lastLine ), parent, false );
        site.addFieldListener( bebabschlag = new FieldCalculation( site, 2, wohnung.bebauungsabschlag(), wohnung
                .bebauungsabschlagInProzent(), wohnung.bodenpreis() ) {

            @Override
            protected Double calculate( ValueProvider values ) {
                Double prozent = values.get( wohnung.bebauungsabschlagInProzent() );
                Double preis = values.get( wohnung.bodenpreis() );
                if (preis == null) {
                    return null;
                }
                if (prozent == null) {
                    return null;
                }
                return preis * prozent / 100;
            }

        } );

        lastLine = newLine;
        newLine = createLabel( parent, "bereinigter Bodenpreis", three().top( lastLine ), SWT.RIGHT );
        createPreisField( wohnung.bereinigterBodenpreis(), five().top( lastLine ), parent, true );
        site.addFieldListener( berPreis = new FieldCalculation( site, 2, wohnung.bereinigterBodenpreis(), wohnung
                .bebauungsabschlag(), wohnung.bodenpreis() ) {

            @Override
            protected Double calculate( ValueProvider values ) {
                Double abschlag = values.get( wohnung.bebauungsabschlag() );
                Double preis = values.get( wohnung.bodenpreis() );
                if (preis == null) {
                    return null;
                }
                if (abschlag == null) {
                    return preis;
                }
                return preis - abschlag;
            }

        } );

        lastLine = newLine;
        newLine = createLabel( parent, "monatlicher Rohertrag in €", one().top( lastLine ), SWT.RIGHT );
        createPreisField( wohnung.monatlicherRohertrag(), two().top( lastLine ), parent, true );
        createLabel( parent, "Jahresrohertrag in €", three().top( lastLine ), SWT.RIGHT );
        createPreisField( wohnung.jahresRohertrag(), five().top( lastLine ), parent, true );
        site.addFieldListener( jahresRohertrag = new FieldCalculation( site, 2, wohnung.jahresRohertrag(), wohnung
                .monatlicherRohertrag() ) {

            @Override
            protected Double calculate( ValueProvider values ) {
                Double abschlag = values.get( wohnung.monatlicherRohertrag() );
                if (abschlag != null) {
                    return abschlag * 12;
                }
                return null;
            }

        } );

        lastLine = newLine;
        newLine = createLabel( parent, "Rohertrag in €/m²", one().top( lastLine ), SWT.RIGHT );
        createPreisField( wohnung.monatlicherRohertragJeQm(), two().top( lastLine ), parent, true );
        createLabel( parent, "Bewirtschaftungkosten in %", three().top( lastLine ), SWT.RIGHT );
        createFlaecheField( wohnung.bewirtschaftungsKostenInProzent(), four().top( lastLine ), parent, true );
        createPreisField( wohnung.bewirtschaftungsKosten(), five().top( lastLine ), parent, false );
        site.addFieldListener( bewirtschaftungskosten = new FieldCalculation( site, 2,
                wohnung.bewirtschaftungsKosten(), wohnung.bewirtschaftungsKostenInProzent(), wohnung.jahresRohertrag() ) {

            @Override
            protected Double calculate( ValueProvider values ) {
                Double prozent = values.get( wohnung.bewirtschaftungsKostenInProzent() );
                Double preis = values.get( wohnung.jahresRohertrag() );
                if (preis == null) {
                    return null;
                }
                if (prozent == null) {
                    return null;
                }
                return preis * prozent / 100;
            }

        } );

        lastLine = newLine;
        newLine = createLabel( parent, "Jahresreinertrag in €", three().top( lastLine ), SWT.RIGHT );
        createPreisField( wohnung.jahresReinertrag(), five().top( lastLine ), parent, true );
        site.addFieldListener( jahresReinertrag = new FieldCalculation( site, 2, wohnung.jahresReinertrag(), wohnung
                .bewirtschaftungsKosten(), wohnung.jahresRohertrag() ) {

            @Override
            protected Double calculate( ValueProvider values ) {
                Double ertrag = values.get( wohnung.jahresRohertrag() );
                if (ertrag != null) {
                    Double kosten = values.get( wohnung.bewirtschaftungsKosten() );
                    if (kosten == null) {
                        return ertrag;
                    }
                    else {
                        return MathUtil.round( ertrag - kosten );
                    }
                }
                return null;
            }

        } );

        lastLine = newLine;
        newLine = createLabel( parent, "Bodenwertanteil der Wohnung in €", three().top( lastLine ), SWT.RIGHT );
        createPreisField( wohnung.bodenwertAnteilDerWohnung(), five().top( lastLine ), parent, true );
        site.addFieldListener( bodenwertAnteilDerWohnung = new FieldCalculation( site, 2, wohnung
                .bodenwertAnteilDerWohnung(), wohnung.wohnflaeche(), wohnung.bereinigterBodenpreis() ) {

            @Override
            protected Double calculate( ValueProvider values ) {
                if (wohnung.flurstueck().get() != null) {
                    Double flaeche = wohnung.flurstueck().get().flaeche().get();
                    if (flaeche != null) {
                        Double zaehler = wohnung.flurstueck().get().flaechenAnteilZaehler().get();
                        Double nenner = wohnung.flurstueck().get().flaechenAnteilNenner().get();
                        Double preis = values.get( wohnung.bereinigterBodenpreis() );
                        if (zaehler != null) {
                            flaeche *= zaehler;
                        }
                        if (nenner != null && !nenner.equals( Double.valueOf( 0.0d ) )) {
                            flaeche /= nenner;
                        }
                        if (preis != null) {
                            flaeche *= preis;
                        }
                        else {
                            flaeche = Double.valueOf( 0.0d );
                        }
                        return MathUtil.round( flaeche );
                    }
                }
                return null;
            }

        } );

        lastLine = newLine;
        newLine = createLabel( parent, "Abschläge berücksichtigen?",
                "beim Kaufpreis die Zu/Abschläge bei\nGaragen/Stellplatz/Anderes berücksichtigen?", one()
                        .top( lastLine ), SWT.RIGHT );
        newFormField( IFormFieldLabel.NO_LABEL )
                .setToolTipText( "beim Kaufpreis die Zu/Abschläge bei\nGaragen/Stellplatz/Anderes berücksichtigen?" )
                .setProperty( new PropertyAdapter( wohnung.garagenBeiLiegenschaftszinsBeruecksichtigen() ) )
                .setField( new CheckboxFormField() ).setLayoutData( two().top( lastLine ).create() ).create();
        createLabel( parent, "Gebäudewertanteil der Wohnung in €", three().top( lastLine ), SWT.RIGHT );
        createPreisField( wohnung.gebaeudewertAnteilDerWohnung(), five().top( lastLine ), parent, true );
        site.addFieldListener( gebaeudewertAnteilDerWohnung = new FieldCalculationWithTrigger( site, 2, wohnung
                .gebaeudewertAnteilDerWohnung(), wohnung.garagenBeiLiegenschaftszinsBeruecksichtigen(), wohnung
                .bodenwertAnteilDerWohnung(), wohnung.bereinigterVollpreis(), wohnung.kaufpreis() ) {

            @Override
            protected Double calculate( ValueProvider values ) {
                Double bodenwert = values.get( wohnung.bodenwertAnteilDerWohnung() );
                if (bodenwert != null) {
                    Double preis = this.triggerValue() != null && this.triggerValue().booleanValue() ? values
                            .get( wohnung.bereinigterVollpreis() ) : values.get( wohnung.kaufpreis() );
                    if (preis != null) {
                        return MathUtil.round( preis - bodenwert );
                    }
                }
                return null;
            }
        } );

        lastLine = newLine;
        newLine = createLabel( parent, "Sachwert der Wohnung in €", three().top( lastLine ), SWT.RIGHT );
        createPreisField( wohnung.sachwertDerWohnung(), five().top( lastLine ), parent, false );
        site.addFieldListener( sachwertDerWohnung = new FieldSummation( site, 2, wohnung.sachwertDerWohnung(), wohnung
                .bodenwertAnteilDerWohnung(), wohnung.gebaeudewertAnteilDerWohnung() ) );

        lastLine = newLine;
        newLine = createLabel( parent, "Jahresreinertrag/Kaufpreis", three().top( lastLine ), SWT.RIGHT );
        createNumberField( IFormFieldLabel.NO_LABEL, null, wohnung.jahresReinErtragZuKaufpreis(),
                five().top( lastLine ), parent, false, 4 );
        site.addFieldListener( jahresReinErtragZuKaufpreis = new FieldCalculationWithTrigger( site, 4, wohnung
                .jahresReinErtragZuKaufpreis(), wohnung.garagenBeiLiegenschaftszinsBeruecksichtigen(), wohnung
                .jahresReinertrag(), wohnung.bereinigterVollpreis(), wohnung.kaufpreis() ) {

            @Override
            protected Double calculate( ValueProvider values ) {
                Double reinertrag = values.get( wohnung.jahresReinertrag() );
                if (reinertrag != null) {
                    Double preis = this.triggerValue() != null && this.triggerValue().booleanValue() ? values
                            .get( wohnung.bereinigterVollpreis() ) : values.get( wohnung.kaufpreis() );
                    if (preis != null && !preis.equals( Double.valueOf( 0.0d ) )) {
                        return reinertrag / preis;
                    }
                }
                return null;
            }
        } );

        lastLine = newLine;
        newLine = createLabel( parent, "Gebäudewertanteil/Kaufpreis", three().top( lastLine ), SWT.RIGHT );
        createNumberField( IFormFieldLabel.NO_LABEL, null, wohnung.gebaeudewertAnteilZuKaufpreis(),
                five().top( lastLine ), parent, false, 4 );
        site.addFieldListener( gebaeudewertAnteilZuKaufpreis = new FieldCalculationWithTrigger( site, 4, wohnung
                .gebaeudewertAnteilZuKaufpreis(), wohnung.garagenBeiLiegenschaftszinsBeruecksichtigen(), wohnung
                .gebaeudewertAnteilDerWohnung(), wohnung.bereinigterVollpreis(), wohnung.kaufpreis() ) {

            @Override
            protected Double calculate( ValueProvider values ) {
                Double gebaeudewertAnteil = values.get( wohnung.gebaeudewertAnteilDerWohnung() );
                if (gebaeudewertAnteil != null) {
                    Double preis = this.triggerValue() != null && this.triggerValue().booleanValue() ? values
                            .get( wohnung.bereinigterVollpreis() ) : values.get( wohnung.kaufpreis() );
                    if (preis != null && !preis.equals( Double.valueOf( 0.0d ) )) {
                        return gebaeudewertAnteil / preis;
                    }
                }
                return null;
            }
        } );

        lastLine = newLine;
        newLine = createLabel( parent, "Liegenschaftszins in %", three().top( lastLine ), SWT.RIGHT );
        createPreisField( wohnung.liegenschaftsZins(), five().top( lastLine ), parent, false );
        site.addFieldListener( liegenschaftsZins = new FieldCalculationWithTrigger( site, 2, wohnung
                .liegenschaftsZins(), wohnung.garagenBeiLiegenschaftszinsBeruecksichtigen(), wohnung
                .gebaeudewertAnteilZuKaufpreis(), wohnung.jahresReinErtragZuKaufpreis(), wohnung.gesamtNutzungsDauer(),
                wohnung.bereinigtesBaujahr(), wohnung.baujahr() ) {

            @Override
            protected Double calculate( ValueProvider values ) {
                Double faktor1 = values.get( wohnung.jahresReinErtragZuKaufpreis() );
                Double faktor2 = values.get( wohnung.gebaeudewertAnteilZuKaufpreis() );
                Double GND = values.get( wohnung.gesamtNutzungsDauer() );
                Double baujahr = values.get( wohnung.bereinigtesBaujahr() );
                if (baujahr == null || baujahr.doubleValue() == 0.0d) {
                    baujahr = values.get( wohnung.baujahr() );
                }

                VertragComposite vertrag = wohnung.vertrag().get();
                int currentYear = (vertrag != null ? vertrag.vertragsDatum().get().getYear() : new Date().getYear()) + 1900;
                Double RND = (GND != null && baujahr != null) ? baujahr + GND - currentYear : null;
                if (RND != null && faktor1 != null && faktor2 != null) {
                    Double liziV = faktor1;
                    Double liziN = faktor1 - ((liziV) / (Math.pow( 1 + liziV, RND ) - 1) * faktor2);
                    int iteration = 1;
                    while (Math.abs( liziN - liziV ) > 0.005d) {
                        liziV = liziN;
                        liziN = faktor1 - ((liziV) / (Math.pow( 1 + liziV, RND ) - 1) * faktor2);
                        iteration++;
                    }
                    // in % umrechnen
                    double result = liziN * 100;
                    return Double.isNaN( result ) ? 0.0d : result;
                }
                return null;
            }
        } );

        lastLine = newLine;
        newLine = createLabel( parent, "Zur Auswertung geeignet?",
                "Ist der Liegenschaftszins geeignet für Auswertungen?", three().top( lastLine ), SWT.RIGHT );
        newFormField( IFormFieldLabel.NO_LABEL )
                .setToolTipText( "Ist der Liegenschaftszins geeignet für Auswertungen?" )
                .setProperty( new PropertyAdapter( wohnung.geeignet() ) ).setField( new CheckboxFormField() )
                .setLayoutData( five().top( lastLine ).create() ).create();

    }
}

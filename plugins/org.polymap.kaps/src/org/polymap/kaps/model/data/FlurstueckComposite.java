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
package org.polymap.kaps.model.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.qi4j.api.common.Optional;
import org.qi4j.api.common.UseDefaults;
import org.qi4j.api.concern.Concerns;
import org.qi4j.api.entity.EntityComposite;
import org.qi4j.api.entity.association.Association;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.property.Property;

import org.polymap.core.qi4j.QiEntity;
import org.polymap.core.qi4j.event.ModelChangeSupport;
import org.polymap.core.qi4j.event.PropertyChangeSupport;

import org.polymap.kaps.importer.ImportColumn;
import org.polymap.kaps.importer.ImportTable;
import org.polymap.kaps.model.Named;

/**
 * 
 * @author <a href="http://www.polymap.de">Steffen Stundzig</a>
 */
@Concerns({ PropertyChangeSupport.Concern.class })
@Mixins({ FlurstueckComposite.Mixin.class, PropertyChangeSupport.Mixin.class,
        ModelChangeSupport.Mixin.class, QiEntity.Mixin.class
// JsonState.Mixin.class
})
@ImportTable("FLURZWI")
public interface FlurstueckComposite
        extends QiEntity, PropertyChangeSupport, ModelChangeSupport, EntityComposite, Named {

    // CREATE TABLE FLURZWI (
    // EINGANGSNR DOUBLE,
    @Optional
    Association<KaufvertragComposite> vertrag();


    // GEMARKUNG VARCHAR(4),
    @Optional
    Association<GemarkungComposite> gemarkung();


    // FLUR VARCHAR(3),
    @Optional
    Association<FlurComposite> flur();


    // FLSTNR1 INTEGER DEFAULT 0,
    @Optional
    @ImportColumn("FLSTNR1")
    @UseDefaults
    Property<Integer> nummer();


    // FLSTNR1U VARCHAR(3),
    @Optional
    @ImportColumn("FLSTNR1U")
    Property<String> unterNummer();


    // BAUBLOCK VARCHAR(12),
    @Optional
    @ImportColumn("BAUBLOCK")
    Property<String> baublock();


    // FLAECHE1 DOUBLE,
    @Optional
    @UseDefaults
    @ImportColumn("FLAECHE1")
    Property<Double> flaeche();


    // FANTZ1 DOUBLE,
    @Optional
    @UseDefaults
    @ImportColumn("FANTZ1")
    Property<Double> flaecheAnteilZaehler();


    // FANTN1 DOUBLE,
    @Optional
    @UseDefaults
    @ImportColumn("FANTN1")
    Property<Double> flaechenAnteilNenner();

    // VERKFL1 DOUBLE,
    @Optional
    @UseDefaults
    @ImportColumn("VERKFL1")
    Property<Double> verkaufteFlaeche();
    
    // NUTZUNG VARCHAR(2),
    @Optional
    Association<NutzungComposite> nutzung();
    
    // LAGE VARCHAR(35), 
    // Enthält nochmal den Strassennamen, in UI nicht zu erkennen
    
    // HAUSNR INTEGER DEFAULT 0,
    @Optional
    @ImportColumn("HAUSNR")
    @UseDefaults
    Property<Integer> hausnummer();
    
    // STRNR VARCHAR(5),
    @Optional
    Association<StrasseComposite> strasse();
    
    // GEBART VARCHAR(3),
    @Optional
    Association<GebaeudeArtComposite> gebaeudeArt();
    
    // KARTBLATT VARCHAR(6),
    @Optional
    @ImportColumn("KARTBLATT")
    Property<String> kartenBlatt();
    
    // XGK DOUBLE, IGNORE, eventuell später mal als geom()
    // YGK DOUBLE,
    
    // HAUPTTEIL VARCHAR(1),
    @Optional
    Property<Boolean> hauptFlurstueck();
    
    // BEMERKUNG VARCHAR(12),
    @Optional
    @ImportColumn("BEMERKUNG")
    Property<String> bemerkung();
    
    // KARTBLATTN VARCHAR(10),
    @Optional
    @ImportColumn("KARTBLATTN")
    Property<String> kartenBlattNummer();
     
    // BAUGEBART VARCHAR(1),
    @Optional
    Association<ArtDesBaugebietsComposite> artDesBaugebiets();
    
    // FLSTNR1X VARCHAR(5), identisch FLSTNR1 
    // FLSTNR1UX VARCHAR(3), identisch FLSTNR1U
    // GEM_FLUR VARCHAR(4), Link zur Gemarkung ist über Gemarkung

    // HZUSNR VARCHAR(12),
    @Optional
    @ImportColumn("HZUSNR")
    Property<String> hausnummerZusatz();
    
    // GEMEINDE INTEGER DEFAULT 0, Link zur Gemeinde ist schon über Gemarkung ignore?
    // RIZONE VARCHAR(7),
    // RIJAHR TIMESTAMP
    @Optional
    Association<RichtwertzoneComposite> richtwertZone();


    /**
     * Methods and transient fields.
     */
    public static abstract class Mixin
            implements FlurstueckComposite {

        private static Log log = LogFactory.getLog( Mixin.class );

    }

}

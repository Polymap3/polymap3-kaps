/*
 * polymap.org
 * Copyright 2011, Falko Br�utigam, and other contributors as
 * indicated by the @authors tag. All rights reserved.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
package org.polymap.kaps.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.polymap.core.qi4j.QiEntity;
import org.polymap.core.qi4j.event.ModelChangeSupport;
import org.polymap.core.qi4j.event.PropertyChangeSupport;
import org.qi4j.api.common.Optional;
import org.qi4j.api.concern.Concerns;
import org.qi4j.api.entity.EntityComposite;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.property.Property;

/**
 *
 *
 * @author <a href="http://www.polymap.de">Falko Br�utigam</a>
 */
@Concerns( {
    PropertyChangeSupport.Concern.class
} )
@Mixins( {
    VertragsArtComposite.Mixin.class,
    PropertyChangeSupport.Mixin.class,
    ModelChangeSupport.Mixin.class,
    QiEntity.Mixin.class
//    JsonState.Mixin.class
} )
//@ImportTable("Referenz_Biotoptypen")
public interface VertragsArtComposite
    extends QiEntity, PropertyChangeSupport, ModelChangeSupport, EntityComposite {

    /** */
    @Optional
//    @ImportColumn("Nr_Biotoptyp")
    Property<String>            nummer();

    /** Import von: Biotoptyp */
    @Optional
//    @ImportColumn("Biotoptyp")
    Property<String>            name();

    /**
     * Methods and transient fields.
     */
    public static abstract class Mixin
            implements VertragsArtComposite {

        private static Log log = LogFactory.getLog( Mixin.class );

    }

}

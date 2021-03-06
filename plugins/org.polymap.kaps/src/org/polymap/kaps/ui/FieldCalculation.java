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
package org.polymap.kaps.ui;

import java.util.HashMap;
import java.util.Map;

import org.qi4j.api.property.Property;

import org.polymap.rhei.field.FormFieldEvent;
import org.polymap.rhei.field.IFormFieldListener;
import org.polymap.rhei.form.IFormEditorPageSite;

/**
 * @author <a href="http://www.polymap.de">Steffen Stundzig</a>
 */
public abstract class FieldCalculation
        implements IFormFieldListener {

    /**
     * @author <a href="http://www.polymap.de">Steffen Stundzig</a>
     */
    public static class ValueProvider {

        private final Map<String, Double> values = new HashMap<String, Double>();


        public void put( Property<Double> term, Double value ) {
            values.put( term.qualifiedName().name(), value );
        }


        public Double get( Property<Double> term ) {
            return values.get( term.qualifiedName().name() );
        }
    }

    private final IFormEditorPageSite           site;

    private final Property<Double>              result;

    private final int                           fractionDigits;

    private final Map<String, Property<Double>> terms;

    private final ValueProvider                 values;

    private Double lastResultValue;


    public FieldCalculation( IFormEditorPageSite site, int fractionDigits, Property<Double> result,
            Property<Double>... operators ) {
        this.site = site;
        this.fractionDigits = fractionDigits;
        this.result = result;
        terms = new HashMap<String, Property<Double>>();
        for (Property<Double> term : operators) {
            terms.put( term.qualifiedName().name(), term );
        }
        values = new ValueProvider();
        for (Property<Double> term : operators) {
            values.put( term, term.get() );
        }
    }


    @Override
    public void fieldChange( FormFieldEvent ev ) {
        if (ev.getEventCode() != IFormFieldListener.VALUE_CHANGE) {
            return;
        }
        String fieldName = ev.getFieldName();
        if (terms.keySet().contains( fieldName )) {
            Double newValue = (Double)ev.getNewValue(); // explizitely deleting this
                                                        // value
            if (newValue == null) {
                newValue = Double.valueOf( 0.0d );
            }
            values.put( terms.get( fieldName ), newValue );
            refreshResult();
        }
    }


    protected abstract Double calculate( ValueProvider values );


    public final void refreshResult() {
        Double resultValue = calculate( values );
        if (resultValue != null) {
            resultValue = Double.isNaN( resultValue) ? 0.0d : resultValue;
            site.setFieldValue( result.qualifiedName().name(),
                    NumberFormatter.getFormatter( fractionDigits ).format( resultValue ) );
        }
        lastResultValue = resultValue;
    }


    /**
     *
     * @return
     */
    public Double getLastResultValue() {
        return lastResultValue;
    }
}

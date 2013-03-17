/* 
 * polymap.org
 * Copyright 2013 Polymap GmbH. All rights reserved.
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
package org.polymap.kaps;

import java.util.ArrayList;
import java.util.List;

import org.opengis.feature.Feature;

import org.polymap.rhei.form.FormEditor;
import org.polymap.rhei.form.IFormEditorPage;
import org.polymap.rhei.form.IFormPageProvider;

import org.polymap.kaps.form.Kaufvertrag1FormEditorPage;
import org.polymap.kaps.form.Kaufvertrag2FormEditorPage;

/**
 * @author <a href="http://www.polymap.de">Steffen Stundzig</a>
 */
public class FormPageProvider implements IFormPageProvider {



	public FormPageProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<IFormEditorPage> addPages(FormEditor formEditor, Feature feature) {
		// log.debug("addPages(): feature= " + feature);
		List<IFormEditorPage> result = new ArrayList();
		if (feature.getType().getName().getLocalPart().equalsIgnoreCase("kaufvertrag")) {
			result.add(new Kaufvertrag1FormEditorPage(feature, formEditor.getFeatureStore()));
			result.add(new Kaufvertrag2FormEditorPage(feature, formEditor.getFeatureStore()));
//			result.add(new Kaufvertrag3FormEditorPage(feature, formEditor.getFeatureStore()));
//			result.add(new Kaufvertrag4FormEditorPage(feature, formEditor.getFeatureStore()));
//			result.add(new Kaufvertrag5FormEditorPage(feature, formEditor.getFeatureStore()));
		}
		
		return result;
	}

}

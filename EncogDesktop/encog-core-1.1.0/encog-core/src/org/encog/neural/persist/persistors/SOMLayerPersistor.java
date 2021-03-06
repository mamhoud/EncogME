/*
 * Encog Neural Network and Bot Library for Java v1.x
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.encog.neural.persist.persistors;

import javax.xml.transform.sax.TransformerHandler;

import org.encog.matrix.Matrix;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.networks.layers.SOMLayer;
import org.encog.neural.persist.EncogPersistedCollection;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.util.XMLUtil;
import org.encog.util.NormalizeInput.NormalizationType;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Persist a som.
 * 
 * @author jheaton
 * 
 */
public class SOMLayerPersistor implements Persistor {

	/**
	 * String token for the multiplicative normalization method.
	 */
	public static final String NORM_TYPE_MULTIPLICATIVE = "MULTIPLICATIVE";

	/**
	 * String token for the z-axis normalization method.
	 */
	public static final String NORM_TYPE_Z_AXIS = "Z_AXIS";

	/**
	 * Load from the specified node.
	 * 
	 * @param layerNode
	 *            The node to load from.
	 * @return The EncogPersistedObject that was loaded.
	 */
	public EncogPersistedObject load(final Element layerNode) {
		final String str = layerNode.getAttribute("neuronCount");
		final String normType = layerNode.getAttribute("normalization");
		final int neuronCount = Integer.parseInt(str);
		final String name = layerNode.getAttribute("name");
		final String description = layerNode.getAttribute("description");

		SOMLayer layer;

		if (normType.equals(SOMLayerPersistor.NORM_TYPE_MULTIPLICATIVE)) {
			layer = new SOMLayer(neuronCount, NormalizationType.MULTIPLICATIVE);
		} else if (normType.equals(SOMLayerPersistor.NORM_TYPE_Z_AXIS)) {
			layer = new SOMLayer(neuronCount, NormalizationType.Z_AXIS);
		} else {
			layer = null;
		}

		final Element matrixElement = XMLUtil.findElement(layerNode,
				"weightMatrix");
		if (matrixElement != null && layer != null) {
			final Element e = XMLUtil.findElement(matrixElement, "Matrix");
			final Persistor persistor = EncogPersistedCollection
					.createPersistor("Matrix");
			final Matrix matrix = (Matrix) persistor.load(e);
			layer.setMatrix(matrix);
		}

		if (layer != null) {
			layer.setName(name);
			layer.setDescription(description);
		}
		return layer;
	}

	/**
	 * Save the specified object.
	 * 
	 * @param object
	 *            The object to save.
	 * @param hd
	 *            The XML object.
	 */
	public void save(final EncogPersistedObject object,
			final TransformerHandler hd) {

		try {
			final SOMLayer layer = (SOMLayer) object;

			final AttributesImpl atts = EncogPersistedCollection
					.createAttributes(object);
			EncogPersistedCollection.addAttribute(atts, "neuronCount", ""
					+ layer.getNeuronCount());
			String normType = null;

			if (layer.getNormalizationType() 
					== NormalizationType.MULTIPLICATIVE) {
				normType = 
					SOMLayerPersistor.NORM_TYPE_MULTIPLICATIVE;
			} else if (layer.getNormalizationType() 
					== NormalizationType.Z_AXIS) {
				normType = SOMLayerPersistor.NORM_TYPE_Z_AXIS;
			}

			if (normType == null) {
				throw new NeuralNetworkError("Unknown normalization type");
			}

			atts.addAttribute("", "", "normalization", "CDATA", "" + normType);

			hd.startElement("", "", layer.getClass().getSimpleName(), atts);

			atts.clear();

			if (layer.hasMatrix()) {

				final Persistor persistor = EncogPersistedCollection
						.createPersistor(layer.getMatrix().getClass()
								.getSimpleName());
				atts.clear();
				hd.startElement("", "", "weightMatrix", atts);
				persistor.save(layer.getMatrix(), hd);
				hd.endElement("", "", "weightMatrix");

			}

			hd.endElement("", "", layer.getClass().getSimpleName());
		} catch (final SAXException e) {
			throw new NeuralNetworkError(e);
		}
	}

}

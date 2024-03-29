/*
 * Copyright 2012 osmdroid authors:
 * Copyright 2012 Nicolas Gramlich
 * Copyright 2012 Theodore Hong
 * Copyright 2012 Fred Eisele
 *
 * Copyright 2014 Hannes Janetzek
 * Copyright 2016 devemux86
 * Copyright 2016 Erik Duisters
 * Copyright 2017 Longri
 * Copyright 2017 nebular
 * Copyright 2017 Luca Osten
 *
 * This file is part of the OpenScienceMap project (http://www.opensciencemap.org).
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.oscim.layers.marker;

import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.GeoPoint;
import org.oscim.core.Point;
import org.oscim.layers.marker.MarkerSymbol.HotspotPlace;
import org.oscim.map.ViewController;

/**
 * Immutable class describing a GeoPoint with a Title and a Description.
 */
public class MarkerItem implements MarkerInterface {
    public final Object uid;
    public String title;
    public String description;
    public GeoPoint geoPoint;
    protected MarkerSymbol mMarker;
    private int zoomLevelDrawn = 0;

    /**
     * @param title       this should be <b>singleLine</b> (no <code>'\n'</code> )
     * @param description a <b>multiLine</b> description ( <code>'\n'</code> possible)
     */
    public MarkerItem(String title, String description, GeoPoint geoPoint) {
        this(null, title, description, geoPoint);
    }

    public MarkerItem(Object uid, String title, String description, GeoPoint geoPoint) {
        this.title = title;
        this.description = description;
        this.geoPoint = geoPoint;
        this.uid = uid;
    }
    
    public MarkerItem(GeoPoint geoPoint, Bitmap bitmap, HotspotPlace hotspotPlace) {
        this(null, null, geoPoint);
        this.mMarker = new MarkerSymbol(bitmap, hotspotPlace);
    }
    
    public MarkerItem(GeoPoint geoPoint, Bitmap bitmap) {
        this(geoPoint, bitmap, HotspotPlace.CENTER);
    }

    public Object getUid() {
        return uid;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return description;
    }

    @Override
    public GeoPoint getPoint() {
        return geoPoint;
    }

    @Override
    public MarkerSymbol getMarker() {
        return mMarker;
    }
    
    @Override
    public int getZoomLevelDrawn() {
        return zoomLevelDrawn;
    }

    public void setMarker(MarkerSymbol marker) {
        mMarker = marker;
    }

    public void setRotation(float rotation) {
        if (mMarker != null)
            mMarker.setRotation(rotation);
    }
    
    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }
    
    public void setZoomLevelDrawn(int zoomLevelDrawn) {
        this.zoomLevelDrawn = zoomLevelDrawn;
    }
    
    public boolean isInside(ViewController viewController, Point pointMapTap) {
        if (geoPoint == null) {
            return false;
        }
        Point pointTmp = new Point();
        viewController.toScreenPoint(geoPoint, pointTmp);
        return mMarker.isInside(
                (float)(pointTmp.x - pointMapTap.x),
                (float)(pointTmp.y - pointMapTap.y));
    }

    /**
     * If a MarkerItem is created using this convenience class instead of MarkerItem,
     * this specific item will not be clusterable.
     */
    public static class NonClusterable extends MarkerItem {
        public NonClusterable(String title, String description, GeoPoint geoPoint) {
            super(null, title, description, geoPoint);
        }

        public NonClusterable(Object uid, String title, String description, GeoPoint geoPoint) {
            super(uid, title, description, geoPoint);
        }
    }
}

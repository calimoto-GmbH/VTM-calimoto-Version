/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2017 devemux86
 * Copyright 2017 nebular
 * Copyright 2017 Andrey Novikov
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
package org.oscim.theme;

import org.oscim.backend.AssetAdapter;
import org.oscim.theme.IRenderTheme.ThemeException;

import java.io.InputStream;
import java.util.Set;

/**
 * Enumeration of all internal rendering themes.
 */
public enum VtmThemes implements ThemeFile {

    DEFAULT("vtm/default.xml"),
    CALIMOTO("vtm/calimoto.xml"),
    CALIMOTO_ROADTRIP("vtm/calimoto_roadtrip.xml"),
    CALIMOTO_NAVIGATION("vtm/calimoto_navigation.xml"),
    CALIMOTO_ROADTRIP_NAVIGATION("vtm/calimoto_roadtrip_navigation.xml"),
    CALIMOTO_MYSPIN("vtm/calimoto_myspin.xml"),
    MAPZEN("vtm/mapzen.xml"),
    NEWTRON("vtm/newtron.xml"),
    CALIMOTO_ONLINE("vtm/calimoto_online.xml"),
    CALIMOTO_ONLINE_NAVIGATION("vtm/calimoto_online_navigation.xml"),
    CALIMOTO_ONLINE_HILLSHADE("vtm/calimoto_online_hillshade.xml"),
    CALIMOTO_ONLINE_SATELLITE("vtm/calimoto_online_satellite.xml"),
    OSMAGRAY("vtm/osmagray.xml"),
    OSMARENDER("vtm/osmarender.xml"),
    TRONRENDER("vtm/tronrender.xml");
    

    private final String mPath;
    private XmlRenderThemeMenuCallback mXmlRenderThemeMenuCallback = null;
    
    VtmThemes(String path) {
        mPath = path;
    }
    
    public void setCategories(final Set<String> categories) {
        if (categories == null) {
            mXmlRenderThemeMenuCallback = null;
        } else {
            mXmlRenderThemeMenuCallback = new XmlRenderThemeMenuCallback() {
                @Override
                public Set<String> getCategories(XmlRenderThemeStyleMenu style) {
                    return categories;
                }
            };
        }
    }

    @Override
    public XmlRenderThemeMenuCallback getMenuCallback() {
        return mXmlRenderThemeMenuCallback;
    }

    @Override
    public String getRelativePathPrefix() {
        return "";
    }

    @Override
    public InputStream getRenderThemeAsStream() throws ThemeException {
        return AssetAdapter.readFileAsStream(mPath);
    }

    @Override
    public boolean isMapsforgeTheme() {
        return false;
    }

    @Override
    public void setMenuCallback(XmlRenderThemeMenuCallback menuCallback) {
    }
}

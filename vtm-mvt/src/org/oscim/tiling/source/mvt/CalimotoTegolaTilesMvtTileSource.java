/*
 * Copyright 2018 devemux86
 * Copyright 2021 calimoto GmbH (Robert Schierz)
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
package org.oscim.tiling.source.mvt;

import org.oscim.server.ServerDnsTiles;
import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.OverzoomTileDataSource;
import org.oscim.tiling.source.UrlTileDataSource;
import org.oscim.tiling.source.UrlTileSource;

public class CalimotoTegolaTilesMvtTileSource extends UrlTileSource {

    private static final String DEFAULT_PATH = "/{Z}/{X}/{Y}.pbf";

    public static class Builder<T extends Builder<T>> extends UrlTileSource.Builder<T> {
        private String locale = "";

        public Builder() {
            super(
                    new ServerDnsTiles(), 
                    DEFAULT_PATH);
            overZoom(14);
        }

        public T locale(String locale) {
            this.locale = locale;
            return self();
        }

        @Override
        public CalimotoTegolaTilesMvtTileSource build() {
            return new CalimotoTegolaTilesMvtTileSource(this);
        }
    }

    @SuppressWarnings("rawtypes")
    public static Builder<?> builder() {
        return new Builder();
    }

    private final String locale;

    public CalimotoTegolaTilesMvtTileSource(Builder<?> builder) {
        super(builder);
        this.locale = builder.locale;
    }

    public CalimotoTegolaTilesMvtTileSource() {
        this(builder());
    }

    public CalimotoTegolaTilesMvtTileSource(String urlString) {
        this(builder().url(urlString));
    }

    @Override
    public ITileDataSource getDataSource() {
        return new OverzoomTileDataSource(new UrlTileDataSource(this, new MvtTileDecoder(locale), getHttpEngine()), mOverZoom);
    }
}

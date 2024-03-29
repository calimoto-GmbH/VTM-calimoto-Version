/*
 * Copyright 2013 mapsforge.org
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2018 devemux86
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
package org.oscim.tiling.source.mapfile;

import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.map.Viewport;
import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.OverzoomTileDataSource;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.mapfile.header.MapFileHeader;
import org.oscim.tiling.source.mapfile.header.MapFileInfo;
import org.oscim.utils.IOUtils;
import org.oscim.debug.Logger;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class MapFileTileSource extends TileSource implements IMapFileTileSource {
    private static final Logger log = new Logger(MapFileTileSource.class);
    /**
     * Amount of cache blocks that the index cache should store.
     */
    private static final int INDEX_CACHE_SIZE = 64;
    private static final String READ_ONLY_MODE = "r";

    MapFileHeader fileHeader;
    MapFileInfo fileInfo;
    IndexCache databaseIndexCache;
    boolean experimental;
    File mapFile;
    private RandomAccessFile mInputFile;

    /**
     * The preferred language when extracting labels from this tile source.
     */
    private String preferredLanguage;
    private String region;
    private Callback callback;

    public MapFileTileSource() {
        this(Viewport.MIN_ZOOM_LEVEL, Viewport.MAX_ZOOM_LEVEL);
    }

    public MapFileTileSource(int zoomMin, int zoomMax) {
        this(zoomMin, zoomMax, BuildingLayer.MIN_ZOOM);
    }

    public MapFileTileSource(int zoomMin, int zoomMax, int overZoom) {
        super(zoomMin, zoomMax, overZoom);
    }

    /**
     * Extracts substring of preferred language from multilingual string using
     * the preferredLanguage setting.
     */
    String extractLocalized(String s) {
        if (callback != null)
            return callback.extractLocalized(s);
        return MapFileUtils.extract(s, preferredLanguage);
    }

    @Override
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public boolean setMapFile(String filename) {
        setOption("file", filename);

        File file = new File(filename);

        if (!file.exists()) {
            return false;
        } else if (!file.isFile()) {
            return false;
        } else if (!file.canRead()) {
            return false;
        }

        return true;
    }

    @Override
    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    public String getRegion() {
        return region;
    }
    
    
    @Override
    public OpenResult open() {
        if (!options.containsKey("file"))
            return new OpenResult("no map file set");

        try {
            // make sure to close any previously opened file first
            //close();

            File file = new File(options.get("file"));

            // check if the file exists and is readable
            if (!file.exists()) {
                return new OpenResult("file does not exist: " + file);
            } else if (!file.isFile()) {
                return new OpenResult("not a file: " + file);
            } else if (!file.canRead()) {
                return new OpenResult("cannot read file: " + file);
            }

            // open the file in read only mode
            mInputFile = new RandomAccessFile(file, READ_ONLY_MODE);
            long mFileSize = mInputFile.length();
            ReadBuffer mReadBuffer = new ReadBuffer(mInputFile);

            fileHeader = new MapFileHeader();
            OpenResult openResult = fileHeader.readHeader(mReadBuffer, mFileSize);

            if (!openResult.isSuccess()) {
                close();
                return openResult;
            }
            fileInfo = fileHeader.getMapFileInfo();
            mapFile = file;
            databaseIndexCache = new IndexCache(mInputFile, INDEX_CACHE_SIZE);

            // Experimental?
            //experimental = fileInfo.fileVersion == 4;

            log.debug("File version: " + fileInfo.fileVersion);
            return OpenResult.SUCCESS;
        } catch (IOException e) {
            log.error(e);
            // make sure that the file is closed
            close();
            return new OpenResult(e.getMessage());
        }
    }

    @Override
    public ITileDataSource getDataSource() {
        try {
            return new OverzoomTileDataSource(new MapDatabase(this), mOverZoom);
        } catch (IOException e) {
            log.debug(e);
        }
        return null;
    }

    @Override
    public void close() {
        IOUtils.closeQuietly(mInputFile);
        mInputFile = null;
        fileHeader = null;
        fileInfo = null;
        mapFile = null;

        if (databaseIndexCache != null) {
            databaseIndexCache.destroy();
            databaseIndexCache = null;
        }
    }

    public MapInfo getMapInfo() {
        return fileInfo;
    }

    public interface Callback {
        /**
         * Extracts substring of preferred language from multilingual string.
         */
        String extractLocalized(String s);
    }
}

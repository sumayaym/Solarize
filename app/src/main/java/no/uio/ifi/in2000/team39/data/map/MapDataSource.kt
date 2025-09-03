package no.uio.ifi.in2000.team39.data.map

import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.RasterLayer
import org.maplibre.android.style.sources.RasterSource
import org.maplibre.android.style.sources.TileSet
import javax.inject.Inject

class MapDataSource @Inject constructor() {
    fun setupRasterLayers(style: Style): Pair<RasterLayer, RasterLayer> {
        val osmSource = RasterSource(
            "osm-source",
            TileSet("osm-tiles", "https://tile.openstreetmap.org/{z}/{x}/{y}.png"),
            256
        )

        val esriSource = RasterSource(
            "esri-source",
            TileSet(
                "esri-tiles",
                "https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}"
            ),
            256
        )

        val osmLayer = RasterLayer("osm-layer", "osm-source")
        val esriLayer = RasterLayer("esri-layer", "esri-source")

        style.addSource(osmSource)
        style.addLayer(osmLayer)

        style.addSource(esriSource)
        style.addLayer(esriLayer)

        return Pair(osmLayer, esriLayer)
    }

}


package com.tkw.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

object WaterIcons {
    val LocalDrink: ImageVector
        get() {
            if (_localDrink != null) {
                return _localDrink!!
            }
            _localDrink = materialIcon(name = "LocalDrink") {
                materialPath {
                    // Water cup/glass icon path
                    moveTo(5.0f, 21.0f)
                    curveTo(4.45f, 21.0f, 3.98f, 20.8f, 3.59f, 20.41f)
                    curveTo(3.2f, 20.02f, 3.0f, 19.55f, 3.0f, 19.0f)
                    lineTo(4.0f, 3.0f)
                    lineTo(20.0f, 3.0f)
                    lineTo(21.0f, 19.0f)
                    curveTo(21.0f, 19.55f, 20.8f, 20.02f, 20.41f, 20.41f)
                    curveTo(20.02f, 20.8f, 19.55f, 21.0f, 19.0f, 21.0f)
                    lineTo(5.0f, 21.0f)
                    close()
                    
                    moveTo(5.5f, 19.0f)
                    lineTo(18.5f, 19.0f)
                    lineTo(18.0f, 5.0f)
                    lineTo(6.0f, 5.0f)
                    lineTo(5.5f, 19.0f)
                    close()
                    
                    // Water level
                    moveTo(7.0f, 15.0f)
                    curveTo(7.0f, 16.1f, 7.9f, 17.0f, 9.0f, 17.0f)
                    lineTo(15.0f, 17.0f)
                    curveTo(16.1f, 17.0f, 17.0f, 16.1f, 17.0f, 15.0f)
                    lineTo(16.5f, 9.0f)
                    lineTo(7.5f, 9.0f)
                    lineTo(7.0f, 15.0f)
                    close()
                }
            }
            return _localDrink!!
        }

    private var _localDrink: ImageVector? = null

    val Timeline: ImageVector
        get() {
            if (_timeline != null) {
                return _timeline!!
            }
            _timeline = materialIcon(name = "Timeline") {
                materialPath {
                    // Timeline/chart icon path
                    moveTo(23.0f, 8.0f)
                    curveTo(22.0f, 8.0f, 21.0f, 9.0f, 21.0f, 10.0f)
                    curveTo(21.0f, 10.29f, 21.06f, 10.57f, 21.17f, 10.83f)
                    lineTo(18.83f, 12.17f)
                    curveTo(18.57f, 12.06f, 18.29f, 12.0f, 18.0f, 12.0f)
                    curveTo(17.71f, 12.0f, 17.43f, 12.06f, 17.17f, 12.17f)
                    lineTo(14.83f, 9.83f)
                    curveTo(14.94f, 9.57f, 15.0f, 9.29f, 15.0f, 9.0f)
                    curveTo(15.0f, 8.0f, 14.0f, 7.0f, 13.0f, 7.0f)
                    curveTo(12.0f, 7.0f, 11.0f, 8.0f, 11.0f, 9.0f)
                    curveTo(11.0f, 9.29f, 11.06f, 9.57f, 11.17f, 9.83f)
                    lineTo(8.83f, 12.17f)
                    curveTo(8.57f, 12.06f, 8.29f, 12.0f, 8.0f, 12.0f)
                    curveTo(7.71f, 12.0f, 7.43f, 12.06f, 7.17f, 12.17f)
                    lineTo(4.83f, 14.83f)
                    curveTo(4.94f, 15.09f, 5.0f, 15.37f, 5.0f, 15.66f)
                    curveTo(5.0f, 16.66f, 4.0f, 17.66f, 3.0f, 17.66f)
                    curveTo(2.0f, 17.66f, 1.0f, 16.66f, 1.0f, 15.66f)
                    curveTo(1.0f, 14.66f, 2.0f, 13.66f, 3.0f, 13.66f)
                    curveTo(3.29f, 13.66f, 3.57f, 13.72f, 3.83f, 13.83f)
                    lineTo(6.17f, 11.17f)
                    curveTo(6.06f, 10.91f, 6.0f, 10.63f, 6.0f, 10.34f)
                    curveTo(6.0f, 9.34f, 7.0f, 8.34f, 8.0f, 8.34f)
                    curveTo(9.0f, 8.34f, 10.0f, 9.34f, 10.0f, 10.34f)
                    curveTo(10.0f, 10.63f, 9.94f, 10.91f, 9.83f, 11.17f)
                    lineTo(12.17f, 13.83f)
                    curveTo(12.43f, 13.72f, 12.71f, 13.66f, 13.0f, 13.66f)
                    curveTo(13.29f, 13.66f, 13.57f, 13.72f, 13.83f, 13.83f)
                    lineTo(16.17f, 11.17f)
                    curveTo(16.06f, 10.91f, 16.0f, 10.63f, 16.0f, 10.34f)
                    curveTo(16.0f, 9.34f, 17.0f, 8.34f, 18.0f, 8.34f)
                    curveTo(19.0f, 8.34f, 20.0f, 9.34f, 20.0f, 10.34f)
                    curveTo(20.0f, 10.63f, 19.94f, 10.91f, 19.83f, 11.17f)
                    lineTo(22.17f, 12.83f)
                    curveTo(22.43f, 12.72f, 22.71f, 12.66f, 23.0f, 12.66f)
                    curveTo(24.0f, 12.66f, 25.0f, 13.66f, 25.0f, 14.66f)
                    curveTo(25.0f, 15.66f, 24.0f, 16.66f, 23.0f, 16.66f)
                    curveTo(22.0f, 16.66f, 21.0f, 15.66f, 21.0f, 14.66f)
                    curveTo(21.0f, 14.37f, 21.06f, 14.09f, 21.17f, 13.83f)
                    lineTo(18.83f, 12.17f)
                    curveTo(18.57f, 12.28f, 18.29f, 12.34f, 18.0f, 12.34f)
                    curveTo(17.71f, 12.34f, 17.43f, 12.28f, 17.17f, 12.17f)
                    lineTo(14.83f, 14.83f)
                    curveTo(14.94f, 15.09f, 15.0f, 15.37f, 15.0f, 15.66f)
                    curveTo(15.0f, 16.66f, 14.0f, 17.66f, 13.0f, 17.66f)
                    curveTo(12.0f, 17.66f, 11.0f, 16.66f, 11.0f, 15.66f)
                    curveTo(11.0f, 15.37f, 11.06f, 15.09f, 11.17f, 14.83f)
                    lineTo(8.83f, 12.17f)
                    curveTo(8.57f, 12.28f, 8.29f, 12.34f, 8.0f, 12.34f)
                    curveTo(7.71f, 12.34f, 7.43f, 12.28f, 7.17f, 12.17f)
                    lineTo(4.83f, 14.83f)
                    curveTo(4.94f, 15.09f, 5.0f, 15.37f, 5.0f, 15.66f)
                    close()
                }
            }
            return _timeline!!
        }

    private var _timeline: ImageVector? = null
}
package GUI.WindowPanels;

/**
 * Created by olekristianaune on 19.03.2016.
 */
public class _Map {

    private static String startAddress; // FIXME: This will need to be geocoded and then inputted as map center

    private static String mapHTML = "" +
            "<!DOCTYPE html>\n"+
            "<html>\n"+
            "<head>\n"+
            "    <meta name=\"viewport\" content=\"initial-scale=1.0, user-scalable=no\"/>\n"+
            "    <style type=\"text/css\">\n"+
            "        html { height: 100% }\n"+
            "        body { height: 100%; margin: 0; padding: 0 }\n"+
            "        #map-canvas { height: 100% }\n"+
            "    </style>\n"+
            "    <script type=\"text/javascript\" src=\"https://maps.googleapis.com/maps/api/js?key=AIzaSyDCG0JH_Eb0FhZLNsQSDU7D_jbaFMl_3sY\"></script>\n"+
            "    <script type=\"text/javascript\">\n"+
            "        var directionsDisplay;\n"+
            "        var directionsService = new google.maps.DirectionsService();\n"+
            "        var map;\n"+
            "\n"+
            "        var startLatLng;\n"+
            "        var endLatLng;\n"+
            "        var waypts = [];\n"+
            "\n"+
            "        function initialize() {\n"+
            "            var mapOptions = {\n"+
            "                center: new google.maps.LatLng(63.4187191,10.3687231), // Trondheim\n"+
            "                zoom: 12\n"+
            "            };\n"+
            "            map = new google.maps.Map(document.getElementById(\"map-canvas\"), mapOptions);\n"+
            "\n"+
            "            directionsDisplay = new google.maps.DirectionsRenderer({\n"+
            "                map: map\n"+
            "            });\n"+
            "\n"+
            "        }\n"+
            "\n"+
            "        google.maps.event.addDomListener(window, 'load', initialize);\n"+
            "    </script>\n"+
            "</head>\n"+
            "<body>\n"+
            "    <div id=\"map-canvas\"></div>\n"+
            "</body>\n"+
            "</html>";

    public static String getMapHTML() { return mapHTML; } // FIXME: Add startAddress input in method
}

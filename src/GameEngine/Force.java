/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameEngine;

import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 *
 * @author onur
 */
class Force {
    private ArrayList<Unit> units;
    private Faction faction;

    void drawStrategicMap(GraphicsContext gc, int x, int y, int edgeLength) {
        Unit u = getLeaderUnit();
        gc.drawImage(new Image("files/units/" +  u.getName().toLowerCase()+ ".png", 
                edgeLength*2, edgeLength*2, false, true), x, y );
    }

    private Unit getLeaderUnit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

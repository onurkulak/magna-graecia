/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameEngine;

import java.util.ArrayList;
import java.awt.Point;

/**
 *
 * @author onur
 */
public class Layout
{
    public Layout(Orientation orientation, FloatingPoint size, FloatingPoint origin)
    {
        this.orientation = orientation;
        this.size = size;
        this.origin = origin;
    }
    
    public Layout(Orientation orientation, double sizex, double sizey, double originx, double originy)
    {
        this.orientation = orientation;
        this.size = new FloatingPoint(sizex, sizey);
        this.origin = new FloatingPoint(originx, originy);
    }
    
    public final Orientation orientation;
    public final FloatingPoint size;
    public final FloatingPoint origin;
    static public Orientation pointy = new Orientation(Math.sqrt(3.0), Math.sqrt(3.0) / 2.0, 0.0, 3.0 / 2.0, Math.sqrt(3.0) / 3.0, -1.0 / 3.0, 0.0, 2.0 / 3.0, 0.5);
    static public Orientation flat = new Orientation(3.0 / 2.0, 0.0, Math.sqrt(3.0) / 2.0, Math.sqrt(3.0), 2.0 / 3.0, 0.0, -1.0 / 3.0, Math.sqrt(3.0) / 3.0, 0.0);

    public FloatingPoint hexToPixel(Hex h)
    {
        Orientation M = orientation;
        double x = (M.f0 * h.q + M.f1 * h.r) * size.x;
        double y = (M.f2 * h.q + M.f3 * h.r) * size.y;
        return new FloatingPoint(x + origin.x, y + origin.y);
    }


    public FractionalHex pixelToHex(FloatingPoint p)
    {
        Orientation M = orientation;
        FloatingPoint pt = new FloatingPoint((p.x - origin.x) / size.x, (p.y - origin.y) / size.y);
        double q = M.b0 * pt.x + M.b1 * pt.y;
        double r = M.b2 * pt.x + M.b3 * pt.y;
        return new FractionalHex(q, r, -q - r);
    }


    public FloatingPoint hexCornerOffset(int corner)
    {
        Orientation M = orientation;
        double angle = 2.0 * Math.PI * (M.start_angle - corner) / 6;
        return new FloatingPoint(size.x * Math.cos(angle), size.y * Math.sin(angle));
    }


    public ArrayList<FloatingPoint> polygonCorners(Hex h)
    {
        ArrayList<FloatingPoint> corners = new ArrayList<FloatingPoint>(){{}};
        FloatingPoint center = hexToPixel(h);
        for (int i = 0; i < 6; i++)
        {
            FloatingPoint offset = hexCornerOffset(i);
            corners.add(new FloatingPoint(center.x + offset.x, center.y + offset.y));
        }
        return corners;
    }

    public Point pixelToHex(double xPixel, double yPixel){
        FractionalHex fh = pixelToHex(new FloatingPoint(xPixel, yPixel));
        Hex h = fh.hexRound();
        OffsetCoord oc = OffsetCoord.qoffsetFromCube(h);
        return new Point(oc.col, oc.row);
    }
}

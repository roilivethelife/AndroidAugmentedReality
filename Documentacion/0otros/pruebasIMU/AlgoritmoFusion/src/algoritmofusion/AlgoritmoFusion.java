/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmofusion;

import algoritmofusion.util.SensorCalc;
import algoritmofusion.util.SensorEvent;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.math.plot.Plot2DPanel;

/**
 *
 * @author roi
 */
public class AlgoritmoFusion {

    final static String FILEPATH = "../1test/log.txt";
    //final static String FILEPATH = "../2pasosAlante/log.txt";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ArrayList<SensorEvent> events = new ArrayList<>();
        int nAcel = 0;
        int nRot = 0;
        try {
            // TODO code application logic here
            File log = new File(FILEPATH);
            Scanner logScan = new Scanner(log);
            while (logScan.hasNext()) {
                String tmp = logScan.nextLine();
                StringTokenizer token = new StringTokenizer(tmp, ",");
                long timestamp = 0;
                float[] values = new float[3];
                boolean rotacion = false;
                int i = 0;
                while (token.hasMoreElements()) {
                    String element = token.nextToken();
                    switch (i) {
                        case 0:
                            if (element.charAt(0) == 'r') {
                                rotacion = true;
                                nRot++;
                            } else {
                                nAcel++;
                            }
                            break;
                        case 1:
                            timestamp = Long.parseLong(element);
                            break;
                        default:
                            values[i - 2] = Float.parseFloat(element);
                            break;
                    }
                    i++;
                }
                events.add(new SensorEvent(values, timestamp, rotacion));
            }
            logScan.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AlgoritmoFusion.class.getName()).log(Level.SEVERE, null, ex);
        }
        SensorCalc sens = new SensorCalc();

        double[] acelDataY = new double[nAcel];
        double[] acelDataZ = new double[nAcel];
        double[] velData = new double[nAcel];
        double[] posData = new double[nAcel];

        int i=0;
        int eje = 1;
        for (SensorEvent next : events) {
            sens.eventoLinearAcel(next);
            if (!next.rotacion) {
                //retrieve data
                acelDataY[i] = sens.filteredAcel[eje];
                acelDataZ[i] = sens.filteredAcel[2];
                velData[i] = sens.getVelocidad()[eje];
                posData[i] = sens.getPosicion()[eje];
                i++;
            }
        }

        // create your PlotPanel (you can use it as a JPanel)
        Plot2DPanel plot = new Plot2DPanel();
        // add a line plot to the PlotPanel
        // put the PlotPanel in a JFrame, as a JPanel
        plot.addLinePlot("AcelY", Color.red, acelDataY);
        plot.addLinePlot("AcelZ", Color.YELLOW, acelDataZ);
        plot.addLinePlot("VelY", Color.green, velData);        
        plot.addLinePlot("PosY", Color.blue, posData);        
        JFrame frame = new JFrame("Aceleracion_Y");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,600);
        frame.setContentPane(plot);
        frame.setVisible(true);

    }

}

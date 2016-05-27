/*  MUD Map (v2) - A tool to create and organize maps for text-based games
 *  Copyright (C) 2014  Neop (email: mneop@web.de)
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *  for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, see <http://www.gnu.org/licenses/>.
 */

/*  File description
 *
 *  The place dialog is used to create and modify places
 */
package mudmap2.frontend.dialog;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import mudmap2.backend.Area;
import mudmap2.backend.Layer;
import mudmap2.backend.Place;
import mudmap2.backend.RiskLevel;
import mudmap2.backend.World;

/**
 * The file dialog is used to create and modify places
 * Derived from MouseListener, so it can be directly attached to buttons
 * 
 * @author neop
 */
public class PlaceDialog extends ActionDialog {

    World world;
    Place place;
    Layer layer;
    int px, py;
    
    // if this area is choosen, it will be replaced with null
    Area area_null;
    
    JTextField textfield_name;
    JComboBox<Area> combobox_area;
    JComboBox<RiskLevel> combobox_risk;
    JSpinner spinner_rec_lvl_min, spinner_rec_lvl_max;
    
    /**
     * Creates a dialog to modify a place
     * @param _parent
     * @param _world
     * @param _place existing place
     */
    public PlaceDialog(JFrame _parent, World _world, Place _place){
        super(_parent, "Edit place - " + _place, true);
        
        world = _world;
        place = _place;
        layer = place.getLayer();
        px = place.getX();
        py = place.getY();
    }
    
    /**
     * Creates a dialog to create a new place
     * @param _parent
     * @param _layer layer or null to create a new one
     * @param _world
     * @param _px place coordinate x
     * @param _py place coordinate y
     */
    public PlaceDialog(JFrame _parent, World _world, Layer _layer, int _px, int _py){
        super(_parent, "Add place", true);
        
        world = _world;
        place = null;
        layer = _layer;
        px = _px;
        py = _py;
    }
    
    /**
     * Creates the dialog
     */
    @Override
    void create(){
        setLayout(new GridLayout(0, 2, 4, 4));
        
        add(new JLabel("Name"));
        if(place != null) textfield_name = new JTextField(place.getName());
        else textfield_name = new JTextField();
        add(textfield_name);
        
        area_null = new Area("none", null);
        
        add(new JLabel("Area"));
        combobox_area = new JComboBox<Area>();
        combobox_area.addItem(area_null);
        for(Area a : world.getAreas()) combobox_area.addItem(a);
        if(place != null && place.getArea() != null) combobox_area.setSelectedItem(place.getArea());
        add(combobox_area);
        
        add(new JLabel("Risk level"));
        combobox_risk = new JComboBox<RiskLevel>();
        for(RiskLevel rl : world.getRiskLevels()) combobox_risk.addItem(rl);
        if(place != null && place.getRiskLevel() != null) combobox_risk.setSelectedItem(place.getRiskLevel());
        add(combobox_risk);
        
        add(new JLabel("Recommended level min"));
        spinner_rec_lvl_min = new JSpinner();
        spinner_rec_lvl_min.setModel(new SpinnerNumberModel((place != null ? place.getRecLevelMin() : -1), -1, 1000, 1));
        add(spinner_rec_lvl_min);
        
        add(new JLabel("Recommended level max"));
        spinner_rec_lvl_max = new JSpinner();
        spinner_rec_lvl_max.setModel(new SpinnerNumberModel((place != null ? place.getRecLevelMax() : -1), -1, 1000, 1));
        add(spinner_rec_lvl_max);
        
        JButton button_cancel = new JButton("Cancel");
        add(button_cancel);
        button_cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                dispose();
            }
        });
        
        JButton button_ok = new JButton("Ok");
        add(button_ok);
        getRootPane().setDefaultButton(button_ok);
        button_ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                save();
                dispose();
            }
        });
        
        // listener for escape key
        getRootPane().registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                dispose();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        pack();
        setLocation(getParent().getX() + (getParent().getWidth() - getWidth()) / 2, getParent().getY() + (getParent().getHeight() - getHeight()) / 2);
    }
    
    /**
     * Saves the place data
     */
    public void save(){
        //if(!textfield_name.getText().isEmpty()){ // name not empty
            try {
                if(layer == null) layer = world.getNewLayer();
                
                // create place if it doesn't exist else just set the name
                if(place == null) world.putPlace(place = new Place(textfield_name.getText(), px, py, layer));
                else place.setName(textfield_name.getText());

                Area a = (Area) combobox_area.getSelectedItem();
                place.setArea(a != area_null ? a : null); // raplce null area with null
                place.setRiskLevel((RiskLevel) combobox_risk.getSelectedItem());

                place.setRecLevelMin((Integer) spinner_rec_lvl_min.getValue());
                place.setRecLevelMax((Integer) spinner_rec_lvl_max.getValue());
            } catch (Exception ex) {
                Logger.getLogger(PlaceDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        //}
        getParent().repaint();
    }
    
    /**
     * Gets the place (eg. after creation
     * @return place
     */
    public Place getPlace(){
        return place;
    }
    
}

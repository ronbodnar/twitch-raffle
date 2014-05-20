/**
 * Copyright (c) 2014 Ron Bodnar.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.aeternaly.raffle.ui.util;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ComboBoxRenderer implements ListCellRenderer {
    
    private JPanel panel;

	private ListCellRenderer listCellRenderer;

    public ComboBoxRenderer(ListCellRenderer delegate) {
        this.listCellRenderer = delegate;
        
    	panel = new JPanel(new BorderLayout());
        panel.add(new JSeparator());
    }
    
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus){
        Component component = listCellRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        
        return value.equals("-") ? panel : component;
    }
    
}
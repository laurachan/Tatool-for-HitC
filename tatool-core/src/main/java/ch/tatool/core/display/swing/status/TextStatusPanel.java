/*******************************************************************************
 * Copyright (c) 2011 Michael Ruflin, Andr� Locher, Claudia von Bastian.
 * 
 * This file is part of Tatool.
 * 
 * Tatool is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published 
 * by the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * Tatool is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Tatool. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package ch.tatool.core.display.swing.status;

import javax.swing.JPanel;


/**
 * Status panel that provides a title and value property only.
 *
 * @author Michael Ruflin
 */
public class TextStatusPanel extends javax.swing.JPanel implements StatusPanel {
	
	private static final long serialVersionUID = -4631097079694011596L;
	
	/** Creates new form TextStatusPanel */
    public TextStatusPanel() {
        initComponents();
    }
    
    // StatusPanel interface
    
    public void setEnabled(boolean enabled) {
    	super.setEnabled(enabled);
    	titleLabel.setEnabled(enabled);
    	valueLabel.setEnabled(enabled);
    }
    
    /** Reset the panel to its default state. */
	public void reset() {
		valueLabel.setText("");
	}
    
    public Object getProperty(String key) {
    	if (PROPERTY_TITLE.equals(key)) {
    		return getTitleLabel().getText();
    	}
    	else if (PROPERTY_VALUE.equals(key)) {
    		return valueLabel.getText();
    	}
    	else {
    		return null;
    	}
	}

	public JPanel getView() {
		return this;
	}

	public void setProperty(String key, Object value) {
		if (PROPERTY_TITLE.equals(key)) {
			String text = String.valueOf(value);
    		titleLabel.setText(text);
    	}
    	else if (PROPERTY_VALUE.equals(key)) {
    		String text = String.valueOf(value);
    		valueLabel.setText(text);
    	}
	}

	
	
	// Implementation

	public javax.swing.JLabel getTitleLabel() {
		return titleLabel;
	}

	public void setTitleLabel(javax.swing.JLabel titleLabel) {
		this.titleLabel = titleLabel;
	}

	public javax.swing.JLabel getValueLabel() {
		return valueLabel;
	}

	public void setValueLabel(javax.swing.JLabel valueLabel) {
		this.valueLabel = valueLabel;
	}

	/** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titleLabel = new javax.swing.JLabel();
        valueLabel = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 235));
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        setLayout(new java.awt.BorderLayout());

        titleLabel.setFont(titleLabel.getFont().deriveFont(titleLabel.getFont().getSize()+6f));
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText(" ");
        add(titleLabel, java.awt.BorderLayout.NORTH);

        valueLabel.setFont(valueLabel.getFont().deriveFont(valueLabel.getFont().getSize()+40f));
        valueLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        valueLabel.setText(" ");
        valueLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        add(valueLabel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JLabel titleLabel;
    javax.swing.JLabel valueLabel;
    // End of variables declaration//GEN-END:variables

}

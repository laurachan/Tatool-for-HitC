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
package ch.tatool.core.display.swing.panel;

import javax.swing.Icon;

/**
 * Displays a simple text in the center of the screen.
 * 
 * @author Michael Ruflin
 */
public class CenteredTextPanel extends javax.swing.JPanel {

	private static final long serialVersionUID = -775453298402854911L;

	/** Creates new form SimpleTextQuestionPanel */
	public CenteredTextPanel() {
		initComponents();
	}

	public void setText(String text) {
		textLabel.setText(text);
	}

	public void setIcon(Icon icon) {
		textLabel.setIcon(icon);
	}

	public void setTextSize(int size) {
		textLabel.setFont(textLabel.getFont().deriveFont(
				textLabel.getFont().getStyle(), size));
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// @SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        textLabel = new javax.swing.JLabel();

        setOpaque(false);
        setLayout(new java.awt.GridLayout(1, 0));

        textLabel.setFont(textLabel.getFont().deriveFont(textLabel.getFont().getStyle() | java.awt.Font.BOLD, textLabel.getFont().getSize()+30));
        textLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        add(textLabel);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel textLabel;
    // End of variables declaration//GEN-END:variables

}

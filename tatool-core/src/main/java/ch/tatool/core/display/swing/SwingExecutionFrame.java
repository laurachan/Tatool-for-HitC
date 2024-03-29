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
package ch.tatool.core.display.swing;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLayeredPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Display implementation usable for task execution.
 *
 * @author Michael Ruflin
 */
public class SwingExecutionFrame extends javax.swing.JFrame implements SwingExecutionDisplay {

    private static final long serialVersionUID = 5488252067183749247L;

    Logger logger = LoggerFactory.getLogger(SwingExecutionFrame.class);
    
    /** Default background color of the frame. */
    private static final Color DEFAULT_BACKGROUND_COLOR = Color.WHITE;
    
    /** Should the view be displayed in full-screen mode? */
    private boolean fullScreenModeEnabled;
    private boolean screenModeInitialized = false; // whether the screen mode has been initialized yet 
    
    /** Holds the panels added to the screen. */
    Map<String, Component> contents;
    
    /** Overlay component */
    private Component overlayComponent;
    
    /** Card Layout used to manage the cards. */
    private CardLayout cardLayout;
    
    /** Creates new form SwingExecutionFrame */
    public SwingExecutionFrame() {
    	contents = new HashMap<String, Component>();
        fullScreenModeEnabled = true;
        
        // initialize the ui components of this frame
        initComponents();
        rootPanel.setBackground(DEFAULT_BACKGROUND_COLOR);
        cardLayout = (CardLayout) rootPanel.getLayout();
        
        // layeredPane resize handler - we need to resize the overlay if the layeredpane size changes
        getLayeredPane().addComponentListener(new ComponentAdapter() {
        	public void componentResized(ComponentEvent e) {
        		resizeOverlayComponent();
        	}
        });
    }

    // Window displaying
    
    /** Set the full screen mode.
     * Note that this is only possible before the frame is being displayed for the first time!
     */
    public void setFullScreenModeEnabled(boolean fullScreenModeEnabled) {
    	this.fullScreenModeEnabled = fullScreenModeEnabled;
    }
    
    /** Get whether the fullscreen mode is enabled. */
    public boolean isFullScreenModeEnabled() {
        return fullScreenModeEnabled;
    }
    
    /** Initializes the full screen mode of the frame. */
    private void initFullScreenMode() {
        this.setResizable(false);
        if (! this.isDisplayable()) {
            this.setUndecorated(true);           
        }
    }
    
    /** Displays this frame in full screen mode. */
    private void showFrameInFullScreen() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        this.setVisible(true);
        gd.setFullScreenWindow(this);
    }
    
    /** Returns to non-full-screen mode and hides this window. */
    private void removeFrameFromScreen() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        gd.setFullScreenWindow(null);
        setVisible(false);
    }

    /**
     * Shows the window.
     */
    public void showWindow() {
    	// check whether we have to initialize the screen mode
    	if (! screenModeInitialized) {
    		screenModeInitialized = true;
    		if (fullScreenModeEnabled) {
    			initFullScreenMode();
    		}
    	}
    	
        if (fullScreenModeEnabled) {
            showFrameInFullScreen();
        } else {
            setSize(800, 600);
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }
    
    /** Hides the window. */
    public void hideWindow() {
        if (fullScreenModeEnabled) {
            removeFrameFromScreen();
        } else {
            setVisible(false);
        }
    }

    /** Hides the window and completely disposed uses resources. */
    public void disposeWindow() {
    	hideWindow();
    	this.dispose();
    }

    
    // SwingExecutionDisplay
    
    /**
	 * Add a card to this display
	 */
	public void addCard(String cardId, Component component) {
		removeCard(cardId);
		contents.put(cardId, component);
		rootPanel.add(component, cardId);
	}
	
	/** Display a previously added card. */
	public void showCard(String cardId) {
		cardLayout.show(rootPanel, cardId);
	}
	
	/** Removes a card from the display. */
	public void removeCard(String cardId) {
		Component component = contents.get(cardId);
		if (component != null) {
			rootPanel.remove(component);
			contents.remove(cardId);
		}
	}
	
	/** Remove all added cards. */
	public void removeAllCards() {
		for (Component component : contents.values()) {
			rootPanel.remove(component);
		}
		contents.clear();
	}
	
	/** Display an empty card. */
	public void displayEmptyCard() {
		cardLayout.show(rootPanel, SwingExecutionDisplay.EMPTY_CARD_ID);
	}
	
	/** Get the currently displayed card. */
	public String getDisplayedCardId() {
		return null;
	}
	
	/** Set the overlay component to be displayed.
	 * @param component the overlay component to display, null to remove any displayed component
	 */
	public void setOverlay(Component component) {
		JLayeredPane layeredPane = getLayeredPane(); 
		if (overlayComponent != null) {
			layeredPane.remove(overlayComponent);
		}
		overlayComponent = component;
		if (overlayComponent != null) {
			layeredPane.add(overlayComponent, JLayeredPane.PALETTE_LAYER);
			resizeOverlayComponent();
		}
	}
    
	/** Resizes the overlayComponent according to the layeredPane size. */
	private void resizeOverlayComponent() {
		if (overlayComponent != null) {
			overlayComponent.setSize(getLayeredPane().getSize());
		}
	}
    
	/** Get the background color of this frame. */
    public Color getBackgroundColor() {
    	return getRootPane().getBackground();
	}

    /** Set the background color of this frame. */
	public void setBackgroundColor(Color color) {
		if (color != null) {
			getRootPane().setBackground(color);
		} else {
			getRootPane().setBackground(DEFAULT_BACKGROUND_COLOR);
		}
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    //@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rootPanel = new javax.swing.JPanel();
        emptyCard = new javax.swing.JPanel();

        rootPanel.setBackground(new java.awt.Color(255, 255, 255));
        rootPanel.setLayout(new java.awt.CardLayout());

        emptyCard.setBackground(new java.awt.Color(255, 255, 255));
        emptyCard.setOpaque(false);
        rootPanel.add(emptyCard, "emptyCard");

        getContentPane().add(rootPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel emptyCard;
    private javax.swing.JPanel rootPanel;
    // End of variables declaration//GEN-END:variables


}

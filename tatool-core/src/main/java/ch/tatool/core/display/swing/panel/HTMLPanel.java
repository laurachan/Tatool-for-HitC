/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ScoreOverviewPanel.java
 *
 * Created on 07.04.2011, 20:12:58
 */

package ch.tatool.core.display.swing.panel;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;

import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

/**
 *
 * @author Andre Locher
 */
public class HTMLPanel extends javax.swing.JPanel {

    private HTMLEditorKit kit;
    
    /** Creates new form ScoreOverviewPanel */
    public HTMLPanel() {
        kit = new HTMLEditorKit();
        initComponents();
    }
    
    /**
     * Displays a HTML String in the editor pane.
     * 
     * @param htmlString the HTML string to display
     * @param baseString the base used to reference images and css inside the html
     */
    public void setHTMLString(String htmlString, String baseString) {
        URL base = getClass().getResource(baseString);
        ((javax.swing.text.html.HTMLDocument)jEditorPane.getDocument()).setBase(base);
        adaptFontSize();
        jEditorPane.setText(htmlString);
    }

	private void adaptFontSize() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		String bodyRule = "";
		if (dim.getWidth() <= 1024) {
			bodyRule = "body { font-size:62.5%}";
		} else if (dim.getWidth() <= 1152) {
			bodyRule = "body { font-size:70.3%}";
		} else if (dim.getWidth() <= 1280) {
			bodyRule = "body { font-size:78.1%}";
		} else if (dim.getWidth() <= 1440) {
			bodyRule = "body { font-size:87.9%}";
		} else if (dim.getWidth() <= 1600) {
			bodyRule = "body { font-size:97.6%}";
		} else if (dim.getWidth() <= 1680) {
			bodyRule = "body { font-size:102.5%}";
		} else if (dim.getWidth() > 1680) {
			bodyRule = "body { font-size:102.5%}";
		}
        ((javax.swing.text.html.HTMLDocument)jEditorPane.getDocument()).getStyleSheet().addRule(bodyRule);
	}
  
    /**
     * Displays a HTML URL in the editor pane.
     * 
     * @param url the url to display
     */
    public void setHTMLPage(URL url) {
    	Document doc = kit.createDefaultDocument();
        jEditorPane.setDocument(doc);
        try {
			jEditorPane.setPage(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane = new javax.swing.JScrollPane();
        jEditorPane = new javax.swing.JEditorPane();

        setLayout(new java.awt.GridLayout(1, 1, 1, 0));

        jScrollPane.setBorder(null);

        jEditorPane.setBorder(null);
        jEditorPane.setEditable(false);
        jEditorPane.setEditorKit(kit);
        jEditorPane.setFocusable(false);
        jScrollPane.setViewportView(jEditorPane);

        add(jScrollPane);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane jEditorPane;
    private javax.swing.JScrollPane jScrollPane;
    // End of variables declaration//GEN-END:variables

}

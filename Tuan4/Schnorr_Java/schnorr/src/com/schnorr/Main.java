package com.schnorr;

import com.schnorr.controller.SchnorrController;
import com.schnorr.model.SchnorrFacade;
import com.schnorr.view.SchnorrView;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * APPLICATION ENTRY POINT
 *
 * Bootstraps the MVC triad on the Swing Event Dispatch Thread:
 * <pre>
 *   Model      → {@link SchnorrFacade}       pure cryptographic logic
 *   View       → {@link SchnorrView}         Swing UI
 *   Controller → {@link SchnorrController}   mediator / event handler
 * </pre>
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            applySystemLookAndFeel();

            SchnorrFacade     model      = new SchnorrFacade();
            SchnorrController controller = new SchnorrController(model);
            SchnorrView       view       = new SchnorrView();
            controller.setView(view);   // wires all action listeners
        });
    }

    /** Attempts to use the OS native L&F; falls back to cross-platform silently. */
    private static void applySystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { /* use default */ }
    }
}

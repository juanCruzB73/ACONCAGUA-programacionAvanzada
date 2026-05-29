package practico1.sockets.src;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class ValidadorUsuario extends DocumentFilter {
    private final int maxLargo;

    public ValidadorUsuario(int maxLargo) {
        this.maxLargo = maxLargo;
    }

    private boolean isValidUsername(String text) {
        // Permit only alphanumeric and underscores. No spaces, accents or symbols.
        return text.matches("[a-zA-Z0-9_]*");
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (string == null) return;
        
        String newText = getNewText(fb, offset, 0, string);
        if (newText.length() <= maxLargo && isValidUsername(string)) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (text == null) {
            super.replace(fb, offset, length, null, attrs);
            return;
        }
        
        String newText = getNewText(fb, offset, length, text);
        if (newText.length() <= maxLargo && isValidUsername(text)) {
            super.replace(fb, offset, length, text, attrs);
        }
    }

    private String getNewText(FilterBypass fb, int offset, int length, String text) throws BadLocationException {
        String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
        return currentText.substring(0, offset) + text + currentText.substring(offset + length);
    }
}

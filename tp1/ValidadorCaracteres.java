package practico1.tp1;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class ValidadorCaracteres {

    // Helper to check if a string contains only letters and spaces
    private static boolean isAlphabetic(String text) {
        return text.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]*");
    }

    // Helper to check if a string contains only digits
    private static boolean isNumeric(String text) {
        return text.matches("[0-9]*");
    }

    // Helper to check if a string contains only telephone characters: digits, +, (, ), -, space
    private static boolean isValidPhoneChar(String text) {
        return text.matches("[0-9+\\-()\\s]*");
    }

    /**
     * Filtro para Nombre/Apellido: Solo letras y espacios, largo máximo 20
     */
    public static DocumentFilter getFiltroAlfabetico(int maxLargo) {
        return new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                
                String newText = getNewText(fb, offset, 0, string);
                if (newText.length() <= maxLargo && isAlphabetic(string)) {
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
                if (newText.length() <= maxLargo && isAlphabetic(text)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        };
    }

    /**
     * Filtro para DNI o CP: Solo dígitos, largo máximo dado
     */
    public static DocumentFilter getFiltroNumerico(int maxLargo) {
        return new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                
                String newText = getNewText(fb, offset, 0, string);
                if (newText.length() <= maxLargo && isNumeric(string)) {
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
                if (newText.length() <= maxLargo && isNumeric(text)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        };
    }

    /**
     * Filtro para Teléfono: Números, +, -, (, ), espacio. Largo máximo 25.
     */
    public static DocumentFilter getFiltroTelefono(int maxLargo) {
        return new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                
                String newText = getNewText(fb, offset, 0, string);
                if (newText.length() <= maxLargo && isValidPhoneChar(string)) {
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
                if (newText.length() <= maxLargo && isValidPhoneChar(text)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        };
    }

    /**
     * Filtro para Pasaporte:
     * - Largo máximo 9.
     * - Índice 0: Letra (se convierte a mayúscula).
     * - Índices 1..8: Dígitos.
     */
    public static DocumentFilter getFiltroPasaporte() {
        return new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                replace(fb, offset, 0, string, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) {
                    super.replace(fb, offset, length, null, attrs);
                    return;
                }

                // Generar texto propuesto letra por letra para validar cada posición
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                StringBuilder proposed = new StringBuilder(currentText);
                proposed.replace(offset, offset + length, text);

                if (proposed.length() > 9) {
                    return; // Excede el largo de 1 letra + 8 dígitos
                }

                // Validar patrón propuesto carácter a carácter
                for (int i = 0; i < proposed.length(); i++) {
                    char c = proposed.charAt(i);
                    if (i == 0) {
                        if (!Character.isLetter(c)) {
                            return; // Primera posición debe ser letra
                        }
                    } else {
                        if (!Character.isDigit(c)) {
                            return; // Posiciones 1 a 8 deben ser dígitos
                        }
                    }
                }

                // Si es válido, insertamos convirtiendo a mayúsculas si corresponde a la letra inicial
                String modifiedText = text;
                if (offset == 0 && text.length() > 0) {
                    char first = Character.toUpperCase(text.charAt(0));
                    modifiedText = first + text.substring(1);
                }
                super.replace(fb, offset, length, modifiedText, attrs);
            }
        };
    }

    /**
     * Filtro para Domicilio: Cualquier carácter con límite de largo máximo
     */
    public static DocumentFilter getFiltroLongitudMaxima(int maxLargo) {
        return new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                
                String newText = getNewText(fb, offset, 0, string);
                if (newText.length() <= maxLargo) {
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
                if (newText.length() <= maxLargo) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        };
    }

    // Helper to construct the future text of a text component after modification
    private static String getNewText(DocumentFilter.FilterBypass fb, int offset, int length, String text) throws BadLocationException {
        String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
        return currentText.substring(0, offset) + text + currentText.substring(offset + length);
    }
}

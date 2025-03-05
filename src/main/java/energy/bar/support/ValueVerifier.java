package energy.bar.support;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

public class ValueVerifier extends InputVerifier {

    @Override
    public boolean shouldYieldFocus(JComponent input) {
        JTextField textField = (JTextField) input;
        String text = textField.getText();

        if (text.isEmpty()) {
            return true; // Aceita campo vazio
        }

        try {
            Double.parseDouble(text.replaceAll("[^0-9]", "")); // Tenta converter para double (sem formatação)
            return true; // Aceita se for numérico
        } catch (NumberFormatException e) {
            // Exibe mensagem de erro
            JOptionPane.showMessageDialog(input, "Digite um valor numérico válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            textField.setText(""); // Limpa o campo
            return false; // Rejeita se não for numérico
        }
    }

    @Override
    public boolean verify(JComponent input) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

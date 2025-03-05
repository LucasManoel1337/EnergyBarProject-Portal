package energy.bar;

import energy.bar.support.LabelEnergyBar;
import javax.swing.*;

class TelaEstoqueBar extends JPanel {

    private EnergyBarProject mainApp;

    LabelEnergyBar labelEnergyBar = new LabelEnergyBar();

    public TelaEstoqueBar() {
        setVisible(true);
        setLayout(null); // Define layout manual (absoluto)
        
        // Criando e adicionando a label EnergyBar
        JLabel energyBarLabel = labelEnergyBar.criarLabelEnergyBar();
        add(energyBarLabel);

        // Carrega a imagem
        ImageIcon icon = new ImageIcon("Arquivos de suporte/imagens/a.jpg");
        JLabel imageLabel = new JLabel(icon);
        imageLabel.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight()); // Define a posição e o tamanho da imagem

        // Adiciona o JLabel ao painel
        add(imageLabel);
    }
}

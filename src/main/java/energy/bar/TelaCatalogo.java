package energy.bar;

import energy.bar.support.LabelEnergyBar;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import energy.bar.db.ConexaoBancoDeDados;
import energy.bar.support.TimerAvisosLabels;

class TelaCatalogo extends JPanel {

    LabelEnergyBar labelEnergyBar = new LabelEnergyBar();
    String[] colunas = {"ID", "Produto"};
    DefaultTableModel modeloTabela = new DefaultTableModel(colunas, 0);
    public JTable tabelaProdutos = new JTable(modeloTabela);
    public JTextField campoNome = new JTextField();
    private JButton bAdicionar;
    private JLabel lfaltaDeDados = new JLabel("Todos os campos devem ser preenchidos!");
    private JLabel lCadastroFeito = new JLabel("Produto cadastrado com sucesso!");
    private JLabel lPordutoIgual = new JLabel("Já existe um produto no catalogo com o mesmo nome!");
    
    ConexaoBancoDeDados b = new ConexaoBancoDeDados();
    TimerAvisosLabels tir = new TimerAvisosLabels();
    

    public TelaCatalogo() {
        setLayout(null);
        
        // Label falta de dados
        lfaltaDeDados.setFont(new Font("Arial", Font.BOLD, 16));
        lfaltaDeDados.setBounds(220, 510, 350, 40); // Define posição e tamanho
        lfaltaDeDados.setForeground(Color.RED);
        lfaltaDeDados.setVisible(false);
        add(lfaltaDeDados);
        
        lPordutoIgual.setFont(new Font("Arial", Font.BOLD, 16));
        lPordutoIgual.setBounds(190, 510, 450, 40); // Define posição e tamanho
        lPordutoIgual.setForeground(Color.RED);
        lPordutoIgual.setVisible(false);
        add(lPordutoIgual);
        
        // Label falta de dados
        lCadastroFeito.setFont(new Font("Arial", Font.BOLD, 16));
        lCadastroFeito.setBounds(250, 510, 350, 40); // Define posição e tamanho
        lCadastroFeito.setForeground(Color.GREEN);
        lCadastroFeito.setVisible(false);
        add(lCadastroFeito);

        // Criando e adicionando a label EnergyBar
        JLabel energyBarLabel = labelEnergyBar.criarLabelEnergyBar();
        add(energyBarLabel);

        tabelaProdutos.setRowHeight(30);
        tabelaProdutos.setFont(new Font("Arial", Font.PLAIN, 14));
        tabelaProdutos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        tabelaProdutos.getTableHeader().setBackground(new Color(32, 3, 3));
        tabelaProdutos.getTableHeader().setForeground(Color.WHITE);
        tabelaProdutos.setBackground(new Color(245, 245, 245));
        tabelaProdutos.setForeground(new Color(0, 0, 0));
        tabelaProdutos.setGridColor(new Color(200, 200, 200));
        tabelaProdutos.setSelectionBackground(new Color(52, 152, 219));
        tabelaProdutos.setSelectionForeground(Color.WHITE);
        tabelaProdutos.setRowHeight(30);
        tabelaProdutos.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(modeloTabela);
        tabelaProdutos.setRowSorter(sorter);

        sorter.setComparator(0, (o1, o2) -> {
            try {
                int id1 = Integer.parseInt(o1.toString());
                int id2 = Integer.parseInt(o2.toString());
                return Integer.compare(id1, id2);
            } catch (NumberFormatException e) {
                return 0;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabelaProdutos);
        scrollPane.setBounds(10, 70, 400, 400);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 2));
        add(scrollPane);

        tabelaProdutos.getColumnModel().getColumn(0).setPreferredWidth(5);
        tabelaProdutos.getColumnModel().getColumn(1).setPreferredWidth(250);

        JLabel lNome = new JLabel("Nome do produto");
        lNome.setFont(new Font("Arial", Font.BOLD, 16));
        lNome.setBounds(420, 60, 300, 40);
        add(lNome);

        campoNome.setBounds(420, 90, 330, 30);
        campoNome.setBackground(Color.LIGHT_GRAY);
        campoNome.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        campoNome.setFont(new Font("Arial", Font.BOLD, 16));
        add(campoNome);

        bAdicionar = new JButton("Adicionar");
        bAdicionar.setFont(new Font("Arial", Font.BOLD, 14));
        bAdicionar.setBounds(630, 130, 120, 30);
        bAdicionar.setBackground(new Color(32, 3, 3));
        bAdicionar.setForeground(Color.WHITE);
        bAdicionar.setFocusPainted(false);
        bAdicionar.setBorderPainted(false);
        add(bAdicionar);

        // Adiciona ação ao botão
        bAdicionar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lfaltaDeDados.setVisible(false);
                lCadastroFeito.setVisible(false);
                lPordutoIgual.setVisible(false);
                
                adicionarProduto();
            }
        });

        carregarProdutos(); // Carrega os produtos na tabela ao iniciar
    }

    // Método para inserir produto no banco
    private void adicionarProduto() {
    String nomeProduto = campoNome.getText().trim();

    if (nomeProduto.isEmpty()) {
        tir.exibirAvisoTemporario(lfaltaDeDados);
        return;
    }

    try (Connection conn = b.getConnection()) {
        // Verificar se o produto já existe no banco
        String verificaProduto = "SELECT COUNT(*) FROM tb_catalogo WHERE produto = ?";
        try (PreparedStatement stmtVerifica = conn.prepareStatement(verificaProduto)) {
            stmtVerifica.setString(1, nomeProduto);
            ResultSet rs = stmtVerifica.executeQuery();
            rs.next();

            if (rs.getInt(1) > 0) { // Se o produto já existir
                tir.exibirAvisoTemporario(lPordutoIgual);
                campoNome.setText("");
                return;
            }
        }

        // Se não existir, inserir o novo produto
        String inserirProduto = "INSERT INTO tb_catalogo (produto) VALUES (?)";
        try (PreparedStatement stmtInserir = conn.prepareStatement(inserirProduto)) {
            stmtInserir.setString(1, nomeProduto);
            stmtInserir.executeUpdate();

            tir.exibirAvisoTemporario(lCadastroFeito);
            campoNome.setText(""); // Limpar campo após inserção
            carregarProdutos(); // Atualizar a tabela na tela
        }

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Erro ao adicionar produto: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }
}

    // Método para carregar produtos do banco para a tabela
    private void carregarProdutos() {
        modeloTabela.setRowCount(0); // Limpa a tabela antes de carregar os dados

        try (Connection conn = b.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, produto FROM tb_catalogo ORDER BY id ASC");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String produto = rs.getString("produto");
                modeloTabela.addRow(new Object[]{id, produto});
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}

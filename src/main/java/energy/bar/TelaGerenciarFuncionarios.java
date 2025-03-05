package energy.bar;

import energy.bar.db.ConexaoBancoDeDados;
import energy.bar.support.LabelEnergyBar;
import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

class TelaGerenciarFuncionarios extends JPanel {

    LabelEnergyBar labelEnergyBar = new LabelEnergyBar();

    String[] colunas = {"ID", "Nome", "Situação", "Unidade"};
    DefaultTableModel modeloTabela = new DefaultTableModel(colunas, 0);
    public JTable tabelaFuncionarios = new JTable(modeloTabela);

    public TelaGerenciarFuncionarios() {
        setLayout(null);

        // Criando e adicionando a label EnergyBar
        JLabel energyBarLabel = labelEnergyBar.criarLabelEnergyBar();
        add(energyBarLabel);

        tabelaFuncionarios.setRowHeight(30);
        tabelaFuncionarios.setFont(new Font("Arial", Font.PLAIN, 14)); // Fonte da tabela
        tabelaFuncionarios.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16)); // Fonte do cabeçalho
        tabelaFuncionarios.getTableHeader().setBackground(new Color(32, 3, 3)); // Cor de fundo do cabeçalho
        tabelaFuncionarios.getTableHeader().setForeground(Color.WHITE); // Cor do texto do cabeçalho
        tabelaFuncionarios.setBackground(new Color(245, 245, 245)); // Cor de fundo da tabela
        tabelaFuncionarios.setForeground(new Color(0, 0, 0)); // Cor do texto
        tabelaFuncionarios.setGridColor(new Color(200, 200, 200)); // Cor das linhas de grade
        tabelaFuncionarios.setSelectionBackground(new Color(52, 152, 219)); // Cor de fundo ao selecionar
        tabelaFuncionarios.setSelectionForeground(Color.WHITE); // Cor do texto ao selecionar
        tabelaFuncionarios.setRowHeight(30); // Ajustando a altura das linhas
        tabelaFuncionarios.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1)); // Bordas da tabela

        // Usando TableRowSorter para ordenar numericamente a coluna ID
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(modeloTabela);
        tabelaFuncionarios.setRowSorter(sorter);

        // Definir o tipo de comparação para a coluna ID (coluna 0)
        sorter.setComparator(0, (o1, o2) -> {
            try {
                int id1 = Integer.parseInt(o1.toString());
                int id2 = Integer.parseInt(o2.toString());
                return Integer.compare(id1, id2); // Comparação numérica
            } catch (NumberFormatException e) {
                return 0; // Em caso de erro de conversão
            }
        });

        tabelaFuncionarios.getColumnModel().getColumn(0).setPreferredWidth(5);
        tabelaFuncionarios.getColumnModel().getColumn(1).setPreferredWidth(80);
        tabelaFuncionarios.getColumnModel().getColumn(2).setPreferredWidth(10);
        tabelaFuncionarios.getColumnModel().getColumn(3).setPreferredWidth(10);

        JScrollPane scrollPane = new JScrollPane(tabelaFuncionarios);
        scrollPane.setVisible(true);
        scrollPane.setBounds(10, 70, 400, 400); // Definindo o tamanho e posição
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 2)); // Bordas do JScrollPane
        add(scrollPane);
        atualizarTabelaFuncionarios();

        JLabel lId = new JLabel("Nome do Funcionario");
        lId.setFont(new Font("Arial", Font.BOLD, 16));
        lId.setBounds(420, 60, 300, 40); // Define posição e tamanho
        lId.setVisible(true);
        add(lId);
        JTextField campoNomeDoFuncionario = new JTextField();
        campoNomeDoFuncionario.setBounds(420, 90, 330, 30);
        campoNomeDoFuncionario.setBackground(Color.LIGHT_GRAY);
        campoNomeDoFuncionario.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        campoNomeDoFuncionario.setFont(new Font("Arial", Font.BOLD, 16));
        campoNomeDoFuncionario.setVisible(true);
        add(campoNomeDoFuncionario);

        JButton bCadastrar = new JButton();
        bCadastrar.setText("Cadastrar");
        bCadastrar.setFont(new Font("Arial", Font.BOLD, 14));
        bCadastrar.setBounds(630, 150, 120, 30);
        bCadastrar.setBackground(new Color(32, 3, 3));
        bCadastrar.setForeground(Color.WHITE);
        bCadastrar.setFocusPainted(false);
        bCadastrar.setBorderPainted(false);
        bCadastrar.setVisible(true);
        add(bCadastrar);

        JLabel lUnidade = new JLabel("Unidade do funcionario");
        lUnidade.setFont(new Font("Arial", Font.BOLD, 16));
        lUnidade.setBounds(420, 120, 320, 30);
        lUnidade.setVisible(true);
        add(lUnidade);

// Criando um ArrayList para armazenar os IDs das unidades
        ArrayList<String> unidadesIds = new ArrayList<>();

        try (Connection conn = ConexaoBancoDeDados.getConnection()) {
            String query = "SELECT id FROM tb_unidades";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();

                // Adiciona os IDs das unidades ao ArrayList
                while (rs.next()) {
                    int id = rs.getInt("id");
                    unidadesIds.add(String.valueOf(id));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

// Converte o ArrayList para um array e preenche o JComboBox
        String[] responsaveis = unidadesIds.toArray(new String[0]);

        JComboBox<String> campoUnidade = new JComboBox<>(responsaveis);
        campoUnidade.setBounds(420, 150, 200, 30);
        campoUnidade.setBackground(Color.LIGHT_GRAY);
        campoUnidade.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        campoUnidade.setFocusable(false);
        campoUnidade.setFont(new Font("Arial", Font.BOLD, 16));
        campoUnidade.setVisible(true);
        add(campoUnidade);

        bCadastrar.addActionListener(e -> {
            // Recuperando os dados dos campos
            String nomeFuncionario = campoNomeDoFuncionario.getText().trim();
            String unidadeId = (String) campoUnidade.getSelectedItem();

            // Verificando se o nome e a unidade foram preenchidos
            if (nomeFuncionario.isEmpty() || unidadeId == null || unidadeId.isEmpty()) {
                System.out.println("Por favor, preencha todos os campos.");
                return;
            }

            // Verificando se já existe um funcionário com o mesmo nome na mesma unidade
            boolean funcionarioExistente = false;

            try (Connection conn = ConexaoBancoDeDados.getConnection()) {
                // Consulta SQL para verificar se já existe funcionário com o mesmo nome e unidade
                String query = "SELECT COUNT(*) FROM tb_funcionarios WHERE nome = ? AND unidade = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, nomeFuncionario);
                    stmt.setInt(2, Integer.parseInt(unidadeId)); // Convertendo o ID da unidade para inteiro
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next() && rs.getInt(1) > 0) {
                        funcionarioExistente = true;
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            if (funcionarioExistente) {
                System.out.println("Já existe um funcionário com esse nome para essa unidade.");
            } else {
                // Cadastrar o novo funcionário
                String insertQuery = "INSERT INTO tb_funcionarios (nome, unidade, situacao) VALUES (?, ?, ?)";
                try (Connection conn = ConexaoBancoDeDados.getConnection()) {
                    try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                        stmt.setString(1, nomeFuncionario);
                        stmt.setInt(2, Integer.parseInt(unidadeId)); // Convertendo o ID da unidade para inteiro
                        stmt.setBoolean(3, true); // Exemplo de situação (você pode personalizar isso)
                        stmt.executeUpdate();

                        System.out.println("Funcionário cadastrado com sucesso.");
                        campoNomeDoFuncionario.setText("");
                        // Atualizar a tabela após o cadastro
                        atualizarTabelaFuncionarios();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    // Função para atualizar a tabela com os dados mais recentes
    private void atualizarTabelaFuncionarios() {
        modeloTabela.setRowCount(0); // Limpar a tabela antes de adicionar novos dados
        try (Connection conn = ConexaoBancoDeDados.getConnection()) {
            String query = "SELECT f.id, f.nome, f.situacao, f.unidade "
                    + "FROM tb_funcionarios f "
                    + "JOIN tb_unidades u ON f.unidade = u.id";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();

                // Preencher a tabela com os dados atualizados
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nome = rs.getString("nome");
                    String situacao = rs.getString("situacao");
                    int unidadeId = rs.getInt("unidade"); // Alterado para pegar o ID da unidade
                    modeloTabela.addRow(new Object[]{id, nome, situacao, unidadeId}); // Exibindo o ID da unidade
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

package energy.bar;

import energy.bar.back.dao.FuncionariosDAO;
import energy.bar.db.ConexaoBancoDeDados;
import energy.bar.db.GerenciadorDeProdutos;
import energy.bar.support.IdFilter;
import energy.bar.support.IdVerifier;
import energy.bar.support.LabelEnergyBar;
import energy.bar.support.TimerAvisosLabels;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AbstractDocument;

class TelaVendas extends JPanel {

    FuncionariosDAO funcionario = new FuncionariosDAO();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss - dd-MM-yyyy");
    String dataHoraAtual = LocalDateTime.now().format(formatter);
    public String dataHora = new java.text.SimpleDateFormat(dataHoraAtual).format(new java.util.Date());

    String[] colunas = {"ID", "Produto", "Qtn", "Valor", "Lote"};
    DefaultTableModel modeloTabela = new DefaultTableModel(colunas, 0);

    public JButton bRemover = new JButton();
    public JTable tabelaProdutos = new JTable(modeloTabela);

    public JTextField campoQtn = new JTextField("");
    public JTextField campoId = new JTextField("");
    public JLabel lProdutoAdicionado = new JLabel("Produto com ID " + campoId.getText() + " adicionado no carrinho");
    public JLabel lProdutoSemEstoque = new JLabel("Produto com ID " + campoId.getText() + " está sem estoque no lote mais velho");
    public JLabel lDescontoAplicado = new JLabel("- Desconto ja aplicado!");

    TimerAvisosLabels tir = new TimerAvisosLabels();
    LabelEnergyBar labelEnergyBar = new LabelEnergyBar();
    private BigDecimal valorTotalDeCompra = BigDecimal.ZERO;

    public JLabel lfaltaDeDados = new JLabel("Campos obrigatorios devem ser preenchidos!");
    public JLabel lCompraFinalizada = new JLabel("Compra feita e cadastrada com sucesso!");
    public JLabel lCarrinhoVazio = new JLabel("Carrinho está vazio!");
    public JLabel lIdOuQtnVazio = new JLabel("ID ou Quantidade está vazio!");
    public JLabel lProdutoNaoExiste = new JLabel("Esse ID de produto não existe ou não foi localizado!");

    String[] responsaveis = {"", "Cliente", "Funcionario"};
    public JComboBox<String> campoTipocliente = new JComboBox<>(responsaveis);

    public JTextField campoTotalDaCompra = new JTextField();
    public JTextField campoDesconto = new JTextField();

    String[] formaDePagamento = {"", "Dinheiro", "Pix", "Débito", "Crédito"};
    public JComboBox<String> campoFormaDePagamento = new JComboBox<>(formaDePagamento);

    public JComboBox<String> campoFuncionario = new JComboBox<>();

    public String descontoStr = campoDesconto.getText().trim();
    public int desconto = 0; // valor padrão para desconto

    public TelaVendas() throws IOException {
        setLayout(null);

        // Criando e adicionando a label EnergyBar
        JLabel energyBarLabel = labelEnergyBar.criarLabelEnergyBar();
        add(energyBarLabel);

        tabelaProdutos.setRowHeight(30);
        tabelaProdutos.setFont(new Font("Arial", Font.PLAIN, 14)); // Fonte da tabela
        tabelaProdutos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16)); // Fonte do cabeçalho
        tabelaProdutos.getTableHeader().setBackground(new Color(32, 3, 3)); // Cor de fundo do cabeçalho
        tabelaProdutos.getTableHeader().setForeground(Color.WHITE); // Cor do texto do cabeçalho
        tabelaProdutos.setBackground(new Color(245, 245, 245)); // Cor de fundo da tabela
        tabelaProdutos.setForeground(new Color(0, 0, 0)); // Cor do texto
        tabelaProdutos.setGridColor(new Color(200, 200, 200)); // Cor das linhas de grade
        tabelaProdutos.setSelectionBackground(new Color(52, 152, 219)); // Cor de fundo ao selecionar
        tabelaProdutos.setSelectionForeground(Color.WHITE); // Cor do texto ao selecionar
        tabelaProdutos.setRowHeight(30); // Ajustando a altura das linhas
        tabelaProdutos.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1)); // Bordas da tabela

        // Usando TableRowSorter para ordenar numericamente a coluna ID
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(modeloTabela);
        tabelaProdutos.setRowSorter(sorter);

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

        tabelaProdutos.getColumnModel().getColumn(0).setPreferredWidth(5);
        tabelaProdutos.getColumnModel().getColumn(1).setPreferredWidth(80);
        tabelaProdutos.getColumnModel().getColumn(2).setPreferredWidth(10);

        JScrollPane scrollPane = new JScrollPane(tabelaProdutos);
        scrollPane.setVisible(true);
        scrollPane.setBounds(10, 70, 400, 400); // Definindo o tamanho e posição
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 2)); // Bordas do JScrollPane
        add(scrollPane);

        lProdutoNaoExiste.setFont(new Font("Arial", Font.BOLD, 16));
        lProdutoNaoExiste.setBounds(200, 465, 400, 40); // Define posição e tamanho
        lProdutoNaoExiste.setForeground(Color.RED);
        lProdutoNaoExiste.setVisible(false);
        add(lProdutoNaoExiste);

        lfaltaDeDados.setFont(new Font("Arial", Font.BOLD, 16));
        lfaltaDeDados.setBounds(220, 465, 350, 40); // Define posição e tamanho
        lfaltaDeDados.setForeground(Color.RED);
        lfaltaDeDados.setVisible(false);
        add(lfaltaDeDados);

        lCompraFinalizada.setFont(new Font("Arial", Font.BOLD, 16));
        lCompraFinalizada.setBounds(220, 465, 350, 40); // Define posição e tamanho
        lCompraFinalizada.setForeground(Color.GREEN);
        lCompraFinalizada.setVisible(false);
        add(lCompraFinalizada);

        lCarrinhoVazio.setFont(new Font("Arial", Font.BOLD, 16));
        lCarrinhoVazio.setBounds(290, 465, 350, 40); // Define posição e tamanho
        lCarrinhoVazio.setForeground(Color.RED);
        lCarrinhoVazio.setVisible(false);
        add(lCarrinhoVazio);

        lIdOuQtnVazio.setFont(new Font("Arial", Font.BOLD, 16));
        lIdOuQtnVazio.setBounds(260, 465, 350, 40); // Define posição e tamanho
        lIdOuQtnVazio.setForeground(Color.RED);
        lIdOuQtnVazio.setVisible(false);
        add(lIdOuQtnVazio);

        lDescontoAplicado.setFont(new Font("Arial", Font.BOLD, 16));
        lDescontoAplicado.setBounds(135, 480, 350, 40); // Define posição e tamanho
        lDescontoAplicado.setForeground(Color.GREEN);
        lDescontoAplicado.setVisible(false);
        add(lDescontoAplicado);

        // Label e TextField ID
        JLabel lId = new JLabel("ID do produto");
        lId.setFont(new Font("Arial", Font.BOLD, 16));
        lId.setBounds(420, 60, 300, 40); // Define posição e tamanho
        lId.setVisible(true);
        add(lId);
        campoId.setBounds(420, 90, 330, 30);
        campoId.setBackground(Color.LIGHT_GRAY);
        campoId.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        campoId.setFont(new Font("Arial", Font.BOLD, 16));
        campoId.setVisible(true);
        add(campoId);
        campoId.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                JTextField textField = (JTextField) input;
                String text = textField.getText().trim();

                // Permite campo vazio para que o usuário possa sair dele
                if (text.isEmpty()) {
                    campoId.setText("");
                    return true;
                }

                return true; // Permite a mudança de foco
            }
        });

        lProdutoAdicionado.setFont(new Font("Arial", Font.BOLD, 16));
        lProdutoAdicionado.setBounds(220, 465, 350, 40); // Define posição e tamanho
        lProdutoAdicionado.setForeground(Color.GREEN);
        lProdutoAdicionado.setVisible(false);
        add(lProdutoAdicionado);

        lProdutoSemEstoque.setFont(new Font("Arial", Font.BOLD, 16));
        lProdutoSemEstoque.setBounds(150, 465, 450, 40); // Define posição e tamanho
        lProdutoSemEstoque.setForeground(Color.RED);
        lProdutoSemEstoque.setVisible(false);
        add(lProdutoSemEstoque);

        // Label e TextField ID
        JLabel lQnt = new JLabel("Quantidade");
        lQnt.setFont(new Font("Arial", Font.BOLD, 16));
        lQnt.setBounds(420, 120, 300, 40); // Define posição e tamanho
        lQnt.setVisible(true);
        add(lQnt);
        campoQtn.setBounds(420, 150, 330, 30);
        campoQtn.setBackground(Color.LIGHT_GRAY);
        campoQtn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        campoQtn.setFont(new Font("Arial", Font.BOLD, 16));
        campoQtn.setVisible(true);
        add(campoQtn);
        campoQtn.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                JTextField textField = (JTextField) input;
                String text = textField.getText().trim();

                // Permite campo vazio para que o usuário possa sair dele
                if (text.isEmpty()) {
                    campoQtn.setText("");
                    return true;
                }

                return true; // Permite a mudança de foco
            }
        });

        JButton bRemover = new JButton();
        bRemover.setText("Remover");
        bRemover.setFont(new Font("Arial", Font.BOLD, 14));
        bRemover.setBounds(420, 190, 120, 30);
        bRemover.setBackground(Color.RED);
        bRemover.setForeground(Color.WHITE);
        bRemover.setFocusPainted(false);
        bRemover.setBorderPainted(false);
        bRemover.setVisible(true);
        add(bRemover);

        JButton bAdicionar = new JButton();
        bAdicionar.setText("Adicionar");
        bAdicionar.setFont(new Font("Arial", Font.BOLD, 14));
        bAdicionar.setBounds(630, 190, 120, 30);
        bAdicionar.setBackground(Color.GREEN);
        bAdicionar.setForeground(Color.WHITE);
        bAdicionar.setFocusPainted(false);
        bAdicionar.setBorderPainted(false);
        bAdicionar.setVisible(true);
        add(bAdicionar);

        JLabel lTipoCliente = new JLabel("Tipo de cliente");
        lTipoCliente.setFont(new Font("Arial", Font.BOLD, 16));
        lTipoCliente.setBounds(420, 230, 330, 30);
        lTipoCliente.setVisible(true);
        add(lTipoCliente);
        campoTipocliente.setBounds(420, 260, 330, 30);
        campoTipocliente.setBackground(Color.LIGHT_GRAY);
        campoTipocliente.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        campoTipocliente.setFocusable(false);
        campoTipocliente.setFont(new Font("Arial", Font.BOLD, 16));
        campoTipocliente.setVisible(true);
        add(campoTipocliente);
        campoTipocliente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selecionado = (String) campoTipocliente.getSelectedItem();

                if ("Funcionario".equals(selecionado)) {
                    campoDesconto.setText("020");
                    campoTotalDaCompra.setText("R$ " + calcularTotalCompra());
                } else {
                    campoDesconto.setText("000");
                    campoTotalDaCompra.setText("R$ " + calcularTotalCompra());
                }

            }
        });

        JLabel lFormaDePagamento = new JLabel("Forma de pagamento");
        lFormaDePagamento.setFont(new Font("Arial", Font.BOLD, 16));
        lFormaDePagamento.setBounds(420, 290, 330, 30);
        lFormaDePagamento.setVisible(true);
        add(lFormaDePagamento);
        campoFormaDePagamento.setBounds(420, 320, 330, 30);
        campoFormaDePagamento.setBackground(Color.LIGHT_GRAY);
        campoFormaDePagamento.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        campoFormaDePagamento.setFocusable(false);
        campoFormaDePagamento.setFont(new Font("Arial", Font.BOLD, 16));
        campoFormaDePagamento.setVisible(true);
        add(campoFormaDePagamento);

        // Label e TextField ID
        JLabel lDesconto = new JLabel("Desconto");
        lDesconto.setFont(new Font("Arial", Font.BOLD, 16));
        lDesconto.setBounds(420, 350, 330, 30);
        lDesconto.setVisible(true);
        add(lDesconto);

        campoDesconto.setBounds(420, 380, 220, 30);
        campoDesconto.setBackground(Color.LIGHT_GRAY);
        campoDesconto.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        campoDesconto.setFont(new Font("Arial", Font.BOLD, 16));
        IdVerifier verifier = new IdVerifier();
        IdFilter idFilter = new IdFilter();
        campoDesconto.setInputVerifier(verifier);
        ((AbstractDocument) campoDesconto.getDocument()).setDocumentFilter(idFilter);
        campoDesconto.setText("000");
        campoDesconto.setVisible(true);
        add(campoDesconto);
        campoDesconto.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                return true; // Permite a mudança de foco
            }
        });

        JButton bAplicarDesconto = new JButton();
        bAplicarDesconto.setText("Aplicar");
        bAplicarDesconto.setFont(new Font("Arial", Font.BOLD, 14));
        bAplicarDesconto.setBounds(650, 380, 100, 30);
        bAplicarDesconto.setBackground(new Color(32, 3, 3));
        bAplicarDesconto.setForeground(Color.WHITE);
        bAplicarDesconto.setFocusPainted(false);
        bAplicarDesconto.setBorderPainted(false);
        bAplicarDesconto.setVisible(true);
        add(bAplicarDesconto);
        bAplicarDesconto.addActionListener(e -> {
            campoTotalDaCompra.setText("R$ " + calcularTotalCompra());
        });

        JLabel lFuncionario = new JLabel("Funcionario");
        lFuncionario.setFont(new Font("Arial", Font.BOLD, 16));
        lFuncionario.setBounds(420, 410, 330, 30);
        lFuncionario.setVisible(true);
        add(lFuncionario);
        campoFuncionario.setBounds(420, 440, 330, 30);
        campoFuncionario.setBackground(Color.LIGHT_GRAY);
        campoFuncionario.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        campoFuncionario.setFocusable(false);
        campoFuncionario.setFont(new Font("Arial", Font.BOLD, 16));
        campoFuncionario.setVisible(true);
        FuncionariosDAO.buscarFuncionariosPorUnidade(campoFuncionario);
        add(campoFuncionario);

        JButton bFinalizarCompra = new JButton();
        bFinalizarCompra.setText("Finalizar Compra");
        bFinalizarCompra.setFont(new Font("Arial", Font.BOLD, 14));
        bFinalizarCompra.setBounds(590, 510, 160, 30);
        bFinalizarCompra.setBackground(Color.GREEN);
        bFinalizarCompra.setForeground(Color.WHITE);
        bFinalizarCompra.setFocusPainted(false);
        bFinalizarCompra.setBorderPainted(false);
        bFinalizarCompra.setVisible(true);
        add(bFinalizarCompra);

        JButton bCancelarCompra = new JButton();
        bCancelarCompra.setText("Cancelar Compra");
        bCancelarCompra.setFont(new Font("Arial", Font.BOLD, 14));
        bCancelarCompra.setBounds(420, 510, 160, 30);
        bCancelarCompra.setBackground(Color.RED);
        bCancelarCompra.setForeground(Color.WHITE);
        bCancelarCompra.setFocusPainted(false);
        bCancelarCompra.setBorderPainted(false);
        bCancelarCompra.setVisible(true);
        add(bCancelarCompra);

        JLabel lTotalDaCompra = new JLabel("Total da compra");
        lTotalDaCompra.setFont(new Font("Arial", Font.BOLD, 16));
        lTotalDaCompra.setBounds(10, 480, 300, 40); // Define posição e tamanho
        lTotalDaCompra.setVisible(true);
        add(lTotalDaCompra);
        campoTotalDaCompra.setBounds(10, 510, 330, 30);
        campoTotalDaCompra.setBackground(Color.LIGHT_GRAY);
        campoTotalDaCompra.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        campoTotalDaCompra.setFont(new Font("Arial", Font.BOLD, 16));
        campoTotalDaCompra.setVisible(true);
        campoTotalDaCompra.setEditable(false);
        campoTotalDaCompra.setText("R$ 00.00");
        add(campoTotalDaCompra);

        bAdicionar.addActionListener(e -> {
            // Esconder mensagens anteriores
            lfaltaDeDados.setVisible(false);
            lProdutoNaoExiste.setVisible(false);
            lCarrinhoVazio.setVisible(false);
            lCompraFinalizada.setVisible(false);
            lProdutoAdicionado.setVisible(false);
            lIdOuQtnVazio.setVisible(false);
            lProdutoSemEstoque.setVisible(false);

            // Captura os valores dos campos
            String id = campoId.getText().trim();
            String qtnStr = campoQtn.getText().trim();

            // Validação dos campos
            if (id.isEmpty() || qtnStr.isEmpty() || id.equals("000") || qtnStr.equals("000")) {
                tir.exibirAvisoTemporario(lIdOuQtnVazio);
                return;
            }

            try {
                int idProduto = Integer.parseInt(id);
                int quantidadeDesejada = Integer.parseInt(qtnStr);

                if (quantidadeDesejada <= 0) {
                    tir.exibirAvisoTemporario(lIdOuQtnVazio);
                    return;
                }

                // Consulta SQL para buscar o produto pelo ID, lote mais antigo e unidade do programa
                String sql = "SELECT id_produto, nome, estoque, valor_venda, lote, validade, data_cadastro "
                        + "FROM tb_produtos "
                        + "WHERE id_produto = ? AND estoque > 0 AND produto_unidade = ? "
                        + "ORDER BY data_cadastro ASC, validade ASC LIMIT 1"; // Lote mais antigo

                try (Connection conn = ConexaoBancoDeDados.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setInt(1, idProduto);
                    stmt.setInt(2, funcionario.getUnidadeDoProgama()); // Filtra pela unidade do funcionário

                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            String nomeProduto = rs.getString("nome");
                            int quantidadeAtual = rs.getInt("estoque");
                            BigDecimal valorDeVenda = rs.getBigDecimal("valor_venda");
                            String loteAntigo = rs.getString("lote");

                            // Verifica se há estoque suficiente
                            if (quantidadeDesejada > quantidadeAtual) {
                                lProdutoSemEstoque.setText("Estoque insuficiente! Disponível: " + quantidadeAtual);
                                tir.exibirAvisoTemporario(lProdutoSemEstoque);
                                return;
                            }

                            // Adiciona o produto ao carrinho (tabela de exibição)
                            modeloTabela.addRow(new Object[]{idProduto, nomeProduto, quantidadeDesejada, valorDeVenda, loteAntigo});

                            // Atualiza o valor total da compra
                            valorTotalDeCompra = valorTotalDeCompra.add(valorDeVenda.multiply(new BigDecimal(quantidadeDesejada)));
                            campoTotalDaCompra.setText(String.format("R$ %.2f", valorTotalDeCompra));

                            // Atualiza o estoque do lote mais antigo no banco de dados
                            String updateSql = "UPDATE tb_produtos SET estoque = estoque - ? WHERE id_produto = ? AND lote = ?";
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                                updateStmt.setInt(1, quantidadeDesejada);
                                updateStmt.setInt(2, idProduto);
                                updateStmt.setString(3, loteAntigo);
                                updateStmt.executeUpdate();
                            }

                            // Limpa os campos após adicionar o produto ao carrinho
                            campoId.setText("");
                            campoQtn.setText("");

                            // Mensagem de sucesso
                            lProdutoAdicionado.setText("Produto com ID " + idProduto + " adicionado no carrinho");
                            tir.exibirAvisoTemporario(lProdutoAdicionado);
                        } else {
                            // Produto não encontrado no banco ou sem estoque disponível
                            tir.exibirAvisoTemporario(lProdutoNaoExiste);
                            campoId.setText("");
                            campoQtn.setText("");
                        }
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "ID e quantidade devem ser números válidos!", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro ao buscar produto no banco de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        bRemover.addActionListener(e -> {
            // Esconder mensagens anteriores
            lfaltaDeDados.setVisible(false);
            lProdutoNaoExiste.setVisible(false);
            lCarrinhoVazio.setVisible(false);
            lCompraFinalizada.setVisible(false);
            lProdutoAdicionado.setVisible(false);
            lIdOuQtnVazio.setVisible(false);

            try {
                removerProduto();
                campoTotalDaCompra.setText("R$ " + calcularTotalCompra());
            } catch (Exception ex) {
                Logger.getLogger(TelaVendas.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "Erro ao remover produto do carrinho.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        bFinalizarCompra.addActionListener(e -> {
    lfaltaDeDados.setVisible(false);
    lProdutoNaoExiste.setVisible(false);
    lCarrinhoVazio.setVisible(false);
    lCompraFinalizada.setVisible(false);
    lProdutoAdicionado.setVisible(false);
    lIdOuQtnVazio.setVisible(false);

    // Verifica se os campos ComboBox e a tabela estão preenchidos
    if (campoTipocliente.getSelectedItem() == null || campoFormaDePagamento.getSelectedItem() == null || campoFuncionario.getSelectedItem() == null
            || campoTipocliente.getSelectedItem().equals("") || campoFormaDePagamento.getSelectedItem().equals("") || campoFuncionario.getSelectedItem().equals("")) {
        tir.exibirAvisoTemporario(lfaltaDeDados);
        return;
    }

    if (modeloTabela.getRowCount() == 0) {
        tir.exibirAvisoTemporario(lCarrinhoVazio);
        return;
    }

    Connection connection = null;
    PreparedStatement stmtCompra = null;
    PreparedStatement stmtProdutoCompra = null;

    try {
        // Estabelece a conexão com o banco de dados
        connection = ConexaoBancoDeDados.getConnection();

        // Inicia a transação
        connection.setAutoCommit(false);

        // 1. Inserir dados na tabela tb_compras
        String sqlCompra = "INSERT INTO tb_compras (data_compra, funcionario, tipo_cliente, forma_pagamento, desconto, valor_total, compra_unidade) VALUES (?, ?, ?, ?, ?, ?, ?)";
        stmtCompra = connection.prepareStatement(sqlCompra, Statement.RETURN_GENERATED_KEYS);

        // Verifique e trate os valores de desconto e total para garantir que não contenham vírgulas
        String descontoStr = campoDesconto.getText().trim().replace(',', '.');  // Substituir vírgula por ponto
        String totalCompraStr = campoTotalDaCompra.getText().replace("R$ ", "").trim().replace(',', '.');  // Substituir vírgula por ponto

        BigDecimal desconto = new BigDecimal(descontoStr);  // Converte o desconto para BigDecimal
        BigDecimal totalCompra = new BigDecimal(totalCompraStr);  // Converte o total da compra para BigDecimal

        // Define os parâmetros da compra
        stmtCompra.setTimestamp(1, new Timestamp(System.currentTimeMillis()));  // Data da compra
        stmtCompra.setString(2, campoFuncionario.getSelectedItem().toString());  // Funcionário
        stmtCompra.setString(3, campoTipocliente.getSelectedItem().toString());  // Tipo de cliente
        stmtCompra.setString(4, campoFormaDePagamento.getSelectedItem().toString());  // Forma de pagamento
        stmtCompra.setBigDecimal(5, desconto);  // Desconto
        stmtCompra.setBigDecimal(6, totalCompra);  // Valor total
        stmtCompra.setInt(7, funcionario.getUnidadeDoProgama());  // Unidade que teve essa compra

        // Executa a inserção
        stmtCompra.executeUpdate();

        // Obtém o ID gerado para a compra
        ResultSet generatedKeys = stmtCompra.getGeneratedKeys();
        int compraId = -1;
        if (generatedKeys.next()) {
            compraId = generatedKeys.getInt(1);  // ID da compra inserido
        }

        // 2. Inserir os dados dos produtos na tabela tb_produtos_compras
        String sqlProdutoCompra = "INSERT INTO tb_produtos_compras (compra_id, produto_id, produto_nome, quantidade, preco_unitario, desconto,  lote) VALUES (?, ?, ?, ?, ?, ?, ?)";
        stmtProdutoCompra = connection.prepareStatement(sqlProdutoCompra);

        for (int i = 0; i < modeloTabela.getRowCount(); i++) {
            String produtoId = modeloTabela.getValueAt(i, 0).toString();  // ID do produto
            String produtoNome = modeloTabela.getValueAt(i, 1).toString();  // Nome do produto
            int quantidade = Integer.parseInt(modeloTabela.getValueAt(i, 2).toString());  // Quantidade
            BigDecimal precoUnitario = new BigDecimal(modeloTabela.getValueAt(i, 3).toString());  // Preço unitário
            String lote = modeloTabela.getValueAt(i, 4).toString();  // Lote

            // Insere os dados do produto
            stmtProdutoCompra.setInt(1, compraId);  // Relaciona com o ID da compra
            stmtProdutoCompra.setInt(2, Integer.parseInt(produtoId));  // ID do produto
            stmtProdutoCompra.setString(3, produtoNome);  // Nome do produto
            stmtProdutoCompra.setInt(4, quantidade);  // Quantidade
            stmtProdutoCompra.setBigDecimal(5, precoUnitario);  // Preço unitário
            stmtProdutoCompra.setBigDecimal(6, desconto);  // Preço unitário
            stmtProdutoCompra.setString(7, lote);  // Lote

            stmtProdutoCompra.addBatch();
        }

        // Executa a inserção dos produtos em lote
        stmtProdutoCompra.executeBatch();

        // Commit da transação
        connection.commit();

        // 3. Atualiza a tela e limpa os campos
        modeloTabela.setRowCount(0);
        campoTipocliente.setSelectedItem("");
        campoFuncionario.setSelectedItem("");
        campoTotalDaCompra.setText("R$ 00.00");
        campoFormaDePagamento.setSelectedItem("");
        campoDesconto.setText("000");
        campoId.setText("");
        campoQtn.setText("");

        lDescontoAplicado.setVisible(false);

        // Exibe a mensagem de compra finalizada
        tir.exibirAvisoTemporario(lCompraFinalizada);

        GerenciadorDeProdutos.removerLotesComEstoqueZero();

        System.out.println("[" + new SimpleDateFormat("HH-mm-ss - dd-MM-yyyy").format(new Date()) + "] - [TelaVendas.java] - Venda cadastrada no banco de dados com sucesso!");
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
});

        bCancelarCompra.addActionListener(e -> {
            try {
                cancelarCompra();
            } catch (SQLException ex) {
                Logger.getLogger(TelaVendas.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

private void removerProduto() {
    // Valida se o campo de ID e quantidade está preenchido corretamente
    String id = campoId.getText().trim();
    String qtnStr = campoQtn.getText().trim();

    if (id.isEmpty() || qtnStr.isEmpty() || id.equals("000") || qtnStr.equals("000")) {
        JOptionPane.showMessageDialog(null, "ID ou Quantidade inválidos.", "Erro", JOptionPane.ERROR_MESSAGE);
        return;
    }

    int quantidadeRemover;
    try {
        quantidadeRemover = Integer.parseInt(qtnStr);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Quantidade deve ser um número válido.", "Erro", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Procura o ID na tabela do carrinho
    int linhaEncontrada = -1;
    int quantidadeNaTabela = 0;

    for (int i = 0; i < modeloTabela.getRowCount(); i++) {
        String idTabela = modeloTabela.getValueAt(i, 0).toString();
        if (idTabela.equals(id)) {
            linhaEncontrada = i;
            quantidadeNaTabela = Integer.parseInt(modeloTabela.getValueAt(i, 2).toString());
            break;
        }
    }

    if (linhaEncontrada == -1) {
        JOptionPane.showMessageDialog(null, "Produto não encontrado no carrinho.", "Erro", JOptionPane.ERROR_MESSAGE);
        return;
    } else if (quantidadeRemover > quantidadeNaTabela) {
        JOptionPane.showMessageDialog(null, "Quantidade a remover maior do que a quantidade no carrinho.", "Erro", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Buscar o lote mais antigo para atualização de estoque
    String sqlLote = "SELECT lote, produto_unidade FROM tb_produtos WHERE id_produto = ? AND produto_unidade = ? ORDER BY data_cadastro ASC, validade ASC LIMIT 1";

    try (Connection conn = ConexaoBancoDeDados.getConnection(); PreparedStatement stmtLote = conn.prepareStatement(sqlLote)) {
        stmtLote.setInt(1, Integer.parseInt(id));
        stmtLote.setInt(2, funcionario.getUnidadeDoProgama()); // Usando a unidade dinâmica

        try (ResultSet rsLote = stmtLote.executeQuery()) {
            if (rsLote.next()) {
                String loteAntigo = rsLote.getString("lote");
                int unidadeProduto = rsLote.getInt("produto_unidade");

                // Verifica se o produto pertence à unidade correta
                if (unidadeProduto != funcionario.getUnidadeDoProgama()) {
                    JOptionPane.showMessageDialog(null, "Erro: Produto pertence a outra unidade.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Atualiza o estoque no banco de dados (adicionando de volta a quantidade removida)
                String updateLoteSql = "UPDATE tb_produtos SET estoque = estoque + ? WHERE id_produto = ? AND lote = ? AND produto_unidade = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateLoteSql)) {
                    updateStmt.setInt(1, quantidadeRemover);
                    updateStmt.setInt(2, Integer.parseInt(id));
                    updateStmt.setString(3, loteAntigo);
                    updateStmt.setInt(4, funcionario.getUnidadeDoProgama()); // Atualizando para a unidade dinâmica
                    updateStmt.executeUpdate();
                }

                // Atualiza a tabela do carrinho
                if (quantidadeRemover < quantidadeNaTabela) {
                    modeloTabela.setValueAt(quantidadeNaTabela - quantidadeRemover, linhaEncontrada, 2);
                } else {
                    modeloTabela.removeRow(linhaEncontrada);
                }

                // Atualiza o valor total da compra
                campoTotalDaCompra.setText("R$ " + calcularTotalCompra());

            } else {
                JOptionPane.showMessageDialog(null, "Nenhum lote encontrado para atualização.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Erro ao buscar lote no banco de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
    }

    // Limpa os campos de entrada após remoção
    campoId.setText("");
    campoQtn.setText("");
}

// Método para verificar se uma string é numérica
    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str); // Tenta converter para número
            return true;
        } catch (NumberFormatException e) {
            return false; // Se der erro, não é numérico
        }
    }

    public double calcularTotalCompra() {
        lDescontoAplicado.setVisible(false);
        double total = 0.0;

        // Percorre todas as linhas da tabela para calcular o total
        for (int i = 0; i < tabelaProdutos.getRowCount(); i++) {
            Object valorVenda = tabelaProdutos.getValueAt(i, 3); // Coluna 3: Valor de venda
            Object quantidade = tabelaProdutos.getValueAt(i, 2); // Coluna 2: Quantidade

            if (valorVenda != null && quantidade != null) {
                try {
                    double valor = Double.parseDouble(valorVenda.toString());
                    int quantidadeItem = Integer.parseInt(quantidade.toString());

                    // Calcula o total (valor * quantidade)
                    total += valor * quantidadeItem;
                } catch (NumberFormatException e) {
                    System.out.println("[" + dataHora + "] - [TelaVendas.java] - Erro ao converter os valores: " + e.getMessage());
                    return 0.0;
                }
            }
        }

        // Obtendo o valor do desconto no formato 3 caracteres
        String descontoStr = campoDesconto.getText().trim();
        int desconto = Integer.parseInt(descontoStr); // Remove os zeros à esquerda automaticamente

        // Se o desconto for "000", significa que não deve aplicar o desconto
        if (desconto > 0) {
            total -= (total * desconto / 100); // Aplica o desconto
            lDescontoAplicado.setVisible(true);
            DecimalFormat df = new DecimalFormat("###,##0.00");
        }

        DecimalFormat df = new DecimalFormat("###,##0.00");

        // Atualiza o texto do JLabel com o valor do desconto aplicado
        lDescontoAplicado.setText("- Desconto de " + desconto + "% aplicado!");

        return Double.parseDouble(df.format(total).replace(",", "."));
    }

    private void cancelarCompra() throws SQLException {
    // Conectar ao banco de dados
    try (Connection conn = ConexaoBancoDeDados.getConnection()) {
        // Iniciar transação para garantir que a operação seja atômica
        conn.setAutoCommit(false);

        // Percorrer cada produto do carrinho (tabela)
        for (int i = 0; i < modeloTabela.getRowCount(); i++) {
            String idTabela = modeloTabela.getValueAt(i, 0).toString(); // ID do produto (coluna 0)
            String quantidadeNaTabelaStr = modeloTabela.getValueAt(i, 2).toString(); // Quantidade do produto (coluna 2)

            // Verificar se a quantidade é válida (numérica)
            if (!isNumeric(quantidadeNaTabelaStr)) {
                System.out.println("[" + dataHora + "] - [TelaVendas.java] - ERRO: Quantidade inválida para o ID " + idTabela);
                continue; // Pular este item e continuar o processamento
            }

            int quantidadeNaTabela = Integer.parseInt(quantidadeNaTabelaStr);

            // Consultar o estoque e o lote mais recente para o produto no banco de dados
            String queryEstoque = "SELECT estoque, lote, produto_unidade FROM tb_produtos WHERE id_produto = ? AND produto_unidade = ? ORDER BY data_cadastro ASC LIMIT 1";
            try (PreparedStatement stmt = conn.prepareStatement(queryEstoque)) {
                stmt.setString(1, idTabela);
                stmt.setInt(2, funcionario.getUnidadeDoProgama());  // Usando a unidade correta
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int estoqueAtualNoBanco = rs.getInt("estoque");
                        String lote = rs.getString("lote");  // Lote do produto
                        int unidadeProduto = rs.getInt("produto_unidade");

                        // Verifica se o produto pertence à unidade correta
                        if (unidadeProduto != funcionario.getUnidadeDoProgama()) {
                            System.out.println("[" + dataHora + "] - [TelaVendas.java] - ERRO: Produto pertence a outra unidade.");
                            continue;
                        }

                        // Atualizar o estoque no banco de dados para o lote correto
                        int novaQuantidade = estoqueAtualNoBanco + quantidadeNaTabela;
                        String updateEstoque = "UPDATE tb_produtos SET estoque = ? WHERE id_produto = ? AND lote = ? AND produto_unidade = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateEstoque)) {
                            updateStmt.setInt(1, novaQuantidade);
                            updateStmt.setString(2, idTabela);
                            updateStmt.setString(3, lote);  // Atualizando o lote correto
                            updateStmt.setInt(4, funcionario.getUnidadeDoProgama());  // Usando a unidade correta
                            updateStmt.executeUpdate();
                            System.out.println("Estoque atualizado com sucesso para o ID " + idTabela + ", lote " + lote);
                        }
                    } else {
                        System.out.println("[" + dataHora + "] - [TelaVendas.java] - ERRO: Produto não encontrado no banco para ID " + idTabela);
                    }
                }
            }
        }

        // Commit na transação para garantir que todas as atualizações sejam aplicadas
        conn.commit();

        // Limpar a tabela de vendas (carrinho)
        modeloTabela.setRowCount(0);
        System.out.println("[" + dataHora + "] - [TelaVendas.java] - Todos os produtos do carrinho foram removidos.");

        // Zerar os campos de ID, quantidade, e outros campos de vendas
        campoId.setText("");
        campoQtn.setText("");
        campoTipocliente.setSelectedItem("");  // Se for JComboBox, por exemplo
        campoFuncionario.setSelectedItem("");  // Se for JComboBox
        campoFormaDePagamento.setSelectedItem("");  // Se for JComboBox
        campoDesconto.setText("000");

        // Atualizar o campo do total da compra
        campoTotalDaCompra.setText("R$ 00.00");
    } catch (SQLException e) {
        // Em caso de erro, fazer rollback na transação
        e.printStackTrace();
        try (Connection conn = ConexaoBancoDeDados.getConnection()) {
            conn.rollback();
        } catch (SQLException rollbackEx) {
            rollbackEx.printStackTrace();
        }
    }
}
}
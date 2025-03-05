package energy.bar;

import energy.bar.back.dao.FuncionariosDAO;
import energy.bar.db.ConexaoBancoDeDados;
import energy.bar.support.Produto;
import energy.bar.support.LabelEnergyBar;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

class TelaSaidas extends JPanel {

    private EnergyBarProject mainApp;
    FuncionariosDAO funcionario = new FuncionariosDAO();
    public JTextField campoDataInicio = new JTextField();
    public JTextField campoDataFim = new JTextField();

    LabelEnergyBar labelEnergyBar = new LabelEnergyBar();

    public JPanel painelArquivos = new JPanel();

    public TelaSaidas(EnergyBarProject mainApp) {
        setLayout(null);
        this.mainApp = mainApp;

        // Criando e adicionando a label EnergyBar
        JLabel energyBarLabel = labelEnergyBar.criarLabelEnergyBar();
        add(energyBarLabel, BorderLayout.NORTH);

        JLabel label = new JLabel("Tela Saídas", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 30));
        add(label, BorderLayout.CENTER);

        painelArquivos.setLayout(null);
        JScrollPane scrollPane = new JScrollPane(painelArquivos);
        scrollPane.setPreferredSize(new Dimension(920, 400));
        scrollPane.setBounds(0, 120, 765, 440);
        add(scrollPane, BorderLayout.SOUTH);

        JButton btnAtualizar = new JButton("Filtrar");
        btnAtualizar.setBounds(270, 60, 100, 30);
        btnAtualizar.addActionListener(e -> atualizarListaArquivos());
        estilizarBotao(btnAtualizar);
        add(btnAtualizar);

        JLabel lDataInicio = new JLabel("Data inicio");
        lDataInicio.setFont(new Font("Arial", Font.BOLD, 16));
        lDataInicio.setBounds(10, 35, 120, 30);
        add(lDataInicio);
        campoDataInicio.setBounds(10, 60, 120, 30);
        estilizarCampo(campoDataInicio);
        campoDataInicio.setEditable(true);
        adicionarPlaceholder(campoDataInicio, "DD-MM-YYYY");
        add(campoDataInicio);

        JLabel lDataFim = new JLabel("Data fim");
        lDataFim.setFont(new Font("Arial", Font.BOLD, 16));
        lDataFim.setBounds(140, 35, 120, 30);
        add(lDataFim);
        campoDataFim.setBounds(140, 60, 120, 30);
        estilizarCampo(campoDataFim);
        campoDataFim.setEditable(true);
        adicionarPlaceholder(campoDataFim, "DD-MM-YYYY");
        add(campoDataFim);

        // Inicializa a listagem a partir do banco de dados
        atualizarListaArquivos();
        
         JLabel lDataEHora = new JLabel("Data e Hora");
        lDataEHora.setFont(new Font("Arial", Font.BOLD, 16));
        lDataEHora.setBounds(10, 94, 120, 30);
        add(lDataEHora);
        
        JLabel lFuncionario = new JLabel("Funcionario");
        lFuncionario.setFont(new Font("Arial", Font.BOLD, 16));
        lFuncionario.setBounds(180, 94, 120, 30);
        add(lFuncionario);
        
        JLabel lTipoDeCliente = new JLabel("Cliente");
        lTipoDeCliente.setFont(new Font("Arial", Font.BOLD, 16));
        lTipoDeCliente.setBounds(310, 94, 120, 30);
        add(lTipoDeCliente);
        
        JLabel lPagamento = new JLabel("Pagamento");
        lPagamento.setFont(new Font("Arial", Font.BOLD, 16));
        lPagamento.setBounds(420, 94, 120, 30);
        add(lPagamento);
        
        JLabel lDesconto = new JLabel("Des.");
        lDesconto.setFont(new Font("Arial", Font.BOLD, 16));
        lDesconto.setBounds(510, 94, 120, 30);
        add(lDesconto);
        
        JLabel lTotal = new JLabel("Total");
        lTotal.setFont(new Font("Arial", Font.BOLD, 16));
        lTotal.setBounds(570, 94, 120, 30);
        add(lTotal);
    }

    private void adicionarPlaceholder(JTextField campo, String textoPlaceholder) {
        campo.setText(textoPlaceholder);
        campo.setForeground(Color.GRAY);

        campo.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (campo.getText().equals(textoPlaceholder)) {
                    campo.setText("");
                    campo.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (campo.getText().isEmpty()) {
                    campo.setText(textoPlaceholder);
                    campo.setForeground(Color.GRAY);
                }
            }
        });
    }

    public void atualizarListaArquivos() {
        painelArquivos.removeAll(); // Remove apenas os componentes do painel de arquivos
        painelArquivos.revalidate(); // Atualiza o layout
        painelArquivos.repaint(); // Redesenha os componentes

        listarHistoricoDeCompras(painelArquivos); // Recarrega os dados do banco de dados
    }

    private void listarHistoricoDeCompras(JPanel painel) {
    try (Connection conn = ConexaoBancoDeDados.getConnection()) {
        String sql;
        PreparedStatement stmt;

        // Verificar se as datas de início e fim foram fornecidas
        LocalDateTime dataInicio = obterDataDoCampo(campoDataInicio);
        LocalDateTime dataFim = obterDataDoCampo(campoDataFim);

        // Definir a consulta SQL com base nos filtros
        if (dataInicio != null && dataFim != null) {
            sql = "SELECT c.id AS compra_id, c.data_compra, c.funcionario, c.tipo_cliente, c.forma_pagamento, c.desconto, c.valor_total, "
                    + "p.produto_id, p.produto_nome, p.quantidade, p.preco_unitario, p.lote "
                    + "FROM tb_compras c "
                    + "JOIN tb_produtos_compras p ON c.id = p.compra_id "
                    + "WHERE c.data_compra BETWEEN ? AND ? "
                    + "AND c.compra_unidade = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(dataInicio));
            stmt.setTimestamp(2, Timestamp.valueOf(dataFim));
            stmt.setInt(3, funcionario.getUnidadeDoProgama());
        } else {
            sql = "SELECT c.id AS compra_id, c.data_compra, c.funcionario, c.tipo_cliente, c.forma_pagamento, c.desconto, c.valor_total, "
                    + "p.produto_id, p.produto_nome, p.quantidade, p.preco_unitario, p.lote "
                    + "FROM tb_compras c "
                    + "JOIN tb_produtos_compras p ON c.id = p.compra_id "
                    + "WHERE c.compra_unidade = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, funcionario.getUnidadeDoProgama());
        }

        // Executar a consulta
        ResultSet rs = stmt.executeQuery();

        // Variável para controlar a posição no painel
        int y = 10;
        int compraIdAnterior = -1; // Variável para controlar o ID da compra

        // Iterando sobre o resultado da consulta
        while (rs.next()) {
            int compraId = rs.getInt("compra_id");

            // Verifica se é uma nova compra (compraId é diferente do anterior)
            if (compraId != compraIdAnterior) {
                // Dados da compra
                String dataCompra = rs.getString("data_compra");
                String funcionarioNome = rs.getString("funcionario");
                String tipoCliente = rs.getString("tipo_cliente");
                String formaPagamento = rs.getString("forma_pagamento");
                String desconto = rs.getString("desconto");
                String valorTotal = rs.getString("valor_total");

                // Criar e adicionar os campos para cada compra
                JTextField txtDataCompra = new JTextField(dataCompra);
                txtDataCompra.setBounds(10, y, 160, 30);
                estilizarCampo(txtDataCompra);

                JTextField txtFuncionario = new JTextField(funcionarioNome);
                txtFuncionario.setBounds(180, y, 120, 30);
                estilizarCampo(txtFuncionario);

                JTextField txtTipoCliente = new JTextField(tipoCliente);
                txtTipoCliente.setBounds(310, y, 100, 30);
                estilizarCampo(txtTipoCliente);

                JTextField txtFormaPagamento = new JTextField(formaPagamento);
                txtFormaPagamento.setBounds(420, y, 80, 30);
                estilizarCampo(txtFormaPagamento);

                JTextField txtDesconto = new JTextField(desconto);
                txtDesconto.setBounds(510, y, 50, 30);
                estilizarCampo(txtDesconto);

                JTextField txtValorTotal = new JTextField("R$ "+valorTotal);
                txtValorTotal.setBounds(570, y, 80, 30);
                estilizarCampo(txtValorTotal);

                JButton btnVisualizar = new JButton("Ver");
                btnVisualizar.setBounds(660, y, 80, 30);
                estilizarBotao(btnVisualizar);
                btnVisualizar.addActionListener(e -> {
                    TelaHistoricoCompra telaHistoricoCompra = mainApp.getTelaHistoricoCompra();
                    telaHistoricoCompra.campoFuncionario.setText(funcionarioNome);
                    telaHistoricoCompra.campoTipocliente.setText(tipoCliente);
                    telaHistoricoCompra.campoFormaDePagamento.setText(formaPagamento);
                    telaHistoricoCompra.campoDesconto.setText(desconto);
                    telaHistoricoCompra.campoTotalDaCompra.setText("R$ "+valorTotal);
                    telaHistoricoCompra.carregarProdutosPorCompra(compraId);
                    mainApp.exibirTela(telaHistoricoCompra);
                });

                // Adicionando os campos ao painel
                painel.add(txtDataCompra);
                painel.add(txtFuncionario);
                painel.add(txtTipoCliente);
                painel.add(txtFormaPagamento);
                painel.add(txtDesconto);
                painel.add(txtValorTotal);
                painel.add(btnVisualizar);

                y += 40; // Ajuste para próxima linha
            }

            // Processar os produtos para a compra atual
            String produtoNome = rs.getString("produto_nome");
            int quantidadeProduto = rs.getInt("quantidade");
            double precoProduto = rs.getDouble("preco_unitario");
            String loteProduto = rs.getString("lote");

            // Exibir os produtos da compra
            JTextField txtProduto = new JTextField(produtoNome + " - " + quantidadeProduto + " x " + precoProduto);
            estilizarCampo(txtProduto);
            painel.add(txtProduto);

            compraIdAnterior = compraId; // Atualiza o ID da compra
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    List<Produto> obterProdutosPorCompra(int idCompra) {
        List<Produto> produtos = new ArrayList<>();
        try (Connection conn = ConexaoBancoDeDados.getConnection()) {
            String sql = "SELECT * FROM tb_produtos_compras WHERE compra_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idCompra);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String codigo = rs.getString("produto_id");
                String nome = rs.getString("produto_nome");
                int quantidade = rs.getInt("quantidade");
                double preco = rs.getDouble("preco_unitario");
                String lote = rs.getString("lote");

                produtos.add(new Produto(codigo, nome, quantidade, preco, lote));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produtos;
    }

    private LocalDateTime obterDataDoCampo(JTextField campo) {
        try {
            String texto = campo.getText().trim();
            if (texto.isEmpty() || texto.equals("DD-MM-YYYY")) {
                return null; // Retorna null se o campo estiver vazio ou com o placeholder
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            return LocalDate.parse(texto, formatter).atStartOfDay();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Data inválida! Formato esperado: DD-MM-YYYY", "Erro de Data", JOptionPane.ERROR_MESSAGE);
            return null; // Retorna null se houver erro na conversão
        }
    }

    private void estilizarCampo(JTextField campo) {
        campo.setEditable(false);
        campo.setBackground(Color.LIGHT_GRAY);
        campo.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        campo.setFont(new Font("Arial", Font.BOLD, 16));
    }

    private void estilizarBotao(JButton botao) {
        botao.setFont(new Font("Arial", Font.BOLD, 14));
        botao.setBackground(new Color(32, 3, 3));
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
    }
}

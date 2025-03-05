package energy.bar;

import energy.bar.back.dao.FuncionariosDAO;
import energy.bar.back.dao.ProdutoDAO;
import energy.bar.back.dto.ProdutoDTO;
import energy.bar.db.ConexaoBancoDeDados;
import energy.bar.support.ValueVerifier;
import energy.bar.support.ValueFilter;
import energy.bar.support.NumberFilter;
import energy.bar.support.IdVerifier;
import energy.bar.support.IdFilter;
import energy.bar.support.EstoqueVerifier;
import java.awt.Color;
import java.awt.Font;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;

import energy.bar.support.LabelEnergyBar;
import energy.bar.support.TimerAvisosLabels;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

public class TelaCadastrarProduto extends JPanel {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss - dd-MM-yyyy");
    String dataHoraAtual = LocalDateTime.now().format(formatter);
    
    public JComboBox<String> campoResponsavel = new JComboBox<>();

    TimerAvisosLabels tir = new TimerAvisosLabels();
    LabelEnergyBar labelEnergyBar = new LabelEnergyBar();
    FuncionariosDAO funcionario = new FuncionariosDAO();

    private EnergyBarProject mainApp; // Adicione o campo para a referência
    private JButton bBuscar = new JButton("Buscar ID");
    private JButton bCadastrar = new JButton("Cadastrar");
    private JButton bCancelar = new JButton("Cancelar");

    private JLabel lCampoIdVazio = new JLabel("Não foi possivel buscar ID com o campo do ID vazio!");
    private JLabel lfaltaDeDados = new JLabel("Todos os campos devem ser preenchidos!");
    private JLabel lCadastroFeito = new JLabel("Produto cadastrado com sucesso!");
    private JLabel lIdNaoExistente = new JLabel("Não existe produto cadastrado com esse ID!");

    public TelaCadastrarProduto(EnergyBarProject mainApp) throws ParseException {
        this.mainApp = mainApp; // Inicialize a referência
        setVisible(true);
        setLayout(null); // Define layout manual (absoluto)

        // Criando e adicionando a label EnergyBar
        JLabel energyBarLabel = labelEnergyBar.criarLabelEnergyBar();
        add(energyBarLabel);

        // Label falta de dados
        lfaltaDeDados.setFont(new Font("Arial", Font.BOLD, 16));
        lfaltaDeDados.setBounds(220, 510, 350, 40); // Define posição e tamanho
        lfaltaDeDados.setForeground(Color.RED);
        lfaltaDeDados.setVisible(false);
        add(lfaltaDeDados);

        // Label falta de dados
        lCadastroFeito.setFont(new Font("Arial", Font.BOLD, 16));
        lCadastroFeito.setBounds(250, 510, 350, 40); // Define posição e tamanho
        lCadastroFeito.setForeground(Color.GREEN);
        lCadastroFeito.setVisible(false);
        add(lCadastroFeito);

        // Label falta de dados
        lCampoIdVazio.setText("Não foi possivel buscar ID com o campo do ID vazio!");
        lCampoIdVazio.setFont(new Font("Arial", Font.BOLD, 16));
        lCampoIdVazio.setBounds(180, 510, 500, 40); // Define posição e tamanho
        lCampoIdVazio.setForeground(Color.RED);
        lCampoIdVazio.setVisible(false);
        add(lCampoIdVazio);

        // Label e TextField ID
        JLabel lId = new JLabel("ID do produto");
        lId.setFont(new Font("Arial", Font.BOLD, 16));
        lId.setBounds(50, 60, 300, 40); // Define posição e tamanho
        lId.setVisible(true);
        add(lId);
        JTextField campoId = new JTextField();
        campoId.setBounds(50, 90, 540, 30);
        campoId.setBackground(Color.LIGHT_GRAY);
        campoId.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        campoId.setFont(new Font("Arial", Font.BOLD, 16));
        IdVerifier verifier = new IdVerifier();
        campoId.setInputVerifier(verifier);
        IdFilter idFilter = new IdFilter();
        ((AbstractDocument) campoId.getDocument()).setDocumentFilter(idFilter);
        campoId.setVisible(true);
        add(campoId);
        campoId.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                JTextField textField = (JTextField) input;
                String text = textField.getText().trim();

                // Permite campo vazio para que o usuário possa sair dele
                if (text.isEmpty()) {
                    return true;
                }

                return true; // Permite a mudança de foco
            }
        });

        bBuscar.setFont(new Font("Arial", Font.BOLD, 14));
        bBuscar.setBounds(600, 90, 110, 30); // Mesmo tamanho do botão anterior
        bBuscar.setBackground(new Color(32, 3, 3));
        bBuscar.setForeground(Color.WHITE);
        bBuscar.setFocusPainted(false);
        bBuscar.setBorderPainted(false);
        bBuscar.setVisible(true);
        add(bBuscar);

        // Label e TextField Nome do produto
        JLabel lNomeProduto = new JLabel("Nome do Produto");
        lNomeProduto.setFont(new Font("Arial", Font.BOLD, 16));
        lNomeProduto.setBounds(50, 115, 300, 40); // Define posição e tamanho
        lNomeProduto.setVisible(true);
        add(lNomeProduto);
        JTextField campoNomeProduto = new JTextField();
        campoNomeProduto.setBounds(50, 145, 660, 30);
        campoNomeProduto.setBackground(Color.LIGHT_GRAY);
        campoNomeProduto.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        campoNomeProduto.setFont(new Font("Arial", Font.BOLD, 16));
        campoNomeProduto.setVisible(true);
        add(campoNomeProduto);

        // Label e TextField Responsavel
        JLabel lResponsavel = new JLabel("Responsável");
        lResponsavel.setFont(new Font("Arial", Font.BOLD, 16));
        lResponsavel.setBounds(50, 170, 300, 40); // Define posição e tamanho
        lResponsavel.setVisible(true);
        add(lResponsavel);
        campoResponsavel.setBounds(50, 200, 660, 30);
        campoResponsavel.setBackground(Color.LIGHT_GRAY);
        campoResponsavel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        campoResponsavel.setFocusable(false);
        campoResponsavel.setFont(new Font("Arial", Font.BOLD, 16));
        campoResponsavel.setVisible(true);
        FuncionariosDAO.buscarFuncionariosPorUnidade(campoResponsavel);
        add(campoResponsavel);

        // Label e TextField Estoque
        JLabel lEstoque = new JLabel("Estoque");
        lEstoque.setFont(new Font("Arial", Font.BOLD, 16));
        lEstoque.setBounds(50, 225, 300, 40); // Define posição e tamanho
        lEstoque.setVisible(true);
        add(lEstoque);
        JTextField campoEstoque = new JTextField();
        campoEstoque.setBounds(50, 255, 660, 30);
        campoEstoque.setBackground(Color.LIGHT_GRAY);
        campoEstoque.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        campoEstoque.setFont(new Font("Arial", Font.BOLD, 16));
        NumberFilter numberFilter = new NumberFilter();
        ((AbstractDocument) campoEstoque.getDocument()).setDocumentFilter(numberFilter);
        EstoqueVerifier estoqueVerifier = new EstoqueVerifier();
        campoEstoque.setInputVerifier(estoqueVerifier);
        campoEstoque.setVisible(true);
        add(campoEstoque);

        // Label e TextField Validade
        JLabel lValidade = new JLabel("Validade");
        lValidade.setFont(new Font("Arial", Font.BOLD, 16));
        lValidade.setBounds(50, 280, 300, 40); // Define posição e tamanho
        lValidade.setVisible(true);
        add(lValidade);
        JTextField campoValidade = new JTextField();
        campoValidade.setBounds(50, 310, 660, 30);
        campoValidade.setBackground(Color.LIGHT_GRAY);
        campoValidade.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        campoValidade.setFont(new Font("Arial", Font.BOLD, 16));
        adicionarPlaceholder(campoValidade, "DD/MM/YYYY");
        campoValidade.setVisible(true);
        add(campoValidade);

        // Label e TextField Valor de custo
        JLabel lValorDeCusto = new JLabel("Valor de custo (valor unitário)");
        lValorDeCusto.setFont(new Font("Arial", Font.BOLD, 16));
        lValorDeCusto.setBounds(50, 335, 300, 40); // Define posição e tamanho
        lValorDeCusto.setVisible(true);
        add(lValorDeCusto);
        JTextField campoValorDeCusto = new JTextField();
        campoValorDeCusto.setBounds(50, 365, 660, 30);
        campoValorDeCusto.setBackground(Color.LIGHT_GRAY);
        campoValorDeCusto.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        campoValorDeCusto.setFont(new Font("Arial", Font.BOLD, 16));
        ValueFilter valueFilterCusto = new ValueFilter();
        ((AbstractDocument) campoValorDeCusto.getDocument()).setDocumentFilter(valueFilterCusto);
        ValueVerifier valueVerifierCusto = new ValueVerifier();
        campoValorDeCusto.setInputVerifier(valueVerifierCusto);
        campoValorDeCusto.setVisible(true);
        add(campoValorDeCusto);

        // Label e TextField Valor de venda
        JLabel lValorDeVenda = new JLabel("Valor de venda (valor unitário)");
        lValorDeVenda.setFont(new Font("Arial", Font.BOLD, 16));
        lValorDeVenda.setBounds(50, 390, 300, 40); // Define posição e tamanho
        lValorDeVenda.setVisible(true);
        add(lValorDeVenda);
        JTextField campoValorDeVenda = new JTextField();
        campoValorDeVenda.setBounds(50, 420, 660, 30);
        campoValorDeVenda.setBackground(Color.LIGHT_GRAY);
        campoValorDeVenda.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        campoValorDeVenda.setFont(new Font("Arial", Font.BOLD, 16));
        ValueFilter valueFilterVenda = new ValueFilter();
        ((AbstractDocument) campoValorDeVenda.getDocument()).setDocumentFilter(valueFilterVenda);
        ValueVerifier valueVerifierVenda = new ValueVerifier();
        campoValorDeVenda.setInputVerifier(valueVerifierVenda);
        campoValorDeVenda.setVisible(true);
        add(campoValorDeVenda);

        // Label e TextField Valor de venda
        JLabel lLote = new JLabel("Lote");
        lLote.setFont(new Font("Arial", Font.BOLD, 16));
        lLote.setBounds(50, 445, 300, 40); // Define posição e tamanho
        lLote.setVisible(true);
        add(lLote);
        JTextField campoLote = new JTextField();
        campoLote.setBounds(50, 475, 530, 30);
        campoLote.setBackground(Color.LIGHT_GRAY);
        campoLote.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        campoLote.setFont(new Font("Arial", Font.BOLD, 16));
        campoLote.setEditable(false);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
        campoLote.setText(LocalDateTime.now().format(formatter));
        campoLote.setVisible(true);
        add(campoLote);

        // Botão Buscar
        JButton bAplicarLote = new JButton("Aplicar Lote");
        bAplicarLote.setFont(new Font("Arial", Font.BOLD, 14));
        bAplicarLote.setBounds(590, 475, 120, 30); // Mesmo tamanho do botão anterior
        bAplicarLote.setBackground(new Color(32, 3, 3));
        bAplicarLote.setForeground(Color.WHITE);
        bAplicarLote.setFocusPainted(false);
        bAplicarLote.setBorderPainted(false);
        bAplicarLote.setVisible(true);
        add(bAplicarLote);
        bAplicarLote.addActionListener(e -> {
            campoLote.setText(LocalDateTime.now().format(formatter));
        });

        // Botão Cancelar
        bCancelar.setFont(new Font("Arial", Font.BOLD, 14));
        bCancelar.setBounds(50, 510, 100, 40); // Mesmo tamanho do botão anterior
        bCancelar.setBackground(Color.RED);
        bCancelar.setForeground(Color.WHITE);
        bCancelar.setFocusPainted(false);
        bCancelar.setBorderPainted(false);
        bCancelar.setVisible(true);
        add(bCancelar);

        // Botão Cadastrar
        bCadastrar.setFont(new Font("Arial", Font.BOLD, 14));
        bCadastrar.setBounds(600, 510, 110, 40); // Mesmo tamanho do botão anterior
        bCadastrar.setBackground(Color.GREEN);
        bCadastrar.setForeground(Color.WHITE);
        bCadastrar.setFocusPainted(false);
        bCadastrar.setBorderPainted(false);
        bCadastrar.setVisible(true);
        add(bCadastrar);

        // Label pesquisa
        lIdNaoExistente.setFont(new Font("Arial", Font.BOLD, 16));
        lIdNaoExistente.setBounds(200, 510, 400, 40); // Define posição e tamanho
        lIdNaoExistente.setForeground(Color.RED);
        lIdNaoExistente.setVisible(false);
        add(lIdNaoExistente);

        bCadastrar.addActionListener(e -> {
            lCampoIdVazio.setVisible(false);
            lfaltaDeDados.setVisible(false);
            lCadastroFeito.setVisible(false);
            lIdNaoExistente.setVisible(false);

            String idText = campoId.getText().trim();
            String nomeDoProduto = campoNomeProduto.getText().trim();
            String responsavel = (String) campoResponsavel.getSelectedItem();
            String estoqueText = campoEstoque.getText().trim();
            String validade = campoValidade.getText().trim();
            String valorDeCustoText = campoValorDeCusto.getText().trim();
            String valorDeVendaText = campoValorDeVenda.getText().trim();
            String lote = campoLote.getText().trim();

            if (idText.isEmpty() || nomeDoProduto.isEmpty() || responsavel.isEmpty() || estoqueText.isEmpty()
                    || validade.isEmpty() || validade.equals("DD/MM/YYYY") || valorDeCustoText.isEmpty() || valorDeVendaText.isEmpty() || lote.isEmpty()) {
                tir.exibirAvisoTemporario(lfaltaDeDados);
                return;
            }

            try {
                int id = Integer.parseInt(idText);
                int estoque = Integer.parseInt(estoqueText);
                double valorDeCusto = Double.parseDouble(valorDeCustoText);
                double valorDeVenda = Double.parseDouble(valorDeVendaText);
                String dataHoraCadastro = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());

                ProdutoDTO produto = new ProdutoDTO(id, nomeDoProduto, responsavel, estoque, validade, valorDeCusto, valorDeVenda, lote, dataHoraCadastro, FuncionariosDAO.getUnidadeDoProgama());
                ProdutoDAO produtoDAO = new ProdutoDAO();

                FuncionariosDAO funcionario = new FuncionariosDAO();

                if (produtoDAO.cadastrarProduto(produto, funcionario)) {
                    tir.exibirAvisoTemporario(lCadastroFeito);

                    // Limpa os campos após o cadastro
                    campoId.setText("");
                    campoNomeProduto.setText("");
                    campoEstoque.setText("");
                    campoResponsavel.setSelectedItem("");
                    campoValidade.setText("");
                    campoValorDeCusto.setText("");
                    campoValorDeVenda.setText("");
                    campoLote.setText("");
                } else {
                    System.out.println("Erro ao cadastrar produto no banco de dados!");
                }

            } catch (NumberFormatException ex) {
                System.out.println("Erro ao converter números: " + ex.getMessage());
                tir.exibirAvisoTemporario(lfaltaDeDados);
            }
        });

        // Ação do botão "Cancelar"
        bCancelar.addActionListener(e -> {
            lCampoIdVazio.setVisible(false);
            lfaltaDeDados.setVisible(false);
            lCadastroFeito.setVisible(false);
            lIdNaoExistente.setVisible(false);

            mainApp.exibirTela(mainApp.getTelaProdutos()); // Voltar para a tela de produtos
            // Limpar os campos (opcional)
            campoId.setText("");
            campoNomeProduto.setText("");
            campoResponsavel.setSelectedItem("");
            campoEstoque.setText("");
            campoValidade.setText("");
            campoValorDeCusto.setText("");
            campoValorDeVenda.setText("");
            campoLote.setText("");
        });

        // Ação do botão "Cadastrar Produto"
        bBuscar.addActionListener(e -> {
            // Esconde mensagens de erro/aviso antes da busca
            lCampoIdVazio.setVisible(false);
            lfaltaDeDados.setVisible(false);
            lCadastroFeito.setVisible(false);
            lIdNaoExistente.setVisible(false);

            String id = campoId.getText().trim();

            if (id.isEmpty()) {
                tir.exibirAvisoTemporario(lCampoIdVazio);
                return;
            }

            String sql = "SELECT id, produto FROM tb_catalogo WHERE id = ?";

            try (Connection conn = ConexaoBancoDeDados.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, Integer.parseInt(id));

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        campoId.setText(String.valueOf(rs.getInt("id")));
                        campoNomeProduto.setText(rs.getString("produto"));
                        campoNomeProduto.setEditable(false); // Nome não pode ser editado após busca

                        System.out.println("Produto encontrado com sucesso!");
                    } else {
                        // Caso o produto não seja encontrado
                        campoId.setText("");
                        campoNomeProduto.setText("");

                        tir.exibirAvisoTemporario(lIdNaoExistente);
                        campoNomeProduto.setEditable(true);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "O ID deve ser um número válido!", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro ao buscar produto no banco de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

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

    public JComboBox<String> getCampoResponsavel() {
        return campoResponsavel;
    }

}

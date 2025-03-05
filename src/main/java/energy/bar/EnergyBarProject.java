package energy.bar;

import energy.bar.back.dao.FuncionariosDAO;
import energy.bar.db.ConexaoBancoDeDados;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import energy.bar.db.GeradorDeProdutos;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class EnergyBarProject {
    
    private String versaoPrograma = "0.8.9";
    private static boolean gerarDados = false;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss - dd-MM-yyyy");
    String dataHoraAtual = LocalDateTime.now().format(formatter);

    GeradorDeProdutos ger = new GeradorDeProdutos();
    FuncionariosDAO f = new FuncionariosDAO();

    private TelaCadastrarProduto telaCadastrarProduto;
    private TelaProdutos telaProdutos; // Adicione o campo para a tela de produtos
    private TelaEstoqueBar telaEstoqueBar;
    private TelaHistoricoCompra telaHistoricoCompra;
    private TelaSaidas telaSaidas;
    private TelaRemover telaRemover;

    public TelaCadastrarProduto getTelaCadastrarProduto() {
        return telaCadastrarProduto;
    }

    public TelaHistoricoCompra getTelaHistoricoCompra() {
        return telaHistoricoCompra;
    }

    public TelaProdutos getTelaProdutos() {
        telaProdutos.atualizarTabelaDeProdutos();
        return telaProdutos;
    }

    public TelaSaidas getTelaSaidas() {
        return telaSaidas;
    }

    private JLabel labelVersao;

    private JFrame janela;
    private JPanel painelPrincipal;
    private JPanel painelConteudo;
    private PainelFaixa painelFaixa;

    private JLabel labelDataHora;

    private BotaoPersonalizado bCatalogo, bProdutos, bBar, bRemover, bVendas, bSaidas; // Adicionado botão de cadastro
    private Font fontePadrao = new Font("Arial", Font.BOLD, 20);
    private Color corPadrao = Color.WHITE;
    private Color corSelecionada = new Color(180, 155, 183);

    private TelaCatalogo telaCatalogo;
    private TelaVendas telaVendas;

    public EnergyBarProject() throws ParseException, IOException {

        telaCadastrarProduto = new TelaCadastrarProduto(this); // Passe "this" para TelaCadastrarProduto
        telaProdutos = new TelaProdutos(this); // Inicialize a tela de produtos

        telaSaidas = new TelaSaidas(this); // Passe "this" para TelaCadastrarProduto
        telaHistoricoCompra = new TelaHistoricoCompra(this);

        configurarJanela();
        configurarPainelPrincipal();
        configurarPainelFaixa();
        configurarTelas(); // Inicializa as telas
        configurarBotoes();
        configurarPainelConteudo();
        adicionarComponentes();
    }

    public void exibirTela(JPanel tela) {
        painelConteudo.removeAll();
        painelConteudo.add(tela, BorderLayout.CENTER);
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    private void configurarJanela() {
        janela = new JFrame("Energy Bar - Portal");
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setBounds(0, 0, 1000, 600);
        janela.setLocationRelativeTo(null);
        janela.setResizable(false);

        try {
            ImageIcon icon = new ImageIcon("Arquivos de suporte/imagens/logo.png");
            janela.setIconImage(icon.getImage());
        } catch (Exception e) {

        }
    }

    private void configurarPainelPrincipal() {
        painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BorderLayout());
    }

    private void configurarPainelFaixa() {
        painelFaixa = new PainelFaixa();
        painelFaixa.setLayout(new GridLayout(8, 1, 5, 10));

        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> atualizarDataHora());
        timer.start();
    }

    private String obterDataHoraAtual() {
        LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return agora.format(formatter);
    }

    private void atualizarDataHora() {
        labelDataHora.setText(obterDataHoraAtual());
    }

    private void configurarTelas() throws ParseException, IOException {
        telaCatalogo = new TelaCatalogo();
        telaProdutos = new TelaProdutos(this); // Passa "this" para TelaProdutos
        telaEstoqueBar = new TelaEstoqueBar();
        telaRemover = new TelaRemover();
        telaVendas = new TelaVendas();
        telaSaidas = new TelaSaidas(this);
        telaHistoricoCompra = new TelaHistoricoCompra(this);
    }

    private void configurarBotoes() throws ParseException {
        // Inicializando o labelDataHora antes de usar
        labelDataHora = new JLabel(obterDataHoraAtual(), SwingConstants.CENTER);
        labelDataHora.setFont(new Font("Arial", Font.PLAIN, 14));
        labelDataHora.setForeground(Color.WHITE);

        // Inicializando os botões
        bCatalogo = criarBotao("CATALOGO", telaCatalogo);
        bCatalogo.setBackground(corSelecionada);
        bProdutos = criarBotao("DISPENÇA", telaProdutos);
        bBar = criarBotao("ESTOQUE BAR", telaEstoqueBar);
        bRemover = criarBotao("REMOVER", telaRemover);
        bVendas = criarBotao("CARRINHO", telaVendas);
        bSaidas = criarBotao("VENDAS", telaSaidas);

        // Adicionando os botões e o labelDataHora no painelFaixa
        painelFaixa.add(bCatalogo);
        painelFaixa.add(bProdutos);
        painelFaixa.add(bBar);
        painelFaixa.add(bRemover);
        painelFaixa.add(bVendas);
        painelFaixa.add(bSaidas);
        painelFaixa.add(labelDataHora); // Agora o labelDataHora é adicionado corretamente

        labelVersao = new JLabel("Versão: " + versaoPrograma, SwingConstants.CENTER);
        labelVersao.setFont(new Font("Arial", Font.PLAIN, 14));
        labelVersao.setForeground(Color.WHITE);
        painelFaixa.add(labelVersao);

        // Inicializando o timer para atualizar a data e hora
        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> atualizarDataHora());
        timer.start();
    }

    private BotaoPersonalizado criarBotao(String texto, JPanel tela) {
        BotaoPersonalizado botao = new BotaoPersonalizado(texto, corPadrao, corSelecionada, fontePadrao);
        botao.addActionListener(e -> {
            resetarBotoes();
            botao.selecionar();
            atualizarTela(tela);
        });
        return botao;
    }

    private void resetarBotoes() {
        bCatalogo.desmarcar();
        bProdutos.desmarcar();
        bBar.desmarcar();
        bRemover.desmarcar();
        bVendas.desmarcar();
        bSaidas.desmarcar();
    }

    private void atualizarTela(JPanel novaTela) {
        painelConteudo.removeAll();
        painelConteudo.add(novaTela, BorderLayout.CENTER);
        painelConteudo.revalidate();
        painelConteudo.repaint();
        
        if (novaTela instanceof TelaSaidas) {
            telaSaidas.atualizarListaArquivos();
        } if (novaTela instanceof TelaProdutos) {
            telaProdutos.atualizarTabelaDeProdutos();
        }
    }

    private void configurarPainelConteudo() {
        painelConteudo = new JPanel();
        painelConteudo.setLayout(new BorderLayout());
        atualizarTela(telaCatalogo); // Tela inicial padrão
    }

    private void adicionarComponentes() {
        painelPrincipal.add(painelFaixa, BorderLayout.WEST);
        painelPrincipal.add(painelConteudo, BorderLayout.CENTER);
        janela.add(painelPrincipal);
        janela.setVisible(true);
    }

    public static void main(String[] args) throws ParseException, IOException, SQLException {
        Connection conexao = null;

        // Formatar data e hora atual
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss - dd-MM-yyyy");
        String dataHoraAtual = LocalDateTime.now().format(formatter);

        try {
            conexao = ConexaoBancoDeDados.conectar();
            System.out.println("[" + dataHoraAtual + "] - [EnergyBarApp.java] - Conexao estabelecida com sucesso!");
            System.out.println("[" + dataHoraAtual + "] - [EnergyBarApp.java] - Inicializando sistema.");

            if (gerarDados == true) {
                GeradorDeProdutos.gerarProdutosDeTeste();
            }

            new EnergyBarProject();

        } catch (SQLException e) {
            System.err.println("Erro ao conectar ou executar operações no banco de dados: " + e.getMessage());

            // Exibir janela de aviso caso não consiga conectar
            JOptionPane.showMessageDialog(null,
                    "Ocorreu um problema em relação ao banco de dados ao tentar abrir o programa, verifique se está conectado com a internet. Caso o erro persista, contate um administrador do programa!",
                    "Energy Bar",
                    JOptionPane.ERROR_MESSAGE);

            System.exit(1);
        }
    }
}

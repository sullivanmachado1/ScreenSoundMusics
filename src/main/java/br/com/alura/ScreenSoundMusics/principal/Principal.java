package br.com.alura.ScreenSoundMusics.principal;

import br.com.alura.ScreenSoundMusics.model.Artista;
import br.com.alura.ScreenSoundMusics.model.Musica;
import br.com.alura.ScreenSoundMusics.model.TipoArtista;
import br.com.alura.ScreenSoundMusics.repository.ArtistaRepository;
import br.com.alura.ScreenSoundMusics.service.ConsultaChatGPT;
import br.com.alura.ScreenSoundMusics.service.ConsumoAPI;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {
    private final ArtistaRepository repositorio;
    private Scanner leitor = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI();
    public Principal(ArtistaRepository repositorio){
        this.repositorio = repositorio;
    }
    public void exibeMenu() {
        var opcao = -1;
        while(opcao != 0){
            var menu = """
                    1- Cadastrar artista
                    2- Cadastrar música
                    3- Listar músicas
                    4- Buscar músicas por artistas
                    5- Pesquisar dados sobre um artista
                    
                    0- Sair
                    """;
            System.out.println(menu);
            opcao = leitor.nextInt();
            leitor.nextLine();

            switch (opcao){
                case 1:
                    cadastrarArtista();
                    break;
                case 2:
                    cadastrarMusica();
                    break;
                case 3:
                    listarMusicas();
                    break;
                case 4:
                    buscarMusicaPorArtista();
                    break;
                case 5:
                    buscarDadosSobreUmArtista();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Nenhuma opção valida");
            }
        }
    }

    private void cadastrarArtista() {
        var cadastrarNovo = "S";

        while (cadastrarNovo.equalsIgnoreCase("s")) {
            System.out.println("Nome do artista que deseja cadastrar: ");
            var nome = leitor.nextLine();
            System.out.println("Qual é o tipo de artista? ");
            var tipo = leitor.nextLine();
            TipoArtista tipoArtista = TipoArtista.valueOf(tipo.toUpperCase());
            Artista artista = new Artista(nome,tipoArtista);
            repositorio.save(artista);
            System.out.println("Cadastrar novo artista (S/N)");
            cadastrarNovo = leitor.nextLine();
        }
    }
    private void cadastrarMusica() {
        System.out.println("Cadastrar música de que artista? ");
        var nome = leitor.nextLine();
        Optional<Artista> artista = repositorio.findByNomeContainingIgnoreCase(nome);
        if(artista.isPresent()){
            System.out.println("Informe o título da música: ");
            var nomeMusica = leitor.nextLine();
            Musica musica = new Musica(nomeMusica);
            musica.setArtista(artista.get());
            artista.get().getMusicas().add(musica);
            repositorio.save(artista.get());
        } else {
            System.out.println("Artista não encontrado");
        }

    }
    private void listarMusicas() {
        List<Artista> artistas = repositorio.findAll();
        artistas.forEach(a -> a.getMusicas().forEach(System.out::println));
    }
    private void buscarMusicaPorArtista() {
        System.out.println("Buscar músicas de que artista? ");
        var nome = leitor.nextLine();
        List<Musica> musicas = repositorio.buscaMusicasPorArtista(nome);
        musicas.forEach(System.out::println);
    }
    private void buscarDadosSobreUmArtista() {
        System.out.println("Pesquisar dados sobre qual artista? ");
        var nome = leitor.nextLine();
        var resposta = ConsultaChatGPT.obterInformacao(nome);
        System.out.println(resposta.trim());
    }
}

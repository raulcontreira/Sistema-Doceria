package br.ceub;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.ceub.model.Cliente;
import br.ceub.model.Produto;
import br.ceub.model.Usuario;
import br.ceub.repository.ClienteRepository;
import br.ceub.repository.ProdutoRepository;
import br.ceub.repository.UsuarioRepository;

/**
 * Ponto de entrada (entry point) do backend Spring Boot.
 *
 * {@code @SpringBootApplication} liga tres coisas de uma vez:
 * <ul>
 *   <li>{@code @Configuration} — esta classe pode declarar Beans;</li>
 *   <li>{@code @EnableAutoConfiguration} — o Spring Boot configura
 *       sozinho o servidor web embutido (Tomcat), o Jackson (JSON), o
 *       Hibernate/JPA e a conexao com o MySQL, com base no
 *       {@code application.properties} e nas dependencias do
 *       {@code pom.xml};</li>
 *   <li>{@code @ComponentScan} — escaneia este pacote (br.ceub) e todos
 *       os subpacotes em busca de {@code @Service}, {@code @RestController},
 *       {@code @Repository}, etc.</li>
 * </ul>
 *
 * O Bean {@link #inicializarDados(...)} roda automaticamente uma unica
 * vez, logo apos a aplicacao subir, e cadastra um usuario administrador
 * padrao (para o sistema ja nascer acessivel) e alguns clientes/produtos
 * de exemplo — mas so na PRIMEIRA execucao (se as tabelas ja tiverem
 * dados, ele nao duplica nada).
 */
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner inicializarDados(UsuarioRepository usuarioRepository,
                                        ClienteRepository clienteRepository,
                                        ProdutoRepository produtoRepository,
                                        PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.findByLogin("admin").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setNome("Administrador");
                admin.setLogin("admin");
                admin.setSenhaHash(passwordEncoder.encode("admin123"));
                admin.setPerfil(Usuario.Perfil.ADMIN);
                usuarioRepository.save(admin);
                System.out.println(">> Usuario admin criado (login: admin / senha: admin123)");
            }

            if (clienteRepository.count() == 0) {
                clienteRepository.save(new Cliente(null, "Maria Silva", "11111111111", "maria@email.com", "61999990001"));
                clienteRepository.save(new Cliente(null, "Joao Souza", "22222222222", "joao@email.com", "61999990002"));
            }

            if (produtoRepository.count() == 0) {
                produtoRepository.save(new Produto(null, "Caneta Esferografica", "Caneta azul", "Papelaria", 2.50, 100, 10));
                produtoRepository.save(new Produto(null, "Caderno Universitario", "200 folhas", "Papelaria", 25.90, 30, 5));
                produtoRepository.save(new Produto(null, "Mochila Escolar", "Mochila reforcada", "Acessorios", 89.90, 8, 3));
            }
        };
    }
}

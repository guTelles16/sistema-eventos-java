package projeto_x.dao;

import projeto_x.model.Evento;
import projeto_x.model.Usuario;
import projeto_x.model.Categoria;
import projeto_x.model.Endereco;

import projeto_x.util.ConexaoBanco;
import projeto_x.util.PersistenceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class EventoDAO {
    /**
     * Salva um novo evento no banco de dados (CREATE).
     * 
     * @param evento O evento a ser salvo.
     * @return O ID do evento recém-criado.
     * @throws PersistenceException Se ocorrer algum erro ao salvar o evento (falha no banco de dados).
     */
    public int salvar(Evento evento) throws PersistenceException {
        String sql = "INSERT INTO eventos (nm_evento, endereco_id, categoria_id, usuario_id, dt_hr_evento, ds_evento, qt_limite_vagas_evento, latitude_evento, longitude_evento) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conexao = ConexaoBanco.conectar()) {
            PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, evento.getNome());

            if (evento.getEndereco() != null && evento.getEndereco().getID() > 0) {
                stmt.setInt(2, evento.getEndereco().getID());
            } else {
                throw new PersistenceException("Endereço do evento é obrigatório e precisa ter um ID válido!");
            }

            if (evento.getCategoria() != null && evento.getCategoria().getID() > 0) {
                stmt.setInt(3, evento.getCategoria().getID());
            } else {
                throw new PersistenceException("Categoria do evento é obrigatória e precisa ter um ID válido!");
            }

            if (evento.getCriador() != null && evento.getCategoria().getID() > 0) {
                stmt.setInt(4, evento.getCriador().getID());
            } else {
                throw new PersistenceException("Criador do evento é obrigatório e precisa ter um ID válido!");
            }

            stmt.setTimestamp(5, Timestamp.valueOf(evento.getDataHora()));
            stmt.setString(6, evento.getDescricao());
            stmt.setInt(7, evento.getCapacidade());
            stmt.setDouble(8, evento.getLatitude());
            stmt.setDouble(9, evento.getLongitude());
            
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // Retorna o ID gerado
                }
                throw new PersistenceException("Falha ao obter o ID do evento: nenhum ID foi gerado!");
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao salvar evento: " + e.getMessage());
        }
    }

    /**
     * Busca um evento pelo ID no banco de dados (READ).
     * 
     * @param id O ID do evento a ser buscado.
     * @return O evento encontrado se o ID existir, null caso contrário.
     * @throws PersistenceException Se ocorrer algum erro ao buscar o evento (falha no banco de dados).
     */
    public Evento buscarPorID(int id) throws PersistenceException {
        String sql = "SELECT " +
                     "e.id_evento, e.nm_evento AS titulo_evento, e.ds_evento, e.dt_hr_evento, e.qt_limite_vagas_evento, " +
                     "e.latitude_evento, e.longitude_evento, " +
                     "e.endereco_id AS endereco_local_evento, " +
                     "u.id_usuario AS criador_id, u.nm_usuario AS criador_nome, u.email AS criador_email, " +
                     "cat.id_categoria, cat.nm_categoria AS nome_categoria, " +
                     "addr.logradouro, addr.nr_logradouro, addr.complemento, addr.bairro, addr.nr_cep " +
                     "FROM eventos e " +
                     "JOIN usuarios u ON e.usuario_criador_id = u.id_usuario " +
                     "JOIN categorias cat ON e.id_categoria = cat.id_categoria " +
                     "JOIN enderecos addr ON e.endereco_id = addr.id_endereco " +
                     "WHERE e.id_evento = ?";
        
        try (Connection conexao = ConexaoBanco.conectar()) {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            
            // Define o ID no placeholder (?)
            stmt.setInt(1, id);

            // Executa a consulta e obtém o resultado
            try (ResultSet rs = stmt.executeQuery()) {
                // Verifica se há resultados
                if (rs.next()) {
                // Cria um objeto Usuario com as informações necessárias
                Usuario criador = new Usuario(
                    rs.getInt("criador_id"),
                    rs.getString("criador_nome"),
                    rs.getString("criador_email")
                );

                // Cria um objeto Categoria com as informações necessárias
                Categoria categoriaEvento = new Categoria(
                    rs.getInt("id_categoria"),
                    rs.getString("nome_categoria")
                );

                // Cria um objeto Endereço com as informações necessárias
                Endereco enderecoLocalEvento = new Endereco(
                    rs.getInt("endereco_local_evento"),
                    rs.getString("logradouro"),
                    rs.getString("nr_logradouro"),
                    rs.getString("complemento"),
                    rs.getString("bairro"),
                    rs.getString("nr_cep")
                );

                // Cria um novo objeto Evento com os dados retornados
                int idEvento = rs.getInt("id_evento");
                String nomeEvento = rs.getString("titulo_evento");
                LocalDateTime dataHoraEvento = rs.getTimestamp("dt_hr_evento").toLocalDateTime();
                String descricaoEvento = rs.getString("ds_evento");
                int capacidadeEvento = rs.getInt("qt_limite_vagas_evento");
                Double latitudeEvento = rs.getDouble("latitude_evento");
                Double longitudeEvento = rs.getDouble("longitude_evento");

                // Retorna o objeto Evento completo
                return new Evento(
                    idEvento,
                    nomeEvento,
                    enderecoLocalEvento,
                    categoriaEvento,
                    dataHoraEvento,
                    descricaoEvento,
                    capacidadeEvento,
                    latitudeEvento,
                    longitudeEvento,
                    criador
                );
            } else {
                throw new PersistenceException("Evento não encontrado com o ID: " + id);
            }
        }

        } catch (SQLException e) {
            System.err.println("Erro SQL ao buscar evento por ID: " + e.getMessage());
            throw new PersistenceException("Erro ao buscar evento: " + e.getMessage());
        }
    }
}

package projeto_x.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import projeto_x.util.ConexaoBanco;
import projeto_x.model.Categoria;

public class CategoriaDAO {

    private Connection conexao;

    public CategoriaDAO() throws Exception {
        this.conexao = ConexaoBanco.conectar();
    }

    public Categoria buscarCategoria(String nome) throws Exception {
        String sql = "SELECT id, nome FROM Categoria WHERE nome = ?";
        Categoria categoria = null;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, nome);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String nomeRecuperado = rs.getString("nome");
                    categoria = new Categoria(id, nomeRecuperado);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Erro ao buscar categoria: " + e.getMessage());
        }

        return categoria;
    }

 public void salvar(Categoria categoria) throws Exception {
        String slq = "INSERT INTO categoria (id, nome) VALUES (?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(slq)) {
            stmt.setString(1, categoria.getNome());
            stmt.setInt(2, categoria.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Erro ao salvar categoria: " + e.getMessage());
        }
    }
}



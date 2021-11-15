package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {

	// criar a conexao
	private Connection conn;

	// construtor de conexao que recebe dadods da conexao
	public DepartmentDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Department obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO department "
					+ "(Name) "
					+ "VALUES "
					+ "(?)" , Statement.RETURN_GENERATED_KEYS);
			
			//não poe o id para auto incrementar
			//return_generate_keys retorna o id do vendedor inserido
			// insere no primeiro interrogação o obj que chegou no parametro.getName
			st.setString(1, obj.getName());
		
			
			int linhasAfetadas = st.executeUpdate();
			//se maior que zero significa que houve inserção
			if(linhasAfetadas > 0) {
				//rs recebe st com id incluso
				ResultSet rs = st.getGeneratedKeys();
				if(rs.next()) {
					//se existir rs joga para a varivel id o id gerado que esta na posiçao 1
					int id = rs.getInt(1);
					//atribui o id gerado dentro do meu objeto obj
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			}
			else {
				throw new DbException("Erro inesperado, nenhuma linha foi inserida");
			}
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public void update(Department obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE department "
					+ "SET Name = ? "
					+ "WHERE id = ?");
	
			// insere no primeiro interrogação o obj que chegou no parametro.getName
			st.setString(1, obj.getName());
			
			// no udate eu tenho o sexto interrogação que é o id do departamento
			st.setInt(2, obj.getId());
		
			st.executeUpdate();
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public void delete(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"DELETE FROM  department "
					+"WHERE Id = ?");
			
			st.setInt(1, id);
			
			st.executeUpdate();
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	

	@Override
	public Department findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement(
					"SELECT department.*, department.Name as DepName " + "FROM department " + "WHERE Id = ?");

			// configurar o interrogação recebendo o id que chegou como parametro
			st.setInt(1, id);
			// rs recebe o resultado com tabelas e colunas
			// converter para um objeto
			rs = st.executeQuery();
			// rs na posição zero é null sem resultado algum
			// se rs.next for verdadeiro tem resultado
			if (rs.next()) {
				// criar metodo para instanciar o departament
				// clica no erro para criar o método automatico lá em baixo
				Department dep = instaciaDepartment(rs);
				return dep;
			} else {
				return null;
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
			// a conexão com o banco só em fechada em Program
			// para permitir que façam as consultas
		}
	}

	@Override
	public List<Department> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement(
					"SELECT department.*, department.Name as DepName " + "FROM department " + " ORDER BY Name ");

			// rs recebe o resultado com tabelas e colunas
			// converter para um objeto
			rs = st.executeQuery();
			// rs na posição zero é null sem resultado algum
			// o rs pode ter vários registros entao percorrer com while
			// declarar uma lista de seller
			List<Department> lista = new ArrayList<>();
			// usar map para verificar se já houve instancia de departamento no caso de
			// varios registros
			// para não instanciar varias vezes pois a busca é por um id do departamento
			// map vazio
			Map<Integer, Department> map = new HashMap<>();

			while (rs.next()) {
				Department dep = map.get(rs.getInt("Id"));
				if (dep == null) {
					dep = instaciaDepartment(rs);
					map.put(rs.getInt("Id"), dep);
					lista.add(dep);
				}
			}
			return lista;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
			// a conexão com o banco só em fechada em Program
			// para permitir que façam as consultas
		}
	}

	public Department instaciaDepartment(ResultSet rs) throws SQLException {
		// instanciar o departament
		Department dep = new Department();
		// entre parentese o nome da coluna
		// vai dar erro mas não vou tratar.. só add Throw declaration para propagar a
		// exeção
		dep.setId(rs.getInt("Id"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

}

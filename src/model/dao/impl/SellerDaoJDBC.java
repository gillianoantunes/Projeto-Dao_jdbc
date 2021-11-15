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
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {

	// criar a conexao
	private Connection conn;

	// construtor de conexao que recebe dadods da conexao
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Seller obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO seller "
					+ "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?)" , Statement.RETURN_GENERATED_KEYS);
			//return_generate_keys retorna o id do vendedor inserido
			// insere no primeiro interrogação o obj que chegou no parametro.getName
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			//navega até o departamento e busca o id
			st.setInt(5, obj.getDepartment().getId());
			
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
	public void update(Seller obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE seller "
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
					+ "WHERE id = ?");
	
			// insere no primeiro interrogação o obj que chegou no parametro.getName
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			//navega até o departamento e busca o id
			st.setInt(5, obj.getDepartment().getId());
			// no udate eu tenho o sexto interrogação que é o id do seller vendedor
			st.setInt(6, obj.getId());
		
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
					"DELETE FROM  seller "
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
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT seller.*, department.Name as DepName "
					+ "FROM seller INNER JOIN department "
				    + "ON seller.DepartmentId = department.Id "
					+ "WHERE seller.Id = ?");
			
			//configurar o interrogação recebendo o id que chegou como parametro
			st.setInt(1, id);
			//rs recebe o resultado com tabelas e colunas
			//converter para um objeto
			rs = st.executeQuery();
			// rs na posição zero é null sem resultado algum
			//se rs.next for verdadeiro tem resultado
			if(rs.next()) {
				//criar metodo para instanciar o departament
				//clica no erro para criar o método automatico lá em baixo
				Department dep = instaciaDepartment(rs);
				
				//criar metodo para instanciar o vendedor seller
				//passa o rs e o dep como parametro
				Seller obj = instanciaSeller(rs,dep);
				return obj;
			  }
			else {
				return null;
			}
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
			//a conexão com o banco só em fechada em Program
			//para permitir que façam as consultas
			}
			
		}		    
		
	private Seller instanciaSeller(ResultSet rs, Department dep) throws SQLException {
		//intanciar o seller
		Seller obj = new Seller();
		obj.setId(rs.getInt("Id"));
		obj.setName(rs.getString("Name"));
		obj.setEmail(rs.getString("Email"));
		obj.setBaseSalary(rs.getDouble("BaseSalary"));
		obj.setBirthDate(rs.getDate("BirthDate"));
		obj.setDepartment(dep);
		return obj;
		
	}

	// metodo que intancia o departamento
	private Department instaciaDepartment(ResultSet rs) throws SQLException {
		// instanciar o departament
		Department dep = new Department();
		// entre parentese o nome da coluna
		// vai dar erro mas não vou tratar.. só add Throw declaration para propagar a
		// exeção
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT seller.*, department.Name as DepName "
					+ "FROM seller INNER JOIN department "
				    + "ON seller.DepartmentId = department.Id "
				    +" ORDER BY Name ");
			
		
			//rs recebe o resultado com tabelas e colunas
			//converter para um objeto
			rs = st.executeQuery();
			// rs na posição zero é null sem resultado algum
			//o rs pode ter vários registros entao percorrer com while
			//declarar uma lista de seller
			List<Seller> lista = new ArrayList<Seller>();
			//usar map para verificar se já houve instancia de departamento no caso de varios registros
			//para não instanciar varias vezes pois a busca é por um id do departamento
			//map vazio
			Map<Integer,Department> map = new HashMap<>();
			
			while(rs.next()) {
				//criar metodo para instanciar o departament
				//clica no erro para criar o método automatico lá em baixo
				//eu não posso criar toda hora um departamento para vários registros
				//usar o map 
				//busca no map o departmento que tem esse ID com metodo getInt
				//se nao tiver retorna null
				Department dep = map.get(rs.getInt("DepartmentId"));
				// se dep igual nulo eu instancio o departamento chamando 
				//o metodo instanciaDepartment passando o resultado rs
				if(dep == null) {
					dep = instaciaDepartment(rs);
					//guarda no map o departamento para na proxima vez não dar nulo
					//guarda o ID do dep
					map.put(rs.getInt("DepartmentId"), dep);
				}
				
				//criar metodo para instanciar o vendedor seller
				//passa o rs e o dep como parametro
				Seller obj = instanciaSeller(rs,dep);
				lista.add(obj);
			  }
			return lista;
		
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
			//a conexão com o banco só em fechada em Program
			//para permitir que façam as consultas
			}
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT seller.*, department.Name as DepName "
					+ "FROM seller INNER JOIN department "
				    + "ON seller.DepartmentId = department.Id "
					+ "WHERE DepartmentId = ? "
				    +" ORDER BY Name ");
			
			//configurar o interrogação recebendo o id que chegou como parametro
			st.setInt(1, department.getId());
			//rs recebe o resultado com tabelas e colunas
			//converter para um objeto
			rs = st.executeQuery();
			// rs na posição zero é null sem resultado algum
			//o rs pode ter vários registros entao percorrer com while
			//declarar uma lista de seller
			List<Seller> lista = new ArrayList<Seller>();
			//usar map para verificar se já houve instancia de departamento no caso de varios registros
			//para não instanciar varias vezes pois a busca é por um id do departamento
			//map vazio
			Map<Integer,Department> map = new HashMap<>();
			
			while(rs.next()) {
				//criar metodo para instanciar o departament
				//clica no erro para criar o método automatico lá em baixo
				//eu não posso criar toda hora um departamento para vários registros
				//usar o map 
				//busca no map o departmento que tem esse ID com metodo getInt
				//se nao tiver retorna null
				Department dep = map.get(rs.getInt("DepartmentId"));
				// se dep igual nulo eu instancio o departamento chamando 
				//o metodo instanciaDepartment passando o resultado rs
				if(dep == null) {
					dep = instaciaDepartment(rs);
					//guarda no map o departamento para na proxima vez não dar nulo
					//guarda o ID do dep
					map.put(rs.getInt("DepartmentId"), dep);
				}
				
				//criar metodo para instanciar o vendedor seller
				//passa o rs e o dep como parametro
				Seller obj = instanciaSeller(rs,dep);
				lista.add(obj);
			  }
			return lista;
		
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
			//a conexão com o banco só em fechada em Program
			//para permitir que façam as consultas
			}
			
	}

}

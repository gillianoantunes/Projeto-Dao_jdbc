package apllication;

import java.util.Date;
import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class Program {

	public static void main(String[] args) {

		// instacia sem new chamando a fabrica DaoFactory.metodo createSellerDao
		// injeção de dependencia sem expor a implementação
		SellerDao sellerDao = DaoFactory.createSellerDao();

		System.out.println("TESTE 1 ====== Seller findById ========");
		// chamando o metodo findById que criamos na classe sellerDao
		Seller seller = sellerDao.findById(3);
		System.out.println(seller);

		System.out.println("\n TESTE 2 ====== Seller findByDepartment ========");
		// declaro variavel department recebendo new Department com id2 e nome null
		Department department = new Department(2, null);
		// lista aqui recebe a lista de resultado do metodo findByDepartment
		List<Seller> lista = sellerDao.findByDepartment(department);
		// imprime a lista
		for (Seller obj : lista) {
			System.out.println(obj);
		}
		
		System.out.println("\n TESTE 3 ====== Seller findAll ========");
		
		// lista aqui recebe a lista de resultado do metodo findByDepartment
		 lista = sellerDao.findAll();
		// imprime a lista
		for (Seller obj : lista) {
			System.out.println(obj);
		}
		
		
		System.out.println("\n TESTE 4 ====== Seller insert ========");
		//instancia o seller e chama a função insert
		Seller newseller = new Seller(null, "Greg", "greg@gmail.com", new Date(), 4000.0, department); 
		sellerDao.insert(newseller);
		//mostra a mensagem com id inserido
		System.out.println("Inserção feita com sucesso! - Novo Id = " + newseller.getId());
	
		
		System.out.println("\n TESTE 5 ====== Seller update ========");
	    //carrega os dados de vendedor de id = 1 no objeto seller
		seller = sellerDao.findById(1);
		//setei um novo nome pro vendedor com id 1
		seller.setName("Marta Silva");
		//chama o update e salva alteração
		sellerDao.update(seller);
		System.out.println("Alteração feita");
	}

}

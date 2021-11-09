package apllication;

import java.util.Date;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class Program {

	public static void main(String[] args) {
		Department obj = new Department(1,"Books");
		System.out.println(obj);
		Seller seller = new Seller(1,"bob", "bob@gmail.com", new Date(),3000.0, obj);
		
		//instacia sem new chamando a fabrica DaoFactory.metodo createSellerDao
		//injeção de dependencia sem expor a implementação
		SellerDao sellerDao = DaoFactory.createSellerDao();
		
		System.out.println(seller);
	}

}

package model.dao;

import model.dao.impl.SellerDaoJDBC;

public class DaoFactory {
	//essa classe tem operaçoes static para instanciar os daos
	//retorna um net SellerDaoJDBC
	public static SellerDao createSellerDao() {
		return new SellerDaoJDBC();
	}
}

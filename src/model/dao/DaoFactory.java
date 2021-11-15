package model.dao;

import db.DB;
import model.dao.impl.DepartmentDaoJDBC;
import model.dao.impl.SellerDaoJDBC;

public class DaoFactory {
	//essa classe tem operaçoes static para instanciar os dados
	//retorna um new SellerDaoJDBC
	public static SellerDao createSellerDao() {
		return new SellerDaoJDBC(DB.getConnection());
	}


	public static DepartmentDao createDepartmentDao() {
		return new DepartmentDaoJDBC(DB.getConnection());
	}
}

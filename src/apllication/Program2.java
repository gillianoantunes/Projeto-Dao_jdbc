package apllication;



import java.util.List;
import java.util.Scanner;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;


public class Program2 {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		// instacia sem new chamando a fabrica DaoFactory.metodo createDepartmentDao
		// injeção de dependencia sem expor a implementação
		DepartmentDao departmentDao = DaoFactory.createDepartmentDao();

		System.out.println("TESTE 1 ======Pesquisa por Id Department findById ========");
		// chamando o metodo findById que criamos na classe DepartmentDao
		Department department = departmentDao.findById(4);
		System.out.println(department);

		System.out.println("\n TESTE 2 ======Pesquisa todos Department findAll ========");
		// lista aqui recebe a lista de resultado do metodo findByDepartment
				 List<Department> lista = departmentDao.findAll();
				// imprime a lista
				for (Department dep : lista) {
					System.out.println(dep);
				}
		
				System.out.println("\n TESTE 3 ====== Department insert ========");
				//instancia o department e chama a função insert
				Department newdep = new Department(null, "Esporte"); 
				departmentDao.insert(newdep);
				//mostra a mensagem com id inserido
				System.out.println("Inserção feita com sucesso! - Novo Id = " + newdep.getId());
			
				System.out.println("\n TESTE 4 ====== Department update ========");
			    //carrega os dados de departamento de id = 6 no objeto department
				department = departmentDao.findById(6);
				//setei um novo nome pro vendedor com id 1
				department.setName("Social");
				//chama o update e salva alteração
				departmentDao.update(department);
				System.out.println("Alteração feita");
				
				System.out.println("\n TESTE 6 ====== Department delete ========");
				System.out.println("Entre com o Id a ser deletado");
				//recebe o id digitado declara o Scanner
				int id = sc.nextInt();
				//chama a função delete com o id digitado
				departmentDao.delete(id);
				System.out.println("Delete com sucesso");
				
				sc.close(); //fecha o Scanner
	}

}
